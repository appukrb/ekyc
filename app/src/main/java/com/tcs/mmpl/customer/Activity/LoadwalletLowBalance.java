package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.billdesk.sdk.PaymentOptions;
import com.bumptech.glide.Glide;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.MyObject;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoadwalletLowBalance extends Activity {
    LinearLayout mainlinear;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    Context context;
    ConnectionDetector connectionDetector;
    private Button btnLoadMoney;
    private EditText edtLoadAmount;

    String loadMoneyMessage;

    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;
    private ProgressDialog pDialog;
    private ImageView imgBanner;
    MyConnectionHelper db;
    String Amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loadwallet_low_balance);
        mainlinear = (LinearLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();
        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        edtLoadAmount=(EditText)findViewById(R.id.edtLoadAmount);
        btnLoadMoney = (Button)findViewById(R.id.btnLoadMoney);

        btnLoadMoney.setTypeface(btnLoadMoney.getTypeface(),Typeface.BOLD);
        imgBanner = (ImageView)findViewById(R.id.imgBanner);
        db = new MyConnectionHelper(getApplicationContext());

        String Value =(getIntent().getStringExtra("Value"));

        Double amount1  = Double.parseDouble(Value);
        long amount=Math.round(amount1)+1;
        edtLoadAmount.setText(String.valueOf(amount));

        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();

        if(c1.moveToNext())
        {

            do
            {
                //// System.out.println("********************************************************"+c1.getString(3)+" " +c1.getString(1));
                if(c1.getString(3).equalsIgnoreCase("LOAD"))
                {
                    Glide.with(LoadwalletLowBalance.this).load(c1.getString(1)).placeholder(R.drawable.default_banner).into(imgBanner);
                    imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }



            }while(c1.moveToNext());
        }

        c1.close();
        String  banner = pref.getString("banner", "");

        ImageView profileImage = (ImageView)findViewById(R.id.imgUser);



        btnLoadMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertBuilder alert = new AlertBuilder(LoadwalletLowBalance.this);
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

                    if(edtLoadAmount.getText().toString().trim().equalsIgnoreCase(""))
                    {
                        alert.showAlert(getResources().getString(R.string.invalid_amount));
                    }
                    else {
                        if(connectionDetector.isConnectingToInternet()) {
                            Amount=edtLoadAmount.getText().toString().trim();
                            String loadMoneyMessage_url = getResources().getString(R.string.loadMoneyMessage)+"?MDN="+pref.getString("mobile_number","")+"&Amount="+edtLoadAmount.getText().toString().trim();;
                            loadMoneyMessage loadMoneyMessage1 = new loadMoneyMessage(getApplicationContext());
                            loadMoneyMessage1.execute(loadMoneyMessage_url);

//                            String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);//+"?Recharge="+recharge;
//                            // System.out.println(billDeskConnect_url);
//                            GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext());
//                            dc.execute(billDeskConnect_url);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                        }

                    }
                }
                else
                {
                    alert.newUser();
                }
            }
        });


    }


    public void openWalletStatement(View v)
    {

        AlertBuilder alert = new AlertBuilder(LoadwalletLowBalance.this);
        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
            if(connectionDetector.isConnectingToInternet())
            {
                String url = getResources().getString(R.string.loadwalletStatement)+ "?MDN="+pref.getString("mobile_number", "");
                WalletStatementAsync w =new WalletStatementAsync(getApplicationContext());
                w.execute(url);
            }
            else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

            }
        }
        else
        {
            alert.newUser();
        }

    }

    public void add50(View v)
    {
        int amount;
        if(edtLoadAmount.getText().toString().trim().isEmpty())
        {
            edtLoadAmount.setText("50");
        }
        else
        {
            amount = 50 + Integer.parseInt(edtLoadAmount.getText().toString().trim());
            edtLoadAmount.setText(String.valueOf(amount));
        }
    }

    public void add100(View v)
    {
        int amount;
        if(edtLoadAmount.getText().toString().trim().isEmpty())
        {
            edtLoadAmount.setText("100");
        }
        else
        {
            amount = 100 + Integer.parseInt(edtLoadAmount.getText().toString().trim());
            edtLoadAmount.setText(String.valueOf(amount));
        }
    }

    public void add200(View v)
    {
        int amount;
        if(edtLoadAmount.getText().toString().trim().isEmpty())
        {
            edtLoadAmount.setText("200");
        }
        else
        {
            amount = 200 + Integer.parseInt(edtLoadAmount.getText().toString().trim());
            edtLoadAmount.setText(String.valueOf(amount));
        }
    }
    public void add500(View v)
    {
        int amount;
        if(edtLoadAmount.getText().toString().trim().isEmpty())
        {
            edtLoadAmount.setText("500");
        }
        else
        {
            amount = 500 + Integer.parseInt(edtLoadAmount.getText().toString().trim());
            edtLoadAmount.setText(String.valueOf(amount));
        }
    }

    public void add1000(View v)
    {
        int amount;
        if(edtLoadAmount.getText().toString().trim().isEmpty())
        {
            edtLoadAmount.setText("1000");
        }
        else
        {
            amount = 1000 + Integer.parseInt(edtLoadAmount.getText().toString().trim());
            edtLoadAmount.setText(String.valueOf(amount));
        }
    }
    public void add5000(View v)
    {
        int amount;
        if(edtLoadAmount.getText().toString().trim().isEmpty())
        {
            edtLoadAmount.setText("5000");
        }
        else
        {
            amount = 5000 + Integer.parseInt(edtLoadAmount.getText().toString().trim());
            edtLoadAmount.setText(String.valueOf(amount));
        }
    }



    private class GetBillDeskConnect extends AsyncTask<String, Void, String> {

        Context context;

        String concatResult,token;

        public GetBillDeskConnect (Context context ) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoadwalletLowBalance.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {
            String jsonStr;
            try
            {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("MDN", pref.getString("mobile_number", "")));
                nameValuePairs.add(new BasicNameValuePair("Amount", edtLoadAmount.getText().toString()));


                // Creating service handler class instance
                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext(),nameValuePairs);

                // Making a request to url and getting response
                jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(),
                        WebServiceHandler.POST1);

                //// System.out.println("Result.........."+jsonStr);

            }
            catch(Exception e)
            {
                return "Failure";
            }
            //jsonStr = "{ \"value\":" + jsonStr + "}";
            if (jsonStr != null) {

                try
                {
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    if(jsonObject.getString("responseStatus").equalsIgnoreCase("SUCCESS"))
                    {

                        String[] resArray = jsonObject.getString("message").split("#");
                        concatResult=resArray[0];
                        token = resArray[1];
                        String[] res = jsonStr.split("\\|");


                        //// System.out.println("msg to billdesk..............." + concatResult);
                        //// System.out.println("Token........................."+token);

                        //// System.out.println("Transid........." + res[1]);

                        return "Success";
                    }
                    else if(jsonObject.getString("responseStatus").equalsIgnoreCase("FAIL"))
                    {
                        return jsonObject.getString("responseMessage");
                    }

                }
                catch (JSONException e)
                {
                    return "Failure";
                }


            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }

            return "Success";
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            if (result == null) {
                Toast.makeText(context, getResources().getString(R.string.apidown),
                        Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("Failure")) {
                Toast.makeText(context, getResources().getString(R.string.apidown),
                        Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("Success")) {
                Float floatValue = Float.parseFloat(edtLoadAmount.getText().toString());
                String amount = String.format("%.2f", floatValue);

                //// System.out.println("Amount::::::::::"+amount);

                String mobile =pref.getString("mobile_number", "").replaceAll("91","");

                Intent intent = new Intent(getApplicationContext(), PaymentOptions.class);

                intent.putExtra("msg",concatResult);
                intent.putExtra("token",token);
                intent.putExtra("user-email", "tthtestmail@gmail.com");
                intent.putExtra("user-mobile",mobile);
                intent.putExtra("amount",amount);
                intent.putExtra("orientation", Configuration.ORIENTATION_PORTRAIT);
                intent.putExtra("txtpaycategory",userInfoPref.getString("billdeskFlag","CC,DC,NB"));
                intent.putExtra("callback", new MyObject());
                //// System.out.println("1...");
                startActivity(intent);
                //finish();
            } else {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }

        }

    }

    private class WalletStatementAsync extends AsyncTask<String, Void, String> {

        Context context;
        int flag = 0;

        private ArrayList<String> amount,transid,date;


        public WalletStatementAsync(Context context) {
            this.context = context;

            amount = new ArrayList<String>();
            transid = new ArrayList<String>();
            date =new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoadwalletLowBalance.this);
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



                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success")) {

                            JSONArray jsonArray = jsonMainObj.getJSONArray("data");

                            if (jsonArray.length() > 0) {


                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject j1 = jsonArray.getJSONObject(i);
                                    amount.add(j1.getString("amount"));
                                    date.add(j1.getString("date"));
                                    transid.add(j1.getString("transcationID"));
                                }


                            }

                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {

                            return jsonMainObj.getString("responseMessage");


                        } else {

                            return "Failure";
                        }

                    } catch (JSONException e) {
                        editor.putInt("flag", 0);
                        editor.commit();
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

                Intent intent = new Intent(getApplicationContext(), WalletStatement.class);
                intent.putExtra("amount", amount);
                intent.putExtra("transid", transid);
                intent.putExtra("date", date);
                startActivity(intent);


            } else if (result.equalsIgnoreCase("Failure")) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();

            } else {
                AlertBuilder alert =new AlertBuilder(LoadwalletLowBalance.this);
                alert.showAlert(result);

            }

        }

    }

    private class loadMoneyMessage extends AsyncTask<String, Void, String> {

        Context context;

        ArrayList<String> id,url;
        String bannerURL;
        ProgressDialog pDialog1;

        public loadMoneyMessage(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog1 = new ProgressDialog(LoadwalletLowBalance.this);
            pDialog1.setMessage(getResources().getString(R.string.loading));
            pDialog1.setCancelable(false);
            pDialog1.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(LoadwalletLowBalance.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        if(jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success"))
                        {
                            if(jsonMainObj.getString("responseMessage").trim().equalsIgnoreCase(""))
                            {
                                return "Failure";
                            }
                            else
                            {
                                loadMoneyMessage=jsonMainObj.getString("responseMessage");
                                return "Success";
                            }

                        }
                        else if(jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Failure1"))
                        {
                            loadMoneyMessage=jsonMainObj.getString("responseMessage");
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

            if (pDialog1.isShowing())
                pDialog1.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success"))
            {

//                Amount=edtLoadAmount.getText().toString().trim();
                Intent intent = new Intent(getBaseContext(), BillDeskActivity.class);
                intent.putExtra("Amount", Amount);
                startActivity(intent);
//                View popupView = getLayoutInflater().inflate(R.layout.popup_delete_favourites, null);
//                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                TextView update_dialog_txt = (TextView) popupView.findViewById(R.id.txtMessage);
//                Button update_dialog_btn_Cancel =(Button)popupView.findViewById(R.id.btnNo);
//                Button update_dialog_btn_ok = (Button) popupView.findViewById(R.id.btnYes);
//                update_dialog_txt.setText(loadMoneyMessage);
//                update_dialog_btn_ok.setText("Proceed");
//                update_dialog_btn_Cancel.setText("Cancel");
//
//                update_dialog_txt.setTypeface(typeface);
//                update_dialog_btn_ok.setTypeface(typeface);
//                update_dialog_btn_Cancel.setTypeface(typeface);
//
//
//                update_dialog_btn_ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        popupWindow.dismiss();
//                        String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);
//                        GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext());
//                        dc.execute(billDeskConnect_url);
//                        popupWindow.dismiss();
//                    }
//                });
//
//                update_dialog_btn_Cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        popupWindow.dismiss();
//                    }
//                });
//
//                popupWindow.setOutsideTouchable(false);
//                popupWindow.setFocusable(true);
//                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);




            } else if (result.equalsIgnoreCase("Failure")) {

                Intent intent = new Intent(getBaseContext(), BillDeskActivity.class);
                intent.putExtra("Amount", Amount);
                startActivity(intent);
//                String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);
//                GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext());
//                dc.execute(billDeskConnect_url);
            }
            else if(result.equalsIgnoreCase("Failure1"))
            {
                AlertBuilder alertBuilder = new AlertBuilder(LoadwalletLowBalance.this);
                AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(loadMoneyMessage);

                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();
            }

        }

    }

//    private class loadMoneyMessage extends AsyncTask<String, Void, String> {
//
//        Context context;
//
//        ArrayList<String> id,url;
//        ProgressDialog pDialog1;
//
//        public loadMoneyMessage(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Showing progress dialog
//            pDialog1 = new ProgressDialog(LoadwalletLowBalance.this);
//            pDialog1.setMessage(getResources().getString(R.string.loading));
//            pDialog1.setCancelable(false);
//            pDialog1.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... arg0) {
//
//            try {
//
//
//                WebServiceHandler serviceHandler = new WebServiceHandler(LoadwalletLowBalance.this);
//                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
//
//                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
//                    try {
//                        JSONObject jsonMainObj = new JSONObject(jsonStr);
//
//
//                        if(jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success"))
//                        {
//
//                            if(jsonMainObj.getString("responseMessage").trim().equalsIgnoreCase(""))
//                            {
//                                return "Failure";
//                            }
//                            else
//                            {
//                                loadMoneyMessage=jsonMainObj.getString("responseMessage");
//                                return "Success";
//                            }
//
//                        }
//                        else if(jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Failure1"))
//                        {
//                            loadMoneyMessage=jsonMainObj.getString("responseMessage");
//                            return "Failure1";
//                        }
//                        else
//                        {
//                            return "Failure";
//                        }
//
//
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
//            } catch (Exception e) {
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
//                return "Failure";
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            if (pDialog1.isShowing())
//                pDialog1.dismiss();
//
//            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);
//
//            if (result.equalsIgnoreCase("Success"))
//            {
//                View popupView = getLayoutInflater().inflate(R.layout.popup_delete_favourites, null);
//                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                TextView update_dialog_txt = (TextView) popupView.findViewById(R.id.txtMessage);
//                Button update_dialog_btn_Cancel =(Button)popupView.findViewById(R.id.btnNo);
//                Button update_dialog_btn_ok = (Button) popupView.findViewById(R.id.btnYes);
//                update_dialog_txt.setText(loadMoneyMessage);
//                update_dialog_btn_ok.setText("Proceed");
//                update_dialog_btn_Cancel.setText("Cancel");
//
//                update_dialog_txt.setTypeface(typeface);
//                update_dialog_btn_ok.setTypeface(typeface);
//                update_dialog_btn_Cancel.setTypeface(typeface);
//
//
//                update_dialog_btn_ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        popupWindow.dismiss();
//                        String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);
//                        //// System.out.println(billDeskConnect_url);
//                        GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext());
//                        dc.execute(billDeskConnect_url);
//                        popupWindow.dismiss();
//                    }
//                });
//
//                update_dialog_btn_Cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        popupWindow.dismiss();
//                    }
//                });
//
//                popupWindow.setOutsideTouchable(false);
//                popupWindow.setFocusable(true);
//                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
//
//
//
//
//            } else if (result.equalsIgnoreCase("Failure")) {
//                String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);//+"?Recharge="+recharge;
//                //// System.out.println(billDeskConnect_url);
//                GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext());
//                dc.execute(billDeskConnect_url);
//            }
//            else if(result.equalsIgnoreCase("Failure1"))
//            {
//                AlertBuilder alertBuilder = new AlertBuilder(LoadwalletLowBalance.this);
//                AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(loadMoneyMessage);
//
//                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Write your code here to execute after dialog closed
//                        dialog.cancel();
//                    }
//                });
//                // Showing Alert Message
//                alertDialog.setCancelable(false);
//                alertDialog.show();
//            }
//
//
//        }
//
//    }


}
