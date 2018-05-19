package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.SmsReciever_one;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ForgotMPINActivity extends Activity {
    private static TextView edtotp;
    LinearLayout mainlinear;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    private ProgressDialog pDialog;
    ConnectionDetector connectionDetector;
    private static Context mContext;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Calendar cal;
    private int day;
    private int month;
    private int year;
    EditText edtDate,edtmailid,edt_last_trans_amount,et_new_mPIN,etconf_mPIN;
    Button btnOTPresend,btnOtpSubmit;
    String mobile_no,email_id1,last_amount1="0",dob1,ForgotMipnurl,walletBalance,sendOTPNew_url,ToastMessage,new_mipn,re_new_mpin,otpRef;
    SharedPreferences userInfoPref,pref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_mpin);
        mainlinear = (LinearLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();
        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);

        mobile_no=pref.getString("mobile_number", "");
        walletBalance=userInfoPref.getString("walletbalance", "");
        //// System.out.println("walletBalance"+walletBalance+"mobile_no"+mobile_no);

        mContext = this;

        connectionDetector = new ConnectionDetector(getApplicationContext());

        edtmailid = (EditText)findViewById(R.id.edtmailid);
        edt_last_trans_amount = (EditText)findViewById(R.id.edt_last_trans_amount);
        edtotp = (EditText)findViewById(R.id.edtotp);
        edtDate = (EditText)findViewById(R.id.edtDateOfBirth);
        et_new_mPIN= (EditText)findViewById(R.id.et_new_mPIN);
        etconf_mPIN= (EditText)findViewById(R.id.etconf_mPIN);
        btnOTPresend=(Button)findViewById(R.id.btnOTPresend);
        btnOtpSubmit=(Button)findViewById(R.id.btnOtpSubmit);
        edtmailid.setText(userInfoPref.getString("emailId",""));

        if(connectionDetector.isConnectingToInternet()) {
            sendOTPNew_url = getApplicationContext().getResources().getString(R.string.forgotMpinOtp) + "?MDN=" + mobile_no;
            // System.out.println("sendOTPNew_url"+sendOTPNew_url);
//            +"&Type=FORGOTMPIN"
            RequestOtp requestOtp = new RequestOtp(getApplicationContext());
            requestOtp.execute(sendOTPNew_url);
        }
        else
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();
        }

        enableBroadcastReceiver();
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtDate.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });

        btnOTPresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionDetector.isConnectingToInternet()) {
                    edtotp.setText("");
                    sendOTPNew_url = getApplicationContext().getResources().getString(R.string.forgotMpinOtp) + "?MDN=" + mobile_no;
                    // System.out.println("sendOTPNew_url"+sendOTPNew_url);
//            +"&Type=FORGOTMPIN"
                    RequestOtp requestOtp = new RequestOtp(getApplicationContext());
                    requestOtp.execute(sendOTPNew_url);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();
                }

            }
        });

        btnOtpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                AlertBuilder alert = new AlertBuilder(ForgotMPINActivity.this);

//                if(edtmailid.getText().toString().trim().equalsIgnoreCase("") || !android.util.Patterns.EMAIL_ADDRESS.matcher(edtmailid.getText().toString().trim()).matches())
//                {
//                    alert.showAlert(getResources().getString(R.string.invalid_email));
//                }
                 if((edt_last_trans_amount.getText().toString().trim().equalsIgnoreCase(""))&&(edtDate.getText().toString().trim().equalsIgnoreCase("")))
                {
                    alert.showAlert(getResources().getString(R.string.mandatory_field2));
                }
