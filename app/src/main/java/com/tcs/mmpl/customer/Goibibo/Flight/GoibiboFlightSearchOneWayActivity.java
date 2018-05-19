package com.tcs.mmpl.customer.Goibibo.Flight;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Goibibo.Flight.Fragment.FlightOneWayFragment;
import com.tcs.mmpl.customer.Goibibo.Flight.Parser.JsonFlightParser;
import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.FlightDetails;
import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;

import java.util.ArrayList;

public class GoibiboFlightSearchOneWayActivity extends FragmentActivity {

    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_flight_search_one_way);

        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        ((TextView)findViewById(R.id.txtPlace)).setText(getIntent().getStringExtra("place"));
        ((TextView)findViewById(R.id.txtDay)).setText(getIntent().getStringExtra("day"));
        FlightSearch flightSearch = new FlightSearch(getApplicationContext());
        flightSearch.execute(getIntent().getStringExtra("url"));

    }

    private class FlightSearch extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;

        public FlightSearch(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboFlightSearchOneWayActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {



                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboFlightSearchOneWayActivity.this);
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

            JsonFlightParser jsonFlightParser = new JsonFlightParser(getApplicationContext(),result);
            ArrayList<FlightDetails> flightDetailsList = jsonFlightParser.convertOnwardFlightSearch();

            if(flightDetailsList.isEmpty())
            {
                AlertBuilder alertBuilder = new AlertBuilder(GoibiboFlightSearchOneWayActivity.this);
                AlertDialog.Builder  alertDialog = alertBuilder.showRetryAlert("No Flights Found");
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
                ((LinearLayout) findViewById(R.id.linOneWay)).setVisibility(View.VISIBLE);
                FlightOneWayFragment flightOneWayFragment = (FlightOneWayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_one_way);
                flightOneWayFragment.showFlightList(flightDetailsList);
            }


        }

    }



}
