package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.tcs.mmpl.customer.R;


public class Coupon extends Activity {

    private WebView wb;
    private LinearLayout linWebVIew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_coupon);

        linWebVIew = (LinearLayout)findViewById(R.id.linWebVIew);

        wb = new WebView(getApplicationContext());
        wb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        wb.clearCache(true);
        wb.loadUrl(getResources().getString(R.string.deals));
        linWebVIew.addView(wb);
    }


}
