package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class SelfHelpFeedback extends Activity {

    private LinearLayout linCategory;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    ConnectionDetector connectionDetector;
    private EditText edtIssue;
    private String desc = "";
    private TextView txtCategory;
    private String subCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_self_help_feedback);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        linCategory = (LinearLayout)findViewById(R.id.linCategory);
        txtCategory = (TextView)findViewById(R.id.txtCategory);
        desc = Uri.encode(getIntent().getStringExtra("desc"), "utf-8");

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        edtIssue = (EditText) findViewById(R.id.edtIssue);

        if(getIntent().getStringExtra("mode").trim().equalsIgnoreCase(getResources().getString(R.string.recharge_bill_payment_help)))
        {
            linCategory.setVisibility(View.VISIBLE);
        }
        else
        {
            linCategory.setVisibility(View.GONE);
        }
    }

    public void openSubCategory(View v)
    {
        final CharSequence[] items = {
                "Mobile", "DTH", "Landline","Data Card","Electricity","Gas"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                txtCategory.setText(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void submitFeedback(View v) {
        //call api here
        if(userInfoPref.getBoolean("new_user_registered_newapp",false)) {

            if(linCategory.getVisibility() == View.VISIBLE)
            {
                if(txtCategory.getText().toString().trim().equalsIgnoreCase(""))
                {
                    AlertBuilder alertBuilder = new AlertBuilder(SelfHelpFeedback.this);
                    alertBuilder.showAlert("Please select the category");
                    return;
                }
                else
                {
                     subCategory = Uri.encode(txtCategory.getText().toString().trim() +"\n","utf-8");

                }
            }

            if (edtIssue.getText().toString().trim().equalsIgnoreCase("") || edtIssue.getText().toString().trim().length() < 10) {
                AlertBuilder alertBuilder = new AlertBuilder(SelfHelpFeedback.this);
                alertBuilder.showAlert("Please enter more details");
            } else {

                String url = getResources().getString(R.string.saveSHM_url) + "?MDN=" + pref.getString("mobile_number", "") + "&category=" + Uri.encode(getIntent().getStringExtra("mode")) + "&issue=" +Uri.encode(edtIssue.getText().toString().trim()) + "&Detail=" +  subCategory+desc;

//                Log.i("url", url);
                SaveSHM saveSHM = new SaveSHM(getApplicationContext());
                saveSHM.execute(url);
            }
        }
        else
        {
            AlertBuilder alertBuilder = new AlertBuilder(SelfHelpFeedback.this);
            alertBuilder.newUser();
        }
    }


    private class SaveSHM extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        ProgressDialog pDialog;


        public SaveSHM(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SelfHelpFeedback.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(SelfHelpFeedback.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);


                Log.i("Response: >>>>>>>>>>>", jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("SUCCESS")) {


                            return jsonMainObj.getString("responseMessage");

                        } else if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("FAILURE")) {
                            return jsonMainObj.getString("responseMessage");

                        } else {
                            return "Failure";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failure";
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failure";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {


            } else if (result.equalsIgnoreCase("Failure")) {

                try {
                    AlertBuilder alertBuilder = new AlertBuilder(SelfHelpFeedback.this);
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

                } catch (Exception e) {
                }

            } else {
                AlertBuilder alertBuilder = new AlertBuilder(SelfHelpFeedback.this);
                AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(result);
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

    }


}
