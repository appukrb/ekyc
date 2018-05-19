package com.tcs.mmpl.customer.Goibibo.Bus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusBasicInfo;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusSeatForHold;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusSeatMapDetails;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusTraveller;
import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.FontClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GoibiboBusBookingReviewActivity extends Activity {

    private LinearLayout linOnwardJourney, linReturnJourney, linTotal,linCancelPolicyReturn,linCancelPolicyOnward;
    private TextView txtOrigin, txtDeptTime, txtDeptDate, txtDestination, txtArrivalTime, txtArrivalDate, txtBoardingPoint, txtBoardingTime, txtBusOperator, txtSeats;
    private TextView txtReturnOrigin, txtReturnDeptTime, txtReturnDeptDate, txtReturnDestination, txtReturnArrivalTime, txtReturnArrivalDate, txtReturnBoardingPoint, txtReturnBoardingTime, txtReturnBusOperator, txtReturnSeats;
    private TextView txtTotal;
    private LinearLayout linPassenger1, linPassenger2, linPassenger3, linPassenger4, linPassenger5, linPassenger6;
    private ImageView imgPassenger1, imgPassenger2, imgPassenger3, imgPassenger4, imgPassenger5, imgPassenger6;
    private TextView txtPassenger1, txtPassenger2, txtPassenger3, txtPassenger4, txtPassenger5, txtPassenger6, txtPassengerHeading, txtOnwardDuration, txtReturnDuration;
    private EditText edtContactNumber, edtContactEmailId;

    private GoibiboBusDatabaseHelper goibiboBusDatabaseHelper;
    private SharedPreferences GoibiboPref,pref;
    private SharedPreferences.Editor GoibiboEditor,editor;

    private int travellerCount = 0;


    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;
    private String fromTravelDate="",toTravelDate="",toBoradingDate="",fromBoradingDate="",onward_skey="",return_skey="",journeyDate="";
    private EditText edtOTP;

    private  int total;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_bus_booking_review);

        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        goibiboBusDatabaseHelper = new GoibiboBusDatabaseHelper(getApplicationContext());

        GoibiboPref = getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        linOnwardJourney = (LinearLayout) findViewById(R.id.linOnwardJourney);
        linReturnJourney = (LinearLayout) findViewById(R.id.linReturnJourney);
        linCancelPolicyReturn = (LinearLayout)findViewById(R.id.linCancelPolicyReturn);
        linCancelPolicyOnward = (LinearLayout)findViewById(R.id.linCancelPolicyOnward);
        linTotal = (LinearLayout) findViewById(R.id.linTotal);

        linPassenger1 = (LinearLayout) findViewById(R.id.linPassenger1);
        linPassenger2 = (LinearLayout) findViewById(R.id.linPassenger2);
        linPassenger3 = (LinearLayout) findViewById(R.id.linPassenger3);
        linPassenger4 = (LinearLayout) findViewById(R.id.linPassenger4);
        linPassenger5 = (LinearLayout) findViewById(R.id.linPassenger5);
        linPassenger6 = (LinearLayout) findViewById(R.id.linPassenger6);

        imgPassenger1 = (ImageView) findViewById(R.id.imgPassenger1);
        imgPassenger2 = (ImageView) findViewById(R.id.imgPassenger2);
        imgPassenger3 = (ImageView) findViewById(R.id.imgPassenger3);
        imgPassenger4 = (ImageView) findViewById(R.id.imgPassenger4);
        imgPassenger5 = (ImageView) findViewById(R.id.imgPassenger5);
        imgPassenger6 = (ImageView) findViewById(R.id.imgPassenger6);

        txtPassenger1 = (TextView) findViewById(R.id.txtPassenger1);
        txtPassenger2 = (TextView) findViewById(R.id.txtPassenger2);
        txtPassenger3 = (TextView) findViewById(R.id.txtPassenger3);
        txtPassenger4 = (TextView) findViewById(R.id.txtPassenger4);
        txtPassenger5 = (TextView) findViewById(R.id.txtPassenger5);
        txtPassenger6 = (TextView) findViewById(R.id.txtPassenger6);


        txtPassengerHeading = (TextView) findViewById(R.id.txtPassengerHeading);
        edtContactNumber = (EditText) findViewById(R.id.edtContactNumber);
        edtContactEmailId = (EditText) findViewById(R.id.edtContactEmailId);
        edtOTP = (EditText)findViewById(R.id.edtOTP);

        txtTotal = (TextView) findViewById(R.id.txtTotal);

        linTotal.setVisibility(View.VISIBLE);


            BusBasicInfo busBasicInfo = goibiboBusDatabaseHelper.getOnwardBusInfo();

            if (!busBasicInfo.getSeats().trim().equalsIgnoreCase("NA")) {

                linCancelPolicyOnward.setVisibility(View.VISIBLE);

                txtOrigin = (TextView) findViewById(R.id.txtOrigin);
                txtDeptTime = (TextView) findViewById(R.id.txtDeptTime);
                txtDeptDate = (TextView) findViewById(R.id.txtDeptDate);
                txtDestination = (TextView) findViewById(R.id.txtDestination);
                txtArrivalTime = (TextView) findViewById(R.id.txtArrivalTime);
                txtArrivalDate = (TextView) findViewById(R.id.txtArrivalDate);
                txtBoardingPoint = (TextView) findViewById(R.id.txtBoardingPoint);
                txtBoardingTime = (TextView) findViewById(R.id.txtBoardingTime);
                txtBusOperator = (TextView) findViewById(R.id.txtBusOperator);
                txtSeats = (TextView) findViewById(R.id.txtSeats);
                txtOnwardDuration = (TextView) findViewById(R.id.txtOnwardDuration);

                linOnwardJourney.setVisibility(View.VISIBLE);

                journeyDate = busBasicInfo.getDeparturedate().split(" ")[0];

                txtOrigin.setText(busBasicInfo.getOrigin().toUpperCase());
                txtDeptTime.setText(busBasicInfo.getDeparturedate().split(" ")[1]);
                txtDeptDate.setText(busBasicInfo.getDeparturedate().split(" ")[0]);
                txtDestination.setText(busBasicInfo.getDestination().toUpperCase());
                txtArrivalTime.setText(busBasicInfo.getArrivaldate().split(" ")[1]);
                txtArrivalDate.setText(busBasicInfo.getArrivaldate().split(" ")[0]);
                txtBoardingPoint.setText(busBasicInfo.getBoardingpoint());
                txtBoardingTime.setText(busBasicInfo.getBoardingtime().split(" ")[1]);
                txtBusOperator.setText(busBasicInfo.getBusoperator());
                txtOnwardDuration.setText(busBasicInfo.getDuration());

                String seatname = goibiboBusDatabaseHelper.getSeatName(getResources().getString(R.string.onw));
                txtSeats.setText(seatname);

              fromTravelDate=txtDeptDate.getText().toString().trim() + " " + txtDeptTime.getText().toString().trim();
               fromBoradingDate=txtArrivalDate.getText().toString().trim() + " " + txtArrivalTime.getText().toString().trim();
            }


        if (GoibiboPref.getString(getResources().getString(R.string.bus_travel), getResources().getString(R.string.up)).equalsIgnoreCase(getResources().getString(R.string.up_down))) {



             busBasicInfo = goibiboBusDatabaseHelper.getReturnBusInfo();

            if (!busBasicInfo.getSeats().trim().equalsIgnoreCase("NA")) {

                linCancelPolicyReturn.setVisibility(View.VISIBLE);

                txtReturnOrigin = (TextView) findViewById(R.id.txtReturnOrigin);
                txtReturnDeptTime = (TextView) findViewById(R.id.txtReturnDeptTime);
                txtReturnDeptDate = (TextView) findViewById(R.id.txtReturnDeptDate);
                txtReturnDestination = (TextView) findViewById(R.id.txtReturnDestination);
                txtReturnArrivalTime = (TextView) findViewById(R.id.txtReturnArrivalTime);
                txtReturnArrivalDate = (TextView) findViewById(R.id.txtReturnArrivalDate);
                txtReturnBoardingPoint = (TextView) findViewById(R.id.txtReturnBoardingPoint);
                txtReturnBoardingTime = (TextView) findViewById(R.id.txtReturnBoardingTime);
                txtReturnBusOperator = (TextView) findViewById(R.id.txtReturnBusOperator);
                txtReturnSeats = (TextView) findViewById(R.id.txtReturnSeats);
                txtReturnDuration = (TextView) findViewById(R.id.txtReturnDuration);

                linReturnJourney.setVisibility(View.VISIBLE);

                txtReturnOrigin.setText(busBasicInfo.getOrigin().toUpperCase());
                txtReturnDeptTime.setText(busBasicInfo.getDeparturedate().split(" ")[1]);
                txtReturnDeptDate.setText(busBasicInfo.getDeparturedate().split(" ")[0]);
                txtReturnDestination.setText(busBasicInfo.getDestination().toUpperCase());
                txtReturnArrivalTime.setText(busBasicInfo.getArrivaldate().split(" ")[1]);
                txtReturnArrivalDate.setText(busBasicInfo.getArrivaldate().split(" ")[0]);
                txtReturnBoardingPoint.setText(busBasicInfo.getBoardingpoint());
                txtReturnBoardingTime.setText(busBasicInfo.getBoardingtime().split(" ")[1]);
                txtReturnBusOperator.setText(busBasicInfo.getBusoperator());
                txtReturnDuration.setText(busBasicInfo.getDuration());

                String seatname = goibiboBusDatabaseHelper.getSeatName(getResources().getString(R.string.ret));
                txtReturnSeats.setText(seatname);


             
                toTravelDate=txtReturnDeptDate.getText().toString().trim() + " " + txtReturnDeptTime.getText().toString().trim();
                toBoradingDate=txtReturnArrivalDate.getText().toString().trim() + " " + txtReturnArrivalTime.getText().toString().trim();
            }


        }

        total = goibiboBusDatabaseHelper.getTotalAmount();
        txtTotal.setText(getResources().getString(R.string.rupee_symbol) + String.valueOf(total));


        travellerCount = goibiboBusDatabaseHelper.getTravellerSeatCount();
        if (travellerCount > 1)
            txtPassengerHeading.setText("ADD PASSENGERS");
        else
            txtPassengerHeading.setText("ADD PASSENGER");

        //// System.out.println("TravellerCount:::" + travellerCount);
        if (travellerCount > 0) {
            for (int i = 1; i <= travellerCount; i++) {

                if (i == 1)
                    linPassenger1.setVisibility(View.VISIBLE);
                else if (i == 2)
                    linPassenger2.setVisibility(View.VISIBLE);
                else if (i == 3)
                    linPassenger3.setVisibility(View.VISIBLE);
                else if (i == 4)
                    linPassenger4.setVisibility(View.VISIBLE);
                else if (i == 5)
                    linPassenger5.setVisibility(View.VISIBLE);
                else if (i == 6)
                    linPassenger6.setVisibility(View.VISIBLE);

            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            String res = (String) data.getStringExtra("result");
            String row_id = (String) data.getStringExtra("row_id");

            switch (requestCode) {
                case 1:
                    imgPassenger1.setImageResource(R.drawable.checkeduser);
                    txtPassenger1.setText(res);
                    linPassenger1.setTag(row_id);
                    break;
                case 2:
                    imgPassenger2.setImageResource(R.drawable.checkeduser);
                    txtPassenger2.setText(res);
                    linPassenger2.setTag(row_id);
                    break;
                case 3:
                    imgPassenger3.setImageResource(R.drawable.checkeduser);
                    txtPassenger3.setText(res);
                    txtPassenger3.setTag(row_id);
                    break;
                case 4:
                    imgPassenger4.setImageResource(R.drawable.checkeduser);
                    txtPassenger4.setText(res);
                    linPassenger4.setTag(row_id);
                    break;
                case 5:
                    imgPassenger5.setImageResource(R.drawable.checkeduser);
                    txtPassenger5.setText(res);
                    linPassenger5.setTag(row_id);
                    break;
                case 6:
                    imgPassenger6.setImageResource(R.drawable.checkeduser);
                    txtPassenger6.setText(res);
                    linPassenger6.setTag(row_id);
                    break;

            }
        } catch (Exception e) {

        }

    }

    public void continueBookingReview(View v) {

        AlertBuilder alertBuilder = new AlertBuilder(GoibiboBusBookingReviewActivity.this);
        int travellerSeatCount = goibiboBusDatabaseHelper.getTravellerCount();
        if (travellerCount != travellerSeatCount) {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_traveller));
        } else if (edtContactNumber.getText().toString().trim().equalsIgnoreCase("") || edtContactNumber.getText().toString().trim().length() < 10) {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_mobile_number));

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtContactEmailId.getText().toString().trim()).matches()) {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_email));
        }
        else if(edtOTP.getText().toString().trim().equalsIgnoreCase(""))
        {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_mpin));
        }
        else {

            CheckBalance checkBalance = new CheckBalance(GoibiboBusBookingReviewActivity.this);
            if (checkBalance.getBalanceCheck(String.valueOf(goibiboBusDatabaseHelper.getTotalAmount()))) {

               AlertBuilder alertBuilder1 = new AlertBuilder(GoibiboBusBookingReviewActivity.this);
                alertBuilder1.showAlert(getResources().getString(R.string.no_balance));


            } else {


                JSONObject jsonMain = new JSONObject();

                ArrayList<BusTraveller> busTravellerArrayList = goibiboBusDatabaseHelper.getTravellerDetails();
                ArrayList<BusSeatMapDetails> busSeatMapDetailsArrayList = goibiboBusDatabaseHelper.getSeatDetails(getResources().getString(R.string.onw));
                ArrayList<String> listOnw = goibiboBusDatabaseHelper.getSkey(getResources().getString(R.string.onw));


                if (!listOnw.isEmpty()) {
                    ArrayList<BusSeatForHold> busSeatForHoldArrayList = new ArrayList<>();
                    JSONArray jsonOnwArray = new JSONArray();
                    for (int i = 0; i < busTravellerArrayList.size(); i++) {

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("title", busTravellerArrayList.get(i).getTitle());
                            jsonObject.accumulate("firstName", busTravellerArrayList.get(i).getFirstname());
                            jsonObject.accumulate("lastName", busTravellerArrayList.get(i).getLastname());
                            jsonObject.accumulate("age", busTravellerArrayList.get(i).getAge());
                            jsonObject.accumulate("eMail", edtContactEmailId.getText().toString().trim());
                            jsonObject.accumulate("mobile", edtContactNumber.getText().toString().trim());

                            jsonObject.accumulate("seatName", busSeatMapDetailsArrayList.get(i).getSeatName());
                            jsonObject.accumulate("baseFare", busSeatMapDetailsArrayList.get(i).getBaseFare());
                            jsonObject.accumulate("sTax", busSeatMapDetailsArrayList.get(i).getServiceTax());
                            jsonObject.accumulate("sTaxp", busSeatMapDetailsArrayList.get(i).getServiceTaxPercentage());
                            jsonObject.accumulate("sChrg", busSeatMapDetailsArrayList.get(i).getServiceCharge());
                            jsonObject.accumulate("actualFare", busSeatMapDetailsArrayList.get(i).getActualSeatFare());
                            jsonObject.accumulate("seatFare", busSeatMapDetailsArrayList.get(i).getSeatFare());

                            jsonOnwArray.put(jsonObject);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    try {


                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("skey", listOnw.get(0));
                        jsonObject.accumulate("bp", listOnw.get(1));
                        jsonObject.accumulate("seats", jsonOnwArray);
                        jsonMain.accumulate("onw", jsonObject);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                busSeatMapDetailsArrayList = goibiboBusDatabaseHelper.getSeatDetails(getResources().getString(R.string.ret));
                ArrayList<String> listRet = goibiboBusDatabaseHelper.getSkey(getResources().getString(R.string.ret));

                if (!listRet.isEmpty()) {
                    ArrayList<BusSeatForHold> busSeatForHoldArrayList = new ArrayList<>();
                    JSONArray jsonRetArray = new JSONArray();
                    for (int i = 0; i < busTravellerArrayList.size(); i++) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.accumulate("title", busTravellerArrayList.get(i).getTitle());
                            jsonObject.accumulate("firstName", busTravellerArrayList.get(i).getFirstname());
                            jsonObject.accumulate("lastName", busTravellerArrayList.get(i).getLastname());
                            jsonObject.accumulate("age", busTravellerArrayList.get(i).getAge());
                            jsonObject.accumulate("eMail", edtContactEmailId.getText().toString().trim());
                            jsonObject.accumulate("mobile", edtContactNumber.getText().toString().trim());

                            jsonObject.accumulate("seatName", busSeatMapDetailsArrayList.get(i).getSeatName());
                            jsonObject.accumulate("baseFare", busSeatMapDetailsArrayList.get(i).getBaseFare());
                            jsonObject.accumulate("sTax", busSeatMapDetailsArrayList.get(i).getServiceTax());
                            jsonObject.accumulate("sTaxp", busSeatMapDetailsArrayList.get(i).getServiceTaxPercentage());
                            jsonObject.accumulate("sChrg", busSeatMapDetailsArrayList.get(i).getServiceCharge());
                            jsonObject.accumulate("actualFare", busSeatMapDetailsArrayList.get(i).getActualSeatFare());
                            jsonObject.accumulate("seatFare", busSeatMapDetailsArrayList.get(i).getSeatFare());

                            jsonRetArray.put(jsonObject);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("skey", listRet.get(0));
                        jsonObject.accumulate("bp", listRet.get(1));
                        jsonObject.accumulate("seats", jsonRetArray);
                        jsonMain.accumulate("ret", jsonObject);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                BusHold busHold = new BusHold(getApplicationContext(), jsonMain.toString());
                busHold.execute(getResources().getString(R.string.bus_hold_url));
            }
        }
    }

    public void addPassenger(View v) {
        Intent intent = new Intent(getApplicationContext(), GoibiboBusTravellerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        LinearLayout linParent = (LinearLayout) v.getParent();
//        linParent.getChildCount();
//
//        // System.out.println("Child Count::::");

        String tag = (String) v.getTag();
        //// System.out.println("Tag:::" + tag);
        intent.putExtra("tag", tag);

        switch (v.getId()) {

            case R.id.linPassenger1:
                intent.putExtra("selected", 1);
                startActivityForResult(intent, 1);
                break;
            case R.id.linPassenger2:
                intent.putExtra("selected", 2);
                startActivityForResult(intent, 2);
                break;
            case R.id.linPassenger3:
                intent.putExtra("selected", 3);
                startActivityForResult(intent, 3);
                break;
            case R.id.linPassenger4:
                intent.putExtra("selected", 4);
                startActivityForResult(intent, 4);
                break;
            case R.id.linPassenger5:
                intent.putExtra("selected", 5);
                startActivityForResult(intent, 5);
                break;
            case R.id.linPassenger6:
                intent.putExtra("selected", 6);
                startActivityForResult(intent, 6);
                break;
        }

    }

    public void generateOTP(View v)
    {
        PurchaseOTP purchaseOTP = new PurchaseOTP(getApplicationContext());
        purchaseOTP.execute(getResources().getString(R.string.purchaseOTP_url));
    }
    private class PurchaseOTP extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;


        public PurchaseOTP(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboBusBookingReviewActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("MDN", pref.getString("mobile_number","")));


                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboBusBookingReviewActivity.this,nameValuePairs);
                String jsonStr = goibiboServiceHandler.makeServiceCall(arg0[0].toString(), GoibiboServiceHandler.POST1);
                return jsonStr;

            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("OTP::" + result);


        }

    }


    private class BusHold extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private String json;

        public BusHold(Context context, String json) {
            this.context = context;
            this.json = json;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboBusBookingReviewActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                //// System.out.println("JSON:::" + json);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("holddata", json));
                nameValuePairs.add(new BasicNameValuePair("MDN", pref.getString("mobile_number","")));
                nameValuePairs.add(new BasicNameValuePair("fromPlace", txtOrigin.getText().toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("toPlace", txtDestination.getText().toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("fromTravelDate", fromTravelDate));
                nameValuePairs.add(new BasicNameValuePair("fromBoradingDate",  fromBoradingDate));
                nameValuePairs.add(new BasicNameValuePair("toTravelDate", toTravelDate));
                nameValuePairs.add(new BasicNameValuePair("toBoradingDate", toBoradingDate));
                nameValuePairs.add(new BasicNameValuePair("MPIN", edtOTP.getText().toString().trim()));

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboBusBookingReviewActivity.this,nameValuePairs);
                String jsonStr = goibiboServiceHandler.makeServiceCall(arg0[0].toString(), GoibiboServiceHandler.POST1);
                return jsonStr;

            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Goibibo ID::" + result);

            try {
                AlertBuilder alertBuilder = new AlertBuilder(GoibiboBusBookingReviewActivity.this);
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(jsonObject.getString("responseMessage"));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                } else if (jsonObject.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(jsonObject.getString("responseMessage"));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                } else {
                    alertBuilder.showAlert(getResources().getString(R.string.apidown));
                }

            }
            catch (Exception e)
            {

            }

        }

    }


    @Override
    public void onBackPressed(){
        goibiboBusDatabaseHelper.deleteSeatMapAll();
        goibiboBusDatabaseHelper.deletbusInfoAll();
        goibiboBusDatabaseHelper.deletebusTraveller();
        goibiboBusDatabaseHelper.deletebusBoardingPoint();
        finish();

    }

    public void openOnwardPolicy(View v)
    {

        ArrayList<String> listOnw = goibiboBusDatabaseHelper.getSkey(getResources().getString(R.string.onw));
        onward_skey = listOnw.get(0);
        Intent intent = new Intent(getApplicationContext(),GoibiboBusCancelPolicyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("skey", onward_skey);
        intent.putExtra("amount",String.valueOf(total));
        intent.putExtra("journey_date",journeyDate);
        startActivity(intent);
    }

    public void openReturnPolicy(View v)
    {

        ArrayList<String> listRet = goibiboBusDatabaseHelper.getSkey(getResources().getString(R.string.ret));
        return_skey = listRet.get(0);
        Intent intent = new Intent(getApplicationContext(),GoibiboBusCancelPolicyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("skey", return_skey);
        intent.putExtra("amount",String.valueOf(total));
        intent.putExtra("journey_date",journeyDate);
        startActivity(intent);
    }


}
