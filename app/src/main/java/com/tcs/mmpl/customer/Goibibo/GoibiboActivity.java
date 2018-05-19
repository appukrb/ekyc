package com.tcs.mmpl.customer.Goibibo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Activity.AlertActivity;
import com.tcs.mmpl.customer.Activity.FavoriteActivity;
import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.Goibibo.Bus.GoibiboBusActivity;
import com.tcs.mmpl.customer.Goibibo.Flight.GoibiboFlightActivity;
import com.tcs.mmpl.customer.Goibibo.Hotel.GoibiboHotelActivity;
import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

public class GoibiboActivity extends FragmentActivity {

    private SharedPreferences GoibiboPref,pref;
    private SharedPreferences.Editor GoibiboEditor,editor;
    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo);

        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        GoibiboPref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();


        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColorResource(R.color.black);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter1 = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter1);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);



        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(GoibiboActivity.this, FavoriteActivity.class);
                                            startActivity(i);
                                        }
                                    }
        );
//        ImageView home = (ImageView) findViewById(R.id.home);
//        home.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent i = new Intent(Electricity_Payment.this, HomeScreenActivity.class);
//                                        startActivity(i);
//                                    }
//                                }
//        );

        //call HamburgerMenu
        new HamburgerMenu(this,(DrawerLayout) findViewById(R.id.drawer_layout),(ExpandableListView) findViewById(R.id.left_drawer),(ImageView) findViewById(R.id.idmenuimg)).setHamburger();


    }
    public void generateOTP(View v)
    {
        GenerateOTPAlert generateOTPAlert = new GenerateOTPAlert(this);
        generateOTPAlert.openAlert();
    }


    public void openinbox(View v)
    {
        Intent s5 =new Intent(getApplicationContext(),AlertActivity.class);
        startActivity(s5);

    }
    public void openhome(View v)
    {
        Intent i = new Intent(GoibiboActivity.this, HomeScreenActivity.class);
        startActivity(i);
    }


//    public void openGoibiboHotel(View v)
//    {
////        Intent i = new Intent(getApplicationContext(),GoibiboBusActivity.class);
////        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        startActivity(i);
//    }
//    public void openGoibiboFlight(View v)
//    {
//        Intent i = new Intent(getApplicationContext(),GoibiboFlightActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);
//    }
//    public void openGoibiboBus(View v)
//    {
//        Intent i = new Intent(getApplicationContext(),GoibiboBusActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);
//    }
//
//    public void openMyBookings(View v)
//    {
//        Intent intent = new Intent(getApplicationContext(),GoibiboMyBookingsActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public String tabtitles[];


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabtitles = getResources().getStringArray(
                    R.array.GoibiboCategory);
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
                    return new GoibiboBusActivity();
                case 1:
                    return new GoibiboFlightActivity();
                case 2:
                    return new GoibiboHotelActivity();
                case 3:
                    return new GoibiboMyBookingsActivity();



            }

            return null;

        }

    }

    class DrawerItemCustomAdapter extends BaseAdapter {
        private Context context;
        private List<ObjectDrawerItem> list;

        public DrawerItemCustomAdapter(Context context, List<ObjectDrawerItem> list) {
            this.context = context;
            this.list = list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {

            return list.get(i);
        }

        @Override
        public long getItemId(int i) {

            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.listview_item_row, parent, false);
            TextView textViewName = (TextView) row.findViewById(R.id.textViewName);
            Typeface custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
            textViewName.setTypeface(custom_font);
            final ObjectDrawerItem objectDrawerItem = list.get(position);
            textViewName.setText(objectDrawerItem.getName());


            return (row);
        }
    }


}
