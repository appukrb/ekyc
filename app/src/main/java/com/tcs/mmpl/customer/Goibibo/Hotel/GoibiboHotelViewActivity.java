package com.tcs.mmpl.customer.Goibibo.Hotel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.timessquare.CalendarPickerView;
import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.Goibibo.Hotel.Parser.JsonHotelParser;
import com.tcs.mmpl.customer.Goibibo.Hotel.Pojo.HotelSelectedDetails;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GoibiboHotelViewActivity extends Activity {

    private TextView txtReturnDate,txtDate,txtHotelName,txtHotelAddress,txtRating,txtRooms;

    private ImageView imgHotel;
    private CalendarPickerView dialogView;
    private AlertDialog theDialog;

    private SimpleDateFormat df;

    private   ArrayList<HotelSelectedDetails> hotelSelectedDetailsArrayList;
    Button btnTotalPrice,btnBookNow;

    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_hotel_view);

        GoibiboPref = getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();


        btnTotalPrice = (Button)findViewById(R.id.btnTotalPrice);
        btnBookNow = (Button)findViewById(R.id.btnBookNow);

        imgHotel = (ImageView)findViewById(R.id.imgHotel);

        txtHotelName = (TextView) findViewById(R.id.txtHotelName);
        txtHotelAddress = (TextView) findViewById(R.id.txtHotelAddress);
        txtRating = (TextView) findViewById(R.id.txtRating);

        txtReturnDate = (TextView) findViewById(R.id.txtReturnDate);
        txtDate = (TextView)findViewById(R.id.txtDate);
        txtRooms = (TextView)findViewById(R.id.txtRooms);


//        df = new SimpleDateFormat("d MMM''yy\nEEEE");
//        Date currentDate = new Date();
//        String strCurrentDate = df.format(currentDate);
//
//        Calendar c = Calendar.getInstance();
//        c.setTime(currentDate);
//        c.add(Calendar.DATE, 1);
//        Date nextDate = c.getTime();
//        String strNextDate = df.format(nextDate);

        txtDate.setText(GoibiboPref.getString("hotel_from",""));
        txtReturnDate.setText(GoibiboPref.getString("hotel_to",""));
        txtRooms.setText(GoibiboPref.getString("hotel_rooms",""));

        btnTotalPrice.setText(getResources().getString(R.string.rupee_symbol) + getIntent().getStringExtra("hotel_amount") + "/night");

        String hotelSelectedurl = getResources().getString(R.string.hotel_city_hotel_details_url)+"?query="+ Uri.encode(GoibiboPref.getString("hotel_query",""),"")+"&hc="+GoibiboPref.getString("hotel_code","");

        //// System.out.println("hotelSelectedurl"+hotelSelectedurl);
        HotelSelected hotelSelected = new HotelSelected(getApplicationContext());
        hotelSelected.execute(hotelSelectedurl);

    }

    public void selectRoom(View v)
    {
        if(!hotelSelectedDetailsArrayList.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), GoibiboHotelSelectRoom.class);
            intent.putExtra("room_data",hotelSelectedDetailsArrayList.get(0).getRoomDetails());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void openOnwardCalendar(View v) {

        showCalendarInDialog("onward", R.layout.control_calendar);
        Calendar next = Calendar.getInstance();
        next.add(Calendar.MONTH, 3);
        dialogView.init(new Date(), next.getTime()) //
                .withSelectedDate(new Date());
    }

    public void openReturnCalendar(View v) {

        showCalendarInDialog("return", R.layout.control_calendar);
        Calendar next = Calendar.getInstance();
        next.add(Calendar.MONTH, 3);
        dialogView.init(new Date(), next.getTime()) //
                .withSelectedDate(new Date());
    }

    private void showCalendarInDialog(final String title, int layoutResId) {
        dialogView = (CalendarPickerView) getLayoutInflater().inflate(layoutResId, null, false);
        theDialog = new AlertDialog.Builder(GoibiboHotelViewActivity.this) //
                .setTitle(title)
                .setView(dialogView)
                .setNeutralButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        String selectedDate = df.format(dialogView.getSelectedDate());
                        if (title.equalsIgnoreCase("onward")) {
                            txtDate.setText(selectedDate);

                            try {
                                if (df.parse(txtDate.getText().toString()).after(df.parse(txtReturnDate.getText().toString()))) {
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(dialogView.getSelectedDate());
                                    c.add(Calendar.DATE, 1);
                                    Date nextDate = c.getTime();
                                    String strNextDate = df.format(nextDate);
                                    txtReturnDate.setText(strNextDate);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                if (df.parse(txtDate.getText().toString()).after(df.parse(selectedDate)))
                                    Toast.makeText(getApplicationContext(), "Return date should be after departure date", Toast.LENGTH_LONG).show();
                                else
                                    txtReturnDate.setText(selectedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .create();
        theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                dialogView.fixDialogDimens();
            }
        });
        theDialog.show();
    }

    private class HotelSelected extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;

        public HotelSelected(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboHotelViewActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboHotelViewActivity.this);
                String jsonStr = goibiboServiceHandler.makeServiceCall(arg0[0].toString(), GoibiboServiceHandler.POST);
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

            JsonHotelParser jsonHotelParser = new JsonHotelParser(GoibiboHotelViewActivity.this, result);
             hotelSelectedDetailsArrayList = jsonHotelParser.convertHotelSelected();

            if(hotelSelectedDetailsArrayList.isEmpty()) {

                AlertBuilder alertBuilder = new AlertBuilder(GoibiboHotelViewActivity.this);
                AlertDialog.Builder  alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        finish();
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
            else
            {


                Picasso.with(GoibiboHotelViewActivity.this).load(getIntent().getStringExtra("hotel_image")).placeholder(R.drawable.backgroud_default_image).into(imgHotel);
                txtHotelName.setText(hotelSelectedDetailsArrayList.get(0).getHn());
                txtRating.setText(hotelSelectedDetailsArrayList.get(0).getGr()+"/5");
                txtHotelAddress.setText(hotelSelectedDetailsArrayList.get(0).getAddress());


                GoibiboEditor.putString("hotel_name",hotelSelectedDetailsArrayList.get(0).getHn());
                GoibiboEditor.commit();
            }


        }

    }
}
