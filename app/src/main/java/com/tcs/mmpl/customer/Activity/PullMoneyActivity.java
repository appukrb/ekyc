package com.tcs.mmpl.customer.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.Profile;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class PullMoneyActivity extends AppCompatActivity {
    ConnectionDetector connectionDetector;
    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    EditText edtPullMoneyAmount,edtPullMoneyMobileNumber,edtPullMoneyotp,edtPullMoneyRemarks;
    String  pullNumber,Amount,PullOTP,sucessResp,sucessotp,Remarks="";
    ProgressDialog pDialog;
    Typeface typeface;
    FontClass fontclass=new FontClass();
    LinearLayout linParent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_money);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
         linParent =(LinearLayout)findViewById(R.id.linParent) ;
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);
//        TextView btnRequestotp1 =(TextView)findViewById(R.id.btnRequestotp1);
//        Button btnpullmoney =(Button)findViewById(R.id.btnpullmoney);
        Button btnRequestotp =(Button)findViewById(R.id.btnRequestotp);

        edtPullMoneyAmount=(EditText)findViewById(R.id.edtPullMoneyAmount);
        edtPullMoneyMobileNumber =(EditText)findViewById(R.id.edtPullMoneyMobileNumber);
        edtPullMoneyRemarks =(EditText)findViewById(R.id.edtPullMoneyRemarks);
        edtPullMoneyotp =(EditText)findViewById(R.id.edtPullMoneyotp);


        btnRequestotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertBuilder alert = new AlertBuilder(PullMoneyActivity.this);

                pullNumber = "91"+edtPullMoneyMobileNumber.getText().toString().trim();
                Amount = edtPullMoneyAmount.getText().toString().trim();
//                Remarks=edtPullMoneyRemarks.getText().toString().trim();
                Remarks= Uri.encode(edtPullMoneyRemarks.getText().toString().trim(), "utf-8");
                int lenth=Remarks.length();


                if(pullNumber.trim().equalsIgnoreCase("") || pullNumber.trim().length()<12)
                {
                    alert.showAlert(getResources().getString(R.string.invalid_mobile));
                }
                else  if(Amount.trim().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_amount));
                }
                else if(lenth > 50){
                    alert.showAlert(getResources().getString(R.string.less50));
                }
                else if (pullNumber.trim().equalsIgnoreCase("") || Amount.trim().equalsIgnoreCase("")) {

                    alert.showAlert(getResources().getString(R.string.validation));


                } else {

                    if(pref.getString("mobile_number","").equalsIgnoreCase(pullNumber))
                    {

                        alert.showAlert(getResources().getString(R.string.invalid_request_number));
                    }
                    else {
                        if(connectionDetector.isConnectingToInternet())
                        {
                            String pullmoney_otp_url=getResources().getString(R.string.pullmoney_otp_url)+ "?MDN=" +
                                    pullNumber+"&PrimaryMDN="+pref.getString("mobile_number", "");

                            // System.out.println("pullmoney_otp_url"+pullmoney_otp_url);
                            GetUserOtp getUserOtp = new GetUserOtp(getApplicationContext());
                            getUserOtp.execute(pullmoney_otp_url);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();
                        }

                    }
                }

            }
        });

