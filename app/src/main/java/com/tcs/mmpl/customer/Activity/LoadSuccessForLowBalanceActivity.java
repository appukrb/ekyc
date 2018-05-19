package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Address;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.Deals;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.RestaurantInvoiceList;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadSuccessForLowBalanceActivity extends Activity {

    TextView txtStatus;
    Button btnProceed,btnHome;
    String msg;

    FontClass fontclass = new FontClass();
    Typeface typeface;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;

    private ConnectionDetector connectionDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_success_for_low_balance);

        LinearLayout mainlinear = (LinearLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        msg = getIntent().getStringExtra("status");

        txtStatus = (TextView)findViewById(R.id.txtStatus);
        btnProceed = (Button)findViewById(R.id.btnProceed);
        btnHome = (Button)findViewById(R.id.btnHome);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getApplicationContext(),HomeScreenActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionDetector.isConnectingToInternet())
                {
                    if(userInfoPref.getString("transaction_flag","1").equalsIgnoreCase("1")) {
                        Transaction transaction = new Transaction(getApplicationContext());
                        transaction.execute(userInfoPref.getString("transaction_url", ""));
                    }
                    else if(userInfoPref.getString("transaction_flag","1").equalsIgnoreCase("3"))
                    {
                        AddGCIOrder addGCIOrder = new AddGCIOrder(getApplicationContext());
                        addGCIOrder.execute(userInfoPref.getString("transaction_url", ""));
                    }
                    else if(userInfoPref.getString("transaction_flag","1").equalsIgnoreCase("4"))
                    {

                        RessyPayment ressyPayment = new RessyPayment(getApplicationContext());
                        ressyPayment.execute(userInfoPref.getString("transaction_url", ""));

                    }
                    else
                    {
                        TransferToBankAsync transferToBankAsync = new TransferToBankAsync(getApplicationContext());
                        transferToBankAsync.execute(userInfoPref.getString("transaction_url", ""));


                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }
            }
        });

        if(connectionDetector.isConnectingToInternet())
        {

            GetUserStatus getUserStatus = new GetUserStatus(getApplicationContext());
            getUserStatus.execute(getResources().getString(R.string.getUserStatus));



                String url = getResources().getString(R.string.loadWalletConfirm);
                LoadWalletConfirm loadWalletConfirm = new LoadWalletConfirm(getApplicationContext());
                loadWalletConfirm.execute(url);

        }
        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {


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
            pDialog = new ProgressDialog(LoadSuccessForLowBalanceActivity.this);
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
                jsonObject.accumulate("Network",getNetworkClass(getApplicationContext()));



                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(LoadSuccessForLowBalanceActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);


                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            firstName = jsonMainObj.getString("firstName");
                            lastName = jsonMainObj.getString("lastName");
                            walletBalance = jsonMainObj.getString("walletBalance");

                            if(userInfoPref.getString("walletbalance","0").equalsIgnoreCase(walletBalance))
                            {
                                return "Failure";

                            }


                            userInfoEditor.putString("firstname", firstName);
                            userInfoEditor.putString("lastname", lastName);
                            userInfoEditor.putString("walletbalance", walletBalance);
                            userInfoEditor.putString("usertype", jsonMainObj.getString("userType"));
                            userInfoEditor.putBoolean("new_user_registered_newapp", true);
                            userInfoEditor.putString("emailId", jsonMainObj.getString("emailId"));
                            userInfoEditor.putString("account", jsonMainObj.getString("account"));
                            userInfoEditor.putString("kycstatus",jsonMainObj.getString("kycstatus"));
                            userInfoEditor.putString("billdeskFlag", jsonMainObj.getString("billdeskFlag"));
                            userInfoEditor.putString("userID", jsonMainObj.getString("userID"));
                            userInfoEditor.commit();


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

//            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {

                txtStatus.setText("Your wallet has been loaded");
                btnProceed.setVisibility(View.VISIBLE);

            } else if (result.equalsIgnoreCase("Failure")) {

                txtStatus.setText("Sorry...Your wallet has not been loaded.Please try after sometime.");
                btnProceed.setVisibility(View.GONE);

            } else {

                txtStatus.setText("Sorry...Your wallet has not been loaded.Please try after sometime.");
                btnProceed.setVisibility(View.GONE);
            }


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


    private class Transaction extends AsyncTask<String, Void, String> {

        Context context;

        String responseMessage;
        ProgressDialog pDialog;

        public Transaction(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoadSuccessForLowBalanceActivity.this);
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

                            if(jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                            {
                                new LocalNotification(LoadSuccessForLowBalanceActivity.this,jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationTitle)),jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationMessage))).sendNotification();
                            }

                            return "Success";

                        } else {
                            responseMessage = jsonMainObj.getString("responseMessage");
                            return "Failed";
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

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoadSuccessForLowBalanceActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(context.getString(R.string.display_app_name));
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
                        Intent i =new Intent(getApplicationContext(),HomeScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();

            }
            else if(result.equalsIgnoreCase("Failed"))
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoadSuccessForLowBalanceActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(context.getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(responseMessage);
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);
                // Setting OK Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        finish();
                        Intent i =new Intent(getApplicationContext(),HomeScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
            else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoadSuccessForLowBalanceActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(context.getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);
                // Setting OK Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        finish();
                        Intent i =new Intent(getApplicationContext(),HomeScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();
            }

        }

    }


    private class TransferToBankAsync extends AsyncTask<String, Void, String> {

        String  remark;
        Context context;

        ProgressDialog pDialog;
        public TransferToBankAsync(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoadSuccessForLowBalanceActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(LoadSuccessForLowBalanceActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);


                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {


                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("status").equalsIgnoreCase("SUCCESS")) {


                            remark = jsonMainObj.getString("remark");

                            if(jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                            {
                                new LocalNotification(LoadSuccessForLowBalanceActivity.this,jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationTitle)),jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationMessage))).sendNotification();
                            }

                            return "Success";

                        } else if (jsonMainObj.getString("status").equalsIgnoreCase("ERROR")) {


                            remark = jsonMainObj.getString("remark");

                            return "Success";

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

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoadSuccessForLowBalanceActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(context.getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(remark);
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);
                // Setting OK Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        finish();
                        Intent i =new Intent(getApplicationContext(),HomeScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();

            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoadSuccessForLowBalanceActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(context.getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);
                // Setting OK Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        finish();
                        Intent i =new Intent(getApplicationContext(),HomeScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();


            } else {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoadSuccessForLowBalanceActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(context.getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);
                // Setting OK Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        finish();
                        Intent i =new Intent(getApplicationContext(),HomeScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();


            }

        }

    }

    private class AddGCIOrder extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;


        public AddGCIOrder(Context context) {
            this.context = context;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoadSuccessForLowBalanceActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler webServiceHandler = new WebServiceHandler(LoadSuccessForLowBalanceActivity.this);
                String jsonStr = webServiceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Deno::::" + jsonStr);
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
                if (jsonObject.getString("responseStatus").trim().equalsIgnoreCase("Success")) {

                    if(jsonObject.getString(getApplicationContext().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                    {
                        new LocalNotification(LoadSuccessForLowBalanceActivity.this,jsonObject.getString(getApplicationContext().getResources().getString(R.string.notificationTitle)),jsonObject.getString(getApplicationContext().getResources().getString(R.string.notificationMessage))).sendNotification();
                    }

                    AlertBuilder alertBuilder = new AlertBuilder(LoadSuccessForLowBalanceActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(jsonObject.getString("responseMessage"));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            Intent intent = new Intent(getApplicationContext(),HomeScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //getActivity().finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();


                } else if (jsonObject.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {
                    AlertBuilder alertBuilder = new AlertBuilder(LoadSuccessForLowBalanceActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(jsonObject.getString("responseMessage"));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            Intent intent = new Intent(getApplicationContext(),HomeScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //getActivity().finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                } else {
                    AlertBuilder alertBuilder = new AlertBuilder(LoadSuccessForLowBalanceActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            Intent intent = new Intent(getApplicationContext(),HomeScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //getActivity().finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            } catch (Exception e) {

                e.printStackTrace();

            }


        }

    }

    private class RessyPayment extends AsyncTask<String, Void, String> {

        private Context context;
        private ProgressDialog pDialog;
        RestaurantInvoiceList item = new RestaurantInvoiceList();

        public RessyPayment(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoadSuccessForLowBalanceActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(LoadSuccessForLowBalanceActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);


                // Create a Pattern object
                Pattern r = Pattern.compile("[,]");
                // Now create matcher object.
                Matcher m;
                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("success").equalsIgnoreCase("true")) {



                            JSONObject obj = new JSONObject(jsonMainObj.getString("invoice"));
                            JSONObject jsonObjectRestaurant = new JSONObject(obj.getString("restaurant"));
                            item.setHotelName(jsonObjectRestaurant.getString("name"));
                            item.setTotal_bill(obj.getString("total_bill"));
                            item.setDiscount(obj.getString("discount"));
                            item.setNet_paid(obj.getString("net_paid"));
                            item.setTransaction_id(obj.getString("transaction_id"));
                            item.setPaid_time(obj.getString("paid_time"));


                            JSONObject jsonObject = new JSONObject(jsonObjectRestaurant.getString("address"));
                            Address address = new Address();
                            address.setAddress(jsonObject.getString("address"));
                            address.setLocality(jsonObject.getString("locality"));
                            address.setCity(jsonObject.getString("city"));
                            address.setLatitude(jsonObject.getString("latitude"));
                            address.setLongitude(jsonObject.getString("longitude"));
                            item.setAddress(address);


                            JSONObject jsonObjectCoupon = new JSONObject(obj.getString("coupon"));
                            item.setCoupon_code(jsonObjectCoupon.getString("coupon_code"));
                            item.setState(jsonObjectCoupon.getString("state"));

                            JSONObject jsonObjectDeals = new JSONObject(jsonObjectCoupon.getString("deal"));
                            Deals deals = new Deals();
                            deals.setId(jsonObjectDeals.getString("id"));
                            deals.setRestaurant_id(jsonObjectDeals.getString("restaurant_id"));
                            deals.setLocality(jsonObjectDeals.getString("locality"));
                            deals.setCity(jsonObjectDeals.getString("city"));
                            deals.setTitle(jsonObjectDeals.getString("title"));
                            deals.setTerms_and_conditions(jsonObjectDeals.getString("terms_and_conditions"));
                            deals.setStart_time(jsonObjectDeals.getString("start_time"));
                            deals.setEnd_time(jsonObjectDeals.getString("end_time"));
                            deals.setNotes(jsonObjectDeals.getString("notes"));

                            item.setDeal(deals);


                            return "Success";


                        } else if (jsonMainObj.getString("success").equalsIgnoreCase("false")) {
                            return jsonMainObj.getString("message");
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
                e.printStackTrace();
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {


            if(pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {

                Intent intent = new Intent(getApplicationContext(),RestaurantInvoiceActivity.class);
                intent.putExtra("invoice_details",item);
                intent.putExtra("expiry_date", userInfoPref.getString("coupon_expiryDate", ""));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(LoadSuccessForLowBalanceActivity.this).create();
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();


                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(LoadSuccessForLowBalanceActivity.this).create();
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();


                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            }

        }

    }


    private class LoadWalletConfirm extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;

        public LoadWalletConfirm(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

//            pDialog = new ProgressDialog(LoadSuccessForLowBalanceActivity.this);
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("msg", msg));

                WebServiceHandler serviceHandler = new WebServiceHandler(LoadSuccessForLowBalanceActivity.this, nameValuePairs);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST1);

                return jsonStr;
            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }

        }

        @Override
        protected void onPostExecute(String result) {

//            if(pDialog.isShowing())
//                pDialog.dismiss();
//
//            if (result.equalsIgnoreCase("Failure")) {
//                linDesc.setVisibility(View.GONE);
//                imgSmiley.setBackgroundResource(R.drawable.failure);
//                txtStatus.setText(getResources().getString(R.string.apidown));
//
//            } else {
//
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//
//                    if (jsonObject.getString("responseStatus").equalsIgnoreCase("Success")) {
//                        try {
//                            linDesc.setVisibility(View.VISIBLE);
//                            imgSmiley.setBackgroundResource(R.drawable.success);
//                            txtStatus.setText(jsonObject.getString("responseMessage"));
//                            txtNumber.setText(jsonObject.getString("mobileNo"));
//                            txtTransactionID.setText(jsonObject.getString("txnId"));
//                            txtTransactionDate.setText(jsonObject.getString("txnDate"));
//                        }
//                        catch (Exception e)
//                        {
//
//                        }
//
//                    } else {
//                        try {
//                            linDesc.setVisibility(View.VISIBLE);
//                            imgSmiley.setBackgroundResource(R.drawable.failure);
//                            txtStatus.setText(jsonObject.getString("responseMessage"));
//                            txtNumber.setText(jsonObject.getString("mobileNo"));
//                            txtTransactionID.setText(jsonObject.getString("txnId"));
//                            txtTransactionDate.setText(jsonObject.getString("txnDate"));
//                        }
//                        catch (Exception e)
//                        {
//
//                        }
//
//                    }
//
//                } catch (Exception e) {
//
//                }
//            }

        }

    }

}
