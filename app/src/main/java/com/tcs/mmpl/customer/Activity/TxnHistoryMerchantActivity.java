package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TxnHistoryMerchantActivity extends ListActivity {
    private static final int MODE_PRIVATE = 0 ;
    ArrayList<String> amount, desc,date,month,day,year,type,status,transId;
    TransactionAdapter adapter;
    private ImageView imgBanner;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    String banner;
    Typeface typeface;
    private ProgressBar progressBar;
    private TextView txtTransactionError;
    FontClass fontclass=new FontClass();
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_txn_history_merchant);
        LinearLayout linParent =(LinearLayout)findViewById(R.id.linParent);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        txtTransactionError = (TextView) findViewById(R.id.txtTransactionError);
        typeface = Typeface.createFromAsset(TxnHistoryMerchantActivity.this.getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);
        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();



        String jsonStr = getIntent().getStringExtra("jsonStr1");
        if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
            try {
                JSONObject jsonMainObj = new JSONObject(jsonStr);

                if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                    JSONArray jsonArray = jsonMainObj.getJSONArray("transctionDetaillist");
                    desc =new ArrayList<String>();
                    amount = new ArrayList<String>();
                    date = new ArrayList<String>();
                    day = new ArrayList<String>();
                    month = new ArrayList<String>();
                    year = new ArrayList<String>();
                    type = new ArrayList<String>();
                    status = new ArrayList<String>();
                    transId = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject j1 = jsonArray.getJSONObject(i);
                        desc.add(j1.getString("category"));
                        amount.add(j1.getString("amount"));
                        date.add(j1.getString("date"));
                        day.add(j1.getString("day"));
                        month.add(j1.getString("month"));
                        year.add(j1.getString("year"));
                        type.add(j1.getString("type"));
                        status.add(j1.getString("status"));
                        transId.add(j1.getString("transactionId"));

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

        txtTransactionError.setVisibility(View.GONE);

        try {
            final ListView lstView = getListView();

            lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            adapter = new TransactionAdapter(TxnHistoryMerchantActivity.this,R.layout.layout_paid, lstView, amount, desc, date, day, month, year, type,status,transId);
            setListAdapter(adapter);
        }
        catch (Exception e)
        {

        }

//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                loadingPopup();
//            }
//        }, 100);
    }

    private void loadingPopup(){

        LayoutInflater layoutInflater = (LayoutInflater) TxnHistoryMerchantActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_mpin_layout, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
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
                onBackPressed();
            }
        });

        btnSubmit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(TxnHistoryMerchantActivity.this, getResources().getString(R.string.mpin), Toast.LENGTH_LONG).show();
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
            progressBar.setVisibility(View.VISIBLE);


        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(TxnHistoryMerchantActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

//                String jsonStr="{\n" +
//                        "  \"responseStatus\": \"Success\",\n" +
//                        "  \"responseMessage\": \"\",\n" +
//                        "  \"errorCode\": \"\",\n" +
//                        "  \"transctionDetaillist\": [\n" +
//                        "    {\n" +
//                        "      \"serviceProvider\": \"MMPL\",\n" +
//                        "      \"serviceInputs\": \"mmpl\",\n" +
//                        "      \"failureReason\": \"\",\n" +
//                        "      \"dateTime\": \"2016-11-0915:24:57\",\n" +
//                        "      \"transactionId\": \"1611090001018339\",\n" +
//                        "      \"type\": \"CREDIT\",\n" +
//                        "      \"day\": \"WEDNESDAY\",\n" +
//                        "      \"month\": \"NOV\",\n" +
//                        "      \"year\": 2016,\n" +
//                        "      \"date\": 9,\n" +
//                        "      \"category\": \"MMPL\",\n" +
//                        "      \"amount\": \"2.00\",\n" +
//                        "      \"status\": \"Success\",\n" +
//                        "      \"fee\": \"0.0\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "      \"serviceProvider\": \"MMPL\",\n" +
//                        "      \"serviceInputs\": \"mmpl\",\n" +
//                        "      \"failureReason\": \"\",\n" +
//                        "      \"dateTime\": \"2016-11-0915:31:46\",\n" +
//                        "      \"transactionId\": \"1611090001018344\",\n" +
//                        "      \"type\": \"CREDIT\",\n" +
//                        "      \"day\": \"WEDNESDAY\",\n" +
//                        "      \"month\": \"NOV\",\n" +
//                        "      \"year\": 2016,\n" +
//                        "      \"date\": 9,\n" +
//                        "      \"category\": \"MMPL\",\n" +
//                        "      \"amount\": \"2.00\",\n" +
//                        "      \"status\": \"Success\",\n" +
//                        "      \"fee\": \"0.0\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "      \"serviceProvider\": \"MMPL\",\n" +
//                        "      \"serviceInputs\": \"mmpl\",\n" +
//                        "      \"failureReason\": \"\",\n" +
//                        "      \"dateTime\": \"2016-11-1010:04:21\",\n" +
//                        "      \"transactionId\": \"1611100001018262\",\n" +
//                        "      \"type\": \"CREDIT\",\n" +
//                        "      \"day\": \"THURSDAY\",\n" +
//                        "      \"month\": \"NOV\",\n" +
//                        "      \"year\": 2016,\n" +
//                        "      \"date\": 10,\n" +
//                        "      \"category\": \"MMPL\",\n" +
//                        "      \"amount\": \"879.00\",\n" +
//                        "      \"status\": \"Success\",\n" +
//                        "      \"fee\": \"0.0\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "      \"serviceProvider\": \"MMPL\",\n" +
//                        "      \"serviceInputs\": \"mmpl\",\n" +
//                        "      \"failureReason\": \"\",\n" +
//                        "      \"dateTime\": \"2016-11-1010:05:17\",\n" +
//                        "      \"transactionId\": \"1611100001018263\",\n" +
//                        "      \"type\": \"CREDIT\",\n" +
//                        "      \"day\": \"THURSDAY\",\n" +
//                        "      \"month\": \"NOV\",\n" +
//                        "      \"year\": 2016,\n" +
//                        "      \"date\": 10,\n" +
//                        "      \"category\": \"MMPL\",\n" +
//                        "      \"amount\": \"10.00\",\n" +
//                        "      \"status\": \"Success\",\n" +
//                        "      \"fee\": \"0.0\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "      \"serviceProvider\": \"MMPL\",\n" +
//                        "      \"serviceInputs\": \"mmpl\",\n" +
//                        "      \"failureReason\": \"\",\n" +
//                        "      \"dateTime\": \"2016-11-1010:05:29\",\n" +
//                        "      \"transactionId\": \"1611100001018346\",\n" +
//                        "      \"type\": \"CREDIT\",\n" +
//                        "      \"day\": \"THURSDAY\",\n" +
//                        "      \"month\": \"NOV\",\n" +
//                        "      \"year\": 2016,\n" +
//                        "      \"date\": 10,\n" +
//                        "      \"category\": \"MMPL\",\n" +
//                        "      \"amount\": \"2.00\",\n" +
//                        "      \"status\": \"Success\",\n" +
//                        "      \"fee\": \"0.0\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "      \"serviceProvider\": \"MMPL\",\n" +
//                        "      \"serviceInputs\": \"mmpl\",\n" +
//                        "      \"failureReason\": \"\",\n" +
//                        "      \"dateTime\": \"2016-11-1010:44:17\",\n" +
//                        "      \"transactionId\": \"1611100001018264\",\n" +
//                        "      \"type\": \"CREDIT\",\n" +
//                        "      \"day\": \"THURSDAY\",\n" +
//                        "      \"month\": \"NOV\",\n" +
//                        "      \"year\": 2016,\n" +
//                        "      \"date\": 10,\n" +
//                        "      \"category\": \"MMPL\",\n" +
//                        "      \"amount\": \"1.00\",\n" +
//                        "      \"status\": \"Success\",\n" +
//                        "      \"fee\": \"0.0\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "      \"serviceProvider\": \"MMPL\",\n" +
//                        "      \"serviceInputs\": \"mmpl\",\n" +
//                        "      \"failureReason\": \"\",\n" +
//                        "      \"dateTime\": \"2016-11-2214:04:55\",\n" +
//                        "      \"transactionId\": \"1611220001020367\",\n" +
//                        "      \"type\": \"CREDIT\",\n" +
//                        "      \"day\": \"TUESDAY\",\n" +
//                        "      \"month\": \"NOV\",\n" +
//                        "      \"year\": 2016,\n" +
//                        "      \"date\": 22,\n" +
//                        "      \"category\": \"MMPL\",\n" +
//                        "      \"amount\": \"1.00\",\n" +
//                        "      \"status\": \"Success\",\n" +
//                        "      \"fee\": \"0.0\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "      \"serviceProvider\": \"MMPL\",\n" +
//                        "      \"serviceInputs\": \"mmpl\",\n" +
//                        "      \"failureReason\": \"\",\n" +
//                        "      \"dateTime\": \"2016-12-0215:29:35\",\n" +
//                        "      \"transactionId\": \"1612020001023249\",\n" +
//                        "      \"type\": \"CREDIT\",\n" +
//                        "      \"day\": \"FRIDAY\",\n" +
//                        "      \"month\": \"DEC\",\n" +
//                        "      \"year\": 2016,\n" +
//                        "      \"date\": 2,\n" +
//                        "      \"category\": \"MMPL\",\n" +
//                        "      \"amount\": \"1.00\",\n" +
//                        "      \"status\": \"Success\",\n" +
//                        "      \"fee\": \"0.0\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "      \"serviceProvider\": \"MMPL\",\n" +
//                        "      \"serviceInputs\": \"mmpl\",\n" +
//                        "      \"failureReason\": \"\",\n" +
//                        "      \"dateTime\": \"2016-12-0215:52:20\",\n" +
//                        "      \"transactionId\": \"1612020001023371\",\n" +
//                        "      \"type\": \"CREDIT\",\n" +
//                        "      \"day\": \"FRIDAY\",\n" +
//                        "      \"month\": \"DEC\",\n" +
//                        "      \"year\": 2016,\n" +
//                        "      \"date\": 2,\n" +
//                        "      \"category\": \"MMPL\",\n" +
//                        "      \"amount\": \"2.00\",\n" +
//                        "      \"status\": \"Success\",\n" +
//                        "      \"fee\": \"0.0\"\n" +
//                        "    },\n" +
//                        "    {\n" +
//                        "      \"serviceProvider\": \"MMPL\",\n" +
//                        "      \"serviceInputs\": \"mmpl\",\n" +
//                        "      \"failureReason\": \"\",\n" +
//                        "      \"dateTime\": \"2016-12-0216:48:00\",\n" +
//                        "      \"transactionId\": \"1612020001023283\",\n" +
//                        "      \"type\": \"CREDIT\",\n" +
//                        "      \"day\": \"FRIDAY\",\n" +
//                        "      \"month\": \"DEC\",\n" +
//                        "      \"year\": 2016,\n" +
//                        "      \"date\": 2,\n" +
//                        "      \"category\": \"MMPL\",\n" +
//                        "      \"amount\": \"3.00\",\n" +
//                        "      \"status\": \"Success\",\n" +
//                        "      \"fee\": \"0.0\"\n" +
//                        "    }\n" +
//                        "  ],\n" +
//                        "  \"count\": 10\n" +
//                        "}";


                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            JSONArray jsonArray = jsonMainObj.getJSONArray("transctionDetaillist");
                            //// System.out.println("I am here>>>>>>>>"+ jsonArray.length());

                            desc =new ArrayList<String>();
                            amount = new ArrayList<String>();
                            date = new ArrayList<String>();
                            day = new ArrayList<String>();
                            month = new ArrayList<String>();
                            year = new ArrayList<String>();
                            type = new ArrayList<String>();
                            status = new ArrayList<String>();
                            transId = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject j1 = jsonArray.getJSONObject(i);
                                //// System.out.println("Category..............."+j1.getString("category"));
                                desc.add(j1.getString("category"));
                                amount.add(j1.getString("amount"));
                                date.add(j1.getString("date"));
                                day.add(j1.getString("day"));
                                month.add(j1.getString("month"));
                                year.add(j1.getString("year"));
                                type.add(j1.getString("type"));
                                status.add(j1.getString("status"));
                                transId.add(j1.getString("transactionId"));

                            }

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

            progressBar.setVisibility(View.GONE);

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {
                txtTransactionError.setVisibility(View.GONE);

                try {
                    final ListView lstView = getListView();

                    lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    adapter = new TransactionAdapter(TxnHistoryMerchantActivity.this,R.layout.layout_paid, lstView, amount, desc, date, day, month, year, type,status,transId);
                    setListAdapter(adapter);
                }
                catch (Exception e)
                {

                }



            } else if (result.equalsIgnoreCase("Failure")) {

                try {
                    AlertBuilder alertBuilder = new AlertBuilder(TxnHistoryMerchantActivity.this);
                    alertBuilder.showAlert(getResources().getString(R.string.apidown));
                }
                catch (Exception e)
                {

                }



            } else {

                txtTransactionError.setVisibility(View.VISIBLE);
                txtTransactionError.setText(result);
            }

        }

    }

    public class TransactionAdapter extends ArrayAdapter<String> {

        private static final int MODE_PRIVATE = 0;
        Context context;
        ListView listView;
        int layout_id;

        ArrayList<String> amount;
        ArrayList<String> desc;
        ArrayList<String> day;
        ArrayList<String> date;
        ArrayList<String> month;
        ArrayList<String> year;
        ArrayList<String> type;
        ArrayList<String> status;
        ArrayList<String> transid;


        public TransactionAdapter(Context context, int resource, ListView lstView, ArrayList<String> amount, ArrayList<String> desc, ArrayList<String> date, ArrayList<String> day, ArrayList<String> month, ArrayList<String> year, ArrayList<String> type, ArrayList<String> status, ArrayList<String> transid) {
            super(context, resource);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.layout_id = resource;
            this.listView = lstView;
            // this.message=message;
            this.amount = amount;
            this.desc = desc;
            this.day = day;
            this.date = date;
            this.month = month;
            this.year = year;
            this.type = type;
            this.status = status;
            this.transid = transid;


        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return amount.size();
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            try {


                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View rowView = inflater.inflate(layout_id, null, true);


                TextView txtDesc = (TextView) rowView.findViewById(R.id.txtDesc);
                TextView txtAmount = (TextView) rowView.findViewById(R.id.txtAmount);
                TextView txtDate = (TextView) rowView.findViewById(R.id.txtDate);
                ImageView imgMode = (ImageView) rowView.findViewById(R.id.imgMode);
                TextView txtTransId=(TextView)rowView.findViewById(R.id.txtTransId);
                TextView txtStatus=(TextView)rowView.findViewById(R.id.txtStatus);



                txtAmount.setText(context.getResources().getString(R.string.rupee_symbol)+amount.get(position));
                txtDesc.setText(desc.get(position));
                txtDate.setText(date.get(position) + " " + month.get(position) + " " + year.get(position));
                txtTransId.setText(Html.fromHtml("Transaction Id : "+transid.get(position)));
                txtStatus.setText(Html.fromHtml("Status : "+status.get(position)));
                if(type.isEmpty())
                    imgMode.setImageResource(R.drawable.minus_mode);
                else if (type.get(position).equalsIgnoreCase("credit"))
                    imgMode.setImageResource(R.drawable.plus_mode);
                else if (type.get(position).equalsIgnoreCase("debit"))
                    imgMode.setImageResource(R.drawable.minus_mode);


//            txtDay.setText(day.get(position));
//            txtMonth.setText(month.get(position));


                return rowView;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                return null;
            }
        }

    }

}
