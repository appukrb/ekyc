package com.tcs.mmpl.customer.fragments;

/**
 * Created by Admin on 9/10/2015.
 */

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Activity.ServiceProviderListActivity;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.PopupBuilder;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class Gas extends Fragment {

    private static final int MODE_PRIVATE = 0;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    private Button button;
    private ViewFlipper viewFlipper_gas;
    private Button btn_confirm;
    Spinner spnr_gas_oprator;
    String[] oprator_gas;

    EditText edtGasNumber,edtAmountGas,edtPromocode,edtMpin;
    TextView txtOperatorGas;

    ProgressDialog pDialog;
    CheckBox chkFavGas;
    private String parameterType,parameterValue,URL;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    ConnectionDetector connectionDetector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_gas,
                container, false);
        typeface=Typeface.createFromAsset(getActivity().getAssets(),"helvetica.otf");
        init(rootView);

        onClick();


        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)) {
                    if (viewFlipper_gas.getDisplayedChild() == 2) {
                        viewFlipper_gas.setDisplayedChild(1);
                    } else if (viewFlipper_gas.getDisplayedChild() == 1) {
                        viewFlipper_gas.setDisplayedChild(0);
                    } else {
                        getActivity().finish();
//                        Intent i = new Intent(getActivity(), HomeScreenActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(i);
                    }


                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    private void onClick() {
        // TODO Auto-generated method stub

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                AlertBuilder alert = new AlertBuilder(getActivity());
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                if(edtGasNumber.getText().toString().trim().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_ca_number));
                }
                else if(txtOperatorGas.getText().toString().trim().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_service_provider));
                }
                else if(edtAmountGas.getText().toString().trim().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_amount));
                }
                else if(edtMpin.getText().toString().trim().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_mpin));
                }
                else if (edtGasNumber.getText().toString().trim().equalsIgnoreCase("") || edtAmountGas.getText().toString().trim().equalsIgnoreCase("") || txtOperatorGas.getText().toString().equalsIgnoreCase("") || edtMpin.getText().toString().trim().equalsIgnoreCase("")) {

                    alert.showAlert(getResources().getString(R.string.validation));
                } else {




                            String gasRecharge_url = "";
                            String mobileNo;
                            String category;
                            String rechargeMobile;
                            String amount;
                            String billerCode;
                            String mpin;
                            String BU = "OTHERS";



                            mobileNo = pref.getString("mobile_number", "");
                            category = Uri.encode("Utility Payments", "utf-8");
                            rechargeMobile = edtGasNumber.getText().toString().trim();
                            amount = edtAmountGas.getText().toString().trim();
                            billerCode = Uri.encode(txtOperatorGas.getText().toString().trim(), "utf-8");
                            mpin = edtMpin.getText().toString();

                            String category1=Uri.decode(category);

                            String BingoString = mobileNo + "|" + ""+"|"+mpin + "|" + category1+ "|" +""+"|"+""+"|"+""+"|"+billerCode+"|"+rechargeMobile+"|"+amount+"|"+""+"|"+BU;
                            String res = Bingo.Bingo_one(BingoString);
                            parameterType = "Gas";
                            parameterValue = mobileNo + "|" + amount + "|" + rechargeMobile + "|" + category + "|" + billerCode ;
                            URL = getActivity().getResources().getString(R.string.billPayment) + "?mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount +  "&BU=" + BU;

                            gasRecharge_url = getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU +"&Checksum="+res;


                            //// System.out.println("URL ......" + gasRecharge_url);
                            CheckBalance checkBalance = new CheckBalance(getActivity());
                            if(checkBalance.getBalanceCheck(amount))
                            {

                                PopupBuilder popup = new PopupBuilder(getActivity());
                                popup.showPopup(checkBalance.getAmount(amount));

                                userInfoEditor.putString("transaction_url", gasRecharge_url);
                                userInfoEditor.putString("transaction_method","POST2");
                                userInfoEditor.putString("transaction_flag","1");
                                userInfoEditor.commit();

                            }
                            else {

                                GasRecharge gasRecharge = new GasRecharge(getActivity());
                                gasRecharge.execute(gasRecharge_url);
                            }






                }


                } else {

                    alert.newUser();

                }

            }
        });


        txtOperatorGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionDetector.isConnectingToInternet())
                {

                    Intent i =new Intent(getActivity(),ServiceProviderListActivity.class);
                    i.putExtra("url",getResources().getString(R.string.service_provider));
                    i.putExtra("type","GAS");
                    startActivityForResult(i, 2);
//                    String operatorurl = "http://nexgenfmpl.com/numberdemo/WebService/listUniqDthOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665";
//                    GetOperator getOperator = new GetOperator(getActivity());
//                    getOperator.execute(operatorurl);
                }
                else {
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();;
                }
            }
        });

    }

    private void init(View rootView) {
        // TODO Auto-generated method stub

        button = (Button) rootView.findViewById(R.id.btnPaymentGas);
        viewFlipper_gas = (ViewFlipper) rootView.findViewById(R.id.viewflipper_gas);
        fontclass.setFont(viewFlipper_gas,typeface );

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        connectionDetector = new ConnectionDetector(getActivity());

        edtGasNumber = (EditText)rootView.findViewById(R.id.edtGasNumber);
        edtAmountGas = (EditText)rootView.findViewById(R.id.edtAmountGas);
       // edtBillCycleGas = (EditText)rootView.findViewById(R.id.edtBillCycleGas);
        edtPromocode = (EditText)rootView.findViewById(R.id.edtPromocode);
        edtMpin = (EditText)rootView.findViewById(R.id.edtMpin);
        txtOperatorGas = (TextView)rootView.findViewById(R.id.txtOperatorGas);
        chkFavGas = (CheckBox)rootView.findViewById(R.id.chkFavGas);

        button.setTypeface(button.getTypeface(),Typeface.BOLD);
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {


        private Context context;
//		LayoutInflater inflater;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub

        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent)
        {


            View v = super.getDropDownView( position,  convertView, parent);
            Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
            ((TextView)v).setTypeface(custom_font);
            ((TextView)v).setPadding(10,10,10,10);

            return v;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {


            View v = super.getView( position,  convertView, parent);
            Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
            ((TextView)v).setTypeface(custom_font);
            return v;
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    private void openAlert(AlertDialog.Builder alertDialog)
    {
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.tick);
        // Setting OK Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

                dialog.cancel();

                edtGasNumber.setText("");
                edtAmountGas.setText("");
                edtPromocode.setText("");
                chkFavGas.setChecked(false);
                txtOperatorGas.setText("");
                edtMpin.setText("");

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                dialog.cancel();
                getActivity().finish();
                Intent i = new Intent(getActivity(), HomeScreenActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(i);

            }
        });
        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        //// System.out.println(requestCode+" "+resultCode );
         if(requestCode == 2){

           try {
               txtOperatorGas.setText(data.getStringExtra("result"));
           }
           catch (Exception e)
           {

           }

        }

    }
    private class GasRecharge extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

        String firstName, lastName, walletBalance;

        public GasRecharge(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            responseMessage = jsonMainObj.getString("responseMessage");

                            if(jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                            {
                                new LocalNotification(getActivity(),jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationTitle)),jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationMessage))).sendNotification();
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

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {

                String url = getResources().getString(R.string.promocode)+"?MDN="+ pref.getString("mobile_number", "")+"&PROMOCODE="+edtPromocode.getText().toString().trim()+"&Transaction=Landline";
                updatePromocode up = new updatePromocode(getActivity());
                up.execute(url);

                if(chkFavGas.isChecked())
                {
                    String addFavorites_url = getActivity().getResources().getString(R.string.addfavourites);
                    AddFavorites addFavorites = new AddFavorites(getActivity());
                    addFavorites.execute(addFavorites_url);

                }


                AlertBuilder alert = new AlertBuilder(getActivity());
                AlertDialog.Builder alertDialog = alert.showRetryAlert(responseMessage+getResources().getString(R.string.make_payment));
                openAlert(alertDialog);



            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

                AlertBuilder alert = new AlertBuilder(getActivity());
                alert.showAlert(result);
            }

        }

    }

    private class updatePromocode extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        public updatePromocode(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(RegisterActivity.this);
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
//                JSONObject jsonObject = new JSONObject();
//
//                jsonObject.accumulate("MDN", pref.getString("mobile_number",""));
//                jsonObject.accumulate("FirstName", Uri.encode(edtFullName.getText().toString(), "utf-8"));
//                jsonObject.accumulate("EMailId", Uri.encode(edtEmailID.getText().toString(),"utf-8"));
//                jsonObject.accumulate("DateOfBirth", Uri.encode(edtDate.getText().toString(),"utf-8"));
//
//
//
//                // 4. convert JSONObject to JSON to String
//                String json = jsonObject.toString();
//
//                // 5. set json to StringEntity
//                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {

                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failure";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }

            return "Sucess";
        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);


        }

    }

    private class AddFavorites extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        public AddFavorites(Context context) {
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
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("MDN", pref.getString("mobile_number", ""));
                jsonObject.accumulate("parameterType",parameterType);
                jsonObject.accumulate("prarameterValue", parameterValue);
                jsonObject.accumulate("URL", URL);

                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity(), se);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            return jsonMainObj.getString("responseMessage");


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

//            if (pDialog.isShowing())
//                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

//                AlertDialog alertDialog = new AlertDialog.Builder(
//                        getActivity()).create();
//
//                // Setting Dialog Title
//                alertDialog.setTitle("mRupee");
//
//                // Setting Dialog Message
//                alertDialog.setMessage(result);
//
//                // Setting Icon to Dialog
//                // alertDialog.setIcon(R.drawable.tick);
//
//                // Setting OK Button
//                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Write your code here to execute after dialog closed
//                        dialog.cancel();
//                    }
//                });
//
//                // Showing Alert Message
//                alertDialog.show();

                // Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

        }

    }
}



