package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.SmsReciever;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;


public class Detact_Mobile_NumberActivity extends Activity {


    SharedPreferences pref, userInfoPref, mobileConnectPref;
    SharedPreferences.Editor editor, userInfoEditor, mobileConnectEditor;

    EditText edtMobileNumber;

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;
    LinearLayout linWelcome, linDetails;
    ImageView imgUser;
    TextView tv_resendOTP;
    EditText edt_10digit;

    LinearLayout mainlinear;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    TextView txtNotify;

    Dialog dialog;
    //    EditText dialog_txt;
    Button btn_submit;
    RequestOtp requestOtp;
    String otpst, responseMessage, mobile_no, sendOTPNew_url;
    static EditText dialog_txt;
    private static Context mContext;

    public static Activity detect_mobile;
    private CheckBox checkMobileTerms;
    private TextView txtTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detact__mobile__number);
        detect_mobile = this;
        mContext = this;
        mainlinear = (LinearLayout) findViewById(R.id.mainlinear);
        checkMobileTerms = (CheckBox)findViewById(R.id.checkMobileTerms);
        txtTerms = (TextView)findViewById(R.id.txtTerms);
        txtTerms.setText(Html.fromHtml("<u>"+getResources().getString(R.string.termsconditions)+"</u>"));
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        edtMobileNumber = (EditText) findViewById(R.id.edtMobileNumber);
        txtNotify = (TextView) findViewById(R.id.txtNotify);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        mobileConnectPref = getApplicationContext().getSharedPreferences("mobile_connect", MODE_PRIVATE);
        mobileConnectEditor = mobileConnectPref.edit();

        if (getIntent().getStringExtra("phone_number").trim().length() == 10) {
            txtNotify.setText("Please verify your mobile number");
            edtMobileNumber.setText(getIntent().getStringExtra("phone_number"));
        }

        btn_submit = (Button) findViewById(R.id.btn_submit);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMobileNumber.getText().toString().trim().equalsIgnoreCase("") || edtMobileNumber.getText().toString().trim().length() < 10) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.valid_mobile), Toast.LENGTH_LONG).show();
                } else {

                    if (connectionDetector.isConnectingToInternet()) {

                        GetUserStatus getUserStatus = new GetUserStatus(getApplicationContext());
                        getUserStatus.execute(getResources().getString(R.string.getUserStatus));

                    } else {

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                    }
                }
            }
        });
    }

    public void openMobileConnectTerms(View v)
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.mobile_connect_terms))));
    }

    public void callMobileConnect(View v) {
        if (edtMobileNumber.getText().toString().trim().equalsIgnoreCase("") || edtMobileNumber.getText().toString().trim().length() < 10)

        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.valid_mobile), Toast.LENGTH_LONG).show();
        }
        else if(!checkMobileTerms.isChecked())
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.terms_and_condition), Toast.LENGTH_LONG).show();
        }
        else {
            if (connectionDetector.isConnectingToInternet()) {
                GetDiscovery getDiscovery = new GetDiscovery(getApplicationContext());
                getDiscovery.execute(getResources().getString(R.string.discovery_url) + "?MDN=91" + edtMobileNumber.getText().toString().trim());

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }
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

    public String getOS() {
        String OS = "";
        try {
            OS = Build.VERSION.RELEASE;
        } catch (Exception e) {
            OS = "";
        }


        return "Android " + OS;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    public String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }


    public static void getSmsDetails(String SMSBody) {
        try {
            dialog_txt.setText(SMSBody);
            dialog_txt.setFocusable(false);

            disableBroadcastReceiver();
        } catch (Exception e) {

        }
    }

    public void enableBroadcastReceiver() {

        ComponentName receiver = new ComponentName(this, SmsReciever.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
//        Toast.makeText(this, "Enabled broadcast receiver", Toast.LENGTH_SHORT).show();
    }

    public static void disableBroadcastReceiver() {
        ComponentName receiver = new ComponentName(mContext, SmsReciever.class);
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
//        Toast.makeText(mContext, "Disabled broadcast receiver", Toast.LENGTH_SHORT).show();
    }

    public String getIMEI(Context context) {
        String IMEI = "";

        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            IMEI = mTelephonyManager.getDeviceId();
        } catch (Exception e) {
            IMEI = "";
        }

        return IMEI;
    }

    private class GetUserStatus extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        public GetUserStatus(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Detact_Mobile_NumberActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

//                //// System.out.println("MDN: >>>>>>>>>>>" + "91"+edtMobileNumber.getText().toString());
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("MDN", "91" + edtMobileNumber.getText().toString());
                jsonObject.accumulate("IMEI", getIMEI(getApplicationContext()));
                jsonObject.accumulate("Model", getDeviceName());
                jsonObject.accumulate("OS", getOS());
                jsonObject.accumulate("Network", getNetworkClass(getApplicationContext()));
//                jsonObject.accumulate("IMEI", "123456");


                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(Detact_Mobile_NumberActivity.this, se);
//                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());

                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                // System.out.println("GetUserStatus: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            firstName = jsonMainObj.getString("firstName");
                            lastName = jsonMainObj.getString("lastName");
                            walletBalance = jsonMainObj.getString("walletBalance");

                            userInfoEditor.putString("firstname", firstName);
                            userInfoEditor.putString("lastname", lastName);
                            userInfoEditor.putString("walletbalance", walletBalance);
                            userInfoEditor.putString("usertype", jsonMainObj.getString("userType"));
                            userInfoEditor.putString("emailId", jsonMainObj.getString("emailId"));
                            userInfoEditor.putString("account", jsonMainObj.getString("account"));
                            userInfoEditor.putString("kycstatus", jsonMainObj.getString("kycstatus"));
                            userInfoEditor.putString("billdeskFlag", jsonMainObj.getString("billdeskFlag"));
                            userInfoEditor.putString("userID", jsonMainObj.getString("userID"));
                            userInfoEditor.commit();

                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            return jsonMainObj.getString("responseMessage");

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("BLOCK1")) {
                            responseMessage =  jsonMainObj.getString("responseMessage");
                            return "BLOCK1";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("BLOCK2")) {
                            responseMessage =  jsonMainObj.getString("responseMessage");
                            return "BLOCK2";

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

            if (result.equalsIgnoreCase("Success")) {
                editor.putString("mobile_number", "91" + edtMobileNumber.getText().toString());
                editor.putBoolean("status_flag", false);
                editor.commit();

                mobile_no = pref.getString("mobile_number", "");
//                sendOTPNew_url = getApplicationContext().getResources().getString(R.string.sendOTPNew) + "?MDN=" + mobile_no + "&Type=REG";

                String BingoString =mobile_no+"|"+"REG";
                String res = Bingo.Bingo_one(BingoString);
                sendOTPNew_url=getApplicationContext().getResources().getString(R.string.sendOTPNew) + "?MDN="+mobile_no+"&Type=REG"+"&Checksum="+res;
                // System.out.println("Bingo"+sendOTPNew_url);
                requestOtp = new RequestOtp(getApplicationContext(), true);
                requestOtp.execute(sendOTPNew_url);


            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();
                ;

            } else if (result.equalsIgnoreCase("BLOCK1")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Detact_Mobile_NumberActivity.this);
                alertDialogBuilder.setTitle("Application Blocked");
                alertDialogBuilder
                        .setMessage(responseMessage)
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        String Bolcked_url = getApplicationContext().getResources().getString(R.string.updateDeviceStatus) + "?MDN=" + edtMobileNumber.getText().toString() + "&IMEI=" + getIMEI(getApplicationContext()) + "&ACTION=" + "BLOCK1";
                                        Bolcked Bolcked1 = new Bolcked(getApplicationContext());
                                        Bolcked1.execute(Bolcked_url);
                                    }
                                })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
