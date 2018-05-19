package com.tcs.mmpl.customer.Goibibo.Bus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.tcs.mmpl.customer.Goibibo.Bus.Fragment.BusOneWayFragment;
import com.tcs.mmpl.customer.Goibibo.Bus.Fragment.BusReturnFragment;
import com.tcs.mmpl.customer.Goibibo.Bus.Parser.JsonParser;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusDetails;
import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.PagerSlidingTabStrip;

import java.util.ArrayList;

public class GoibiboBusSearchRoundTripActivity extends FragmentActivity {

    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;

    private String busCondition="NA",seat="NA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_bus_search_round_trip);

        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        //// System.out.println("Bus Round Bus::::::::::::::");

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
            pDialog = new ProgressDialog(GoibiboBusSearchRoundTripActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboBusSearchRoundTripActivity.this);
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

            JsonParser jsonParser = new JsonParser(GoibiboBusSearchRoundTripActivity.this, result);
            ArrayList<BusDetails> busDetailsOnwardList = jsonParser.convertOnwardBusSearch(seat,busCondition);

            jsonParser = new JsonParser(GoibiboBusSearchRoundTripActivity.this, result);
            ArrayList<BusDetails> busDetailsReturnList = jsonParser.convertReturnBusSearch(seat,busCondition);

            if(busDetailsOnwardList.isEmpty() || busDetailsReturnList.isEmpty())
            {
                AlertBuilder alertBuilder = new AlertBuilder(GoibiboBusSearchRoundTripActivity.this);
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
            else {

                ((LinearLayout) findViewById(R.id.linRoundTrip)).setVisibility(View.VISIBLE);
                PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
                tabs.setTextColorResource(R.color.black);
                ViewPager pager = (ViewPager) findViewById(R.id.pager);
                PagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), busDetailsOnwardList, busDetailsReturnList);
                pager.setAdapter(adapter);
                final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                        getResources().getDisplayMetrics());
                pager.setPageMargin(pageMargin);
                tabs.setViewPager(pager);
            }

        }

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public String tabtitles[];

        private int tabxml[] = {R.layout.fragment_bus_one_way, R.layout.fragment_return};

        private String result;
        ArrayList<BusDetails> busDetailsOnwardList , busDetailsReturnList;

        public MyPagerAdapter(FragmentManager fm, ArrayList<BusDetails> busDetailsOnwardList , ArrayList<BusDetails> busDetailsReturnList) {
            super(fm);
            this.busDetailsOnwardList = busDetailsOnwardList;
            this.busDetailsReturnList = busDetailsReturnList;
            tabtitles = getResources().getStringArray(
                    R.array.GoibiboList);


        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabtitles[position];
        }

        @Override
        public int getCount() {
            return tabtitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:

                    return BusOneWayFragment.newInstance(busDetailsOnwardList);
                case 1:

                    return BusReturnFragment.newInstance(busDetailsReturnList);
            }

            return null;

        }

    }

}