//        btnpullmoney.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (connectionDetector.isConnectingToInternet()) {
//                    AlertBuilder alert = new AlertBuilder(PullMoneyActivity.this);
//
//                    PullOTP = edtPullMoneyotp.getText().toString().trim();
//                    pullNumber = "91"+edtPullMoneyMobileNumber.getText().toString().trim();
//                    Amount = edtPullMoneyAmount.getText().toString().trim();
//
//                    if(pullNumber.equalsIgnoreCase("") || pullNumber.length()<12)
//                    {
//                        alert.showAlert(getResources().getString(R.string.invalid_mobile));
//                    }
//                    else  if(Amount.equalsIgnoreCase(""))
//                    {
//                        alert.showAlert(getResources().getString(R.string.invalid_amount));
//                    }
//                    else if (PullOTP.equalsIgnoreCase("") || PullOTP.length() < 4) {
//                        alert.showAlert(getResources().getString(R.string.invalid_otp));
//                    }
//                    else if (pullNumber.equalsIgnoreCase("") || Amount.equalsIgnoreCase("") || PullOTP.equalsIgnoreCase("")) {
//
//                        alert.showAlert(getResources().getString(R.string.validation));
//
//
//                    } else {
//
//                        String pullmoney_url=getResources().getString(R.string.pullmoney_url)+
//                                "?MDN=" + pref.getString("mobile_number", "") +
//                                "&custMDN=" +pullNumber+
//                                "&Otp=" +PullOTP+
//                                "&Amount=" + Amount;
//                        Pullmoney pullmoney = new Pullmoney(getApplicationContext());
//                        pullmoney.execute(pullmoney_url);
//
//                        // System.out.println("pullmoney_url"+pullmoney_url);
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

    private class GetUserOtp extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private Profile profile;

        public GetUserOtp(Context context) {
            this.context = context;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PullMoneyActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(PullMoneyActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {


                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success")) {
                            sucessotp=jsonMainObj.getString("responseMessage");
                            return "Success";
                        } else if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {
                            return jsonMainObj.getString("responseMessage");
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

            return "Failure";

        }

        @Override
        protected void onPostExecute(String result) {

            if(pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {

                loadingPopup();
//                Toast.makeText(getApplicationContext(), sucessotp, Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("Failure")) {

                AlertBuilder alertBuilder = new AlertBuilder(PullMoneyActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));

            } else {
                AlertBuilder alertBuilder = new AlertBuilder(PullMoneyActivity.this);
                alertBuilder.showAlert(result);
            }

        }

    }

    private void loadingPopup(){

        LayoutInflater layoutInflater = (LayoutInflater) PullMoneyActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_pullmoney_layout, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final EditText edtMpin = (EditText) popupView.findViewById(R.id.edittext_edit_popup);
        TextView txtpullnumber = (TextView) popupView.findViewById(R.id.txtpullnumber);
        TextView txtpullamount = (TextView) popupView.findViewById(R.id.txtpullamount);
        Button btnCancel = (Button) popupView.findViewById(R.id.button_pop_no);
        Button btnSubmit = (Button) popupView.findViewById(R.id.button_pop_yes);

        String sourceString = "<b>" + "Amount  :" + "</b> " + Amount;
        txtpullamount.setText(Html.fromHtml(sourceString));
//        txtpullamount.setText("Amount  :"+Amount );
        String sourceString1 = "<b>" + "Customer Number :" + "</b> " + pullNumber;
        txtpullnumber.setText(Html.fromHtml(sourceString1));
//        txtpullnumber.setText("Customer Number :"+pullNumber);


        edtMpin.setTypeface(typeface);
        btnCancel.setTypeface(typeface);
        btnSubmit.setTypeface(typeface);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connectionDetector.isConnectingToInternet())
                {
                    String pullmoney_otp_url=getResources().getString(R.string.pullmoney_otp_url)+ "?MDN=" + pullNumber+"&PrimaryMDN="+pref.getString("mobile_number", "");

                    // System.out.println("pullmoney_otp_url"+pullmoney_otp_url);
                    GetUserOtp getUserOtp = new GetUserOtp(getApplicationContext());
                    getUserOtp.execute(pullmoney_otp_url);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();
                }
                popupWindow.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMpin.getText().toString().trim().equalsIgnoreCase("")|| edtMpin.getText().toString().trim().length() < 4) {
                    Toast.makeText(PullMoneyActivity.this, getResources().getString(R.string.invalid_otp), Toast.LENGTH_LONG).show();
                } else {
                    popupWindow.dismiss();
                    PullOTP= edtMpin.getText().toString().trim();
                    if (connectionDetector.isConnectingToInternet()) {
                    AlertBuilder alert = new AlertBuilder(PullMoneyActivity.this);
//                    PullOTP = edtPullMoneyotp.getText().toString().trim();
//                    pullNumber = "91"+edtPullMoneyMobileNumber.getText().toString().trim();
//                    Amount = edtPullMoneyAmount.getText().toString().trim();
                    if(pullNumber.equalsIgnoreCase("") || pullNumber.length()<12)
                    {
                        alert.showAlert(getResources().getString(R.string.invalid_mobile));
                    }
                    else  if(Amount.equalsIgnoreCase(""))
                    {
                        alert.showAlert(getResources().getString(R.string.invalid_amount));
                    }
                    else if (PullOTP.equalsIgnoreCase("") || PullOTP.length() < 4) {
                        alert.showAlert(getResources().getString(R.string.invalid_otp));
                    }
                    else if (pullNumber.equalsIgnoreCase("") || Amount.equalsIgnoreCase("") || PullOTP.equalsIgnoreCase("")) {

                        alert.showAlert(getResources().getString(R.string.validation));

                    } else {

                        String pullmoney_url=getResources().getString(R.string.pullmoney_url)+
                                "?MDN=" + pref.getString("mobile_number", "") +
                                "&custMDN=" +pullNumber+
                                "&Otp=" +PullOTP+
                                "&Amount=" + Amount +
                                "&Remarks="+Remarks;
                        Pullmoney pullmoney = new Pullmoney(getApplicationContext());
                        pullmoney.execute(pullmoney_url);

                        // System.out.println("pullmoney_url"+pullmoney_url);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }

                }

            }
        });

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }

    private class Pullmoney extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private Profile profile;

        public Pullmoney(Context context) {
            this.context = context;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PullMoneyActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(PullMoneyActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {


                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success")) {
                            sucessResp=jsonMainObj.getString("responseMessage");

                            return "Success";
                        } else if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {
                            return jsonMainObj.getString("responseMessage");
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

            return "Failure";

        }

        @Override
        protected void onPostExecute(String result) {

            if(pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success"))
            {
                Toast.makeText(getApplicationContext(), sucessResp, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), PullMoneyActivity.class);
                startActivity(intent);
            } else if (result.equalsIgnoreCase("Failure")) {

                AlertBuilder alertBuilder = new AlertBuilder(PullMoneyActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));

            } else {
                AlertBuilder alertBuilder = new AlertBuilder(PullMoneyActivity.this);
                alertBuilder.showAlert(result);
            }

        }

    }

}
