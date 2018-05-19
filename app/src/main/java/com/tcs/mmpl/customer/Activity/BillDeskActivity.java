package com.tcs.mmpl.customer.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.billdesk.sdk.PaymentOptions;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyObject;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BillDeskActivity extends AppCompatActivity {
    FontClass fontclass = new FontClass();
    ConnectionDetector connectionDetector;
    Button btncreditcard,btnDebitCard,btnnetbanking,btnmmid,btnSavedcards;
    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;
    String Amount,loadMoneyMessage,getMMID_url,Typecard="",Bankid="";
    private ProgressDialog pDialog;
    Typeface typeface;
    LinearLayout mainlinear,getMMID_linlayout,lin_getMMID_url;
    TextView txt_getMMID;

    ArrayList<String> bankID ;
    ArrayList<String> bankName ;

    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bill_desk);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        btncreditcard=(Button)findViewById(R.id.btncreditcard);
        btnDebitCard=(Button)findViewById(R.id.btnDebitCard);
        btnnetbanking=(Button)findViewById(R.id.btnnetbanking);
        btnmmid=(Button)findViewById(R.id.btnmmid);
        btnSavedcards=(Button)findViewById(R.id.btnSavedcards);

        mainlinear = (LinearLayout) findViewById(R.id.mainlinear);
        getMMID_linlayout = (LinearLayout) findViewById(R.id.getMMID_linlayout);
        lin_getMMID_url = (LinearLayout) findViewById(R.id.lin_getMMID_url);
        txt_getMMID=(TextView)findViewById(R.id.txt_getMMID);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

         Amount = getIntent().getStringExtra("Amount");

//        String Text_info_url =getApplicationContext().getResources().getString(R.string.getMMID);
//        Text_info Text_info2 = new Text_info(getApplicationContext());
//        Text_info2.execute(Text_info_url);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();
        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        String Mystring=userInfoPref.getString("billdeskFlag", "");
        // System.out.println(Mystring);
//        Mystring="MMID";
        if(Mystring.contains(","))
        {
            String[] billDeskButtons = Mystring.split(",");

            for(int i=0;i<billDeskButtons.length;i++)
            {
                if(billDeskButtons[i].equalsIgnoreCase("CC"))
                {
                    btncreditcard.setVisibility(View.VISIBLE);
                }
                else if(billDeskButtons[i].equalsIgnoreCase("DB"))
                {
                    btnDebitCard.setVisibility(View.VISIBLE);

                }
                else if(billDeskButtons[i].equalsIgnoreCase("NB"))
                {
                    btnnetbanking.setVisibility(View.VISIBLE);

                }
                else if(billDeskButtons[i].equalsIgnoreCase("MMID"))
                {
                    btnmmid.setVisibility(View.VISIBLE);

                }
            }
        }
        else
        {
            if(Mystring.equalsIgnoreCase("CC"))
            {
                btncreditcard.setVisibility(View.VISIBLE);
            }
            else if(Mystring.equalsIgnoreCase("DB"))
            {
                btnDebitCard.setVisibility(View.VISIBLE);

            }
            else if(Mystring.equalsIgnoreCase("NB"))
            {
                btnnetbanking.setVisibility(View.VISIBLE);

            }
            else if(Mystring.equalsIgnoreCase("MMID"))
            {
                btnmmid.setVisibility(View.VISIBLE);

            }
        }




        btncreditcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionDetector.isConnectingToInternet()) {
                    String loadMoneyMessage_url = getResources().getString(R.string.CreditCardMessage);
                    loadMoneyMessage loadMoneyMessage1 = new loadMoneyMessage(getApplicationContext());
                    loadMoneyMessage1.execute(loadMoneyMessage_url);

                }
                else
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();
                }
            }
        });


        btnDebitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Typecard="Debit Card";
                String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);
                GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext(),"DC-F",Bankid,Typecard);
                dc.execute(billDeskConnect_url);
//                Intent i = new Intent(getApplicationContext(), BilldeskListActivity.class);
//                i.putExtra("url", getResources().getString(R.string.getDebitBank));
//                startActivityForResult(i, 1);
            }
        });


        btnnetbanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), BilldeskListActivity.class);
