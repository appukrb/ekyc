package com.tcs.mmpl.customer.Goibibo.Bus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;

import org.json.JSONObject;

public class GoibiboBusCancelPolicyActivity extends Activity {


    private String skey,amount,date;

    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;

    private LinearLayout linMain;

    private TextView txtCancelPolicy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_bus_cancel_policy);


        txtCancelPolicy = (TextView)findViewById(R.id.txtCancelPolicy);

        skey = getIntent().getStringExtra("skey");
        amount = getIntent().getStringExtra("amount");
        date = getIntent().getStringExtra("journey_date");

        linMain = (LinearLayout)findViewById(R.id.linMain);


        GoibiboPref = getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

        String cancelPolicyUrl = getResources().getString(R.string.bus_cancel_policy_url)+"?skey="+skey+"&amount="+amount+"&journey_date="+date;

        //// System.out.println(cancelPolicyUrl);
        BusCancelPolicy busCancelPolicy = new BusCancelPolicy(getApplicationContext());
        busCancelPolicy.execute(cancelPolicyUrl);
    }


    private class BusCancelPolicy extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;

        public BusCancelPolicy(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboBusCancelPolicyActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboBusCancelPolicyActivity.this);
                String jsonStr = goibiboServiceHandler.makeServiceCall(arg0[0].toString(), GoibiboServiceHandler.POST);
                return jsonStr;


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Plociy::"+result);

            try {

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("responseStatus").equalsIgnoreCase("success")) {
                    linMain.setVisibility(View.VISIBLE);


                    String html = "<html>"+jsonObject.getString("responseMessage")+"</html>";
                    WebView browser = (WebView) findViewById(R.id.webview);
                    browser.getSettings().setJavaScriptEnabled(true);
                    browser.loadData(html, "text/html", "UTF-8");

                    linMain.addView(browser);


                } else {

                    AlertBuilder alertBuilder = new AlertBuilder(GoibiboBusCancelPolicyActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
            catch (Exception e)
            {

            }


        }

    }


    public void closePolicy(View v)
    {
        finish();
    }

}
