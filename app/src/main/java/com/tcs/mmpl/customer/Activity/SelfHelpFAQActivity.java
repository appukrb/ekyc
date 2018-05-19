package com.tcs.mmpl.customer.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.fragments.AccountFragment;
import com.tcs.mmpl.customer.fragments.AddMoneyFragment;
import com.tcs.mmpl.customer.fragments.MPINOTPFragment;
import com.tcs.mmpl.customer.fragments.MoneyTransferFragment;
import com.tcs.mmpl.customer.fragments.RechargeBillPayFragment;
import com.tcs.mmpl.customer.utility.PagerSlidingTabStrip;

public class SelfHelpFAQActivity extends FragmentActivity {


    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String mode="";
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_self_help_faq);


        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();


        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColorResource(R.color.black);
        Typeface custom_font = Typeface.createFromAsset(getApplication().getAssets(), "helvetica-bold.ttf");

        tabs.setTypeface(custom_font, Typeface.NORMAL);
        pager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter adapter1 = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter1);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);


        tabs.setViewPager(pager);
        pager.setCurrentItem(getIntent().getIntExtra("index", 0), true);

    }



    public void closeFeedback(View v)
    {
         finish();
    }

    public void rateUs(View v)
    {

        int index = pager.getCurrentItem();
        switch (index)
        {
            case 0 : mode = getResources().getString(R.string.recharge_bill_payment_help);
                break;

            case 1: mode = getResources().getString(R.string.add_money_help);
                break;

            case 2 : mode = getResources().getString(R.string.mpin_otp_help);
                break;

            case 3 : mode = getResources().getString(R.string.money_transfer_help);
                break;

            case 4 : mode = getResources().getString(R.string.account_help);
                break;


        }

        Intent intent = new Intent(getApplicationContext(),SelfHelpFeedback.class);
        intent.putExtra("mode",mode);
        intent.putExtra("desc", "NA");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);



    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public String tabtitles[];


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabtitles = getResources().getStringArray(
                    R.array.SHCategory);
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
                    return new RechargeBillPayFragment();
                case 1:
                    return new AddMoneyFragment();
                case 2:
                    return new MPINOTPFragment();
                case 3:
                    return new MoneyTransferFragment();
                case 4:
                    return new AccountFragment();

            }

            return null;

        }

    }

}