//                i.putExtra("url", getResources().getString(R.string.getNetBank));
//                startActivityForResult(i, 2);
                InternetBankingList internetBankingList = new InternetBankingList(getApplicationContext());
                internetBankingList.execute(getResources().getString(R.string.getNetBank));
            }
        });

        btnmmid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MMIDList mmidList = new MMIDList(getApplicationContext());
                mmidList.execute(getResources().getString(R.string.getMMIDbBank));
            }
        });

        btnSavedcards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Typecard="Saved Cards";
                String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);
                GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext(),"QP-F",Bankid,Typecard);
                dc.execute(billDeskConnect_url);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 ){
            Typecard="Debit Card";
            String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);
            GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext(),"DC-F",Bankid,Typecard);
            dc.execute(billDeskConnect_url);
        }
        else
        {
            try
            {
                Bankid=data.getStringExtra("bankID");
            } catch (Exception e) {

            }
            if(!Bankid.equalsIgnoreCase(""))
            {
                Typecard="Net banking";
                String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);
                GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext(),"NB-F",Bankid,Typecard);
                dc.execute(billDeskConnect_url);
            }

        }



    }

    private class GetBillDeskConnect extends AsyncTask<String, Void, String> {

        Context context;
        String concatResult,token;
        String txtpaycategory,Bankid,Typecard;
        public GetBillDeskConnect (Context context ,String txtpaycategory,String Bankid,String Typecard ) {
            this.context = context;
            this.txtpaycategory=txtpaycategory;
            this.Bankid = Bankid;
            this.Typecard = Typecard;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(BillDeskActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {
            String jsonStr;
            try
            {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("MDN", pref.getString("mobile_number", "")));
                nameValuePairs.add(new BasicNameValuePair("Amount", Amount));
                nameValuePairs.add(new BasicNameValuePair("Bankid", Bankid));
                nameValuePairs.add(new BasicNameValuePair("Typecard", Typecard ));

                // System.out.println("mytag"+"MDN"+pref.getString("mobile_number", "")+ "\t Amount"+Amount+ "\t Bankid"+Bankid+ "\t Typecard"+Typecard+ "\t txtpaycategory"+txtpaycategory );


                // Creating service handler class instance
                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext(),nameValuePairs);

                // Making a request to url and getting response
                jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST1);

                // System.out.println("Result.........."+jsonStr);

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
                Float floatValue = Float.parseFloat(Amount);
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
                intent.putExtra("txtpaycategory",txtpaycategory);
                intent.putExtra("callback", new MyObject());
                //// System.out.println("1...");
                startActivity(intent);
                //finish();
            } else {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
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
            pDialog1 = new ProgressDialog(BillDeskActivity.this);
            pDialog1.setMessage(getResources().getString(R.string.loading));
            pDialog1.setCancelable(false);
            pDialog1.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(BillDeskActivity.this);
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
                View popupView = getLayoutInflater().inflate(R.layout.popup_delete_favourites, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                TextView update_dialog_txt = (TextView) popupView.findViewById(R.id.txtMessage);
                Button update_dialog_btn_Cancel =(Button)popupView.findViewById(R.id.btnNo);
                Button update_dialog_btn_ok = (Button) popupView.findViewById(R.id.btnYes);
                update_dialog_txt.setText(loadMoneyMessage);
                update_dialog_btn_ok.setText("Proceed");
                update_dialog_btn_Cancel.setText("Cancel");

                update_dialog_txt.setTypeface(typeface);
                update_dialog_btn_ok.setTypeface(typeface);
                update_dialog_btn_Cancel.setTypeface(typeface);


                update_dialog_btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popupWindow.dismiss();
                        String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);
                        //// System.out.println(billDeskConnect_url);
                        Typecard="Credit Card";
                        GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext(),"CC-F",Bankid,Typecard);
                        dc.execute(billDeskConnect_url);
//                        popupWindow.dismiss();
                    }
                });

                update_dialog_btn_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);




            } else if (result.equalsIgnoreCase("Failure")) {
                String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);//+"?Recharge="+recharge;
                //// System.out.println(billDeskConnect_url);
                Typecard="Credit Card";
                GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext(),"CC-F",Bankid,Typecard);
                dc.execute(billDeskConnect_url);
            }
            else if(result.equalsIgnoreCase("Failure1"))
            {
                AlertBuilder alertBuilder = new AlertBuilder(BillDeskActivity.this);
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

    private class InternetBankingList extends AsyncTask<String, Void, String> {

        Context context;


        String firstName, lastName, walletBalance;

        public InternetBankingList(Context context) {
            this.context = context;
            bankID = new ArrayList<String>();
            bankName = new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(BillDeskActivity.this);
            pDialog.setMessage("Loading ...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//                nameValuePairs.add(new BasicNameValuePair("Type",type));
                WebServiceHandler serviceHandler = new WebServiceHandler(BillDeskActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                Log.i("Response: >>>>>>>>>>>", jsonStr);

                jsonStr = "{ \"value\":"+jsonStr+"}";

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");

                        //// System.out.println("Length of jsonArray..............." + jsonArray.length());


                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = (JSONObject) jsonArray.get(i);
//                                operatorName.add(obj.getString("serviceProviderName"));
                                bankID.add(obj.getString("bankid"));
                                bankName.add(obj.getString("bankname"));


                            }
                            return "Success";
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
                e.printStackTrace();
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }



        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);

            if (result.equalsIgnoreCase("Success")) {
                //Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();

//                ArrayAdapter<String> adp=new ArrayAdapter<String> (getBaseContext(),android.R.layout.simple_dropdown_item_1line,bankName);
//                adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

//                listBillDesk.setAdapter(adp);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(BillDeskActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                // create view for add item in dialog
                View convertView = (View) inflater.inflate(R.layout.popuplist, null);
                // add custom view in dialog
                alertDialog.setView(convertView);
                ListView lv = (ListView) convertView.findViewById(R.id.mylistview);
                final AlertDialog alert = alertDialog.create();
//                alert.getWindow().setLayout(400, 400); //Controlling width and height.
                alert.setTitle(" Select Your Bank ...."); // Title
                MyAdapter myadapter = new MyAdapter(BillDeskActivity.this, R.layout.listpopupitem, bankName);
                lv.setAdapter(myadapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        // TODO Auto-generated method stub
                         Bankid=bankID.get(position);

                        if(!Bankid.equalsIgnoreCase(""))
                        {
                            Typecard="Net banking";
                            String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);
                            GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext(),"NB-F",Bankid,Typecard);
                            dc.execute(billDeskConnect_url);
                        }

                        Toast.makeText(BillDeskActivity.this, "You have selected -: " + bankName.get(position), Toast.LENGTH_SHORT).show();
                        alert.cancel();
                    }
                });
                // show dialog
                alert.show();


            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(BillDeskActivity.this).create();

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
                        finish();
                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(BillDeskActivity.this).create();

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
                        finish();
                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            }

        }

    }

    private class MMIDList extends AsyncTask<String, Void, String> {

        Context context;


        String firstName, lastName, walletBalance;

        public MMIDList(Context context) {
            this.context = context;
            bankID = new ArrayList<String>();
            bankName = new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(BillDeskActivity.this);
            pDialog.setMessage("Loading ...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//                nameValuePairs.add(new BasicNameValuePair("Type",type));
                WebServiceHandler serviceHandler = new WebServiceHandler(BillDeskActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                Log.i("Response: >>>>>>>>>>>", jsonStr);

                jsonStr = "{ \"value\":"+jsonStr+"}";

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");
                        //// System.out.println("Length of jsonArray..............." + jsonArray.length());

                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = (JSONObject) jsonArray.get(i);
//                                operatorName.add(obj.getString("serviceProviderName"));
                                bankID.add(obj.getString("bankurl"));
                                bankName.add(obj.getString("bankname"));


                            }
                            return "Success";
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
                e.printStackTrace();
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }



        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);

            if (result.equalsIgnoreCase("Success")) {
                //Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();

//                ArrayAdapter<String> adp=new ArrayAdapter<String> (getBaseContext(),android.R.layout.simple_dropdown_item_1line,bankName);
//                adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

//                listBillDesk.setAdapter(adp);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(BillDeskActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                // create view for add item in dialog
                View convertView = (View) inflater.inflate(R.layout.popuplist, null);
                // add custom view in dialog
                alertDialog.setView(convertView);
                ListView lv = (ListView) convertView.findViewById(R.id.mylistview);
                final AlertDialog alert = alertDialog.create();
//                alert.getWindow().setLayout(400, 400); //Controlling width and height.
                alert.setTitle(" Select Your Bank ...."); // Title
                MyAdapter myadapter = new MyAdapter(BillDeskActivity.this, R.layout.listpopupitem, bankName);
                lv.setAdapter(myadapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        // TODO Auto-generated method stub
                        Bankid=bankID.get(position);

                        if(!Bankid.equalsIgnoreCase(""))
                        {
//                            Typecard="Net banking";
//                            String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);
//                            GetBillDeskConnect dc = new GetBillDeskConnect(getApplicationContext(),"NB-F",Bankid,Typecard);
//                            dc.execute(billDeskConnect_url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bankID.get(position)));
                            startActivity(intent);

                            // System.out.println(bankName.get(position)+bankID.get(position));
                        }

//                        Toast.makeText(BillDeskActivity.this, "You have selected -: " + bankName.get(position)+bankID.get(position), Toast.LENGTH_SHORT).show();
                        alert.cancel();
                    }
                });
                // show dialog
                alert.show();


            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(BillDeskActivity.this).create();

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
                        finish();
                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(BillDeskActivity.this).create();

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
                        finish();
                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            }

        }

    }

    private class ViewHolder {
        TextView tvSname;
    }

    class MyAdapter extends ArrayAdapter<String> {
        LayoutInflater inflater;
        Context myContext;
        List<String> newList;
        public MyAdapter(Context context, int resource, List<String> list) {
            super(context, resource, list);
            // TODO Auto-generated constructor stub
            myContext = context;
            newList = list;
            inflater = LayoutInflater.from(context);
        }
        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.listpopupitem, null);
                holder.tvSname = (TextView) view.findViewById(R.id.tvtext_item);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.tvSname.setText(newList.get(position).toString());

            return view;
        }
    }

    class MainListHolder {
        private TextView tvText;
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


//            if(pDialog.isShowing())
//                pDialog.dismiss();

            //// System.out.println(activityName);
            //// System.out.println(urlLink);
            if(!activityName.equalsIgnoreCase("NA") && !urlLink.equalsIgnoreCase("NA"))
            {
                try {

                    String className = getPackageName()+".Activity."+activityName;
                    Class<?> myClass = Class.forName(className);
                    //Activity activity = (Activity) myClass.newInstance();
                    Intent re = new Intent(getApplicationContext(),myClass);
                    re.putExtra("option", urlLink);
                    startActivity(re);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    //// System.out.println("exception "+e.getMessage());
                }
            }
            else if(!activityName.equalsIgnoreCase("NA") && urlLink.equalsIgnoreCase("NA"))
            {
                try {
                    String className = getPackageName()+".Activity."+activityName;
                    Class<?> myClass = Class.forName(className);
                    //Activity activity = (Activity) myClass.newInstance();
                    Intent re = new Intent(getApplicationContext(),myClass);
                    startActivity(re);
                }
                catch (Exception e)
                {

                }

            }
            else if(activityName.equalsIgnoreCase("NA") && !urlLink.equalsIgnoreCase("NA"))
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink)));
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
            pDialog = new ProgressDialog(BillDeskActivity.this);
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
                AlertBuilder alert =new AlertBuilder(BillDeskActivity.this);
                alert.showAlert(result);

            }

        }

    }

    private class Text_info extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

        public Text_info(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(BillDeskActivity.this);
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
                responseMessage=jsonStr;



            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                if (responseMessage != null || !responseMessage.equalsIgnoreCase("")) {
                    String string = responseMessage;
                    String[] parts = string.split("\\|");
                    String part1 = parts[0];
                    String part2 = parts[1];
                    //// System.out.println("part1" + part1 + "part2" + part2);
                    getMMID_linlayout.setVisibility(View.VISIBLE);
                    txt_getMMID.setText(part1);
                    getMMID_url = part2;
                }
            }catch (Exception e){}

        }

    }
}
