package com.tcs.mmpl.customer.Goibibo.Hotel;

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
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.Goibibo.Hotel.Pojo.RoomDetails;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.CheckBalance;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GoibiboHotelBookReviewActivity extends Activity {


    private RoomDetails roomDetails;
    private TextView txtHotelName,txtCheckIn,txtCheckOut,txtRooms,txtStandardEP,txtTax,txtTotalAmount;
    ImageView imgHotel;

    private SharedPreferences GoibiboPref,pref;
    private SharedPreferences.Editor GoibiboEditor,editor;
    int totalAmount;

    private EditText edtMpin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_hotel_book_review);

        edtMpin  = (EditText)findViewById(R.id.edtMpin);

        GoibiboPref = getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();


        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        roomDetails = (RoomDetails) getIntent().getSerializableExtra("room_details");

        txtHotelName = (TextView)findViewById(R.id.txtHotelName);
        txtCheckIn = (TextView)findViewById(R.id.txtCheckIn);
        txtCheckOut = (TextView)findViewById(R.id.txtCheckOut);
        txtStandardEP = (TextView)findViewById(R.id.txtStandardEP);
        txtRooms = (TextView)findViewById(R.id.txtRooms);
        txtTax = (TextView)findViewById(R.id.txtTax);
        txtTotalAmount = (TextView)findViewById(R.id.txtTotalAmount);


        imgHotel = (ImageView)findViewById(R.id.imgHotel);


        Picasso.with(GoibiboHotelBookReviewActivity.this).load(roomDetails.getRoom_info().getImage()).placeholder(R.drawable.backgroud_default_image).into(imgHotel);
        txtHotelName.setText(GoibiboPref.getString("hotel_name", ""));
        txtCheckIn.setText(GoibiboPref.getString("hotel_from","").replaceAll("\n", ","));
        txtCheckOut.setText(GoibiboPref.getString("hotel_to","").replaceAll("\n",","));
        txtRooms.setText(GoibiboPref.getString("hotel_rooms", ""));
        txtStandardEP.setText(getResources().getString(R.string.rupee_symbol)+roomDetails.getTp());
        txtTax.setText(getResources().getString(R.string.rupee_symbol)+roomDetails.getTtc());
        totalAmount = Integer.parseInt(roomDetails.getMp())+Integer.parseInt(roomDetails.getTtc());
        txtTotalAmount.setText(getResources().getString(R.string.rupee_symbol)+String.valueOf(roomDetails.getTp_alltax()));


    }

    public void continueBookingReview(View v)
    {

        AlertBuilder alertBuilder = new AlertBuilder(GoibiboHotelBookReviewActivity.this);

        if(edtMpin.getText().toString().trim().equalsIgnoreCase(""))
        {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_mpin));
        }
        else {

            CheckBalance checkBalance = new CheckBalance(GoibiboHotelBookReviewActivity.this);
            if (checkBalance.getBalanceCheck(String.valueOf(totalAmount))) {

                AlertBuilder alertBuilder1 = new AlertBuilder(GoibiboHotelBookReviewActivity.this);
                alertBuilder1.showAlert(getResources().getString(R.string.no_balance));


            } else {

                HotelBook hotelBook = new HotelBook(getApplicationContext());
                hotelBook.execute(getResources().getString(R.string.hotel_book_url));
            }
        }
    }

    public void openHotelPolicy(View v)
    {
        Intent intent = new Intent(getApplicationContext(),GoibiboHotelCancelPolicyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("room_details",roomDetails);
        startActivity(intent);
    }

    private class HotelBook extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;

        private JSONArray jsonRepriceBookingData;
        public HotelBook(Context context ) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboHotelBookReviewActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                String customer_details = "{\"firstname\":\"mRUPEE\",\"lastname\":\" mRUPEE\",\"email\":\"abc@gmail.com\",\"mobile\":\"9812345676\",\" country_phone_code\":\"+91\",\"title\":\"Mr\"}";

                List<NameValuePair> nameValuePairs = new ArrayList<>();

                nameValuePairs.add(new BasicNameValuePair("MPIN", edtMpin.getText().toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("MDN", pref.getString("mobile_number", "")));
                nameValuePairs.add(new BasicNameValuePair("query", GoibiboPref.getString("hotel_query","")));
                nameValuePairs.add(new BasicNameValuePair("hc", GoibiboPref.getString("hotel_code","")));
                nameValuePairs.add(new BasicNameValuePair("ibp",  roomDetails.getIbp()));
                nameValuePairs.add(new BasicNameValuePair("rpc", roomDetails.getRpc()));
                nameValuePairs.add(new BasicNameValuePair("rtc",roomDetails.getRtc()));
                nameValuePairs.add(new BasicNameValuePair("fwdp",roomDetails.getFwdp()));
                nameValuePairs.add(new BasicNameValuePair("customer_details",customer_details));
                nameValuePairs.add(new BasicNameValuePair("amount",String.valueOf(totalAmount)));
                nameValuePairs.add(new BasicNameValuePair("city",GoibiboPref.getString("hotel_city", "")));
                nameValuePairs.add(new BasicNameValuePair("fromdate",GoibiboPref.getString("hotel_from","").replaceAll("\n", ",")));
                nameValuePairs.add(new BasicNameValuePair("todate",GoibiboPref.getString("hotel_to","").replaceAll("\n",",")));


                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboHotelBookReviewActivity.this, nameValuePairs);
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
                AlertBuilder alertBuilder = new AlertBuilder(GoibiboHotelBookReviewActivity.this);
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
