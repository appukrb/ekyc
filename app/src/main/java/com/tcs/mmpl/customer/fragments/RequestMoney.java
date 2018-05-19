package com.tcs.mmpl.customer.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.FontClass;

/**
 * Created by Admin on 9/15/2015.
 */
public class RequestMoney extends Fragment
{
    FontClass fontclass=new FontClass();
    Typeface typeface;
    private Button button;
    private ViewFlipper viewFlipper_request_money;

    private Button btn_confirm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_request_money,
                container, false);
        typeface=Typeface.createFromAsset(getActivity().getAssets(),"helvetica.otf");
        init(rootView);


        return rootView;
    }


    private void init(View rootView) {
        // TODO Auto-generated method stub

        viewFlipper_request_money = (ViewFlipper) rootView.findViewById(R.id.viewflipper_requestMoney);
        fontclass.setFont(viewFlipper_request_money,typeface );

    }
}
