package com.tcs.mmpl.customer.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.service.MarketService;
import com.bumptech.glide.Glide;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.Purse;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MerchantLandingActivity extends AppCompatActivity {
    LinearLayout linView1,linView2,linView3,linView4,linView5,linView6,linView7;
    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;
    private String bannerName = "", bannerUrl = "";
    ImageView imgBanner;
    String mdn, imei,jsonStr1;
    MyConnectionHelper db;
    TextView txtmechbal;
    Typeface typeface;
    FontClass fontclass=new FontClass();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_merchant_landing);
        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();
        connectionDetector = new ConnectionDetector(getApplicationContext());
        db = new MyConnectionHelper(getApplicationContext());
        linView1 = (LinearLayout)findViewById(R.id.linView1);
        linView2 = (LinearLayout)findViewById(R.id.linView2);
        linView3 = (LinearLayout)findViewById(R.id.linView3);
        linView4 = (LinearLayout)findViewById(R.id.linView4);
        linView5 = (LinearLayout)findViewById(R.id.linView5);
        linView6 = (LinearLayout)findViewById(R.id.linView6);
        linView7 = (LinearLayout)findViewById(R.id.linView7);
        imgBanner = (ImageView) findViewById(R.id.imgBanner);
        txtmechbal = (TextView) findViewById(R.id.txtmechbal);
        RelativeLayout mainlinear =(RelativeLayout)findViewById(R.id.mainlinear);

        mdn = pref.getString("mobile_number", "");
        imei = getIMEI(getApplicationContext());

        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        txtmechbal.setText(" ₹ "+userInfoPref.getString("walletbalance",""));
        linView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

//                    Intent i = new Intent(getApplicationContext(), View_My_Profile.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);

                    Intent i = new Intent(getApplicationContext(), MerchantProfileActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
//                    ArrayList<Purse> purseArrayList = new ArrayList<Purse>();
//                    Purse purse = new Purse();
//                    purse.setPurseImage("https://app.mrupee.in:8443/purse/cash.png");
//                    purse.setPurseName("Cash");
//                    purse.setPurseBalance(userInfoPref.getString("walletbalance",""));
//                    purse.setPurseTextColor("#FFFFFF");
//                    purse.setPurseBackgroundColor("#0cc2f0");
//                    purseArrayList.add(purse);
//
//                    Intent i = new Intent(getApplicationContext(), PurseSubBalanceActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    i.putExtra("purseArrayList", purseArrayList);
//                    startActivity(i);
//                    if (connectionDetector.isConnectingToInternet()) {
//
////                        AlertBuilder alertBuilder = new AlertBuilder(MerchantLandingActivity.this);
////                        alertBuilder.showAlert("Message");
//                        GetPurse getPurse = new GetPurse(getApplicationContext());
//                        getPurse.execute(getResources().getString(R.string.purse_url) + "?MDN=" + pref.getString("mobile_number", ""));
//                    } else {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
//                    }
                } else {
                    AlertBuilder alert = new AlertBuilder(MerchantLandingActivity.this);
                    alert.newUser();
                }
            }
        });
        linView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
//                    Intent i = new Intent(getApplicationContext(), TxnHistoryMerchantActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
                    loadingPopup();

                } else {
                    AlertBuilder alert = new AlertBuilder(MerchantLandingActivity.this);
                    alert.newUser();
                }
            }
        });

        linView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                    Intent i = new Intent(getApplicationContext(), DetailedStatementyMerchantActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    AlertBuilder alert = new AlertBuilder(MerchantLandingActivity.this);
                    alert.newUser();
                }
            }
        });
        linView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                    Intent i1 = new Intent(getApplicationContext(), ChangeMPINActivity.class);
                    i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    i1.putExtra("status", "0");
                    startActivity(i1);
                } else {
                    AlertBuilder alert = new AlertBuilder(MerchantLandingActivity.this);
                    alert.newUser();
                }

            }
        });

        linView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

                    Intent i = new Intent(getApplicationContext(), WebActivity.class);
                    i.putExtra("option", "MERCONTACT");
                    startActivity(i);
