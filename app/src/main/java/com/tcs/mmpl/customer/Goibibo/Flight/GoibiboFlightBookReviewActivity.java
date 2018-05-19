package com.tcs.mmpl.customer.Goibibo.Flight;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.BookingData;
import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.FlightDetails;
import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.FlightTraveller;
import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.CheckBalance;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoibiboFlightBookReviewActivity extends Activity {

    private LinearLayout linOnwardJourney, linReturnJourney, linTotal;
    private TextView txtOnwardInfo, txtOnwardTimingInfo, txtOnwardWarnings, txtOnwardAirlines, txtOnwardFlightCode, txtOnwardSeatingClass, txtOnwardFrom, txtOnwardTo, txtOnwardDeptTime, txtOnwardArrTime;
    private TextView txtReturnInfo, txtReturnTimingInfo, txtReturnWarnings, txtReturnAirlines, txtReturnFlightCode, txtReturnSeatingClass, txtReturnFrom, txtReturnTo, txtReturnDeptTime, txtReturnArrTime;
    private TextView txtTotal, txtNoOfPassengers;
    private LinearLayout linPassenger1, linPassenger2, linPassenger3, linPassenger4, linPassenger5, linPassenger6, linPassenger7, linPassenger8, linPassenger9;
    private ImageView imgPassenger1, imgPassenger2, imgPassenger3, imgPassenger4, imgPassenger5, imgPassenger6, imgPassenger7, imgPassenger8, imgPassenger9;
    private TextView txtPassenger1, txtPassenger2, txtPassenger3, txtPassenger4, txtPassenger5, txtPassenger6, txtPassenger7, txtPassenger8, txtPassenger9;
    private EditText edtContactNumber, edtContactEmailId,edtOTP;
    private GoibiboFlightDatabaseHelper goibiboFlightDatabaseHelper;
    private SharedPreferences GoibiboPref,pref;
    private SharedPreferences.Editor GoibiboEditor,editor;

    private int total = 0;
    private int totalCount;

    private String bookingdata = "", querydata = "", contactinfo = "", paxinfo = "",searchKey_onward = "",searchKey_return = "",type="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_flight_book_review);


        GoibiboPref = getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();


        goibiboFlightDatabaseHelper = new GoibiboFlightDatabaseHelper(getApplicationContext());
        goibiboFlightDatabaseHelper.deleteFlightTraveller();

        linPassenger1 = (LinearLayout) findViewById(R.id.linPassenger1);
        linPassenger2 = (LinearLayout) findViewById(R.id.linPassenger2);
        linPassenger3 = (LinearLayout) findViewById(R.id.linPassenger3);
        linPassenger4 = (LinearLayout) findViewById(R.id.linPassenger4);
        linPassenger5 = (LinearLayout) findViewById(R.id.linPassenger5);
        linPassenger6 = (LinearLayout) findViewById(R.id.linPassenger6);
        linPassenger7 = (LinearLayout) findViewById(R.id.linPassenger7);
        linPassenger8 = (LinearLayout) findViewById(R.id.linPassenger8);
        linPassenger9 = (LinearLayout) findViewById(R.id.linPassenger9);

        imgPassenger1 = (ImageView) findViewById(R.id.imgPassenger1);
        imgPassenger2 = (ImageView) findViewById(R.id.imgPassenger2);
        imgPassenger3 = (ImageView) findViewById(R.id.imgPassenger3);
        imgPassenger4 = (ImageView) findViewById(R.id.imgPassenger4);
        imgPassenger5 = (ImageView) findViewById(R.id.imgPassenger5);
        imgPassenger6 = (ImageView) findViewById(R.id.imgPassenger6);
        imgPassenger7 = (ImageView) findViewById(R.id.imgPassenger7);
        imgPassenger8 = (ImageView) findViewById(R.id.imgPassenger8);
        imgPassenger9 = (ImageView) findViewById(R.id.imgPassenger9);

        txtPassenger1 = (TextView) findViewById(R.id.txtPassenger1);
        txtPassenger2 = (TextView) findViewById(R.id.txtPassenger2);
        txtPassenger3 = (TextView) findViewById(R.id.txtPassenger3);
        txtPassenger4 = (TextView) findViewById(R.id.txtPassenger4);
        txtPassenger5 = (TextView) findViewById(R.id.txtPassenger5);
        txtPassenger6 = (TextView) findViewById(R.id.txtPassenger6);
        txtPassenger7 = (TextView) findViewById(R.id.txtPassenger7);
        txtPassenger8 = (TextView) findViewById(R.id.txtPassenger8);
        txtPassenger9 = (TextView) findViewById(R.id.txtPassenger9);

        linOnwardJourney = (LinearLayout) findViewById(R.id.linOnwardJourney);
        linReturnJourney = (LinearLayout) findViewById(R.id.linReturnJourney);
        linTotal = (LinearLayout) findViewById(R.id.linTotal);

        edtContactNumber = (EditText) findViewById(R.id.edtContactNumber);
        edtContactEmailId = (EditText) findViewById(R.id.edtContactEmailId);
        edtOTP = (EditText)findViewById(R.id.edtOTP);

        txtTotal = (TextView) findViewById(R.id.txtTotal);
        txtNoOfPassengers = (TextView) findViewById(R.id.txtNoOfPassengers);


        BookingData bookingData = null;
        JSONArray jsonBookingDataArray = new JSONArray();
        JSONObject jsonQueryData = new JSONObject();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat change_df = new SimpleDateFormat("yyyy-MM-dd");

        //For Onward Tickets
        FlightDetails flightDetails = goibiboFlightDatabaseHelper.getFlightOnwardInfo();
        if (!flightDetails.getBookingdata().trim().equalsIgnoreCase("NA")) {

            Date d1 = null;
            try {
                d1 = df.parse(flightDetails.getDepdate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String strdepdate = change_df.format(d1);

            searchKey_onward = flightDetails.getSearchKey();

//            Toast.makeText(getApplicationContext(),flightDetails.getBookingdata(),Toast.LENGTH_LONG).show();
//            bookingData = new BookingData();
//            bookingData.setBooking_data(flightDetails.getBookingdata());
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(flightDetails.getBookingdata());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonBookingDataArray.put(jsonObject);

            try {
                jsonQueryData.accumulate("origin", flightDetails.getOrigin());
                jsonQueryData.accumulate("destination", flightDetails.getDestination());
                jsonQueryData.accumulate("depdate", strdepdate);
                jsonQueryData.accumulate("adults", GoibiboPref.getString("adult", "1"));
                jsonQueryData.accumulate("infants", GoibiboPref.getString("child", "0"));
                jsonQueryData.accumulate("children", GoibiboPref.getString("infant", "0"));
            } catch (JSONException e) {

            }


            txtOnwardInfo = (TextView) findViewById(R.id.txtOnwardInfo);
            txtOnwardTimingInfo = (TextView) findViewById(R.id.txtOnwardTimingInfo);
            txtOnwardWarnings = (TextView) findViewById(R.id.txtOnwardWarnings);
            txtOnwardAirlines = (TextView) findViewById(R.id.txtOnwardAirlines);
            txtOnwardFlightCode = (TextView) findViewById(R.id.txtOnwardFlightCode);
            txtOnwardSeatingClass = (TextView) findViewById(R.id.txtOnwardSeatingClass);
            txtOnwardFrom = (TextView) findViewById(R.id.txtOnwardFrom);
            txtOnwardTo = (TextView) findViewById(R.id.txtOnwardTo);
            txtOnwardDeptTime = (TextView) findViewById(R.id.txtOnwardDeptTime);
            txtOnwardArrTime = (TextView) findViewById(R.id.txtOnwardArrTime);

            linOnwardJourney.setVisibility(View.VISIBLE);


            txtOnwardInfo.setText(flightDetails.getOrigin() + " " + getResources().getString(R.string.right_arrow_symbol) + " " + flightDetails.getDestination());
            txtOnwardTimingInfo.setText(flightDetails.getDepdate() + "|" + flightDetails.getStops() + " Stops|" + flightDetails.getDuration());
            txtOnwardWarnings.setText(flightDetails.getWarnings());
            txtOnwardAirlines.setText(flightDetails.getAirline());
            txtOnwardFlightCode.setText(flightDetails.getCarrierid() + " - " + flightDetails.getFlightno());
            if (flightDetails.getSeatingclass().trim().equalsIgnoreCase("E"))
                txtOnwardSeatingClass.setText("Economy");
            else
                txtOnwardSeatingClass.setText("Business");
            txtOnwardFrom.setText(flightDetails.getOrigin());
            txtOnwardTo.setText(flightDetails.getDestination());
            txtOnwardDeptTime.setText(flightDetails.getDeptime());
            txtOnwardArrTime.setText(flightDetails.getArrtime());

            total += Integer.parseInt(flightDetails.getTotalfare());


        }


        //For Return Tickets
        if (GoibiboPref.getString(getResources().getString(R.string.flight_travel), getResources().getString(R.string.up)).equalsIgnoreCase(getResources().getString(R.string.up_down))) {

            type="2";
            flightDetails = goibiboFlightDatabaseHelper.getFlightReturnInfo();

            if (!flightDetails.getBookingdata().trim().equalsIgnoreCase("NA")) {

                Date d1 = null;
                try {
                    d1 = df.parse(flightDetails.getArrdate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String strarrdate = change_df.format(d1);

                searchKey_return = flightDetails.getSearchKey();
//                bookingData = new BookingData();
//                bookingData.setBooking_data(flightDetails.getBookingdata());

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(flightDetails.getBookingdata());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonBookingDataArray.put(jsonObject);

                try {
                    jsonQueryData.accumulate("arrdate", strarrdate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                txtReturnInfo = (TextView) findViewById(R.id.txtReturnInfo);
                txtReturnTimingInfo = (TextView) findViewById(R.id.txtReturnTimingInfo);
                txtReturnWarnings = (TextView) findViewById(R.id.txtReturnWarnings);
                txtReturnAirlines = (TextView) findViewById(R.id.txtReturnAirlines);
                txtReturnFlightCode = (TextView) findViewById(R.id.txtReturnFlightCode);
                txtReturnSeatingClass = (TextView) findViewById(R.id.txtReturnSeatingClass);
                txtReturnFrom = (TextView) findViewById(R.id.txtReturnFrom);
                txtReturnTo = (TextView) findViewById(R.id.txtReturnTo);
                txtReturnDeptTime = (TextView) findViewById(R.id.txtReturnDeptTime);
                txtReturnArrTime = (TextView) findViewById(R.id.txtReturnArrTime);
                linReturnJourney.setVisibility(View.VISIBLE);


                txtReturnInfo.setText(flightDetails.getOrigin() + " " + getResources().getString(R.string.right_arrow_symbol) + " " + flightDetails.getDestination());
                txtReturnTimingInfo.setText(flightDetails.getDepdate() + "|" + flightDetails.getStops() + " Stops|" + flightDetails.getDuration());
                txtReturnWarnings.setText(flightDetails.getWarnings());
                txtReturnAirlines.setText(flightDetails.getAirline());
                txtReturnFlightCode.setText(flightDetails.getCarrierid() + " - " + flightDetails.getFlightno());
                if (flightDetails.getSeatingclass().trim().equalsIgnoreCase("E"))
                    txtReturnSeatingClass.setText("Economy");
                else
                    txtReturnSeatingClass.setText("Business");
                txtReturnFrom.setText(flightDetails.getOrigin());
                txtReturnTo.setText(flightDetails.getDestination());
                txtReturnDeptTime.setText(flightDetails.getDeptime());
                txtReturnArrTime.setText(flightDetails.getArrtime());

                total += Integer.parseInt(flightDetails.getTotalfare());


            }
        }


        bookingdata = jsonBookingDataArray.toString();
        querydata = jsonQueryData.toString();

        //Set Totals
        linTotal.setVisibility(View.VISIBLE);
        int adult = 0, child = 0, infant = 0;
        adult = Integer.parseInt(GoibiboPref.getString("adult", "1"));
        child = Integer.parseInt(GoibiboPref.getString("child", "0"));
        infant = Integer.parseInt(GoibiboPref.getString("infant", "0"));
        totalCount = adult + child + infant;
        total = total * totalCount;
        txtTotal.setText(getResources().getString(R.string.rupee_symbol) + String.valueOf(total));
        txtNoOfPassengers.setText(GoibiboPref.getString("passengers", ""));


        //Add Passengers
        if (totalCount > 0) {
            for (int i = 1; i <= adult; i++) {

                if (i == 1) {
                    linPassenger1.setVisibility(View.VISIBLE);
                    txtPassenger1.setText("Adult");
                } else if (i == 2) {
                    linPassenger2.setVisibility(View.VISIBLE);
                    txtPassenger2.setText("Adult");
                } else if (i == 3) {
                    linPassenger3.setVisibility(View.VISIBLE);
                    txtPassenger3.setText("Adult");
                } else if (i == 4) {
                    linPassenger4.setVisibility(View.VISIBLE);
                    txtPassenger4.setText("Adult");
                } else if (i == 5) {
                    linPassenger5.setVisibility(View.VISIBLE);
                    txtPassenger5.setText("Adult");
                } else if (i == 6) {
                    linPassenger6.setVisibility(View.VISIBLE);
                    txtPassenger6.setText("Adult");
                } else if (i == 7) {
                    linPassenger7.setVisibility(View.VISIBLE);
                    txtPassenger7.setText("Adult");
                } else if (i == 8) {
                    linPassenger8.setVisibility(View.VISIBLE);
                    txtPassenger8.setText("Adult");
                } else if (i == 9) {
                    linPassenger9.setVisibility(View.VISIBLE);
                    txtPassenger9.setText("Adult");
                }


            }
            for (int i = adult + 1; i <= adult + child; i++) {

                if (i == 1) {
                    linPassenger1.setVisibility(View.VISIBLE);
                    txtPassenger1.setText("Child");
                } else if (i == 2) {
                    linPassenger2.setVisibility(View.VISIBLE);
                    txtPassenger2.setText("Child");
                } else if (i == 3) {
                    linPassenger3.setVisibility(View.VISIBLE);
                    txtPassenger3.setText("Child");
                } else if (i == 4) {
                    linPassenger4.setVisibility(View.VISIBLE);
                    txtPassenger4.setText("Child");
                } else if (i == 5) {
                    linPassenger5.setVisibility(View.VISIBLE);
                    txtPassenger5.setText("Child");
                } else if (i == 6) {
                    linPassenger6.setVisibility(View.VISIBLE);
                    txtPassenger6.setText("Child");
                } else if (i == 7) {
                    linPassenger7.setVisibility(View.VISIBLE);
                    txtPassenger7.setText("Child");
                } else if (i == 8) {
                    linPassenger8.setVisibility(View.VISIBLE);
                    txtPassenger8.setText("Child");
                } else if (i == 9) {
                    linPassenger9.setVisibility(View.VISIBLE);
                    txtPassenger9.setText("Child");
                }


            }

            for (int i = adult + child + 1; i <= adult + child + infant; i++) {

                if (i == 1) {
                    linPassenger1.setVisibility(View.VISIBLE);
                    txtPassenger1.setText("Infant");
                } else if (i == 2) {
                    linPassenger2.setVisibility(View.VISIBLE);
                    txtPassenger2.setText("Infant");
                } else if (i == 3) {
                    linPassenger3.setVisibility(View.VISIBLE);
                    txtPassenger3.setText("Infant");
                } else if (i == 4) {
                    linPassenger4.setVisibility(View.VISIBLE);
                    txtPassenger4.setText("Infant");
                } else if (i == 5) {
                    linPassenger5.setVisibility(View.VISIBLE);
                    txtPassenger5.setText("Infant");
                } else if (i == 6) {
                    linPassenger6.setVisibility(View.VISIBLE);
                    txtPassenger6.setText("Infant");
                } else if (i == 7) {
                    linPassenger7.setVisibility(View.VISIBLE);
                    txtPassenger7.setText("Infant");
                } else if (i == 8) {
                    linPassenger8.setVisibility(View.VISIBLE);
                    txtPassenger8.setText("Infant");
                } else if (i == 9) {
                    linPassenger9.setVisibility(View.VISIBLE);
                    txtPassenger9.setText("Infant");
                }


            }
        }

    }

    public void addPassenger(View v) {

        String tag = (String) v.getTag();
        //// System.out.println("Tag:::" + tag);

        LinearLayout linearLayout = (LinearLayout) v;
        TextView txt = (TextView) linearLayout.getChildAt(1);

        switch (v.getId()) {

            case R.id.linPassenger1:
                openPassenger(txt, tag);
                break;
            case R.id.linPassenger2:
                openPassenger(txt, tag);
                break;
            case R.id.linPassenger3:
                openPassenger(txt, tag);
                break;
            case R.id.linPassenger4:
                openPassenger(txt, tag);
                break;
            case R.id.linPassenger5:
                openPassenger(txt, tag);
                break;
            case R.id.linPassenger6:
                openPassenger(txt, tag);
                break;
            case R.id.linPassenger7:
                openPassenger(txt, tag);
                break;
            case R.id.linPassenger8:
                openPassenger(txt, tag);
                break;
            case R.id.linPassenger9:
                openPassenger(txt, tag);
                break;
        }

    }

    public void openPassenger(TextView txt, String tag) {
        Intent intent = null;
        if (txt.getText().toString().trim().equalsIgnoreCase("Adult")) {
            intent = new Intent(getApplicationContext(), GoibiboFlightAdultTravellerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selected", 1);
            intent.putExtra("tag", tag);
            startActivityForResult(intent, 1);
        } else if (txt.getText().toString().trim().equalsIgnoreCase("Child")) {
            intent = new Intent(getApplicationContext(), GoibiboFlightChildInfantTravellerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selected", 2);
            intent.putExtra("tag", tag);
            startActivityForResult(intent, 2);
        } else if (txt.getText().toString().trim().equalsIgnoreCase("Infant")) {
            intent = new Intent(getApplicationContext(), GoibiboFlightChildInfantTravellerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("selected", 3);
            intent.putExtra("tag", tag);
            startActivityForResult(intent, 3);
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
                case 7:
                    imgPassenger7.setImageResource(R.drawable.checkeduser);
                    txtPassenger7.setText(res);
                    linPassenger7.setTag(row_id);
                    break;
                case 8:
                    imgPassenger8.setImageResource(R.drawable.checkeduser);
                    txtPassenger8.setText(res);
                    linPassenger8.setTag(row_id);
                    break;
                case 9:
                    imgPassenger9.setImageResource(R.drawable.checkeduser);
                    txtPassenger9.setText(res);
                    linPassenger9.setTag(row_id);
                    break;


            }
        } catch (Exception e) {

        }

    }

    public void continueBookingReview(View v) {

        AlertBuilder alertBuilder = new AlertBuilder(GoibiboFlightBookReviewActivity.this);

        int travellerCount = goibiboFlightDatabaseHelper.getFlightTravellerCount();

        if(totalCount != travellerCount)
        {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_traveller));
        }
        else if (edtContactNumber.getText().toString().trim().length() != 10) {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_mobile_number));

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtContactEmailId.getText().toString().trim()).matches()) {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_email));

        }
        else if(edtOTP.getText().toString().trim().equalsIgnoreCase(""))
        {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_mpin));
        }
        else {

            CheckBalance checkBalance = new CheckBalance(GoibiboFlightBookReviewActivity.this);
            if (checkBalance.getBalanceCheck(String.valueOf(total))) {

                AlertBuilder alertBuilder1 = new AlertBuilder(GoibiboFlightBookReviewActivity.this);
                alertBuilder1.showAlert(getResources().getString(R.string.no_balance));


            } else {


                ArrayList<FlightTraveller> flightTravellerArrayList = goibiboFlightDatabaseHelper.getFlightTravellerDetails();

                JSONObject jsonPaxInfo = new JSONObject();
                JSONArray jsonPaxInfoArray = new JSONArray();
                for (int i = 0; i < flightTravellerArrayList.size(); i++) {

                    FlightTraveller flightTraveller = flightTravellerArrayList.get(i);

                    JSONObject jsonObject = new JSONObject();

                    try {

                        jsonObject.accumulate("FirstName", flightTraveller.getFirstName());
                        jsonObject.accumulate("eticketnumber", flightTraveller.getEticketnumber());
                        jsonObject.accumulate("LastName", flightTraveller.getLastName());
                        jsonObject.accumulate("Title", flightTraveller.getTitle());
                        jsonObject.accumulate("DateOfBirth", flightTraveller.getDateOfBirth());
                        jsonObject.accumulate("Type", flightTraveller.getType());

                        //// System.out.println("paxinfo::" + jsonObject.toString());

                        jsonPaxInfoArray.put(jsonObject);
                    } catch (JSONException e) {

                    }
                }


                paxinfo = String.valueOf(jsonPaxInfoArray);

                JSONObject jsonContactInfo = new JSONObject();
                try {
                    jsonContactInfo.accumulate("pincode", "110001");
                    jsonContactInfo.accumulate("state", "Delhi");
                    jsonContactInfo.accumulate("firstname", "Ritesh");
                    jsonContactInfo.accumulate("lastname", "Shah");
                    jsonContactInfo.accumulate("email", edtContactEmailId.getText().toString().trim());
                    jsonContactInfo.accumulate("landline", "");
                    jsonContactInfo.accumulate("mobile", edtContactNumber.getText().toString().trim());
                    jsonContactInfo.accumulate("payment", "CARD");
                    jsonContactInfo.accumulate("address", "mRUPEE");
                    jsonContactInfo.accumulate("city", "New Delhi");

                    contactinfo = jsonContactInfo.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                FlightBook flightBook = new FlightBook(getApplicationContext());
                flightBook.execute(getResources().getString(R.string.flight_book_url));
            }
        }

    }

    private class FlightBook extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;

        private JSONArray jsonRepriceBookingData;
        public FlightBook(Context context ) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboFlightBookReviewActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                Log.i("bookingdata",bookingdata);
                Log.i("querydata",querydata);
                Log.i("fare",String.valueOf(total));
                Log.i("searchKey_onward",searchKey_onward);
                Log.i("searchKey_return", searchKey_return);
                Log.i("paxinfo", paxinfo);
                Log.i("contactinfo", contactinfo);


                List<NameValuePair> nameValuePairs = new ArrayList<>();

                nameValuePairs.add(new BasicNameValuePair("bookingdata", bookingdata));
                nameValuePairs.add(new BasicNameValuePair("querydata", querydata));
                nameValuePairs.add(new BasicNameValuePair("fare", String.valueOf(total)));
                nameValuePairs.add(new BasicNameValuePair("searchKey_onward", searchKey_onward));
                if(GoibiboPref.getString(getResources().getString(R.string.flight_travel),getResources().getString(R.string.up)).equalsIgnoreCase(getResources().getString(R.string.up_down)))
                nameValuePairs.add(new BasicNameValuePair("searchKey_return", searchKey_return));
                nameValuePairs.add(new BasicNameValuePair("paxinfo",  paxinfo));
                nameValuePairs.add(new BasicNameValuePair("contactinfo", contactinfo));
                nameValuePairs.add(new BasicNameValuePair("MDN",pref.getString("mobile_number","")));
                nameValuePairs.add(new BasicNameValuePair("fromPlace",GoibiboPref.getString("flight_from","")));
                nameValuePairs.add(new BasicNameValuePair("toPlace",GoibiboPref.getString("flight_to","")));
                nameValuePairs.add(new BasicNameValuePair("fromTravelDate",txtOnwardFrom.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("fromBoradingDate",txtOnwardTo.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("MPIN", edtOTP.getText().toString().trim()));

                if (GoibiboPref.getString(getResources().getString(R.string.flight_travel), getResources().getString(R.string.up)).equalsIgnoreCase(getResources().getString(R.string.up_down)))
                {
                    nameValuePairs.add(new BasicNameValuePair("toTravelDate", txtReturnFrom.getText().toString()));
                    nameValuePairs.add(new BasicNameValuePair("toBoradingDate", txtReturnTo.getText().toString()));
                }
                else {
                    nameValuePairs.add(new BasicNameValuePair("toTravelDate", ""));
                    nameValuePairs.add(new BasicNameValuePair("toBoradingDate", ""));
                }
                nameValuePairs.add(new BasicNameValuePair("Type",type));


                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboFlightBookReviewActivity.this, nameValuePairs);
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

//            try {
//                File myFile = new File("/sdcard/bookrequest.txt");
//                myFile.createNewFile();
//                FileOutputStream fOut = new FileOutputStream(myFile);
//                OutputStreamWriter myOutWriter =
//                        new OutputStreamWriter(fOut);
//                myOutWriter.append(bookingdata+"\n\n");
//                myOutWriter.append(querydata+"\n\n");
//                myOutWriter.append(String.valueOf(total)+"\n\n");
//                myOutWriter.append(searchKey_onward+"\n\n");
//                myOutWriter.append(searchKey_return+"\n\n");
//                myOutWriter.append(paxinfo+"\n\n");
//                myOutWriter.append(contactinfo+"\n\n");
//
//                myOutWriter.close();
//                fOut.close();
//                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }



            Log.i("Goibibo Flight Book::" , result);

            try {
                AlertBuilder alertBuilder = new AlertBuilder(GoibiboFlightBookReviewActivity.this);
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

            } catch (Exception e) {

            }

        }

    }

}
