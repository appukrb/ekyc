package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.R;


public class ConfirmMobileActivity extends Activity {
    TextView txt_header_1,txt_header_2,txt_header_3;
    LinearLayout mainlinear;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    Button btn_changemPIN;
    LinearLayout webview_linear;
    TextView txtRegisteredMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_confirm_mobile);
        mainlinear = (LinearLayout)findViewById(R.id.mainlinear);
        typeface=Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear,typeface );

        txtRegisteredMobile = (TextView)findViewById(R.id.txtRegisteredMobile);
//        webview_linear = (LinearLayout)findViewById(R.id.webview_linear);
//        webview_linear.setVisibility(View.GONE);

       /* txt_header_1 = (TextView) findViewById(R.id.txt_header_1);
        txt_header_2 = (TextView) findViewById(R.id.txt_header_2);
        txt_header_3 = (TextView) findViewById(R.id.txt_header_3);*/

        txtRegisteredMobile.setText("Mobile Number "+getIntent().getStringExtra("mobile_number")+" is successfully registered");
        btn_changemPIN=(Button) findViewById(R.id.btnChangeMPin);

      /*  Typeface custom_font = Typeface.createFromAsset(getAssets(), "helvetica.otf");

        txt_header_1.setTypeface(custom_font);
        txt_header_2.setTypeface(custom_font);
        txt_header_3.setTypeface(custom_font);
        btn_changemPIN.setTypeface(custom_font);*/

        btn_changemPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Intent i= new Intent(getApplicationContext(),ChangeMPINActivity.class);
                i.putExtra("status","1");
                startActivity(i);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog

    }

}
