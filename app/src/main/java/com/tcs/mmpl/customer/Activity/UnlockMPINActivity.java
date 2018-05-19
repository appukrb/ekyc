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
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.UnlockReciever;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UnlockMPINActivity extends Activity {


    private static EditText edtOTP;
    private static Context mContext;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    private ConnectionDetector connectionDetector;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Calendar cal;
    private int day;
    private int month;
    private int year;
    private EditText edtConfirmNewMpin, edtNewMpin, edtDateOfBirth, edtLastTransAmount;
    private LinearLayout linParent;
    private TextView txtGenerateOTP;
    private String newMPIN = "";
    private String last_trans_amount = "";
    private String dob = "";
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            edtDateOfBirth.setText(selectedDay + " / " + (selectedMonth + 1) + " / " + selectedYear);
        }
    };

    public static void disableBroadcastReceiver() {
        ComponentName receiver = new ComponentName(mContext, UnlockReciever.class);
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }

    public static void getSmsDetails(String SMSBody) {
        try {
            edtOTP.setText(SMSBody);
            disableBroadcastReceiver();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_mpin);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        linParent = (LinearLayout) findViewById(R.id.linParent);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        edtOTP = (EditText) findViewById(R.id.edtOTP);

        edtLastTransAmount = (EditText) findViewById(R.id.edtLastTransAmount);
        edtDateOfBirth = (EditText) findViewById(R.id.edtDateOfBirth);
        edtNewMpin = (EditText) findViewById(R.id.edtNewMpin);
        edtConfirmNewMpin = (EditText) findViewById(R.id.edtConfirmNewMpin);
        txtGenerateOTP = (TextView) findViewById(R.id.txtGenerateOTP);

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
                edtDateOfBirth.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                fromDatePickerDialog.show();
            }
        });

    }

    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    public void generateOTP(View v) {
        if (connectionDetector.isConnectingToInternet()) {
            edtOTP.setText("");
            String otpURL = getApplicationContext().getResources().getString(R.string.sendOTPNew) + "?MDN=" + pref.getString("mobile_number", "") + "&Type=UNLOCK";
            RequestOtp requestOtp = new RequestOtp(getApplicationContext());
            requestOtp.execute(otpURL);
        }
    }

    public void unlockOTP(View v) {


        newMPIN = Uri.encode(edtNewMpin.getText().toString().trim(), "utf-8");

        if (!edtLastTransAmount.getText().toString().trim().equalsIgnoreCase(""))
            last_trans_amount = Uri.encode(edtLastTransAmount.getText().toString().trim(), "utf-8");

        if (!edtDateOfBirth.getText().toString().trim().equalsIgnoreCase(""))
            dob = Uri.encode(edtDateOfBirth.getText().toString().trim(), "utf-8");

        AlertBuilder alert = new AlertBuilder(UnlockMPINActivity.this);

        if (edtDateOfBirth.getText().toString().trim().equalsIgnoreCase("")) {
            alert.showAlert(getResources().getString(R.string.invalid_dob));
        } else if (edtLastTransAmount.getText().toString().trim().equalsIgnoreCase("")) {
            alert.showAlert(getResources().getString(R.string.last_trans));
        } else if (edtNewMpin.getText().toString().trim().equalsIgnoreCase("") || edtNewMpin.getText().toString().trim().length() < 4) {

            alert.showAlert(getResources().getString(R.string.mpin));

        } else if (!edtConfirmNewMpin.getText().toString().trim().equals(edtNewMpin.getText().toString().trim())) {
            alert.showAlert(getResources().getString(R.string.mpinnotmatching));

        } else if (edtOTP.getText().toString().trim().equalsIgnoreCase("") || edtOTP.getText().toString().trim().length() < 6) {
            alert.showAlert(getResources().getString(R.string.invalid_otp));

        } else {
            String verifyOtp_url = getApplicationContext().getResources().getString(R.string.verifyOTP) + "?MDN=" + pref.getString("mobile_number", "") + "&OTP=" + edtOTP.getText().toString().trim();
            VerifyOtp verifyOtp = new VerifyOtp(getApplicationContext());
            verifyOtp.execute(verifyOtp_url);
        }

    }

    public void enableBroadcastReceiver() {
        ComponentName receiver = new ComponentName(this, UnlockReciever.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    private class RequestOtp extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        private ProgressDialog pDialog;
        private String otpMessage;

        public RequestOtp(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UnlockMPINActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);


                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Success")) {
                            otpMessage = jsonMainObj.getString("responseMessage");
//                            editor.putString("responseMessage", ToastMessage);
//                            editor.commit();
                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Failure")) {
                            otpMessage = jsonMainObj.getString("responseMessage");
//                            editor.putString("responseMessage", ToastMessage);
//                            editor.commit();
                            return "failure";

                        } else {
                            return "Failed";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failed";
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
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

                enableBroadcastReceiver();
                txtGenerateOTP.setText("Resend OTP");

            } else if (result.equalsIgnoreCase("Failure")) {


                try {
                    disableBroadcastReceiver();
                } catch (Exception e) {

                }

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();
            }

        }

    }

    private class VerifyOtp extends AsyncTask<String, Void, String> {

        Context context;


        String firstName, lastName;
        private ProgressDialog pDialog;
        private String otpMessage;


        public VerifyOtp(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UnlockMPINActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);


                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Success")) {
                            otpMessage = jsonMainObj.getString("responseMessage");
//                            editor.putString("responseMessage", ToastMessage);
//                            editor.commit();
                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Failure")) {
                            otpMessage = jsonMainObj.getString("responseMessage");
//                            editor.putString("responseMessage", ToastMessage);
//                            editor.commit();
                            return "Failure";

                        } else {
                            return "Failed";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failed";
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
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

                String unlock_url = getApplicationContext().getResources().getString(R.string.unlock_account_url) + "?mobileNo=" + pref.getString("mobile_number", "") + "&ans1=" + dob + "&ans2=&ans3=" + last_trans_amount + "&ans4=&newMPIN=" + newMPIN;

                UnlockMPIN unlockMPIN = new UnlockMPIN(getApplicationContext());
                unlockMPIN.execute(unlock_url);

            } else if (result.equalsIgnoreCase("Failure")) {
                Toast.makeText(UnlockMPINActivity.this, otpMessage, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(UnlockMPINActivity.this, getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();

            }

        }

    }

    private class UnlockMPIN extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;
        private ProgressDialog pDialog;

        public UnlockMPIN(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UnlockMPINActivity.this);
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

            if (result.equalsIgnoreCase("Success")) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UnlockMPINActivity.this);
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

                        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();

            } else if (result.equalsIgnoreCase("Failure")) {

                AlertBuilder alertBuilder = new AlertBuilder(UnlockMPINActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));

            } else {
                AlertBuilder alertBuilder = new AlertBuilder(UnlockMPINActivity.this);
                alertBuilder.showAlert(responseMessage);

            }

        }

    }


}
