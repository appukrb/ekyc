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
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
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

public class ElectriCity extends Fragment
{
    private static final int MODE_PRIVATE = 0;
    private Button button;
    private ViewFlipper viewFlipper_electricity;
    private Button btn_confirm;
    Spinner spnr_elec_location;
    String[] location;

    FontClass fontclass=new FontClass();
    Typeface typeface;

    private EditText edtNumberElectricity,edtAmountElectricity,edtMpinElectricity,edtPromocode,edtBillCycleElectricity;
    private TextView txtOperatorElectricity;

    private CheckBox chkFavElectricity;
    private Button btnPaymentElectricity;

    private String parameterType,parameterValue,URL;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_electricity,
                container, false);
        typeface=Typeface.createFromAsset(getActivity().getAssets(),"helvetica.otf");

        init(rootView);

        onClick();


        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)) {
                    if (viewFlipper_electricity.getDisplayedChild() == 2) {
                        viewFlipper_electricity.setDisplayedChild(1);
                    } else if (viewFlipper_electricity.getDisplayedChild() == 1) {
                        viewFlipper_electricity.setDisplayedChild(0);
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

        btnPaymentElectricity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

             //   viewFlipper_electricity.setDisplayedChild(1);

                AlertBuilder alert = new AlertBuilder(getActivity());
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                if(edtNumberElectricity.getText().toString().trim().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_ca_number));
                }
                else if (txtOperatorElectricity.getText().toString().trim().equalsIgnoreCase("")){

                    alert.showAlert(getResources().getString(R.string.invalid_service_provider));
                }
                else if (edtAmountElectricity.getText().toString().trim().equalsIgnoreCase("")){

                    alert.showAlert(getResources().getString(R.string.invalid_amount));
                }
//                else if (edtBillCycleElectricity.getText().toString().trim().equalsIgnoreCase("")){
//
//                    alert.showAlert(getResources().getString(R.string.billcycle_limit));
//                }
                else if (edtMpinElectricity.getText().toString().trim().equalsIgnoreCase("")){

                    alert.showAlert(getResources().getString(R.string.invalid_mpin));
                }
                else if (edtNumberElectricity.getText().toString().trim().equalsIgnoreCase("") || edtAmountElectricity.getText().toString().trim().equalsIgnoreCase("") || txtOperatorElectricity.getText().toString().equalsIgnoreCase("") || edtMpinElectricity.getText().toString().trim().equalsIgnoreCase("")) {

                    alert.showAlert( getResources().getString(R.string.invalid_ca_number));
                }
                else {

//                    int billCycle = Integer.parseInt(edtBillCycleElectricity.getText().toString().trim());
//                    if (billCycle > 0 && billCycle <= 31) {

                            String electricityRecharge_url = "";
                            String mobileNo;
                            String category;
                            String rechargeMobile;
                            String amount;
                            String billerCode;
                            String mpin;
                            String BU = "OTHERS";


                            mobileNo = pref.getString("mobile_number", "");
                            category = Uri.encode("Utility Payments", "utf-8");
                            rechargeMobile = edtNumberElectricity.getText().toString().trim();
                            amount = edtAmountElectricity.getText().toString().trim();
                            billerCode = Uri.encode(txtOperatorElectricity.getText().toString().trim(), "utf-8");
                            mpin = edtMpinElectricity.getText().toString();

                            String category1=Uri.decode(category);

                            String BingoString = mobileNo + "|" + ""+"|"+mpin + "|" + category1+ "|" +""+"|"+""+"|"+""+"|"+billerCode+"|"+rechargeMobile+"|"+amount+"|"+""+"|"+BU;
                            String res = Bingo.Bingo_one(BingoString);
                            parameterType = "Electricity";
                            parameterValue = mobileNo + "|" + amount + "|" + rechargeMobile + "|" + category + "|" + billerCode + "|" ;
                            URL = getActivity().getResources().getString(R.string.billPayment) + "?mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&billCycle=&BU=" + BU;

                            electricityRecharge_url = getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&billCycle=&BU=" + BU+"&Checksum="+res;


                            //// System.out.println("URL ......" + electricityRecharge_url);

                            CheckBalance checkBalance = new CheckBalance(getActivity());
                            if(checkBalance.getBalanceCheck(amount))
                            {

                                PopupBuilder popup = new PopupBuilder(getActivity());
                                popup.showPopup(checkBalance.getAmount(amount));

                                userInfoEditor.putString("transaction_url", electricityRecharge_url);
                                userInfoEditor.putString("transaction_method","POST2");
                                userInfoEditor.putString("transaction_flag","1");
                                userInfoEditor.commit();

                            }
                            else {
                                ElectricityRecharge electricityRecharge = new ElectricityRecharge(getActivity());
                                electricityRecharge.execute(electricityRecharge_url);
                            }

//                            txtNumberLandline.setText(edtNumberLandline.getText().toString().trim());
//                            txtAmountLandline.setText(edtAmountLandline.getText().toString().trim());
//
//                            viewFlipper_landline.setDisplayedChild(1);

//                    } else {
//                        alert.showAlert(getResources().getString(R.string.billcycle_limit));
//                    }


                }

                } else {

                    alert.newUser();

                }

            }
        });

        btn_confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                viewFlipper_electricity.setDisplayedChild(2);


            }
        });


        txtOperatorElectricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionDetector.isConnectingToInternet())
                {

                    Intent i =new Intent(getActivity(),ServiceProviderListActivity.class);
                    i.putExtra("url",getResources().getString(R.string.service_provider));
                    i.putExtra("type","ELECTRICITY");
                    startActivityForResult(i, 2);

                }
                else {
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();;
                }
            }
        });
    }

    private void init(View rootView)
    {
        // TODO Auto-generated method stub


        viewFlipper_electricity = (ViewFlipper) rootView.findViewById(R.id.viewflipper_electicity);
        btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm);
        fontclass.setFont(viewFlipper_electricity,typeface );

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        connectionDetector = new ConnectionDetector(getActivity());
        edtNumberElectricity = (EditText)rootView.findViewById(R.id.edtNumberElectricity);
        edtAmountElectricity = (EditText)rootView.findViewById(R.id.edtAmountElectricity);
        edtBillCycleElectricity = (EditText)rootView.findViewById(R.id.edtBillCycleElectricity);
        edtMpinElectricity = (EditText)rootView.findViewById(R.id.edtMpinElectricity);
        edtPromocode = (EditText)rootView.findViewById(R.id.edtPromocode);

        txtOperatorElectricity = (TextView)rootView.findViewById(R.id.txtOperatorElectricity);
        chkFavElectricity = (CheckBox)rootView.findViewById(R.id.chkFavElectricity);

        btnPaymentElectricity = (Button)rootView.findViewById(R.id.btnPaymentElectricity);
        btnPaymentElectricity.setTypeface(btnPaymentElectricity.getTypeface(), Typeface.BOLD);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        //// System.out.println(requestCode + " " + resultCode);
        if(requestCode == 2){

        try {

            txtOperatorElectricity.setText(data.getStringExtra("result"));
        }
        catch (Exception e)
        {

        }

        }

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

                edtNumberElectricity.setText("");
                edtAmountElectricity.setText("");
                edtPromocode.setText("");
                chkFavElectricity.setChecked(false);
                txtOperatorElectricity.setText("");
                edtMpinElectricity.setText("");
                edtBillCycleElectricity.setVisibility(View.GONE);




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


    private class ElectricityRecharge extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

        String firstName, lastName, walletBalance;

        public ElectricityRecharge(Context context) {
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

                if(chkFavElectricity.isChecked())
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
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


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
            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

        }

    }
}
