package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.FontClass;


public class ViewFavoriteActivity extends Activity {

    Typeface custom_font;
    LinearLayout linParent;

    Button btnFavConfirm;
    FontClass fontclass = new FontClass();
    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_favorite);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        linParent = (LinearLayout)findViewById(R.id.linParent);
        custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.ttf");
        fontclass.setFont(linParent, typeface);
        btnFavConfirm = (Button)findViewById(R.id.btnFavConfirm);
        btnFavConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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

}
