package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.CallJavascript;
import com.tcs.mmpl.customer.utility.WebServiceHandler;


public class WebActivity extends Activity {

    LinearLayout linWebVIew,linWriteToUs;
    WebView wb;
    private SharedPreferences userInfoPref;
    private SharedPreferences.Editor userInfoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web);

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        if(userInfoPref.getString("notify","false").trim().equalsIgnoreCase("true"))
        {
            String url = getResources().getString(R.string.saveNotification)+"?MSG="+userInfoPref.getString("msg","|");
            com.tcs.mmpl.customer.Adapter.SendNotficationClicked sendNotficationClicked = new com.tcs.mmpl.customer.Adapter.SendNotficationClicked(WebActivity.this, userInfoPref.getString("msg","|"));
            sendNotficationClicked.execute(url);
            userInfoEditor.putString("notify","false");
            userInfoEditor.commit();

        }

        linWebVIew = (LinearLayout)findViewById(R.id.linWebVIew);
        linWriteToUs = (LinearLayout)findViewById(R.id.linWriteToUs);

        wb = new WebView(getApplicationContext());
        wb.addJavascriptInterface(new CallJavascript(this), "Android");
        WebSettings webSettings = wb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        wb.clearCache(true);

        //// System.out.println(getIntent().getStringExtra("option"));

        if(getIntent().getStringExtra("option").toString().equalsIgnoreCase("LINK"))
        {
            String url=getIntent().getStringExtra("url").toString();
            wb.loadUrl(url);
            linWebVIew.addView(wb);
        }
        else if (getIntent().getStringExtra("option").toString().equalsIgnoreCase("TRAVEL")) {
            linWriteToUs.setVisibility(View.GONE);

            GetHTML getHTML = new GetHTML(getApplicationContext());
            getHTML.execute(getResources().getString(R.string.htmlPage) + "?Type=TRAVEL");


        }
        else if (getIntent().getStringExtra("option").toString().equalsIgnoreCase("FAQ")) {
            linWriteToUs.setVisibility(View.GONE);

            GetHTML getHTML = new GetHTML(getApplicationContext());
            getHTML.execute(getResources().getString(R.string.htmlPage) + "?Type=FAQ");


        } else if (getIntent().getStringExtra("option").toString().equalsIgnoreCase("PRIVACY")) {
            linWriteToUs.setVisibility(View.GONE);
            GetHTML getHTML = new GetHTML(getApplicationContext());
            getHTML.execute(getResources().getString(R.string.htmlPage)+"?Type=PRIVACY");

        }else if (getIntent().getStringExtra("option").toString().equalsIgnoreCase("ABOUT")) {
            linWriteToUs.setVisibility(View.GONE);
            GetHTML getHTML = new GetHTML(getApplicationContext());
            getHTML.execute(getResources().getString(R.string.htmlPage) + "?Type=ABOUT");

        }else if (getIntent().getStringExtra("option").toString().equalsIgnoreCase("CONTACT")) {
            linWriteToUs.setVisibility(View.VISIBLE);
            GetHTML getHTML = new GetHTML(getApplicationContext());
            getHTML.execute(getResources().getString(R.string.htmlPage) + "?Type=CONTACT");

        }else if (getIntent().getStringExtra("option").toString().equalsIgnoreCase("getMMID")) {
            String getMMID_url=getIntent().getStringExtra("getMMID").toString();
            wb.loadUrl(getMMID_url);
            linWebVIew.addView(wb);
        }
        else if (getIntent().getStringExtra("option").toString().equalsIgnoreCase("KYC")) {
            linWriteToUs.setVisibility(View.GONE);
            GetHTML getHTML = new GetHTML(getApplicationContext());
            getHTML.execute(getResources().getString(R.string.htmlPage) + "?Type=KYC");


        }
        else if (getIntent().getStringExtra("option").toString().equalsIgnoreCase("TNC")){
            linWriteToUs.setVisibility(View.GONE);
            GetHTML getHTML = new GetHTML(getApplicationContext());
            getHTML.execute(getResources().getString(R.string.htmlPage)+"?Type=TNC");

        }
        else if (getIntent().getStringExtra("option").toString().equalsIgnoreCase("purse")){
            linWriteToUs.setVisibility(View.GONE);
            GetHTML getHTML = new GetHTML(getApplicationContext());
            getHTML.execute(getResources().getString(R.string.htmlPage)+"?Type=PURSE");

        }
        else
        {
            linWriteToUs.setVisibility(View.GONE);
            GetHTML getHTML = new GetHTML(getApplicationContext());
            getHTML.execute(getResources().getString(R.string.htmlPage)+"?Type="+getIntent().getStringExtra("option"));

        }
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

    public void writeToUs(View v)
    {

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"care@mrupee.in"});
        email.putExtra(Intent.EXTRA_SUBJECT, "");
        email.putExtra(Intent.EXTRA_TEXT, "");

//need this to prompts email client only
        email.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Sorry! No application found to send the email",Toast.LENGTH_LONG).show();
        }
    }


    private class GetHTML extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

//        String firstName, lastName, walletBalance;
        ProgressDialog pDialog;

        public GetHTML(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(WebActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            String jsonStr = "";

            try {

                 WebServiceHandler serviceHandler = new WebServiceHandler(getApplication());
                 jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println(jsonStr);

                if(jsonStr.trim().equalsIgnoreCase(""))
                    return "Failure";
                else
                    return jsonStr;
            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure1";
            }



        }

        @Override
        protected void onPostExecute(String result) {

            if(pDialog.isShowing())
                pDialog.dismiss();
            try {
                if(!result.equalsIgnoreCase("Failure")) {

                    wb.loadUrl(result);
                    linWebVIew.removeAllViews();
                    linWebVIew.addView(wb);
                }
            }
            catch (Exception e)
            {

            }
        }

    }


}
