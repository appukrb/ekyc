package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class  SplashActivity extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    String bannerNew;
    MyDBHelper db;
    MyConnectionHelper db1;
    ConnectionDetector connectionDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();


        db = new MyDBHelper(getApplicationContext());
        db1 = new MyConnectionHelper(getApplicationContext());

        connectionDetector = new ConnectionDetector(getApplicationContext());
        // System.out.println((userInfoPref.getString("usertype",""))+"usertype");
        boolean root=false;
//         root =isRooted();
        root =RootUtil.isDeviceRooted();

        if(root)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
            // Setting Dialog Title
            alertDialog.setTitle("Custom Rom");
            // Setting Dialog Message
            alertDialog.setMessage("Its Seems Your using Custom Rom");
            // Setting Icon to Dialog
            // alertDialog.setIcon(R.drawable.tick);
            // Setting OK Button
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    dialog.cancel();

                    if(connectionDetector.isConnectingToInternet())
                    {
                        String version = getResources().getString(R.string.app_version);


                        if(pref.getString("version","").trim().equalsIgnoreCase(version)) {

                            editor.putString("version",version);
                            editor.commit();

                            if (pref.getString("mobile_number", "").trim().equalsIgnoreCase(""))
                                bannerNew = getResources().getString(R.string.getbannernew) + "?MDN=000" + "&Type=" + "ANDROID";
                            else
                                bannerNew = getResources().getString(R.string.getbannernew) + "?MDN=" + pref.getString("mobile_number", "") + "&Type=" + "ANDROID";

                            GetBannerNew getBannerNew = new GetBannerNew(getApplicationContext());
                            getBannerNew.execute(bannerNew);
                        }
                        else
                        {

                            editor.putString("version",version);
                            editor.commit();

                            if (pref.getString("mobile_number", "").trim().equalsIgnoreCase(""))
                                bannerNew = getResources().getString(R.string.getbannernew) + "?MDN=000" + "&Type=" + "ANDROID";
                            else
                                bannerNew = getResources().getString(R.string.getbannernew) + "?MDN=" + pref.getString("mobile_number", "") + "&Type=" + "ANDROID";

                            GetBannerNew getBannerNew = new GetBannerNew(getApplicationContext());
                            getBannerNew.execute(bannerNew);

//                String mPhoneNumber = "";
//                editor.putString("mobile_number", "91" + mPhoneNumber);
//                editor.putBoolean("status_flag", true);
//                editor.putBoolean("got_mobile_number_newapp",false);
//                editor.commit();
//
//                userInfoEditor.putBoolean("new_user_registered_newapp",false);
//                userInfoEditor.commit();
//
//                Intent i = new Intent(SplashActivity.this, Detact_Mobile_NumberActivity.class);
//                i.putExtra("phone_number", mPhoneNumber);
//                startActivity(i);
//                finish();
                        }

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();
                        finish();
                    }

                }
            });
            // Showing Alert Message
            alertDialog.setCancelable(false);
            alertDialog.show();
        }else {
            if (connectionDetector.isConnectingToInternet()) {
                String version = getResources().getString(R.string.app_version);


                if (pref.getString("version", "").trim().equalsIgnoreCase(version)) {

                    editor.putString("version", version);
                    editor.commit();

                    if (pref.getString("mobile_number", "").trim().equalsIgnoreCase(""))
                        bannerNew = getResources().getString(R.string.getbannernew) + "?MDN=000" + "&Type=" + "ANDROID";
                    else
                        bannerNew = getResources().getString(R.string.getbannernew) + "?MDN=" + pref.getString("mobile_number", "") + "&Type=" + "ANDROID";

                    GetBannerNew getBannerNew = new GetBannerNew(getApplicationContext());
                    getBannerNew.execute(bannerNew);
                } else {

                    editor.putString("version", version);
                    editor.commit();

                    if (pref.getString("mobile_number", "").trim().equalsIgnoreCase(""))
                        bannerNew = getResources().getString(R.string.getbannernew) + "?MDN=000" + "&Type=" + "ANDROID";
                    else
                        bannerNew = getResources().getString(R.string.getbannernew) + "?MDN=" + pref.getString("mobile_number", "") + "&Type=" + "ANDROID";

                    GetBannerNew getBannerNew = new GetBannerNew(getApplicationContext());
                    getBannerNew.execute(bannerNew);

//                String mPhoneNumber = "";
//                editor.putString("mobile_number", "91" + mPhoneNumber);
//                editor.putBoolean("status_flag", true);
//                editor.putBoolean("got_mobile_number_newapp",false);
//                editor.commit();
//
//                userInfoEditor.putBoolean("new_user_registered_newapp",false);
//                userInfoEditor.commit();
//
//                Intent i = new Intent(SplashActivity.this, Detact_Mobile_NumberActivity.class);
//                i.putExtra("phone_number", mPhoneNumber);
//                startActivity(i);
//                finish();
                }

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                finish();
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

    private void showUpdateDialog(String upgradeMessage) {

        View popupView = getLayoutInflater().inflate(R.layout.popup_update, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        TextView update_dialog_txt = (TextView) popupView.findViewById(R.id.update_dialog_txt);
        Button update_dialog_btn_Cancel =(Button)popupView.findViewById(R.id.update_dialog_btn_Cancel);
        Button update_dialog_btn_ok = (Button) popupView.findViewById(R.id.update_dialog_btn_ok);

        update_dialog_txt.setText(upgradeMessage);

        update_dialog_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tcs.mmpl.customer")));
//                popupWindow.dismiss();
            }
        });

        update_dialog_btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

//            popupWindow.setOutsideTouchable(false);
//            popupWindow.setFocusable(true);
//            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
//
//
//            popupWindow.dismiss();
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);



    }

    private class GetBanner extends AsyncTask<String, Void, String> {

        Context context;

        String bannerURL;

        public GetBanner(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(SplashActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

//              // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {

                        db1.fun_delete_multibanner();
                        JSONObject jsonMainObj = new JSONObject(jsonStr);
                        JSONArray posts = jsonMainObj.optJSONArray("lstBanner");
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject jsonObject = posts.getJSONObject(i);

                            db1.fun_insertAll_tbl_multibanner(
                                    jsonObject.getString("bannerName"),
                                    jsonObject.getString("bannerDownloadLink"),
                                    jsonObject.getString("bannerURL"),
                                    jsonObject.getString("flag"));

                        }

                        return "Success";

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

//            if (pDialog.isShowing())
//                pDialog.dismiss();

//            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {



                try {
//                    DownloadImage DI = new DownloadImage(getApplicationContext());
//                    DI.execute(bannerURL);
                }
                catch (Exception e)
                {

                }


            } else if (result.equalsIgnoreCase("Failure")) {


                // Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {


            }

        }

    }

    private class GetBannerNew extends AsyncTask<String, Void, String> {

        Context context;

        String bannerURL;
        private String versionStatus = "",upgradeMessage="";
        private String downTimeMessage;

        public GetBannerNew(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(SplashActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);


                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {


                        db1.fun_delete_multibanner();
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if(jsonMainObj.getString("downTime").equalsIgnoreCase("NO"))
                        {
                            versionStatus = jsonMainObj.getString("versionStatus");
                            upgradeMessage = jsonMainObj.getString("upgradeMessage");
                            userInfoEditor.putString("rate_us_status", jsonMainObj.getString("rateUsStatus"));
                            userInfoEditor.putString("rate_message", jsonMainObj.getString("rateMsg"));
                            userInfoEditor.commit();


                            JSONArray posts = jsonMainObj.optJSONArray("lstBanner");
                            for (int i = 0; i < posts.length(); i++) {
                                JSONObject jsonObject = posts.getJSONObject(i);

                                db1.fun_insertAll_tbl_multibanner(
                                        jsonObject.getString("bannerName"),
                                        jsonObject.getString("bannerDownloadLink"),
                                        jsonObject.getString("bannerURL"),
                                        jsonObject.getString("flag"));

                            }



                            return "Success";
                        }
                        else if(jsonMainObj.getString("downTime").equalsIgnoreCase("YES"))
                        {
                            downTimeMessage = jsonMainObj.getString("downtimeMessage");
                            return "Failure1";
                        }
                        else
                        {
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
            if (result.equalsIgnoreCase("Success"))
            {
                if(versionStatus.trim().equalsIgnoreCase("true")) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            String mPhoneNumber = "";
                            try {
                                TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                                mPhoneNumber = tMgr.getLine1Number();

                            } catch (Exception e) {
                                mPhoneNumber = "";
                            }


                            if (pref.getBoolean("otpscreen", false)) {
                                Intent i = new Intent(SplashActivity.this, OTPActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra("flag", pref.getBoolean("flag", false));
                                i.putExtra("session", "1");
                                startActivity(i);
                                finish();
                            } else {
                                if (userInfoPref.getBoolean("new_user_registered_newapp", false) == false) {

                                    if (pref.getBoolean("got_mobile_number_newapp", false) == false) {


                                        if (mPhoneNumber == null) {
//                                        //// System.out.println("PhoneNumber is " + mPhoneNumber);
                                            mPhoneNumber = "";
//                                        //// System.out.println("PhoneNumber is " + mPhoneNumber);
                                            editor.putString("mobile_number", "91" + mPhoneNumber);
                                            editor.putInt("status", 0);
                                            editor.commit();


                                            Toast.makeText(getApplicationContext(), "Mobile number not detected", Toast.LENGTH_LONG).show();

                                            Intent i = new Intent(SplashActivity.this, Detact_Mobile_NumberActivity.class);
                                            i.putExtra("phone_number", mPhoneNumber);
                                            startActivity(i);
                                            finish();
                                        } else {

                                            editor.putString("mobile_number", "91" + mPhoneNumber);
                                            editor.putBoolean("status_flag", true);
                                            editor.commit();
                                            Intent i = new Intent(SplashActivity.this, Detact_Mobile_NumberActivity.class);
                                            i.putExtra("phone_number", mPhoneNumber);
                                            startActivity(i);
                                            finish();
                                        }
                                    } else {
                                        if(userInfoPref.getString("usertype","").equalsIgnoreCase("M"))
                                        {
                                            Intent i = new Intent(SplashActivity.this, MerchantLandingActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                        else
                                        {Intent i = new Intent(SplashActivity.this, HomeScreenActivity.class);

                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                } else {
                                    if(userInfoPref.getString("usertype","").equalsIgnoreCase("M"))
                                    {
                                        Intent i = new Intent(SplashActivity.this, MerchantLandingActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else
                                    {

                                        Intent i = new Intent(SplashActivity.this, HomeScreenActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            }
                        }
                    }, SPLASH_TIME_OUT);
                }
                else {
                    showUpdateDialog(upgradeMessage);
                }



            } else if (result.equalsIgnoreCase("Failure")) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(context.getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(context.getString(R.string.apidown));
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);
                // Setting OK Button
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
            else if(result.equalsIgnoreCase("Failure1"))
            {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(context.getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(downTimeMessage);
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

    private class checkVersion_url extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

//        String firstName, lastName, walletBalance;

        public checkVersion_url(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler serviceHandler = new WebServiceHandler(getApplication());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
//                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject MYjsonMainObj = new JSONObject(jsonStr);

//                        if (MYjsonMainObj.getString("responseStatus").equalsIgnoreCase("FALSE")) {
                        if (MYjsonMainObj.getString("responseStatus").equalsIgnoreCase("Success")) {

//                            responseMessage = jsonMainObj.getString("responseMessage");;
//                            //// System.out.println("Response+FALSE");

                            return "Success";

                        } else if (MYjsonMainObj.getString("responseStatus").equalsIgnoreCase("Failure")) {
//                            return MYjsonMainObj.getString("responseMessage");
                            return "Failure";

                        } else {

                            return "Failure1";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failure1";
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failure1";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure1";
            }



        }

        @Override
        protected void onPostExecute(String result) {

            if (result.equalsIgnoreCase("Success"))
            {
//                GetOffers g = new GetOffers(getApplicationContext());
//                g.execute(" https://app.mrupee.in:8443/mRupeeServiceNew/getOffers");

                String banner_url = getResources().getString(R.string.getmultibanner);
                GetBanner gb = new GetBanner(getApplicationContext());
                gb.execute(banner_url);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        String mPhoneNumber = "";
                        try {
                            TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                            mPhoneNumber = tMgr.getLine1Number();

                        } catch (Exception e) {
                            mPhoneNumber = "";
                        }

//                        //// System.out.println("PhoneNumber is " + mPhoneNumber);

                        if (pref.getBoolean("otpscreen", false)) {
                            Intent i = new Intent(SplashActivity.this, OTPActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("flag",pref.getBoolean("flag",false));
                            i.putExtra("session","1");
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            if (userInfoPref.getBoolean("new_user_registered_newapp", false) == false) {

                                if (pref.getBoolean("got_mobile_number_newapp", false) == false) {


                                    if (mPhoneNumber == null) {
//                                        //// System.out.println("PhoneNumber is " + mPhoneNumber);
                                        mPhoneNumber = "";
//                                        //// System.out.println("PhoneNumber is " + mPhoneNumber);
                                        editor.putString("mobile_number", "91" + mPhoneNumber);
                                        editor.putInt("status", 0);
                                        editor.commit();


                                        Toast.makeText(getApplicationContext(), "Mobile number not detected", Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(SplashActivity.this, Detact_Mobile_NumberActivity.class);
                                        i.putExtra("phone_number", mPhoneNumber);
                                        startActivity(i);
                                        finish();
                                    } else {

                                        editor.putString("mobile_number", "91" + mPhoneNumber);
                                        editor.putBoolean("status_flag", true);
                                        editor.commit();
                                        Intent i = new Intent(SplashActivity.this, Detact_Mobile_NumberActivity.class);
                                        i.putExtra("phone_number", mPhoneNumber);
                                        startActivity(i);
                                        finish();
                                    }
                                } else {
                                    if(userInfoPref.getString("usertype","").equalsIgnoreCase("M"))
                                    {
                                        Intent i = new Intent(SplashActivity.this, MerchantLandingActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else
                                    {
                                        Intent i = new Intent(SplashActivity.this, HomeScreenActivity.class);
                                        startActivity(i);
                                        finish();
                                    }

                                }
                            } else {
                                if(userInfoPref.getString("usertype","").equalsIgnoreCase("M"))
                                {
                                    Intent i = new Intent(SplashActivity.this, MerchantLandingActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                                else
                                {
                                    Intent i = new Intent(SplashActivity.this, HomeScreenActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }
                    }
                }, SPLASH_TIME_OUT);

            } else if (result.equalsIgnoreCase("Failure")) {
                showUpdateDialog("");


            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(context.getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(context.getString(R.string.apidown));
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);
                // Setting OK Button
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

    private class GetOffers extends AsyncTask<String, Void, String> {

        Context context;

        ArrayList<String> id,url;
        String bannerURL;

        public GetOffers(Context context) {
            this.context = context;

            id = new ArrayList<String>();
            url = new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(SplashActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

//                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        if(jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success"))
                        {
                            db.fun_deleteAll_tbl_offers();
                            JSONArray jsonArray = jsonMainObj.getJSONArray("lstGetOffersDetails");

                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                id.add(jsonObject.getString("couponID"));
                                url.add(jsonObject.getString("couponImage"));

//                                //// System.out.println("URL:::::::::"+jsonObject.getString("merchantUrl"));
//                                //// System.out.println("About Me:::::::::::"+jsonObject.getString("merchantAboutMe"));

                                db.fun_insert_tbl_offers(jsonObject.getString("couponID"), jsonObject.getString("couponImage"),jsonObject.getString("merchantUrl"),jsonObject.getString("merchantAboutMe"));
                            }

                        }





                        return "Success";

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

//            if (pDialog.isShowing())
//                pDialog.dismiss();

//            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {



                try {
//                    for(int i=0;i<url.size();i++) {
//
//                        DownloadOfferImage DI = new DownloadOfferImage(getApplicationContext(),id.get(i));
//                        DI.execute(url.get(i));
//                    }
                }
                catch (Exception e)
                {

                }


            } else if (result.equalsIgnoreCase("Failure")) {


                // Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {


            }

        }

    }

    private class DownloadOfferImage extends AsyncTask<String, Void, Bitmap> {

        Context context;

        String id;

        public DownloadOfferImage(Context context,String id) {

            this.context = context;
            this.id = id;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(SplashActivity.this);
            // mProgressDialog.setMessage("Loading...");
            // mProgressDialog.setIndeterminate(false);
            // // Show progressdialog
            // mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];
            Bitmap bitmap = null;

            // initilize the default HTTP client object
            final DefaultHttpClient client = new DefaultHttpClient();

            // forming a HttoGet request
            final HttpGet getRequest = new HttpGet(imageURL.replace(" ","%20"));
            try {

                HttpResponse response = client.execute(getRequest);

                // check 200 OK for success
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ImageDownloader", "Error " + statusCode
                            + " while retrieving bitmap from " + imageURL);
                    return null;

                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        // getting contents from the stream
                        inputStream = entity.getContent();

                        // decoding stream data back into image Bitmap that
                        // android understands
                        bitmap = BitmapFactory.decodeStream(inputStream);

                        return bitmap;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                // You Could provide a more explicit error message for
                // IOException
                getRequest.abort();
                Log.e("ImageDownloader", "Something went wrong while"
                        + " retrieving bitmap from " + e.toString());
            }
            //
            // Bitmap bitmap = null;
            // try {
            // // Download Image from URL
            // InputStream input = new java.net.URL(imageURL).openStream();
            // // Decode Bitmap
            // bitmap = BitmapFactory.decodeStream(input);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            try {
                // Set the bitmap into ImageView
                // image.setImageBitmap(result);
                // // Close progressdialog
                // mProgressDialog.dismiss();

                // image.setImageBitmap(result);

                // //// System.out.println("Height: " + result.getHeight() + "Width: "
                // + result.getWidth());
                if (result != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    result.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    db.updateOffers(stream.toByteArray(),id);

//                    String encodedImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
//
//
//                    editor.putString("banner", encodedImage);
//                    editor.commit();

                    // db.updateCategory(stream.toByteArray(), id);
                }
            }
            catch (Exception e)
            {

            }



        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        Context context;


        public DownloadImage(Context context) {

            this.context = context;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            // mProgressDialog = new ProgressDialog(SplashActivity.this);
            // mProgressDialog.setMessage("Loading...");
            // mProgressDialog.setIndeterminate(false);
            // // Show progressdialog
            // mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];
            Bitmap bitmap = null;

            // initilize the default HTTP client object
            final DefaultHttpClient client = new DefaultHttpClient();

            // forming a HttoGet request
            final HttpGet getRequest = new HttpGet(imageURL);
            try {

                HttpResponse response = client.execute(getRequest);

                // check 200 OK for success
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ImageDownloader", "Error " + statusCode
                            + " while retrieving bitmap from " + imageURL);
                    return null;

                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        // getting contents from the stream
                        inputStream = entity.getContent();

                        // decoding stream data back into image Bitmap that
                        // android understands
                        bitmap = BitmapFactory.decodeStream(inputStream);

                        return bitmap;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                // You Could provide a more explicit error message for
                // IOException
                getRequest.abort();
                Log.e("ImageDownloader", "Something went wrong while"
                        + " retrieving bitmap from " + e.toString());
            }
            //
            // Bitmap bitmap = null;
            // try {
            // // Download Image from URL
            // InputStream input = new java.net.URL(imageURL).openStream();
            // // Decode Bitmap
            // bitmap = BitmapFactory.decodeStream(input);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            try {
                // Set the bitmap into ImageView
                // image.setImageBitmap(result);
                // // Close progressdialog
                // mProgressDialog.dismiss();

                // image.setImageBitmap(result);

                // //// System.out.println("Height: " + result.getHeight() + "Width: "
                // + result.getWidth());
                if (result != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    result.compress(Bitmap.CompressFormat.PNG, 100, stream);


                    String encodedImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);


                    editor.putString("banner", encodedImage);
                    editor.commit();

                    // db.updateCategory(stream.toByteArray(), id);
                }
            }
            catch (Exception e)
            {

            }



        }
    }

    public static boolean isRooted() {

        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }

        // try executing commands
        return canExecuteCommand("/system/xbin/which su") ||
                canExecuteCommand("/system/bin/which su") ||
                canExecuteCommand("which su");
    }

    // executes a command on the system
    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully=false;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }

        return executedSuccesfully;
    }

    public static class RootUtil {
        public static boolean isDeviceRooted() {
            return
//                    checkRootMethod1() ||
                            checkRootMethod2() ||
                            checkRootMethod3();
        }

        private static boolean checkRootMethod1() {
            String buildTags = android.os.Build.TAGS;
            System.out.println("buildTags"+buildTags);
            return buildTags != null && buildTags.contains("test-keys");
        }

        private static boolean checkRootMethod2() {
            String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                    "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
            for (String path : paths) {
                if (new File(path).exists()) return true;
            }
            return false;
        }

        private static boolean checkRootMethod3() {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                if (in.readLine() != null) return true;
                return false;
            } catch (Throwable t) {
                return false;
            } finally {
                if (process != null) process.destroy();
            }
        }
    }

}