//                    Intent i = new Intent(getApplicationContext(), View_My_Profile.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
                } else {
                    AlertBuilder alert = new AlertBuilder(MerchantLandingActivity.this);
                    alert.newUser();
                }
            }
        });
        linView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                    Intent intent = new Intent(getApplicationContext(), PullMoneyActivity.class);
                    startActivity(intent);
                } else {
                    AlertBuilder alert = new AlertBuilder(MerchantLandingActivity.this);
                    alert.newUser();
                }

            }
        });
        linView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), WebActivity.class);
                i.putExtra("option", "MERCONTACT");
                startActivity(i);
            }
        });

        if (connectionDetector.isConnectingToInternet()) {

            GetUserStatus getUserStatus = new GetUserStatus(getApplicationContext());
            getUserStatus.execute(getResources().getString(R.string.getUserStatus));
        } else {

            editor.putInt("status", 0);
            editor.commit();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

        }




        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();
        if (c1.moveToNext()) {
            do {
                if (c1.getString(3).equalsIgnoreCase("RETHOME")) {
                    bannerName=c1.getString(0);
                    bannerUrl=c1.getString(1);
                    break;
                }
            } while (c1.moveToNext());


        }

        c1.close();

        Glide.with(MerchantLandingActivity.this).load(bannerUrl).placeholder(R.drawable.default_banner).into(imgBanner);
        imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);

        // System.out.println(bannerUrl+"/n bannerUrl /n"+bannerName+"/n bannerName /n");

        imgBanner.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                String bannerURL = getResources().getString(R.string.bannerlink)+"?Type=bannerName";
                GetBannerLink getBannerLink = new GetBannerLink(getApplicationContext());
                getBannerLink.execute(bannerURL);
           }
       });

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

    private void showRateDialog() {

        if (userInfoPref.getString("rate_us_status", "NOTDONE").trim().equalsIgnoreCase("NOTDONE")) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MerchantLandingActivity.this);
            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.rate_title));
            // Setting Dialog Message
            alertDialog.setMessage(userInfoPref.getString("rate_message", getResources().getString(R.string.rate_explanation)));
            // Setting Icon to Dialog
            // alertDialog.setIcon(R.drawable.tick);
            // Setting OK Button


            alertDialog.setNegativeButton(getResources().getString(R.string.rate_now), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    dialog.cancel();
                    userInfoEditor.putString("rate_us_status", "DONE");
                    userInfoEditor.commit();
                    final String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("market://details?id="
                                        + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id="
                                        + appPackageName)));
                    }

                    String rateURL = getResources().getString(R.string.rateus) + "?MDN=" + pref.getString("mobile_number", "") + "&rateStatus=RATE";
                    RateUs rateUs = new RateUs(getApplicationContext());
                    rateUs.execute(rateURL);

                }
            });

            alertDialog.setPositiveButton(getResources().getString(R.string.rate_later), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    dialog.cancel();
                    userInfoEditor.putString("rate_us_status", "DONE");
                    userInfoEditor.commit();
                    String rateURL = getResources().getString(R.string.rateus) + "?MDN=" + pref.getString("mobile_number", "") + "&rateStatus=LATER";
                    RateUs rateUs = new RateUs(getApplicationContext());
                    rateUs.execute(rateURL);


                }
            });
            alertDialog.setNeutralButton(getResources().getString(R.string.rate_never), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    dialog.cancel();

                    userInfoEditor.putString("rate_us_status", "DONE");
                    userInfoEditor.commit();

                    String rateURL = getResources().getString(R.string.rateus) + "?MDN=" + pref.getString("mobile_number", "") + "&rateStatus=NOTHANKS";
                    RateUs rateUs = new RateUs(getApplicationContext());
                    rateUs.execute(rateURL);

                }
            });


            // Showing Alert Message
            alertDialog.setCancelable(false);
            alertDialog.show();


        }
    }

    private class GetPurse extends AsyncTask<String, Void, String> {

        Context context;

        ProgressDialog pDialog;
        ArrayList<Purse> purseArrayList;
        private String responseMessage = "";


        public GetPurse(Context context) {
            this.context = context;

            purseArrayList = new ArrayList<Purse>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MerchantLandingActivity.this);
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

                Log.d("response", jsonStr);

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

                                if (jsonObject1.getString("purseName").trim().equalsIgnoreCase("Cash")) {
                                    userInfoEditor.putString("walletbalance", jsonObject1.getString("purseBalance").trim());
                                    userInfoEditor.commit();
                                }
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

                AlertBuilder alertBuilder = new AlertBuilder(MerchantLandingActivity.this);
                alertBuilder.showAlert(responseMessage);
            } else {
                AlertBuilder alertBuilder = new AlertBuilder(MerchantLandingActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));
            }
        }

    }

    private class GetUserStatus extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance, responseMessage;

        public GetUserStatus(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MerchantLandingActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("MDN", pref.getString("mobile_number", ""));
                jsonObject.accumulate("IMEI", getIMEI(getApplicationContext()));
                jsonObject.accumulate("Model", getDeviceName());
                jsonObject.accumulate("OS", getOS());
                jsonObject.accumulate("Network", getNetworkClass(getApplicationContext()));


                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(MerchantLandingActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                Log.d("response", jsonStr);

                // System.out.println("jsonStr"+jsonStr);


                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            firstName = jsonMainObj.getString("firstName");
                            lastName = jsonMainObj.getString("lastName");
                            walletBalance = jsonMainObj.getString("walletBalance");

//                            bannerName = jsonMainObj.getString("bannerName");
//                            bannerUrl = jsonMainObj.getString("bannerDownloadLink");

                            userInfoEditor.putString("firstname", firstName);
                            userInfoEditor.putString("lastname", lastName);
                            userInfoEditor.putString("walletbalance", walletBalance);
                            userInfoEditor.putString("usertype", jsonMainObj.getString("userType"));
                            userInfoEditor.putBoolean("new_user_registered_newapp", true);
                            userInfoEditor.putString("emailId", jsonMainObj.getString("emailId"));
                            userInfoEditor.putString("account", jsonMainObj.getString("account"));
                            userInfoEditor.putString("kycstatus", jsonMainObj.getString("kycstatus"));
                            userInfoEditor.putString("billdeskFlag", jsonMainObj.getString("billdeskFlag"));
                            userInfoEditor.putString("userID", jsonMainObj.getString("userID"));
                            userInfoEditor.commit();


                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
//                            bannerName = jsonMainObj.getString("bannerName");
//                            bannerUrl = jsonMainObj.getString("bannerDownloadLink");
                            return jsonMainObj.getString("responseMessage");

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("BLOCK1")) {
                            responseMessage = jsonMainObj.getString("responseMessage");

                            return "BLOCK1";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("BLOCK2")) {
                            responseMessage = jsonMainObj.getString("responseMessage");
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

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + bannerUrl);

            if (result.equalsIgnoreCase("Success")) {


                showRateDialog();
//                AppRater appRater = new AppRater(HomeScreenActivity.this);
//                appRater.setDaysBeforePrompt(3);
//                appRater.setLaunchesBeforePrompt(7);
//                appRater.setPhrases(R.string.rate_title, R.string.rate_explanation, R.string.rate_now, R.string.rate_later, R.string.rate_never);
//                appRater.show();
                txtmechbal.setText(" ₹ "+userInfoPref.getString("walletbalance",""));
                editor.putInt("status", 2);
                editor.putBoolean("status_flag", false);
                editor.putBoolean("got_mobile_number_newapp", true);
                editor.commit();

//                linWelcome.setVisibility(View.VISIBLE);
//                linDetails.setVisibility(View.VISIBLE);

//                txtBalance.setText(userInfoPref.getString("walletbalance", ""));


            } else if (result.equalsIgnoreCase("Failure")) {

                editor.putInt("status", 0);
                editor.putBoolean("status_flag", true);
                editor.putBoolean("got_mobile_number_newapp", true);
                editor.commit();

                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

                    //  txtWelcome.setText("Hi " + userInfoPref.getString("firstname", ""));
//                    txtBalance.setText( userInfoPref.getString("walletbalance", ""));
                }

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else if (result.equalsIgnoreCase("BLOCK1")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MerchantLandingActivity.this);
                alertDialogBuilder.setTitle("Application Blocked");
                alertDialogBuilder
                        .setMessage(responseMessage)
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        String Bolcked_url = getApplicationContext().getResources().getString(R.string.updateDeviceStatus) + "?MDN=" + mdn + "&IMEI=" + imei + "&ACTION=" + "BLOCK1";
                                        Bolcked Bolcked1 = new Bolcked(getApplicationContext());
                                        Bolcked1.execute(Bolcked_url);
                                    }
                                })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            } else if (result.equalsIgnoreCase("BLOCK2")) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeScreenActivity.this);
