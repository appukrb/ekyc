package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.PopupBuilder;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONObject;

public class DenominationTransactionActivity extends Activity {


    private ImageView imgBrand;
    private TextView edtBeneficiaryName, edtBeneficiaryPhoneNumber, edtBeneficiaryEmail, edtMpin, edtPersonalMessage, edtValue, edtQuantity, txtAmount;
    private Button btnDenomination;

    private int amount = 0;
    private SharedPreferences pref;
    SharedPreferences.Editor editor;

    FontClass fontclass=new FontClass();
    Typeface typeface;
    private LinearLayout linParent;
    private SharedPreferences userInfoPref;
    private SharedPreferences.Editor userInfoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_denomination_transaction);

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        linParent = (LinearLayout)findViewById(R.id.linParent);
        typeface= Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        imgBrand = (ImageView) findViewById(R.id.imgBrand);

        edtBeneficiaryName = (EditText) findViewById(R.id.edtBeneficiaryName);
        edtBeneficiaryPhoneNumber = (EditText) findViewById(R.id.edtBeneficiaryPhone);
        edtBeneficiaryEmail = (EditText) findViewById(R.id.edtBeneficiaryEmail);
        edtMpin = (EditText) findViewById(R.id.edtMpin);
        edtPersonalMessage = (EditText) findViewById(R.id.edtBeneficiaryPersonalMessage);
        edtValue = (EditText) findViewById(R.id.edtValue);
        edtQuantity = (EditText) findViewById(R.id.edtQuantity);
        txtAmount = (TextView) findViewById(R.id.txtAmount);
        btnDenomination = (Button) findViewById(R.id.btnDenomination);

        Picasso.with(this).load(getIntent().getStringExtra("image")).placeholder(R.drawable.backgroud_default_image).into(imgBrand);
        edtValue.setText(getIntent().getStringExtra("name"));
        edtValue.setEnabled(false);
        edtValue.setFocusable(false);

        edtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() > 0) {
                    amount = 0;

                    amount = Integer.parseInt(edtValue.getText().toString().trim()) * Integer.parseInt(edtQuantity.getText().toString().trim());

                    txtAmount.setText("Total Amount - " + getResources().getString(R.string.rupee_symbol) + " " + amount);
                } else {
                    txtAmount.setText("Total Amount - " + getResources().getString(R.string.rupee_symbol) + " 0");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (edtQuantity.getText().toString().trim().equalsIgnoreCase(""))
            txtAmount.setText("Total Amount - " + getResources().getString(R.string.rupee_symbol) + " 0");
        else {
            amount = 0;
             amount = Integer.parseInt(edtValue.getText().toString().trim()) * Integer.parseInt(edtQuantity.getText().toString().trim());

            txtAmount.setText("Total Amount - " + getResources().getString(R.string.rupee_symbol) + " " + amount);
        }


        btnDenomination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertBuilder alertBuilder = new AlertBuilder(DenominationTransactionActivity.this);
                if(edtQuantity.getText().toString().trim().equalsIgnoreCase("") || edtQuantity.getText().toString().trim().equalsIgnoreCase("0") )
                {
                    alertBuilder.showAlert("Please enter quantity");
                }
                else if (edtBeneficiaryName.getText().toString().trim().equalsIgnoreCase("")) {
                    alertBuilder.showAlert(getResources().getString(R.string.valid_name));
                } else if (edtBeneficiaryPhoneNumber.getText().toString().trim().equalsIgnoreCase("")) {
                    alertBuilder.showAlert(getResources().getString(R.string.invalid_mobile_number));

                } else if (edtBeneficiaryEmail.getText().toString().trim().equalsIgnoreCase("") || !android.util.Patterns.EMAIL_ADDRESS.matcher(edtBeneficiaryEmail.getText().toString().trim()).matches()) {
                    alertBuilder.showAlert(getResources().getString(R.string.invalid_email));

                } else if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {
                    alertBuilder.showAlert(getResources().getString(R.string.invalid_mpin));

                } else if (edtPersonalMessage.getText().toString().trim().equalsIgnoreCase("")) {
                    alertBuilder.showAlert(getResources().getString(R.string.valid_name));

                } else {
                    String gci_order_url = getResources().getString(R.string.add_order_url)
                            + "?accessToken=" + getIntent().getStringExtra("accesstoken")
                            + "&skuId=" + getIntent().getStringExtra("skuid")
                            + "&Name=" +  Uri.encode(edtBeneficiaryName.getText().toString().trim(),"utf-8")
                            + "&Qty=" + edtQuantity.getText().toString().trim()
                            + "&Amount=" + amount
                            + "&Email=" + Uri.encode(edtBeneficiaryEmail.getText().toString().trim(),"utf-8")
                            + "&MDN=" + pref.getString("mobile_number", "")
                            +"&payeeMobileNo=91"+edtBeneficiaryPhoneNumber.getText().toString().trim()
                            +"&PersonalMessage="+Uri.encode(edtPersonalMessage.getText().toString().trim(),"utf-8")
                            +"&Brand="+ Uri.encode(getIntent().getStringExtra("brandname"),"utf-8")
                            +"&MPIN="+edtMpin.getText().toString().trim();

                    CheckBalance checkBalance = new CheckBalance(DenominationTransactionActivity.this);
                    if(checkBalance.getBalanceCheck(String.valueOf(amount))) {

                        PopupBuilder popup = new PopupBuilder(DenominationTransactionActivity.this);
                        popup.showPopup(checkBalance.getAmount(String.valueOf(amount)));
                        userInfoEditor.putString("transaction_url", gci_order_url);
                        userInfoEditor.putString("transaction_method","POST2");
                        userInfoEditor.putString("transaction_flag","3");
                        userInfoEditor.commit();
                    }

                    else {

                        AddGCIOrder addGCIOrder = new AddGCIOrder(getApplicationContext());
                        addGCIOrder.execute(gci_order_url);
                    }

                }
            }
        });


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
            pDialog = new ProgressDialog(DenominationTransactionActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler webServiceHandler = new WebServiceHandler(DenominationTransactionActivity.this);
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
                        new LocalNotification(DenominationTransactionActivity.this,jsonObject.getString(getApplicationContext().getResources().getString(R.string.notificationTitle)),jsonObject.getString(getApplicationContext().getResources().getString(R.string.notificationMessage))).sendNotification();
                    }


                    AlertBuilder alertBuilder = new AlertBuilder(DenominationTransactionActivity.this);
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
                    AlertBuilder alertBuilder = new AlertBuilder(DenominationTransactionActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(jsonObject.getString("responseMessage"));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            //getActivity().finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                } else {
                    AlertBuilder alertBuilder = new AlertBuilder(DenominationTransactionActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
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

}
