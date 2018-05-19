package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CheckBalanceActivity extends Activity {

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;

    FontClass fontclass=new FontClass();
    Typeface typeface;
    RelativeLayout mainlinear;


    String balance;
    TextView txtCheckBalance,txtPurse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_balance);

        mainlinear = (RelativeLayout)findViewById(R.id.mainlinear);
        typeface=Typeface.createFromAsset(getApplicationContext().getAssets(),"helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        txtCheckBalance = (TextView)findViewById(R.id.txtCheckBalance);
        txtPurse = (TextView)findViewById(R.id.txtPurse);
        balance = getIntent().getStringExtra("balance");
        //// System.out.println("Balance is......." + balance);

        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        if(connectionDetector.isConnectingToInternet())
        {
            String url = getResources().getString(R.string.getpurse)+"?MDN="+pref.getString("mobile_number", "");
            GetPurseDetails getPurseDetails = new GetPurseDetails(getApplicationContext());
            getPurseDetails.execute(url);
        }

        txtCheckBalance.setText("Your wallet balance is Rs. "+balance);

       ImageView imgBanner = (ImageView)findViewById(R.id.imgBanner);

        String  banner = pref.getString("banner", "");
        MyConnectionHelper db;

        db = new MyConnectionHelper(getApplicationContext());

        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();

        if(c1.moveToNext())
        {

            do
            {
                //// System.out.println("********************************************************"+c1.getString(3)+" " +c1.getString(1));
                if(c1.getString(3).equalsIgnoreCase("LAND"))
                {
                    Glide.with(CheckBalanceActivity.this).load(c1.getString(1)).into(imgBanner);
                    imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
            }while(c1.moveToNext());
        }

        c1.close();
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
                finish();
                Intent re =new Intent(getApplicationContext(),Electricity_Payment.class);
                re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(re);
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

    private class GetPurseDetails extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        ProgressDialog pDialog;
        String responseMessage;

        String mealPurseBalance = "0",giftPurseBalance = "0",loyaltyPurseBalance = "0";
        public GetPurseDetails(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CheckBalanceActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(CheckBalanceActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            responseMessage = jsonMainObj.getString("responseMessage");
                            mealPurseBalance = jsonMainObj.getString("purseBalance");
                            giftPurseBalance = jsonMainObj.getString("giftPurseBalance");
                            loyaltyPurseBalance = jsonMainObj.getString("loyaltyPurseBalance");

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



            Date dNow = new Date( );
            SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy hh:mm");
            String currentDate = ft.format(dNow) + "hours ";

            if(result.equalsIgnoreCase("SUCCESS"))
            {
                txtPurse.setText("Your Meal Purse Balance is "+mealPurseBalance+" as of "+currentDate+",Your Gift Purse Balance is "+giftPurseBalance+" as of "+currentDate+" ,Your Loyalty Purse Balance is "+loyaltyPurseBalance+" as of "+currentDate+"");
            }
            else
            {
                txtPurse.setText("Your Meal Purse Balance is 0.00 as of "+currentDate+",Your Gift Purse Balance is 0.00 as of "+currentDate+",Your Loyalty Purse Balance is 0.00 as of "+currentDate+"");
            }


        }

    }
}
