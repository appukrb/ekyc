package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
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

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.SmsReciever_Two;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangeMPINActivity extends Activity {
    EditText edtOldMpin;
    EditText edtNewMpin;
    EditText edtConfirmNewMpin;
    static EditText edt_chgmPIN_otp;
    Button btnMpinSubmit,btnMpin_OTP_Resend;
    LinearLayout mainlinear,header_forgotMpin;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    private static Context mContext;
    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;
    LinearLayout webview_linear;
    TextView txtSMSAlert;
    String status,ToastMessage,sendOTPNew_url,mobile_no,oldMpin,newMpin,confirmNewMpin;

    String responseStatus,responseMessage,refno,otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_mpin);
        mainlinear = (LinearLayout)findViewById(R.id.mainlinear);
        header_forgotMpin = (LinearLayout)findViewById(R.id.header_forgotMpin);
        typeface=Typeface.createFromAsset(getApplicationContext().getAssets(),"helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();
        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        mobile_no=pref.getString("mobile_number", "");

        if(!userInfoPref.getString("usertype","").equalsIgnoreCase("C"))
        {
            header_forgotMpin.setVisibility(View.GONE);
        }

        connectionDetector = new ConnectionDetector(getApplicationContext());
        if(connectionDetector.isConnectingToInternet()) {
            String BingoString =mobile_no+"|"+"CHANGEMPIN";
            String res = Bingo.Bingo_one(BingoString);
            sendOTPNew_url = getApplicationContext().getResources().getString(R.string.sendOTPNew) + "?MDN=" + mobile_no+"&Type=CHANGEMPIN"+"&Checksum="+res;
//            sendOTPNew_url = getApplicationContext().getResources().getString(R.string.sendOTPNew) + "?MDN=" + mobile_no+"&Type=CHANGEMPIN";
            //// System.out.println(sendOTPNew_url);
            RequestOtp requestOtp = new RequestOtp(getApplicationContext());
            requestOtp.execute(sendOTPNew_url);
            // System.out.println("sendOTPNew_url"+sendOTPNew_url);
        }
        edtOldMpin = (EditText) findViewById(R.id.edtOldMpin);
        edtNewMpin = (EditText) findViewById(R.id.edtNewMpin);
        edtConfirmNewMpin = (EditText) findViewById(R.id.edtConfirmNewMpin);
        edt_chgmPIN_otp = (EditText) findViewById(R.id.edt_chgmPIN_otp);
        btnMpinSubmit =(Button) findViewById(R.id.btnMpinSubmit);
        btnMpin_OTP_Resend=(Button)findViewById(R.id.btnMpin_OTP_Resend);

        status = getIntent().getStringExtra("status");

        if(status.trim().equalsIgnoreCase("0")) {
            txtSMSAlert = (TextView) findViewById(R.id.txtSMSAlert);
            txtSMSAlert.setVisibility(View.GONE);
        }

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        btnMpin_OTP_Resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionDetector.isConnectingToInternet()) {
                    edt_chgmPIN_otp.setText("");
                    String BingoString =mobile_no+"|"+"CHANGEMPIN";
                    String res = Bingo.Bingo_one(BingoString);
                    sendOTPNew_url = getApplicationContext().getResources().getString(R.string.sendOTPNew) + "?MDN=" + mobile_no+"&Type=CHANGEMPIN"+"&Checksum="+res;
                    RequestOtp requestOtp = new RequestOtp(getApplicationContext());
                    requestOtp.execute(sendOTPNew_url);

                    // System.out.println("sendOTPNew_url"+sendOTPNew_url);
                }
            }
        });
        header_forgotMpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    disableBroadcastReceiver();
                }
                catch (Exception e){
                    // System.out.println("Exception"+e);
                }
                Intent i = new Intent(getApplicationContext(), ForgotMPINActivity.class);
                startActivity(i);
            }
        });
        btnMpinSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connectionDetector.isConnectingToInternet()) {

                    oldMpin = edtOldMpin.getText().toString();
                    newMpin = edtNewMpin.getText().toString();
                    confirmNewMpin = edtConfirmNewMpin.getText().toString();
                    String userstring = edt_chgmPIN_otp.getText().toString().trim();

                    if (oldMpin.trim().equalsIgnoreCase("") || oldMpin.length() < 4) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.oldmpin), Toast.LENGTH_LONG).show();
                    }   else if (newMpin.equalsIgnoreCase("") || newMpin.length() < 4) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.newmpin), Toast.LENGTH_LONG).show();
                    }else if (confirmNewMpin.equalsIgnoreCase("") || confirmNewMpin.length() < 4) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Confirmmpin), Toast.LENGTH_LONG).show();
                    }else if (userstring.equalsIgnoreCase("") || userstring.length() < 4) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_otp), Toast.LENGTH_LONG).show();
                    }
                    else {

                        if(newMpin.equalsIgnoreCase(confirmNewMpin)) {
                            String BingoString =mobile_no+"|"+"CHANGEMPIN"+"|"+userstring;
                            String res = Bingo.Bingo_one(BingoString);
                            sendOTPNew_url=getApplicationContext().getResources().getString(R.string.verifyOTP) + "?MDN="+mobile_no+"&OTP="+userstring+"&Type=CHANGEMPIN"+"&Checksum="+res;
                            VerifyOtp verifyOtp = new VerifyOtp(getApplicationContext());
                            verifyOtp.execute(sendOTPNew_url);
                            // System.out.println("VerifyOtp"+sendOTPNew_url);

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.mpinnotmatching), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {


                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                }


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
    public void onBackPressed()
    {
        // code here to show dialog
        if(status.trim().equalsIgnoreCase("0"))
            super.onBackPressed();

    }

    public static void disableBroadcastReceiver ()
    {
        ComponentName receiver = new ComponentName(mContext, SmsReciever_Two.class);
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

    public void enableBroadcastReceiver()
    {
        ComponentName receiver = new ComponentName(this, SmsReciever_Two.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    public static void getSmsDetails (String SMSBody)
    {
        try {
            edt_chgmPIN_otp.setText(SMSBody);
            disableBroadcastReceiver();
        }
        catch (Exception e){

        }
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
            pDialog = new ProgressDialog(ChangeMPINActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>RequestOtp" + jsonStr);

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


            if (result.equalsIgnoreCase("Success"))
            {

                enableBroadcastReceiver();

            }
            else if (result.equalsIgnoreCase("Failure"))
            {


                try
                    {
                        disableBroadcastReceiver();
                    }
                catch (Exception e)
                    {

                    }

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
            pDialog = new ProgressDialog(ChangeMPINActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                // System.out.println("Response: >>>>>>>>>>>VerifyOtp" + jsonStr);

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


            if (result.equalsIgnoreCase("Success"))
            {

                String BingoString =responseStatus+"|"+responseMessage+"|"+refno;
                String res = Bingo.Bingo_one(BingoString);
                // System.out.println(res+"res");

                if(res.equals(otp)) {
                    String BingoString1 = pref.getString("mobile_number", "") + "|" + oldMpin + "|" + newMpin;
                    String res1 = Bingo.Bingo_one(BingoString1);
                    String changeMpinurl = getResources().getString(R.string.changempin) + "?MPIN=" +
                            oldMpin + "&userType=C&mobileNo=" + pref.getString("mobile_number", "") + "&newMpin=" + newMpin + "&Checksum=" + res1;
                    ChangeMpin changempin = new ChangeMpin(getApplicationContext());
                    changempin.execute(changeMpinurl);
                    // System.out.println("changeMpinurl" + changeMpinurl);
                }
            }
            else if (result.equalsIgnoreCase("Failure"))
            {
                Toast.makeText(ChangeMPINActivity.this,ToastMessage,Toast.LENGTH_LONG).show();
                //// System.out.println("ToastMessage" + ToastMessage);
            }

        }

    }

    private class ChangeMpin extends AsyncTask<String, Void, String> {

        Context context;

        String responseMessage;

        public ChangeMpin(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ChangeMPINActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();



                WebServiceHandler serviceHandler = new WebServiceHandler(context);
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());

                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {


                            responseMessage = jsonMainObj.getString("responseMessage");


                            return "Success";

                        }

                        else
                        {
                            responseMessage = jsonMainObj.getString("responseMessage");
                            return responseMessage;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failure";
                    }
                }
                else {
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


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeMPINActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(responseMessage);
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);
                // Setting OK Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();

                        finish();
                        Intent intent =new Intent(getApplicationContext(),HomeScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();

            }
            else if(result.equalsIgnoreCase("Failure"))
            {

                AlertBuilder alertBuilder = new AlertBuilder(ChangeMPINActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));
                edtNewMpin.setText("");
                edtConfirmNewMpin.setText("");
            }
            else {
                AlertBuilder alertBuilder = new AlertBuilder(ChangeMPINActivity.this);
                alertBuilder.showAlert(responseMessage);
                edtNewMpin.setText("");
                edtConfirmNewMpin.setText("");

            }

        }

    }

}
