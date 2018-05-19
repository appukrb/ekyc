package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.CallJavascript;

public class MobileConnectActivity extends Activity {

    private static int PAGE_LOAD_PROGRESS = 0;
    private WebView linMobConnectView;
    private String client_id ;
    private String client_secret;
    private String authorization_url="";
    private String token_url="";
    private SharedPreferences mobileConnectPref;
    private SharedPreferences.Editor mobileConnectEditor;
    public static Activity mobile_connect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mobile_connect);


        mobile_connect = this;

        mobileConnectPref = getApplicationContext().getSharedPreferences("mobile_connect", MODE_PRIVATE);
        mobileConnectEditor = mobileConnectPref.edit();

        //// System.out.println("1:::::"+mobileConnectPref.getString("authorization_url",""));
        //// System.out.println("2::::::::"+mobileConnectPref.getString("client_secret", ""));
        //// System.out.println(mobileConnectPref.getString("client_id",""));
        //// System.out.println(mobileConnectPref.getString("token_url",""));

        String mobile_connect_gateway = mobileConnectPref.getString("authorization_url","");

        //// System.out.println("Auth ::::"+mobile_connect_gateway);
        linMobConnectView = (WebView)findViewById(R.id.linMobConnectView);
        linMobConnectView.setWebViewClient(new WebViewClient());
        linMobConnectView.addJavascriptInterface(new CallJavascript(this), "Android");
        linMobConnectView.getSettings().setLoadsImagesAutomatically(true);
        linMobConnectView.getSettings().setJavaScriptEnabled(true);
        linMobConnectView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        linMobConnectView.setBackgroundColor(0);
        linMobConnectView.loadUrl(mobile_connect_gateway);

        Intent intent = getIntent();
        client_id = intent.getStringExtra("client_id");
        client_secret = intent.getStringExtra("client_secret");
        authorization_url = intent.getStringExtra("authorization_url");
        token_url = intent.getStringExtra("token_url");
    }


//    //Custom web chrome client
//    public class MyWebChromeClient extends WebChromeClient {
//
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//            PAGE_LOAD_PROGRESS = newProgress;
//            //Log.i(TAG, "Page progress [" + PAGE_LOAD_PROGRESS + "%]");
//            super.onProgressChanged(view, newProgress);
//        }
//    }

    public class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {

            // TODO Auto-generated method stub

            super.onPageFinished(view, url);

        }

    }

}