//                                android.os.Process.killProcess(android.os.Process.myPid());
//                                System.exit(1);
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            } else if (result.equalsIgnoreCase("BLOCK2")) {
                AlertBuilder alertBuilder = new AlertBuilder(Detact_Mobile_NumberActivity.this);
                AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(responseMessage);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();


            } else {

                editor.putString("mobile_number", "91" + edtMobileNumber.getText().toString());
                editor.putBoolean("status_flag", false);
//                editor.putBoolean("got_mobile_number_newapp", true);
                editor.commit();
                mobile_no = pref.getString("mobile_number", "");
//                sendOTPNew_url = getApplicationContext().getResources().getString(R.string.sendOTPNew) + "?MDN=" + mobile_no + "&Type=REG";
                String BingoString =mobile_no+"|"+"REG";
                String res = Bingo.Bingo_one(BingoString);
                sendOTPNew_url=getApplicationContext().getResources().getString(R.string.sendOTPNew) + "?MDN="+mobile_no+"&Type=REG"+"&Checksum="+res;
                // System.out.println("Bingo"+sendOTPNew_url);
                requestOtp = new RequestOtp(getApplicationContext(), false);
                requestOtp.execute(sendOTPNew_url);

            }

        }

    }

    private class RequestOtp extends AsyncTask<String, Void, String> {

        Context context;
        String ToastMessage;
        boolean flag;

        String firstName, lastName, walletBalance;

        public RequestOtp(Context context, boolean flag) {
            this.context = context;
            this.flag = flag;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Detact_Mobile_NumberActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Success")) {
                            ToastMessage = jsonMainObj.getString("responseMessage");
                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Failure")) {
                            ToastMessage = jsonMainObj.getString("responseMessage");
//                            editor.putString("responseMessage", ToastMessage);
//                            editor.commit();
                            return "failure";

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

            editor.putBoolean("flag", flag);
            editor.commit();
            if (result.equalsIgnoreCase("Success")) {
                // Toast.makeText(getApplicationContext(),ToastMessage,Toast.LENGTH_LONG).show();
                finish();
                Intent i = new Intent(getApplicationContext(), OTPActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("flag", flag);
                i.putExtra("session", "0");
                startActivity(i);

            } else if (result.equalsIgnoreCase("Failure")) {

//                String responseMessage1 = pref.getString("responseMessage","");
                Toast.makeText(getApplicationContext(), ToastMessage, Toast.LENGTH_LONG).show();

            }

        }

    }

    private class Bolcked extends AsyncTask<String, Void, String> {

        Context context;
        String firstName, ToastMessage;

        public Bolcked(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Detact_Mobile_NumberActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

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


            if (result.equalsIgnoreCase("Success")) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

            } else if (result.equalsIgnoreCase("Failure")) {
                // Toast.makeText(HomeScreenActivity.this,ToastMessage,Toast.LENGTH_LONG).show();
            }

        }

    }

    private class GetDiscovery extends AsyncTask<String, Void, String> {

        Context context;
        String ToastMessage;

        String firstName, lastName, walletBalance;

        public GetDiscovery(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Detact_Mobile_NumberActivity.this);
            pDialog.setMessage("Verification in progress");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);
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
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("Success")) {
                    String client_id = jsonObject.getString("clientId");
                    String client_secret = jsonObject.getString("clientSecret");
                    String authorization_url = jsonObject.getString("authorizationUrl");
                    String token_url = jsonObject.getString("tokenUrl");


                    //// System.out.println("client_id: >>>>>>>>>>>" + client_id);
                    //// System.out.println("client_secret: >>>>>>>>>>>" + client_secret);

                    editor.putString("mobile_number", "91" + edtMobileNumber.getText().toString());
                    editor.commit();

                    mobileConnectEditor.clear();
                    mobileConnectEditor.putString("client_id", client_id);
                    mobileConnectEditor.putString("client_secret", client_secret);
                    mobileConnectEditor.putString("authorization_url", authorization_url);
                    mobileConnectEditor.putString("token_url", token_url);
                    mobileConnectEditor.commit();

                    Intent intent = new Intent(getApplicationContext(), MobileConnectActivity.class);
                    intent.putExtra("client_id", client_id);
                    intent.putExtra("client_secret", client_secret);
                    intent.putExtra("authorization_url", authorization_url);
                    intent.putExtra("token_url", token_url);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("responseMessage"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException ex) {
                //// System.out.println("JSON EXCEPTION: >>>>>>>>>>>" + ex.getMessage());


            } catch (Exception e) {
                //// System.out.println("EXCEPTION: >>>>>>>>>>>" + e.getMessage());


            }


        }

    }


}
