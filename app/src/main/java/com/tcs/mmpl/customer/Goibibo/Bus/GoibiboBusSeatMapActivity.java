package com.tcs.mmpl.customer.Goibibo.Bus;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tcs.mmpl.customer.Goibibo.Bus.Fragment.BusLowerSeatMapFragment;
import com.tcs.mmpl.customer.Goibibo.Bus.Fragment.BusSeatMapFragment;
import com.tcs.mmpl.customer.Goibibo.Bus.Fragment.BusUpperSeatMapFragment;
import com.tcs.mmpl.customer.Goibibo.Bus.Parser.JsonParser;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusDetails;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusSeatMapDetails;
import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.PagerSlidingTabStrip;

import java.util.ArrayList;

public class GoibiboBusSeatMapActivity extends FragmentActivity {


    private BusDetails busDetails;
    public Button btnTotalPrice;
    private GoibiboBusDatabaseHelper goibiboBusDatabaseHelper;
    private String skey;
    private LinearLayout linFragment;

    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_bus_seat_map);


        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);


        goibiboBusDatabaseHelper = new GoibiboBusDatabaseHelper(getApplicationContext());

        linFragment = (LinearLayout) findViewById(R.id.linFragment);
        btnTotalPrice = (Button) findViewById(R.id.btnTotalPrice);
        busDetails = (BusDetails) getIntent().getSerializableExtra("BusDetails");
        skey = busDetails.getSkey();


        BusSeatMap busSeatMap = new BusSeatMap(getApplicationContext());
        busSeatMap.execute(getResources().getString(R.string.bus_seat_map_url) + "?skey=" + skey);
    }

    private class BusSeatMap extends AsyncTask<String, Void, String> {
        private Context context;
        private ProgressDialog pDialog;

        public BusSeatMap(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboBusSeatMapActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboBusSeatMapActivity.this);
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

            JsonParser jsonParser = new JsonParser(GoibiboBusSeatMapActivity.this, result);
            ArrayList<BusSeatMapDetails> busSeatMapDetailsArrayList = jsonParser.convertSeatMap();

            if (busSeatMapDetailsArrayList.isEmpty()) {

            } else {

                boolean deck2Flag = false;
                for (BusSeatMapDetails b : busSeatMapDetailsArrayList) {
                    if (b.getDeck().trim().equalsIgnoreCase("2")) {
                        deck2Flag = true;
                        break;
                    }
                }
                if (deck2Flag) {
                    //// System.out.println("Coming if part Seat map");
                    ((LinearLayout) findViewById(R.id.linLowerUpper)).setVisibility(View.VISIBLE);
                    PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
                    tabs.setTextColorResource(R.color.black);
                    ViewPager pager = (ViewPager) findViewById(R.id.pager);
                    PagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), busSeatMapDetailsArrayList);
                    pager.setAdapter(adapter);
                    final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                            getResources().getDisplayMetrics());
                    pager.setPageMargin(pageMargin);
                    tabs.setViewPager(pager);

                } else {

                    linFragment.setVisibility(View.VISIBLE);
                    BusSeatMapFragment busSeatMapFragment = (BusSeatMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_bus_seat_map);
                    busSeatMapFragment.showSeat(busSeatMapDetailsArrayList);
                }

            }


        }

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public String tabtitles[];
        private ArrayList<BusSeatMapDetails> result;

        public MyPagerAdapter(FragmentManager fm, ArrayList<BusSeatMapDetails> result) {
            super(fm);
            this.result = result;
            tabtitles = getResources().getStringArray(
                    R.array.Decks);
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
                    return BusLowerSeatMapFragment.newInstance(result);
                case 1:
                    return BusUpperSeatMapFragment.newInstance(result);


            }

            return null;

        }

    }


}