//                alertDialogBuilder.setTitle("Application Blocked");
//                alertDialogBuilder
//                        .setMessage(getResources().getString(R.string.block2))
//                        .setCancelable(false)
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//
//                                        String Bolcked_url=getApplicationContext().getResources().getString(R.string.updateDeviceStatus) + "?MDN="+mdn+"&IMEI="+imei+"&ACTION="+"BLOCK2";
//                                        Bolcked Bolcked1 = new Bolcked(getApplicationContext());
//                                        Bolcked1.execute(Bolcked_url);
//                                    }
//                                })
//
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                                android.os.Process.killProcess(android.os.Process.myPid());
//                                System.exit(1);
//                            }
//                        });
//
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();

                AlertBuilder alertBuilder = new AlertBuilder(MerchantLandingActivity.this);
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

                editor.putInt("status", 1);
                editor.putBoolean("status_flag", false);
                editor.putBoolean("got_mobile_number_newapp", true);
                editor.commit();

                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                    // txtWelcome.setText("Hi " + userInfoPref.getString("firstname", ""));
//                    txtBalance.setText( userInfoPref.getString("walletbalance", ""));
                }

            }

            try {
                MarketService ms = new MarketService(MerchantLandingActivity.this);
                ms.level(MarketService.REVISION).checkVersion();
            } catch (Exception e) {

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
            pDialog = new ProgressDialog(MerchantLandingActivity.this);
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

    private class RateUs extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

//        String firstName, lastName, walletBalance;

        public RateUs(Context context) {
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


                return "Success";

            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure1";
            }


        }

        @Override
        protected void onPostExecute(String result) {


        }

    }

    private class GetBannerLink extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;
        private ProgressDialog pDialog;
        private String activityName;
        private String urlLink;

        public GetBannerLink(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(HomeScreenActivity.this);
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler serviceHandler = new WebServiceHandler(getApplication());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
                // System.out.println(jsonStr);
                JSONObject jsonObject = new JSONObject(jsonStr);

                activityName = jsonObject.getString("activityName");
                urlLink = jsonObject.getString("externalURL");


                return "Success";

            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure1";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (!activityName.equalsIgnoreCase("NA") && !urlLink.equalsIgnoreCase("NA")) {
                try {

                    String className = getPackageName() + ".Activity." + activityName;
                    Class<?> myClass = Class.forName(className);
                    //Activity activity = (Activity) myClass.newInstance();
                    Intent re = new Intent(getApplicationContext(), myClass);
                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    re.putExtra("option", urlLink);
                    startActivity(re);
                } catch (Exception e) {
                    e.printStackTrace();
                    //// System.out.println("exception " + e.getMessage());
                }
            } else if (!activityName.equalsIgnoreCase("NA") && urlLink.equalsIgnoreCase("NA")) {
                try {
                    String className = getPackageName() + ".Activity." + activityName;
                    Class<?> myClass = Class.forName(className);
                    //Activity activity = (Activity) myClass.newInstance();
                    Intent re = new Intent(getApplicationContext(), myClass);
                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(re);
                } catch (Exception e) {

                }

            } else if (activityName.equalsIgnoreCase("NA") && !urlLink.equalsIgnoreCase("NA")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink)));
            }


        }

    }

    private void loadingPopup(){

        LayoutInflater layoutInflater = (LayoutInflater) MerchantLandingActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_mpin_layout, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final EditText edtMpin = (EditText) popupView.findViewById(R.id.edittext_edit_popup);

        Button btnCancel = (Button) popupView.findViewById(R.id.button_pop_no);
        Button btnSubmit = (Button) popupView.findViewById(R.id.button_pop_yes);

        edtMpin.setTypeface(typeface);
        btnCancel.setTypeface(typeface);
        btnSubmit.setTypeface(typeface);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
//                onBackPressed();
            }
        });

        btnSubmit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMpin.getText().toString().trim().equalsIgnoreCase("")|| edtMpin.getText().toString().trim().length() < 4) {
                    Toast.makeText(MerchantLandingActivity.this, getResources().getString(R.string.mpin), Toast.LENGTH_LONG).show();
                } else {
                    popupWindow.dismiss();
                    String MPIN= edtMpin.getText().toString().trim();
                    String transurl = getResources().getString(R.string.getTxnHistoryMerchant) + "?MDN=" + pref.getString("mobile_number", "")+"&MPIN="+MPIN;
                    GetAccountStatement getAccountStatement = new GetAccountStatement(getApplicationContext());
                    getAccountStatement.execute(transurl);
                }

            }
        });

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }

    private class GetAccountStatement extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        ProgressDialog pDialog;

        public GetAccountStatement(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MerchantLandingActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(MerchantLandingActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
                jsonStr1=jsonStr;
                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
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
                    Intent i = new Intent(getApplicationContext(), TxnHistoryMerchantActivity.class);
                    i.putExtra("jsonStr1", jsonStr1);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
            } else if (result.equalsIgnoreCase("Failure")) {

                try {
                    AlertBuilder alertBuilder = new AlertBuilder(MerchantLandingActivity.this);
                    alertBuilder.showAlert(getResources().getString(R.string.apidown));
                }
                catch (Exception e)
                {

                }

            } else {
                AlertBuilder alertBuilder = new AlertBuilder(MerchantLandingActivity.this);
                alertBuilder.showAlert(result);
//                txtTransactionError.setVisibility(View.VISIBLE);
//                txtTransactionError.setText(result);
            }

        }

    }
}
