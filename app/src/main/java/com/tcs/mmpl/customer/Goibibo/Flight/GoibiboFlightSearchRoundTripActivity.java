package com.tcs.mmpl.customer.Goibibo.Flight;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Goibibo.Flight.Fragment.FlightOneWayFragment;
import com.tcs.mmpl.customer.Goibibo.Flight.Fragment.FlightReturnFragment;
import com.tcs.mmpl.customer.Goibibo.Flight.Parser.JsonFlightParser;
import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.FlightDetails;
import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.PagerSlidingTabStrip;

import java.util.ArrayList;

public class GoibiboFlightSearchRoundTripActivity extends FragmentActivity {

    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;
    GoibiboFlightDatabaseHelper goibiboFlightDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_flight_search_round_trip);

        goibiboFlightDatabaseHelper = new GoibiboFlightDatabaseHelper(getApplicationContext());

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
            pDialog = new ProgressDialog(GoibiboFlightSearchRoundTripActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboFlightSearchRoundTripActivity.this);
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
                AlertBuilder alertBuilder = new AlertBuilder(GoibiboFlightSearchRoundTripActivity.this);
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

                ((LinearLayout) findViewById(R.id.linRoundTrip)).setVisibility(View.VISIBLE);
                PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
                tabs.setTextColorResource(R.color.black);
                ViewPager pager = (ViewPager) findViewById(R.id.pager);
                PagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(),result);
                pager.setAdapter(adapter);
                final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                        getResources().getDisplayMetrics());
                pager.setPageMargin(pageMargin);
                tabs.setViewPager(pager);
            }


        }

    }

    public class MyPagerAdapter extends FragmentPagerAdapter
    {

        public String tabtitles[];

        private int tabxml[] = {R.layout.fragment_bus_one_way, R.layout.fragment_return};

        private String result;

        public MyPagerAdapter(FragmentManager fm,String result) {
            super(fm);
            this.result = result;
            tabtitles = getResources().getStringArray(
                    R.array.GoibiboList);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return tabtitles[position];
        }

        @Override
        public int getCount()
        {
            return tabtitles.length;
        }

        @Override
        public Fragment getItem(int position)
        {

            switch (position)
            {
                case 0:
                    JsonFlightParser jsonFlightParser = new JsonFlightParser(getApplicationContext(),result);
                    ArrayList<FlightDetails> flightDetailsList = jsonFlightParser.convertOnwardFlightSearch();
                    return FlightOneWayFragment.newInstance(flightDetailsList);
                case 1:
                    jsonFlightParser = new JsonFlightParser(getApplicationContext(),result);
                    flightDetailsList = jsonFlightParser.convertReturnFlightSearch();
                    return FlightReturnFragment.newInstance(flightDetailsList);
            }

            return null;

        }

    }

    public void Book(View v)
    {

        int c1 = goibiboFlightDatabaseHelper.getFlightOnwardCount();
        int c2 = goibiboFlightDatabaseHelper.getFlightReturnCount();

        if(c1==0)
        {
            AlertBuilder alertBuilder = new AlertBuilder(GoibiboFlightSearchRoundTripActivity.this);
            alertBuilder.showAlert(getResources().getString(R.string.flight_onward));
        }
        else if(c2==0)
        {
            AlertBuilder alertBuilder = new AlertBuilder(GoibiboFlightSearchRoundTripActivity.this);
            alertBuilder.showAlert(getResources().getString(R.string.flight_return));
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(),GoibiboFlightBookReviewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }


}