//                else if(edtDate.getText().toString().trim().equalsIgnoreCase(""))
//                {
//                    alert.showAlert(getResources().getString(R.string.invalid_date));
//                }
                else if(edtotp.getText().toString().trim().equalsIgnoreCase("") || edtotp.getText().toString().trim().length()<4)
                {
                    alert.showAlert(getResources().getString(R.string.invalid_otp));
                }else if(et_new_mPIN.getText().toString().trim().equalsIgnoreCase("") || et_new_mPIN.getText().toString().trim().length()<4)
                {
                    alert.showAlert(getResources().getString(R.string.newmpin));
                }else if(etconf_mPIN.getText().toString().trim().equalsIgnoreCase("") || etconf_mPIN.getText().toString().trim().length()<4)
                {
                    alert.showAlert(getResources().getString(R.string.Confirmmpin));
                }
                else {

                    String userstring = edtotp.getText().toString().trim();
                    new_mipn = et_new_mPIN.getText().toString().trim();
                    re_new_mpin = etconf_mPIN.getText().toString().trim();
                    email_id1 = Uri.encode(edtmailid.getText().toString().trim(), "utf-8");
                    last_amount1=Uri.encode(edt_last_trans_amount.getText().toString().trim(), "utf-8");
                    dob1=Uri.encode(edtDate.getText().toString().trim(), "utf-8");

                    email_id1="";


                        if(new_mipn.equalsIgnoreCase(re_new_mpin)) {
                            if(connectionDetector.isConnectingToInternet()) {

                                String BingoString = mobile_no + "|" + dob1+"|"+email_id1+"|"+last_amount1+"|"+walletBalance+"|"+new_mipn+"|"+otpRef+"|"+userstring ;
                                String res = Bingo.Bingo_one(BingoString);

                                ForgotMipnurl = getApplicationContext().getResources().getString(R.string.forgotMPINNew) +
                                        "?mobileNo=" + mobile_no + "&ans1=" + dob1 + "&ans2=" + email_id1 + "&ans3=" +
                                        last_amount1 + "&ans4=" + walletBalance + "&newMPIN=" + new_mipn + "&otpRef=" + otpRef + "&otp=" + userstring+"&Checksum="+res;
                                 System.out.println(ForgotMipnurl);
                                ForgotMipn ForgotMipn1 = new ForgotMipn(getApplicationContext());
                                ForgotMipn1.execute(ForgotMipnurl);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();
                            }

//                           String verifyOtp_url=getApplicationContext().getResources().getString(R.string.verifyOTP) + "?MDN="+mobile_no+"&OTP="+userstring+"&Type=FORGOTMPIN";
//                            VerifyOtp verifyOtp = new VerifyOtp(getApplicationContext());
//                            verifyOtp.execute(verifyOtp_url);
                        }
                        else
                        {
                            alert.showAlert(getResources().getString(R.string.mpinnotmatching));
//                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.mpinnotmatching), Toast.LENGTH_LONG).show();
                        }



//                        editor.commit();
//                        ForgotMipnurl=getApplicationContext().getResources().getString(R.string.ForGot_MPIN) + "?mobileNo="+mobile_no+"&ans1="+dob1+"&ans2="+email_id1+"&ans3="+last_amount1+"&ans4="+walletBalance;
//
//                        ForgotMipn ForgotMipn1 = new ForgotMipn(getApplicationContext());
//                        ForgotMipn1.execute(ForgotMipnurl);



                }
            }
        });
    }

    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            edtDate.setText(selectedDay + " / " + (selectedMonth + 1) + " / " + selectedYear);
        }
    };

    public static void disableBroadcastReceiver ()
    {
        ComponentName receiver = new ComponentName(mContext, SmsReciever_one.class);
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }

    public void enableBroadcastReceiver()
    {
        ComponentName receiver = new ComponentName(this, SmsReciever_one.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    public static void getSmsDetails (String SMSBody)
    {
        try {
            edtotp.setText(SMSBody);
            disableBroadcastReceiver();
        }
        catch (Exception e){

        }
    }

    private class RequestOtp extends AsyncTask<String, Void, String> {

        Context context;


        public RequestOtp(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ForgotMPINActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Success"))
                        {
                            ToastMessage=jsonMainObj.getString("responseMessage");
                            otpRef=jsonMainObj.getString("otprefCode");
//                            editor.putString("responseMessage", ToastMessage);
//                            editor.commit();
                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Failure"))
                        {
                            ToastMessage =jsonMainObj.getString("responseMessage");
                            otpRef=jsonMainObj.getString("otprefCode");
//                            editor.putString("responseMessage", ToastMessage);
//                            editor.commit();
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


            if (result.equalsIgnoreCase("Success"))
            {
               // Toast.makeText(ForgotMPINActivity.this,ToastMessage,Toast.LENGTH_LONG).show();
                enableBroadcastReceiver();

            }
            else if (result.equalsIgnoreCase("Failure"))
            {
//                Toast.makeText(ForgotMPINActivity.this,ToastMessage,Toast.LENGTH_LONG).show();
                AlertBuilder alertBuilder = new AlertBuilder(ForgotMPINActivity.this);
                alertBuilder.showAlert(ToastMessage);
                try
                {
                    disableBroadcastReceiver();
                }
                catch (Exception e)
                {

                }

            }else {
                Toast.makeText(ForgotMPINActivity.this,getResources().getString(R.string.apidown),Toast.LENGTH_LONG).show();
            }

        }

    }

    private class ForgotMipn extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

        public ForgotMipn(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ForgotMPINActivity.this);
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

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            responseMessage = jsonMainObj.getString("responseMessage");

                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            responseMessage = jsonMainObj.getString("responseMessage");
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

            if (result.equalsIgnoreCase("Success"))
            {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ForgotMPINActivity.this);
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

                        Intent intent =new Intent(getApplicationContext(),HomeScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();

            }
            else if (result.equalsIgnoreCase("Failure"))
            {

                AlertBuilder alertBuilder = new AlertBuilder(ForgotMPINActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));

            }
            else
            {
                AlertBuilder alertBuilder = new AlertBuilder(ForgotMPINActivity.this);
                alertBuilder.showAlert(responseMessage);

            }

        }

    }

    private class VerifyOtp extends AsyncTask<String, Void, String> {

        Context context;


        public VerifyOtp(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ForgotMPINActivity.this);
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
                            ToastMessage =jsonMainObj.getString("responseMessage");
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

                ForgotMipnurl=getApplicationContext().getResources().getString(R.string.ForGot_MPIN) + "?mobileNo="+mobile_no+"&ans1="+dob1+"&ans2="+email_id1+"&ans3="+last_amount1+"&ans4="+walletBalance+"&newMPIN="+new_mipn;
                //// System.out.println(ForgotMipnurl);
                ForgotMipn ForgotMipn1 = new ForgotMipn(getApplicationContext());
                ForgotMipn1.execute(ForgotMipnurl);

            }
            else if (result.equalsIgnoreCase("Failure"))
            {
                Toast.makeText(ForgotMPINActivity.this,ToastMessage,Toast.LENGTH_LONG).show();
                //// System.out.println("ToastMessage" + ToastMessage);

            }

        }

    }
}
