package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.tcs.mmpl.customer.R;


public class Shopping extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_shopping);
    }


}
