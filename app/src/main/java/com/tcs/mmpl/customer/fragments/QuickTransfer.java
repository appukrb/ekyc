package com.tcs.mmpl.customer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.R;

/**
 * Created by Admin on 9/15/2015.
 */
public class  QuickTransfer extends Fragment
{
    private Button button;
    private ViewFlipper viewFlipper_quickTransfer;

    private Button btn_confirm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_quick_transfer,
                container, false);

        init(rootView);

        // onClick();


       /* rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK ))
                {
                    if(viewFlipper_recharge.getDisplayedChild() == 2)
                    {
                        viewFlipper_recharge.setDisplayedChild(1);
                    }
                    else if(viewFlipper_recharge.getDisplayedChild() == 1)
                    {
                        viewFlipper_recharge.setDisplayedChild(0);
                    }
                    else
                    {
                        getActivity().finish();
                    }

                    return true;
                }
                return false;
            }
        } );*/

        return rootView;
    }

    /* private void onClick()
     {
         // TODO Auto-generated method stub

         button.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View arg0) {
                 // TODO Auto-generated method stub

                 viewFlipper_recharge.setDisplayedChild(1);



             }
         });

         btn_confirm.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View arg0) {
                 // TODO Auto-generated method stub

                 viewFlipper_landline.setDisplayedChild(2);



             }
         });

     }
 */
    private void init(View rootView) {
        // TODO Auto-generated method stub

        //button = (Button) rootView.findViewById(R.id.payment_landline);
        viewFlipper_quickTransfer = (ViewFlipper) rootView.findViewById(R.id.viewflipper_quick_Transfer);

        //  btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm_landline);

    }
}
