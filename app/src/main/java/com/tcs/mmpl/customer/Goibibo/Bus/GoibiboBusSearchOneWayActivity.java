package com.tcs.mmpl.customer.Goibibo.Bus;

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

import com.tcs.mmpl.customer.Goibibo.Bus.Fragment.BusOneWayFragment;
import com.tcs.mmpl.customer.Goibibo.Bus.Parser.JsonParser;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusDetails;
import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;

import java.util.ArrayList;

public class GoibiboBusSearchOneWayActivity extends FragmentActivity {

    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;

    private String busCondition="NA",seat="NA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_bus_search);

        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        int f1 = getIntent().getIntExtra("ac",0);
        int f2 = getIntent().getIntExtra("nonac",0);
        int f3 = getIntent().getIntExtra("sleeper",0);
        int f4 = getIntent().getIntExtra("seater",0);
        if(f1 == 1)
        {
            busCondition = "ac";
        }

        if(f2 == 1)
        {
            busCondition = "nonac";
        }

        if(f3 == 1 && f4 ==1)
        {
            seat = "SLST";
        }
        else if(f3 == 1)
        {
            seat = "SL";
        }
        else if(f4 == 1)
        {
            seat = "ST";
        }

        ((TextView)findViewById(R.id.txtPlace)).setText(getIntent().getStringExtra("place"));
        ((TextView)findViewById(R.id.txtDay)).setText(getIntent().getStringExtra("day"));

        BusSearch busSearch = new BusSearch(getApplicationContext());
        busSearch.execute(getIntent().getStringExtra("url"));


    }

    private class BusSearch extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;

        public BusSearch(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboBusSearchOneWayActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboBusSearchOneWayActivity.this);
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

            JsonParser jsonParser = new JsonParser(GoibiboBusSearchOneWayActivity.this, result);
            final ArrayList<BusDetails> busDetailsList = jsonParser.convertOnwardBusSearch(seat,busCondition);

            if(busDetailsList.isEmpty()) {

                AlertBuilder alertBuilder = new AlertBuilder(GoibiboBusSearchOneWayActivity.this);
                AlertDialog.Builder  alertDialog = alertBuilder.showRetryAlert("No Buses Found");
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
                BusOneWayFragment busOneWayFragment = (BusOneWayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_one_way);
                busOneWayFragment.showBusList(busDetailsList);

            }


        }

    }


}
