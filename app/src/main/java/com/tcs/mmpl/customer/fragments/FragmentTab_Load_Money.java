package com.tcs.mmpl.customer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Admin on 9/9/2015.
 */
public class FragmentTab_Load_Money extends Fragment {
    int positonTab = 0;
    int tabxml_loadMoney[];
    Context context;

    public FragmentTab_Load_Money(Context context, int position, int[] tabxml_loadMoney)
    {
        // TODO Auto-generated constructor stub
        this.context = context;
        positonTab=position;
        this.tabxml_loadMoney=tabxml_loadMoney;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub

        View view = inflater.inflate(tabxml_loadMoney[positonTab], container, false);

        return view;




    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);


    }
}
