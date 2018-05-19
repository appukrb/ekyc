package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.CircularProgressBar;
import com.tcs.mmpl.customer.utility.SmsReciever;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class OTPActivity extends Activity {
 
    private static TextView txtResendOTP;
    private static Button btnSubmitOTP,btnCancel;
    private static EditText edtOTP;
    ProgressDialog pDialog;
    private SharedPreferences pref,userInfoPref;
    private SharedPreferences.Editor editor,userInfoEditor;
    String  mobile_no,sendOTPNew_url;
    private boolean flag;
    private static Context mContext;
    String ToastMessage;
    private String session;
    String responseStatus,responseMessage,refno,otp;

    private static LinearLayout linProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = this;

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        editor.putBoolean("otpscreen", true);
        editor.commit();

        mobile_no=pref.getString("mobile_number", "");
        txtResendOTP = (TextView)findViewById(R.id.txtResendOTP);
        btnSubmitOTP = (Button)findViewById(R.id.btnSubmitOTP);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        edtOTP = (EditText)findViewById(R.id.edtOTP);

        linProgress = (LinearLayout)findViewById(R.id.linProgress);

        flag = getIntent().getBooleanExtra("flag", false);
        session = getIntent().getStringExtra("session");




        if(session.equalsIgnoreCase("0"))
        {
            txtResendOTP.setEnabled(false);
            btnSubmitOTP.setEnabled(false);
            btnCancel.setEnabled(false);
            linProgress.setVisibility(View.VISIBLE);
            edtOTP.setFocusable(false);
            edtOTP.setEnabled(false);
            enableBroadcastReceiver();
//            OpenProgress openProgress = new OpenProgress(getApplicationContext());
//            openProgress.execute();

            final CircularProgressBar c2 = (CircularProgressBar) findViewById(R.id.circularprogressbar2);

            c2.animateProgressTo(20, 0, new CircularProgressBar.ProgressAnimationListener() {

                @Override
                public void onAnimationStart() {
                }

                @Override
                public void onAnimationProgress(int progress) {
                    c2.setTitle(progress+"");
                }

                @Override
                public void onAnimationFinish() {
//                c2.setSubTitle("done");
//                Toast.makeText(getApplicationContext(),"done",Toast.LENGTH_LONG);
//                    try
//                    {
//                        disableBroadcastReceiver();
//                    }
//                    catch (Exception e)
//                    {
//
//                    }
                    linProgress.setVisibility(View.GONE);
                    txtResendOTP.setEnabled(true);
                    btnSubmitOTP.setEnabled(true);
                    btnCancel.setEnabled(true);
                    edtOTP.setFocusableInTouchMode(true);
                    edtOTP.setEnabled(true);
                }
            });

        }



    }

    @Override
    public void onBackPressed() {

    }

    public void SubmitOTP(View v)
    {
        if (edtOTP.getText().toString().trim().equalsIgnoreCase("") || (edtOTP.getText().toString().trim().length()<4)) {
            Toast.makeText(getApplicationContext(), "Please enter value for mandatory fields.", Toast.LENGTH_LONG).show();
            return;
        } else {

            String userstring = edtOTP.getText().toString().trim();
            String BingoString =mobile_no+"|"+"REG"+"|"+userstring;
            String res = Bingo.Bingo_one(BingoString);
            sendOTPNew_url=getApplicationContext().getResources().getString(R.string.verifyOTP) + "?MDN="+mobile_no+"&OTP="+userstring+"&Type=REG"+"&Checksum="+res;
            // System.out.println(sendOTPNew_url);
            VerifyOtp verifyOtp = new VerifyOtp(getApplicationContext());
            verifyOtp.execute(sendOTPNew_url);
//|| edtotp.getText().toString().trim().length()<4
//            if(userInfoPref.getString("usertype","C").equalsIgnoreCase("C"))
//            {
//                editor.putBoolean("otpscreen",false);
//                editor.commit();
//                Intent i = new Intent(OTPActivity.this, HomeScreenActivity.class);
//                userInfoEditor.putBoolean("new_user_registered_newapp", flag);
//                editor.putBoolean("got_mobile_number_newapp", true);
//                userInfoEditor.commit();
//                editor.commit();
//                startActivity(i);
//                finish();
//            }
//            else
//            {
//                editor.putBoolean("otpscreen",false);
//                editor.commit();
//                Intent i = new Intent(OTPActivity.this, MerchantLandingActivity.class);
//                userInfoEditor.putBoolean("new_user_registered_newapp", flag);
//                editor.putBoolean("got_mobile_number_newapp", true);
//                userInfoEditor.commit();
//                editor.commit();
//                startActivity(i);
//                finish();
//            }
        }
    }

    public void CancelOTP(View v)
    {

        editor.putBoolean("otpscreen", false);
        editor.putBoolean("flag",false);
        editor.commit();

        Intent i = new Intent(getApplicationContext(),Detact_Mobile_NumberActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("phone_number","");
        startActivity(i);
        finish();
    }

    public void ResendOTP(View v) {


        String BingoString =mobile_no+"|"+"REG";
        String res = Bingo.Bingo_one(BingoString);
        sendOTPNew_url=getApplicationContext().getResources().getString(R.string.sendOTPNew) + "?MDN="+mobile_no+"&Type=REG"+"&Checksum="+res;
        // System.out.println("Bingo"+sendOTPNew_url);
        RequestOtp requestOtp = new RequestOtp(getApplicationContext());
        requestOtp.execute(sendOTPNew_url);
        edtOTP.setText("");
        enableBroadcastReceiver();
    }

    public static void getSmsDetails (String SMSBody)
    {
        try {
            edtOTP.setText(SMSBody);
            disableBroadcastReceiver();

            linProgress.setVisibility(View.GONE);
            txtResendOTP.setEnabled(true);
            btnSubmitOTP.setEnabled(true);
            btnCancel.setEnabled(true);
            edtOTP.setFocusableInTouchMode(true);
            edtOTP.setEnabled(true);
        }
        catch (Exception e){

        }
    }

    public void enableBroadcastReceiver()
    {
        ComponentName receiver = new ComponentName(this, SmsReciever.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
       // showProgress();

    }

    public static void disableBroadcastReceiver ()
    {
        ComponentName receiver = new ComponentName(mContext, SmsReciever.class);
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
//        Toast.makeText(mContext, "Disabled broadcast receiver", Toast.LENGTH_SHORT).show();

//hideProgress();
    }

    private class RequestOtp extends AsyncTask<String, Void, String> {

        Context context;


        String firstName, lastName, walletBalance;

        public RequestOtp(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(OTPActivity.this);
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

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Success"))
                        {
                            ToastMessage=jsonMainObj.getString("responseMessage");
//                            editor.putString("responseMessage", ToastMessage);
//                            editor.commit();
                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Failure"))
                        {
                            ToastMessage =jsonMainObj.getString("responseMessage");
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

            editor.putBoolean("flag",flag);
            editor.commit();
            if (result.equalsIgnoreCase("Success"))
            {
//                String responseMessage1 = pref.getString("responseMessage","");
                Toast.makeText(getApplicationContext(),ToastMessage,Toast.LENGTH_LONG).show();

            }
            else if (result.equalsIgnoreCase("Failure"))
            {
                Toast.makeText(getApplicationContext(),ToastMessage,Toast.LENGTH_LONG).show();

            }

        }

    }

    private class VerifyOtp extends AsyncTask<String, Void, String> {

        Context context;


        String firstName, lastName, walletBalance;

        public VerifyOtp(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(OTPActivity.this);
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

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Success"))
                        {
                            ToastMessage =jsonMainObj.getString("responseMessage");

                            responseStatus=jsonMainObj.getString("responseStatus");
                            responseMessage=jsonMainObj.getString("responseMessage");
                            refno=jsonMainObj.getString("refno");
                            otp=jsonMainObj.getString("otp");

//                            editor.putString("responseMessage", ToastMessage);
//                            editor.commit();
                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Failure"))
                        {
                            ToastMessage =jsonMainObj.getString("responseMessage");
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

            editor.putBoolean("flag",flag);
            editor.commit();
            if (result.equalsIgnoreCase("Success"))
            {
               // Toast.makeText(getApplicationContext(),ToastMessage,Toast.LENGTH_LONG).show();

                String BingoString =responseStatus+"|"+responseMessage+"|"+refno;
                String res = Bingo.Bingo_one(BingoString);
                // System.out.println(res+"res");

                if(res.equals(otp)) {

                    if (userInfoPref.getString("usertype", "").equalsIgnoreCase("M")) {
                        editor.putBoolean("otpscreen", false);
                        editor.commit();
                        Intent i = new Intent(OTPActivity.this, MerchantLandingActivity.class);
                        userInfoEditor.putBoolean("new_user_registered_newapp", flag);
                        editor.putBoolean("got_mobile_number_newapp", true);
                        userInfoEditor.commit();
                        editor.commit();
                        startActivity(i);
                        finish();
                    } else {
                        editor.putBoolean("otpscreen", false);
                        editor.commit();
                        Intent i = new Intent(OTPActivity.this, HomeScreenActivity.class);
                        userInfoEditor.putBoolean("new_user_registered_newapp", flag);
                        editor.putBoolean("got_mobile_number_newapp", true);
                        userInfoEditor.commit();
                        editor.commit();
                        startActivity(i);
                        finish();

                    }
                }
                else
                    {
                        Toast.makeText(getApplicationContext(),"We have a Security Issue",Toast.LENGTH_LONG).show();
                }
            }
            else if (result.equalsIgnoreCase("Failure"))
            {
                Toast.makeText(getApplicationContext(),ToastMessage,Toast.LENGTH_LONG).show();
                //// System.out.println("ToastMessage"+ToastMessage);
            }

        }

    }

//    private class ResendOtp extends AsyncTask<Void, String, String> {
//        Context context;
//        ProgressDialog pDialog;
//        String user;
//
//
//        public ResendOtp(Context context) {
//            this.context = context;
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(OTPActivity.this);
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(Void... arg0) {
//
//            String res = null;
//
//            try {
//                String json = "";
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.accumulate("MDN", pref.getString("mobile_number",""));
//                json = jsonObject.toString();
////                //// System.out.println(json);
//                // 5. set json to StringEntity
//                StringEntity se = new StringEntity(json);
//                WebServiceHandler sh = new WebServiceHandler(context, se);
//                String jsonStr = sh.makeServiceCall(getResources().getString(R.string.sendOTP),WebServiceHandler.POST);
//
//                res = jsonStr;
//
//                if (jsonStr != null) {
//                    try {
//                        JSONObject jsonMainObj = new JSONObject(jsonStr);
//
//                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {
//
//                            editor.putInt("otp", jsonMainObj.getInt("otp"));
//                            editor.commit();
//                            return "Success";
//
//                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
//
//                            editor.putString("responseMessage", jsonMainObj.getString("responseMessage"));
//                            editor.commit();
//                            return "failure";
//
//                        }
//                        else
//                        {
//                            return "Failure";
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        return "Failure";
//                    }
//                } else {
//                    Log.e("ServiceHandler", "Couldn't get any data from the url");
//                    return "Failure";
//                }
//
//
//
//            } catch (Exception e) {
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
//                return "Failure";
//            }
//
//
//        }
//
//
//        protected void onPostExecute(final String result) {
//            if (pDialog.isShowing())
//                pDialog.dismiss();
//
//            Log.d("Result::::", ">" + result);
//
//            if (result.equalsIgnoreCase("Success"))
//            {
//                txtResendOTP.setEnabled(false);
//                btnSubmitOTP.setEnabled(false);
//                btnCancel.setEnabled(false);
//                edtOTP.setFocusableInTouchMode(false);
//                edtOTP.setEnabled(false);
//                linProgress.setVisibility(View.VISIBLE);
//                enableBroadcastReceiver();
////            OpenProgress openProgress = new OpenProgress(getApplicationContext());
////            openProgress.execute();
//
//                final CircularProgressBar c2 = (CircularProgressBar) findViewById(R.id.circularprogressbar2);
//
//                c2.animateProgressTo(20, 0, new CircularProgressBar.ProgressAnimationListener() {
//
//                    @Override
//                    public void onAnimationStart() {
//                    }
//
//                    @Override
//                    public void onAnimationProgress(int progress) {
//                        c2.setTitle(progress+"");
//                    }
//
//                    @Override
//                    public void onAnimationFinish() {
////                        try
////                        {
////                            disableBroadcastReceiver();
////                        }
////                        catch (Exception e)
////                        {
////
////                        }
//
//                        linProgress.setVisibility(View.GONE);
//                        txtResendOTP.setEnabled(true);
//                        btnSubmitOTP.setEnabled(true);
//                        btnCancel.setEnabled(true);
//                        edtOTP.setFocusableInTouchMode(true);
//                        edtOTP.setEnabled(true);
//                    }
//                });
//            }
//            else
//            {
//                String responseMessage1 = pref.getString("responseMessage","");
//                Toast.makeText(getApplicationContext(),responseMessage1,Toast.LENGTH_LONG).show();
//            }
//        }
//    }


//    private class OpenProgress extends AsyncTask<Void, String, String> {
//        Context context;
//        ProgressDialog pDialog;
//        String user;
//
//
//        public OpenProgress(Context context) {
//            this.context = context;
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(OTPActivity.this);
//            progressDialog.setMessage("Checking OTP...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(Void... arg0) {
//
//            try {
//                Thread.sleep(60000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return "";
//        }
//
//
//        protected void onPostExecute(final String result) {
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();
//
//
//
//
//        }
//    }
//
//    public void showProgress()
//    {
//        progressDialog = new ProgressDialog(OTPActivity.this);
//        progressDialog.setMessage("Checking OTP...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//    }
//
//    public static void hideProgress()
//    {
//        if(progressDialog.isShowing())
//            progressDialog.dismiss();
//
//    }

}
