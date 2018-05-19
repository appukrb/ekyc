package com.tcs.mmpl.customer.utility;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.tcs.mmpl.customer.Activity.Detact_Mobile_NumberActivity;
import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Activity.MobileConnectActivity;
import com.tcs.mmpl.customer.R;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 03-03-2016.
 */
public class CallJavascript {

    private static final int MODE_PRIVATE = 0 ;
    Context mContext;
    private SharedPreferences mobileConnectPref,pref,userInfoPref;
    private SharedPreferences.Editor mobileConnectEditor,editor,userInfoEditor;


    /** Instantiate the interface and set the context */
    public CallJavascript(Context c) {
        mContext = c;

        mobileConnectPref = mContext.getSharedPreferences("mobile_connect", MODE_PRIVATE);
        mobileConnectEditor = mobileConnectPref.edit();

        pref = mContext.getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = mContext.getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void openCall(String number) {
       // Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+number));
        mContext.startActivity(callIntent);
    }

    @JavascriptInterface
    public void openMail(String emailid)
    {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ emailid});

        email.putExtra(Intent.EXTRA_SUBJECT, "");
        email.putExtra(Intent.EXTRA_TEXT, "");

        //need this to prompts email client only
        email.setType("message/rfc822");

        mContext.startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @JavascriptInterface
    public void AuthResponse(String code,String state)
    {
        //// System.out.println("State:::::::" + state);
        //// System.out.println("Code:::::::" + code);


        if(code.trim().equalsIgnoreCase("NA"))
        {
            try
            {
                MobileConnectActivity.mobile_connect.finish();
            }catch (Exception e)
            {

            }
        }
        else {

            String save_token = mContext.getResources().getString(R.string.save_token)+"?MDN="+pref.getString("mobile_number", "")+"&Url="+mobileConnectPref.getString("token_url","")+"&Code="+Uri.encode(code.trim(),"utf-8")+"&clientID="+Uri.encode(mobileConnectPref.getString("client_id",""),"utf-8")+"&clientSecret="+Uri.encode(mobileConnectPref.getString("client_secret",""),"utf-8");
            //// System.out.println(save_token);
            GetToken getToken = new GetToken(mContext, code);
            getToken.execute(save_token);
        }

    }

    @JavascriptInterface
    public void closeError()
    {      try
            {
                MobileConnectActivity.mobile_connect.finish();
            }catch (Exception e)
            {

            }
    }


    public String getOS()
    {
        String OS = "";
        try {
            OS = Build.VERSION.RELEASE;
        }
        catch (Exception e)
        {
            OS="";
        }


        return "Android "+OS;
    }
    public  String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private  String capitalize(String str) {
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

    public String getIMEI(Context context)
    {
        String IMEI = "";

        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            IMEI = mTelephonyManager.getDeviceId();
        }
        catch (Exception e)
        {
            IMEI = "";
        }

        return  IMEI;
    }

    private class GetToken extends AsyncTask<String, Void, String> {

        Context context;
        String ToastMessage;

        String firstName, lastName, walletBalance;
        ProgressDialog pDialog;
        private String code;

        public GetToken(Context context,String code) {
            this.context = context;
            this.code = code;

            //// System.out.println("Code inside Token::: "+code);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(context.getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(context);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("After Saving Token: >>>>>>>>>>>" + jsonStr);

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
            try
            {

                GetUserStatus getUserStatus = new GetUserStatus(context);
                getUserStatus.execute(context.getResources().getString(R.string.getUserStatus));


            }
            catch (Exception e)
            {

            }


        }

    }

    private class GetUserStatus extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;
        ProgressDialog pDialog;

