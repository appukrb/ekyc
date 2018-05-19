package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoadSuccessActivity extends Activity {

    private TextView txtStatus, txtNumber, txtTransactionID, txtTransactionDate;
    private ImageView imgSmiley;
    Button btnProceed, btnLoad;
    String msg;

    FontClass fontclass = new FontClass();
    Typeface typeface;
    private ConnectionDetector connectionDetector;

    private LinearLayout linDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load_success);
        connectionDetector = new ConnectionDetector(getApplicationContext());

        LinearLayout mainlinear = (LinearLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtNumber = (TextView) findViewById(R.id.txtNumber);
        txtTransactionID = (TextView) findViewById(R.id.txtTransactionID);
        txtTransactionDate = (TextView) findViewById(R.id.txtTransactionDate);
        imgSmiley = (ImageView) findViewById(R.id.imgSmiley);
        linDesc = (LinearLayout)findViewById(R.id.linDesc);
        msg = getIntent().getStringExtra("status");

        if (connectionDetector.isConnectingToInternet()) {
            String url = getResources().getString(R.string.loadWalletConfirm);
            LoadWalletConfirm loadWalletConfirm = new LoadWalletConfirm(getApplicationContext());
            loadWalletConfirm.execute(url);
        }


//        try {
//            String data[] = msg.split("\\|");
//            //if (checkSum.equalsIgnoreCase(data[data.length - 1])) {
//            if (data[14].equalsIgnoreCase("0300")) {
//                //// System.out.println("Inside Success");
//
//                txtStatus.setText("Amount successfully credited to the wallet");
//
//
//            } else if (data[14].equalsIgnoreCase("0399")) {
//                //// System.out.println("Inside Failure");
//
//
//                txtStatus.setText("Invalid Authentication at Bank");
//
//
//            } else if (data[14].equalsIgnoreCase("NA")) {
//                //// System.out.println("Inside Failure");
//
//
//                txtStatus.setText("Invalid Input in the Request Message");
//
//            } else if (data[14].equalsIgnoreCase("0002")) {
//                //// System.out.println("Inside Failure");
//
//
//                txtStatus.setText("BillDesk is waiting for Response from Bank");
//
//            } else if (data[14].equalsIgnoreCase("0001")) {
//                //// System.out.println("Inside Failure");
//
//
//                txtStatus.setText("Error at BillDesk");
//
//            }
//            //}
//            else {
//
//                txtStatus.setText("BillDesk Failed");
//
//            }
//        } catch (Exception e) {
//
//        }

        btnProceed = (Button) findViewById(R.id.btnProceed);
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Intent i = new Intent(getApplicationContext(), LoadMoneyActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_success, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class LoadWalletConfirm extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;

        public LoadWalletConfirm(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

            pDialog = new ProgressDialog(LoadSuccessActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("msg", msg));

                WebServiceHandler serviceHandler = new WebServiceHandler(LoadSuccessActivity.this, nameValuePairs);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST1);

                return jsonStr;
            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            if(pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Failure")) {
                linDesc.setVisibility(View.GONE);
                imgSmiley.setBackgroundResource(R.drawable.failure);
                txtStatus.setText(getResources().getString(R.string.apidown));

            } else {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("responseStatus").equalsIgnoreCase("Success")) {
                        try {

                            if(jsonObject.getString(getApplicationContext().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                            {
                                new LocalNotification(LoadSuccessActivity.this,jsonObject.getString(getApplicationContext().getResources().getString(R.string.notificationTitle)),jsonObject.getString(getApplicationContext().getResources().getString(R.string.notificationMessage))).sendNotification();
                            }

                            linDesc.setVisibility(View.VISIBLE);
                            imgSmiley.setBackgroundResource(R.drawable.success);
                            txtStatus.setText(jsonObject.getString("responseMessage"));
                            txtNumber.setText(jsonObject.getString("mobileNo"));
                            txtTransactionID.setText(jsonObject.getString("txnId"));
                            txtTransactionDate.setText(jsonObject.getString("txnDate"));
                        }
                        catch (Exception e)
                        {

                        }

                    } else {
                        try {
                            linDesc.setVisibility(View.VISIBLE);
                            imgSmiley.setBackgroundResource(R.drawable.failure);
                            txtStatus.setText(jsonObject.getString("responseMessage"));
                            txtNumber.setText(jsonObject.getString("mobileNo"));
                            txtTransactionID.setText(jsonObject.getString("txnId"));
                            txtTransactionDate.setText(jsonObject.getString("txnDate"));
                        }
                        catch (Exception e)
                        {

                        }

                    }

                } catch (Exception e) {

                }
            }

        }

    }

}
