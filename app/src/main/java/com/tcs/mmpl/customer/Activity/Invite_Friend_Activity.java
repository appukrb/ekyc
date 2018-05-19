package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.R;


public class Invite_Friend_Activity extends Activity {
    LinearLayout mainlinear;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    TextView txt_header_1,txt_header_2,txt_header_3,txt_header_4,txt_header_5;
    Button btn_load_money,btn_invite_friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite__friend_);
        mainlinear = (LinearLayout)findViewById(R.id.mainlinear);
        typeface=Typeface.createFromAsset(getApplicationContext().getAssets(),"helvetica.otf");
        fontclass.setFont(mainlinear,typeface );
       /* txt_header_1 = (TextView) findViewById(R.id.txt_header_1);
        txt_header_2 = (TextView) findViewById(R.id.txt_header_2);
        txt_header_3 = (TextView) findViewById(R.id.txt_header_3);

        txt_header_4 = (TextView) findViewById(R.id.txt_header_4);
        txt_header_5 = (TextView) findViewById(R.id.txt_header_5);

        btn_load_money = (Button) findViewById(R.id.btn_load_money);
        btn_invite_friend = (Button) findViewById(R.id.btn_invite_friend);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "helvetica.otf");

        txt_header_1.setTypeface(custom_font);
        txt_header_2.setTypeface(custom_font);
        txt_header_3.setTypeface(custom_font);
        txt_header_4.setTypeface(custom_font);
        txt_header_5.setTypeface(custom_font);
        btn_load_money.setTypeface(custom_font);
        btn_invite_friend.setTypeface(custom_font);*/
    }



}
