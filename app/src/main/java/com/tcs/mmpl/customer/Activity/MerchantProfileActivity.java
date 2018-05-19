package com.tcs.mmpl.customer.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.Profile;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class MerchantProfileActivity extends AppCompatActivity {
    RelativeLayout mainlinear;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    TextView txtUserName, txtUserNumber, txtmerchemaild, txtmerchaccountNumber, txtmerchtype,txtmerchaddress,txtmerchcontactPerson;
    LinearLayout linProfile;
    String dob="",emailid,mpin;
    private String bannerName = "", bannerUrl = "";
    MyConnectionHelper db;
    ImageView edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_profile);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
//        // System.out.println("emailId"+userInfoPref.getString("emailId", ""));
        linProfile = (LinearLayout) findViewById(R.id.linProfile);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtUserNumber = (TextView) findViewById(R.id.txtUserNumber);
        txtmerchcontactPerson = (TextView) findViewById(R.id.txtmerchcontactPerson);
        txtmerchaddress = (TextView) findViewById(R.id.txtmerchaddress);
        txtmerchtype = (TextView) findViewById(R.id.txtmerchtype);
        txtmerchaccountNumber = (TextView) findViewById(R.id.txtmerchaccountNumber);
        txtmerchemaild = (TextView) findViewById(R.id.txtmerchemaild);
        ImageView imgBanner = (ImageView) findViewById(R.id.imgBanner);

        String profile_url = getResources().getString(R.string.merchantProfile) + "?MDN=" + pref.getString("mobile_number", "");
        GetProfile gb = new GetProfile(getApplicationContext());
        gb.execute(profile_url);

        db = new MyConnectionHelper(getApplicationContext());

        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();

        if (c1.moveToNext()) {

            do {

                if(userInfoPref.getString("usertype","").equalsIgnoreCase("M"))
                {
                    if (c1.getString(3).equalsIgnoreCase("RETPROFILE")) {
                        bannerName=c1.getString(0);
                        bannerUrl=c1.getString(1);
                        break;
                    }
                }
                else
                {

                    if (c1.getString(3).equalsIgnoreCase("LAND")) {
                        bannerName=c1.getString(0);
                        bannerUrl=c1.getString(1);
                        break;
                    }

                }

            } while (c1.moveToNext());
        }

        c1.close();
//        String banner = pref.getString("banner", "");
        // System.out.println(bannerUrl+"/n bannerUrl /n"+bannerName+"/n bannerName /n");

        Glide.with(MerchantProfileActivity.this).load(bannerUrl).into(imgBanner);
        imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
//        if(!banner.trim().isEmpty()) {
//            byte[] byteArray = Base64.decode(banner, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
//                    byteArray.length);
//            imgBanner.setImageBitmap(bitmap);
//            imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
//        }

        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bannerURL = getResources().getString(R.string.bannerlink)+"?Type="+bannerName;
                GetBannerLink getBannerLink = new GetBannerLink(getApplicationContext());
                getBannerLink.execute(bannerURL);
                // System.out.println(bannerUrl+"/n bannerUrl /n"+bannerName+"/n bannerName /n");
                // System.out.println(bannerURL+bannerURL);


            }
        });
    }

    private class GetProfile extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private Profile profile;

        public GetProfile(Context context) {
            this.context = context;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MerchantProfileActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                WebServiceHandler serviceHandler = new WebServiceHandler(MerchantProfileActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {

                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success")) {

                            profile = new Profile();
                            profile.setFirstName(jsonMainObj.getString("name"));
                            profile.setMobileNumber(jsonMainObj.getString("mercahntMobieNumber"));
                            profile.setAccountType(jsonMainObj.getString("type"));
                            profile.setLastname(jsonMainObj.getString("contactPerson"));
                            profile.setAmount(jsonMainObj.getString("accountNumber"));
                            profile.setDob(jsonMainObj.getString("address"));
                            profile.setEmail(userInfoPref.getString("emailId", ""));
//                            // System.out.println("emailId"+userInfoPref.getString("emailId", ""));
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
                linProfile.setVisibility(View.VISIBLE);

                if(!profile.getMobileNumber().trim().equalsIgnoreCase(""))
                    txtUserNumber.setText(profile.getMobileNumber());
                else
                    txtUserNumber.setText("-");

                if(!profile.getDob().trim().equalsIgnoreCase(""))
//                    edittext_edit_dob.setText(profile.getDob());
                    dob=profile.getDob();

//                    edittext_edit_dob.setText("");

//                if(!profile.getEmail().trim().equalsIgnoreCase(""))
//                    txtEmail.setText("my mail id ");
//                else
//                    txtEmail.setText("-");

                emailid=profile.getEmail();
                // System.out.println("dob"+dob+"emailid"+emailid);

                if(!profile.getFirstName().trim().equalsIgnoreCase(""))
                    txtUserName.setText(profile.getFirstName());
                else
                    txtUserName.setText("-");


                if(!profile.getAmount().trim().equalsIgnoreCase(""))
                    txtmerchaccountNumber.setText((profile.getAmount()));
                else
                    txtmerchaccountNumber.setText("-");

                if(!profile.getAccountType().trim().equalsIgnoreCase(""))

                    txtmerchtype.setText(profile.getAccountType());
                else

                    txtmerchtype.setText("-");

                if(!profile.getLastname().trim().equalsIgnoreCase(""))

                    txtmerchcontactPerson.setText(profile.getLastname());
                else

                    txtmerchcontactPerson.setText("-");

                if(!profile.getEmail().trim().equalsIgnoreCase(""))
                    txtmerchemaild.setText(profile.getEmail());
                else
                    txtmerchemaild.setText("-");

                if(!profile.getDob().trim().equalsIgnoreCase(""))
                    txtmerchaddress.setText(profile.getDob());
                else
                    txtmerchaddress.setText("-");

            } else if (result.equalsIgnoreCase("Failure")) {

                AlertBuilder alertBuilder = new AlertBuilder(MerchantProfileActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));

            } else {
                AlertBuilder alertBuilder = new AlertBuilder(MerchantProfileActivity.this);
                alertBuilder.showAlert(result);
            }

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
}