        public GetUserStatus(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(context.getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

//                //// System.out.println("MDN: >>>>>>>>>>>" + "91"+edtMobileNumber.getText().toString());
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("MDN", pref.getString("mobile_number", ""));
                jsonObject.accumulate("IMEI", getIMEI(context));
                jsonObject.accumulate("Model", getDeviceName());
                jsonObject.accumulate("OS", getOS());
                jsonObject.accumulate("Network",getNetworkClass(context));
//                jsonObject.accumulate("IMEI", "123456");


                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(context, se);
//                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());

                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            firstName = jsonMainObj.getString("firstName");
                            lastName = jsonMainObj.getString("lastName");
                            walletBalance = jsonMainObj.getString("walletBalance");

                            userInfoEditor.putString("firstname",firstName);
                            userInfoEditor.putString("lastname",lastName);
                            userInfoEditor.putString("walletbalance",walletBalance);
                            userInfoEditor.putString("usertype", jsonMainObj.getString("userType"));
                            userInfoEditor.putString("emailId", jsonMainObj.getString("emailId"));
                            userInfoEditor.putString("account",jsonMainObj.getString("account"));
                            userInfoEditor.putString("kycstatus",jsonMainObj.getString("kycstatus"));
                            userInfoEditor.putString("billdeskFlag", jsonMainObj.getString("billdeskFlag"));
                            userInfoEditor.putString("userID", jsonMainObj.getString("userID"));
                            userInfoEditor.commit();

                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            return jsonMainObj.getString("responseMessage");

                        }
                        else if(jsonMainObj.getString("responseStatus").equalsIgnoreCase("BLOCK1")){
                            return "BLOCK1";

                        }else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("BLOCK2")){
                            return "BLOCK2";

                        }
                        else {
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

            try{
                Detact_Mobile_NumberActivity.detect_mobile.finish();
            }
            catch (Exception e)
            {

            }
            if (result.equalsIgnoreCase("Success"))
            {
                MobileConnectActivity.mobile_connect.finish();
                editor.putString("mobile_number", pref.getString("mobile_number", ""));
                editor.putBoolean("status_flag", false);
                editor.putBoolean("got_mobile_number_newapp", true);
                editor.commit();

                userInfoEditor.putBoolean("new_user_registered_newapp", true);
                userInfoEditor.commit();

                Intent i = new Intent(mContext, HomeScreenActivity.class);
                mContext.startActivity(i);

            } else if (result.equalsIgnoreCase("Failure")) {
                Toast.makeText(context, context.getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("BLOCK1")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Application Blocked");
                alertDialogBuilder
                        .setMessage(context.getResources().getString(R.string.block1))
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        String Bolcked_url=context.getResources().getString(R.string.updateDeviceStatus) + "?MDN="+pref.getString("mobile_number", "").substring(2)+"&IMEI="+getIMEI(context)+"&ACTION="+"BLOCK1";
                                        Bolcked Bolcked1 = new Bolcked(context);
                                        Bolcked1.execute(Bolcked_url);
                                    }
                                })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                MobileConnectActivity.mobile_connect.finish();
                                dialog.cancel();
//                                android.os.Process.killProcess(android.os.Process.myPid());
//                                System.exit(1);
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();



            }else if (result.equalsIgnoreCase("BLOCK2")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Application Blocked");
                alertDialogBuilder
                        .setMessage(context.getResources().getString(R.string.block2))
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        String Bolcked_url=context.getResources().getString(R.string.updateDeviceStatus) + "?MDN="+pref.getString("mobile_number", "").substring(2)+"&IMEI="+getIMEI(context)+"&ACTION="+"BLOCK2";
                                        Bolcked Bolcked1 = new Bolcked(context);
                                        Bolcked1.execute(Bolcked_url);
                                    }
                                })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MobileConnectActivity.mobile_connect.finish();
                                dialog.cancel();
//                                android.os.Process.killProcess(android.os.Process.myPid());
//                                System.exit(1);
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }

            else {

                MobileConnectActivity.mobile_connect.finish();
                editor.putString("mobile_number", pref.getString("mobile_number", ""));
                editor.putBoolean("status_flag", false);
                editor.putBoolean("got_mobile_number_newapp", true);
                editor.commit();

                userInfoEditor.putBoolean("new_user_registered_newapp", false);
                userInfoEditor.commit();

                Intent i = new Intent(mContext, HomeScreenActivity.class);
                mContext.startActivity(i);

            }

        }

    }

    private class Bolcked extends AsyncTask<String, Void, String> {

        Context context;
        String firstName,ToastMessage;
        ProgressDialog pDialog;

        public Bolcked(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(context.getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(context);
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


            if (result.equalsIgnoreCase("Success"))
            {
                MobileConnectActivity.mobile_connect.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

            }
            else if (result.equalsIgnoreCase("Failure"))
            {
                MobileConnectActivity.mobile_connect.finish();
                // Toast.makeText(HomeScreenActivity.this,ToastMessage,Toast.LENGTH_LONG).show();
            }

        }

    }


}
