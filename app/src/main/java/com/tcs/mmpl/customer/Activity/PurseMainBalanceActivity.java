package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.Purse;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PurseMainBalanceActivity extends Activity {


    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;
    private ConnectionDetector connectionDetector;
    private TextView txtWalletBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_purse_main_balance);

        txtWalletBalance = (TextView) findViewById(R.id.txtWalletBalance);
        connectionDetector = new ConnectionDetector(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        txtWalletBalance.setText(Html.fromHtml("<u><b>Wallet Balance - Rs " + userInfoPref.getString("walletbalance", "0") + "</b></u>"));

    }

    public void openSubPurse(View v) {
        if (connectionDetector.isConnectingToInternet()) {
            GetPurse getPurse = new GetPurse(getApplicationContext());
            getPurse.execute(getResources().getString(R.string.purse_url) + "?MDN=" + pref.getString("mobile_number", ""));
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
        }
    }

    private class GetPurse extends AsyncTask<String, Void, String> {

        Context context;

        ProgressDialog pDialog;
        private String responseMessage = "";
        ArrayList<Purse> purseArrayList ;


        public GetPurse(Context context) {
            this.context = context;

            purseArrayList = new ArrayList<Purse>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(PurseMainBalanceActivity.this);
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

                try {
                    if (jsonStr.trim().equalsIgnoreCase(""))
                        return "Failed";
                    else {
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        if (jsonObject.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            responseMessage = jsonObject.getString("responseMessage");

                            JSONArray jsonArray = jsonObject.getJSONArray("purse");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                Purse purse = new Purse();
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                purse.setPurseImage(jsonObject1.getString("purseImage"));
                                purse.setPurseName(jsonObject1.getString("purseName"));
                                purse.setPurseBalance(jsonObject1.getString("purseBalance"));
                                purse.setPurseTextColor(jsonObject1.getString("purseTextColor"));
                                purse.setPurseBackgroundColor(jsonObject1.getString("purseBackgroundColor"));

                                purseArrayList.add(purse);


                            }


                            return "Success";
                        } else {
                            responseMessage = jsonObject.getString("responseMessage");
                            return "Failure";
                        }
                    }
                } catch (JSONException ex) {
                    return "Failed";
                }
            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failed";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {
                Intent i = new Intent(getApplicationContext(), PurseSubBalanceActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("purseArrayList", purseArrayList);
                startActivity(i);
            } else if (result.equalsIgnoreCase("Failure")) {

                AlertBuilder alertBuilder = new AlertBuilder(PurseMainBalanceActivity.this);
                alertBuilder.showAlert(responseMessage);
            } else {
                AlertBuilder alertBuilder = new AlertBuilder(PurseMainBalanceActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));
            }

        }

    }


}
