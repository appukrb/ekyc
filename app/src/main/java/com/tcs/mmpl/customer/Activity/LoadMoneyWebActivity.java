package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.tcs.mmpl.customer.R;


public class LoadMoneyWebActivity extends Activity {

    LinearLayout linWebVIew;
    WebView wb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load_money_web);

        linWebVIew = (LinearLayout)findViewById(R.id.linWebVIew);

        wb = new WebView(getApplicationContext());
        wb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        wb.loadUrl(getIntent().getStringExtra("url"));

        linWebVIew.addView(wb);
    }


}
