package com.tcs.mmpl.customer.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tcs.mmpl.customer.fragments.DTH;
import com.tcs.mmpl.customer.fragments.Datacard;
import com.tcs.mmpl.customer.fragments.ElectriCity;
import com.tcs.mmpl.customer.fragments.Gas;
import com.tcs.mmpl.customer.fragments.Landline;
import com.tcs.mmpl.customer.fragments.Mobile;
import com.tcs.mmpl.customer.R;


public class CustomViewPagerAdapter extends FragmentPagerAdapter {

    Context context;
    public String tabtitles[];
    private int tabxml[] = { R.layout.fragment_tab_mobile, R.layout.fragment_tab_dth ,R.layout.fragment_tab_landline,
            R.layout.fragment_tab_datacard, R.layout.fragment_tab,
            R.layout.fragment_tab_gas};

    public CustomViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        tabtitles = context.getResources().getStringArray(
                R.array.operationTypeList);
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
                // Top Rated fragment activity
                return new Mobile();
            case 1:
                // Games fragment activity
                return new DTH();

            case 2:
                // Top Rated fragment activity
                return new Landline();
            case 3:
                // Games fragment activity
                return new Datacard();
            case 4:
                // Movies fragment activity
                return new ElectriCity();

            case 5:
                // Movies fragment activity
                return new Gas();
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabtitles[position];
    }
}
