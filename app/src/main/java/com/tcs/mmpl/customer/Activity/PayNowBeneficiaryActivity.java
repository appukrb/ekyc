package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.tcs.mmpl.customer.Adapter.BeneficiaryDetails;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PayNowBeneficiaryActivity extends Activity {

    private MyDBHelper dbHelper;
    private Spinner spinner;

    private SharedPreferences pref;
    SharedPreferences.Editor editor;

    private Button btnPayNow;
    private EditText edtAmount,edtMPin;
    private String strbeneficiaryCode;

    FontClass fontclass = new FontClass();
    Typeface typeface;

    LinearLayout linParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pay_now_beneficiary);

        linParent = (LinearLayout) findViewById(R.id.linParent);

        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);


        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        dbHelper = new MyDBHelper(getApplicationContext());
        spinner = (Spinner)findViewById(R.id.spinner);

        edtAmount = (EditText)findViewById(R.id.edtAmount);
        edtMPin = (EditText)findViewById(R.id.edtMpin);

        btnPayNow = (Button)findViewById(R.id.btnPayNow);
        String beneficiaryurl = getResources().getString(R.string.getBeneficiary) + "?MDN=" + pref.getString("mobile_number", "") ;


        //// System.out.println(beneficiaryurl);
        GetBeneficiary getBeneficiary = new GetBeneficiary(getApplicationContext());
        getBeneficiary.execute(beneficiaryurl);


        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertBuilder alertBuilder = new AlertBuilder(PayNowBeneficiaryActivity.this);
                if(edtAmount.getText().toString().trim().equalsIgnoreCase(""))
                {
                    alertBuilder.showAlert(getResources().getString(R.string.invalid_amount));
                }
                else if(edtMPin.getText().toString().trim().equalsIgnoreCase(""))
                {
                    alertBuilder.showAlert(getResources().getString(R.string.invalid_mpin));
                }
                else
                {
                    String transfertobankurl = getResources().getString(R.string.transfertobank) + "?mobileNo1=" + pref.getString("mobile_number", "") + "&benefiaryCode=" + strbeneficiaryCode + "&Amount=" + edtAmount.getText().toString().trim() + "&Mpin=" +  edtMPin.getText().toString().trim();

                    TransferToBankAsync transfertobank = new TransferToBankAsync(getApplicationContext());
                    transfertobank.execute(transfertobankurl);

                    //// System.out.println(transfertobankurl);
                }


            }
        });




    }

    private class GetBeneficiary extends AsyncTask<String, Void, String> {

        Context context;
        int flag = 0;

        private ArrayList<String> beneficiaryID,beneficiaryNickName,mobileNumber,accountNumber,beneficiaryCode;
        private ProgressDialog pDialog;

        public GetBeneficiary(Context context) {
            this.context = context;

            beneficiaryID = new ArrayList<String>();
            beneficiaryNickName = new ArrayList<String>();
            mobileNumber =new ArrayList<String>();
            accountNumber = new ArrayList<String>();
            beneficiaryCode = new ArrayList<String>();


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PayNowBeneficiaryActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(PayNowBeneficiaryActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);



                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {

                        dbHelper.fun_deleteAll_tbl_beneficiary_details();
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            flag =1;
                            JSONArray jsonArray = jsonMainObj.getJSONArray("beneficiaryDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject j1 = (JSONObject) jsonArray.get(i);
//
                                dbHelper.fun_insert_tbl_beneficiary_details(j1.getString("beneficiaryCode"), j1.getString("beneficiaryNickname"), j1.getString("accountNumber"),
                                        j1.getString("accountType"), j1.getString("mobileNumber"),j1.getString("bankType"),j1.getString("bankName"),
                                        j1.getString("branchName"),j1.getString("beneficiaryAadhaarNo"),j1.getString("aadharStatus"),j1.getString("routingBankType"),j1.getString("validateStatus"),j1.getString("beneficiaryName"),j1.getString("ifsccode"));


                            }

                            return "Success";

                        }  else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("No Beneficiary")) {

                            flag = 0;

                            return "Success";

                        } else if(jsonMainObj.getString("responseStatus").equalsIgnoreCase("Failure")) {
                            return jsonMainObj.getString("responseMessage");
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
                    return "Success";
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

                if (flag == 1) {

                    BeneficiaryDetails bd = new BeneficiaryDetails();
                    Cursor cursor = dbHelper.fun_select_tbl_beneficiary_details();
                    int len = cursor.getCount();


                    if (len > 0) {
                        List<String> categories = new ArrayList<String>();
                        if (cursor.moveToFirst()) {
                            // Spinner Drop down elements

                            do {

                                beneficiaryID.add(cursor.getString(0));
                                beneficiaryCode.add(cursor.getString(1));
                                beneficiaryNickName.add(cursor.getString(2));
                                accountNumber.add(cursor.getString(4));
                                mobileNumber.add(cursor.getString(8));

                                bd.setBeneficiaryID(cursor.getString(0));
                                bd.setBeneficiaryName(cursor.getString(2));
                                bd.setMobileNumber(cursor.getString(8));
                                bd.setAccountNumber(cursor.getString(4));

                                categories.add(cursor.getString(2)+"-"+cursor.getString(4));



                            }while (cursor.moveToNext());


                        }



                        // Creating adapter for spinner
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PayNowBeneficiaryActivity.this, android.R.layout.simple_spinner_item, categories);

                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        spinner.setAdapter(dataAdapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String item = parent.getItemAtPosition(position).toString();

                                strbeneficiaryCode = dbHelper.fun_select_beneficiary_code(item.split("-")[1]);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }



                } else if (flag == 0) {

                    AlertDialog alertDialog = new AlertDialog.Builder(
                            PayNowBeneficiaryActivity.this).create();

                    // Setting Dialog Title
                    alertDialog.setTitle(getResources().getString(R.string.display_app_name));

                    // Setting Dialog Message
                    alertDialog.setMessage("No Beneficiary Found");

                    // Setting Icon to Dialog
                    // alertDialog.setIcon(R.drawable.tick);

                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.dismiss();
//                            viewFlipper_managebeneficiary.setDisplayedChild(1);
                        }
                    });

                    alertDialog.setCancelable(false);
                    // Showing Alert Message
                    alertDialog.show();
                    //  linErrorBeneficiary.setVisibility(View.VISIBLE);
                }


                // addHeaders();
                //addData();

            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {


                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }

        }

    }

    private class TransferToBankAsync extends AsyncTask<String, Void, String> {

        String transactionfee, totalAmount, dateTime, product, amount, remark, status;
        Context context;
        ProgressDialog pDialog;

        public TransferToBankAsync(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PayNowBeneficiaryActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(PayNowBeneficiaryActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);


                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {


                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("status").equalsIgnoreCase("SUCCESS")) {

                            status = jsonMainObj.getString("status");
                            transactionfee = jsonMainObj.getString("transcationFee");
                            totalAmount = jsonMainObj.getString("totalAmount");
                            dateTime = jsonMainObj.getString("dateTime");
                            product = jsonMainObj.getString("product");
                            amount = jsonMainObj.getString("amount");
                            remark = jsonMainObj.getString("remark");

                            return "Success";

                        } else if (jsonMainObj.getString("status").equalsIgnoreCase("ERROR")) {

                            status = jsonMainObj.getString("status");
                            transactionfee = jsonMainObj.getString("transcationFee");
                            totalAmount = jsonMainObj.getString("totalAmount");
                            dateTime = jsonMainObj.getString("dateTime");
                            product = jsonMainObj.getString("product");
                            amount = jsonMainObj.getString("amount");
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


            AlertBuilder alertBuilder = new AlertBuilder(PayNowBeneficiaryActivity.this);

            if (result.equalsIgnoreCase("Success")) {

                alertBuilder.showAlert(remark);

            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {


                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }

        }

    }


}
