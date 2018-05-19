package com.tcs.mmpl.customer.fragments;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.Activity.BrowsePlanListActivity;
import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Activity.ServiceProviderListActivity;
import com.tcs.mmpl.customer.Activity.StateListActivity;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 9/10/2015.
 */
public class DTH extends Fragment {
    private static final int MODE_PRIVATE = 0;
    private Button btnPaymentDTH,btnConfirmDTHDone;
    private EditText edtNumberDTH,edtAmountDTH,edtMpinDTH;
    private TextView txtNumberDTH,txtAmountDTH,txtConfirmDTHDone,txtOperatorDTH,txtBrowsePlansDTH,txtStateDTH;
    private ViewFlipper viewFlipper_dth;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    private Button btn_confirm;
    private RadioButton radioPrepaidDTH,radioPostpaidDTH;
    private CheckBox chkFavDTH;
    private LinearLayout linBrowsePlansMobile;


    ProgressDialog pDialog;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    ConnectionDetector connectionDetector;

    private String parameterType,parameterValue,URL;
    EditText edtpromocode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_dth,
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
                    if (viewFlipper_dth.getDisplayedChild() == 2) {
                        viewFlipper_dth.setDisplayedChild(1);
                    } else if (viewFlipper_dth.getDisplayedChild() == 1) {
                        viewFlipper_dth.setDisplayedChild(0);
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

    private void init(View rootView) {
        // TODO Auto-generated method stub

        viewFlipper_dth = (ViewFlipper) rootView.findViewById(R.id.viewflipper_dth);
        fontclass.setFont(viewFlipper_dth,typeface );

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        connectionDetector = new ConnectionDetector(getActivity());

        linBrowsePlansMobile = (LinearLayout)rootView.findViewById(R.id.linBrowsePlansMobile);
        txtOperatorDTH = (TextView)rootView.findViewById(R.id.txtOperatorDTH);
        txtBrowsePlansDTH = (TextView)rootView.findViewById(R.id.txtBrowsePlansDTH);
        btnPaymentDTH = (Button) rootView.findViewById(R.id.btnPaymentDTH);
        edtNumberDTH = (EditText)rootView.findViewById(R.id.edtNumberDTH);
        edtAmountDTH = (EditText)rootView.findViewById(R.id.edtAmountDTH);
        radioPrepaidDTH = (RadioButton)rootView.findViewById(R.id.radioPrepaidDTH);
        radioPostpaidDTH = (RadioButton)rootView.findViewById(R.id.radioPostpaidDTH);
        chkFavDTH = (CheckBox)rootView.findViewById(R.id.chkFavDTH);
        edtpromocode = (EditText)rootView.findViewById(R.id.edtPromocode);
        txtStateDTH = (TextView)rootView.findViewById(R.id.txtStateDTH);

        btnPaymentDTH.setTypeface(btnPaymentDTH.getTypeface(),Typeface.BOLD);

        txtNumberDTH = (TextView)rootView.findViewById(R.id.txtNumberDTH);
        txtAmountDTH = (TextView)rootView.findViewById(R.id.txtAmountDTH);
        edtMpinDTH = (EditText)rootView.findViewById(R.id.edtMpinDTH);
        btn_confirm = (Button) rootView.findViewById(R.id.btnConfirmDTH);

        txtConfirmDTHDone = (TextView)rootView.findViewById(R.id.txtConfirmDTHDone);
        btnConfirmDTHDone = (Button)rootView.findViewById(R.id.btnConfirmDTHDone);

    }
    private void onClick() {
        // TODO Auto-generated method stub



        btnPaymentDTH.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                AlertBuilder alert = new AlertBuilder(getActivity());

                if (userInfoPref.getBoolean("new_user_registered_newapp", false)){
                    if(edtNumberDTH.getText().toString().trim().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_subscriber_id));
                }
                else if(txtOperatorDTH.getText().toString().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_dth_operator));
                }
//                else if(txtStateDTH.getText().toString().equalsIgnoreCase(""))
//                {
//                    alert.showAlert(getResources().getString(R.string.invalid_state));
//                }
                else if(edtAmountDTH.getText().toString().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_amount));
                }
                else if(edtMpinDTH.getText().toString().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_mpin));
                }
                else if (edtNumberDTH.getText().toString().trim().equalsIgnoreCase("") || edtAmountDTH.getText().toString().trim().equalsIgnoreCase("") || txtOperatorDTH.getText().toString().equalsIgnoreCase("") ||  edtMpinDTH.getText().toString().trim().equalsIgnoreCase("")) {

                    alert.showAlert( getResources().getString(R.string.validation));
                } else {

                    if (edtAmountDTH.length() < 3) {
                        alert.showAlert(getResources().getString(R.string.dth_amount));
                    }
                    else {

                        String dthrecharge_url = "";
                        String mobileNo;
                        String category;
                        String rechargeMobile;
                        String amount;
                        String billerCode;
                        String mpin;
                        String region;
                        String BU = "OTHERS";


                        mobileNo = pref.getString("mobile_number", "");
                        category = Uri.encode("DTH Recharge", "utf-8");
                        rechargeMobile = edtNumberDTH.getText().toString().trim();
                        amount = edtAmountDTH.getText().toString().trim();
                        billerCode = Uri.encode(txtOperatorDTH.getText().toString().trim(), "utf-8");
                        mpin = edtMpinDTH.getText().toString();
                        //  region = Uri.encode(txtStateDTH.getText().toString().trim(), "utf-8");
                        String category1=Uri.decode(category);

                        String BingoString = mobileNo + "|" + ""+"|"+mpin + "|" + category1+ "|" + ""+"|"+""+"|"+""+"|"+billerCode+"|"+rechargeMobile+"|"+amount+"|"+""+"|"+BU;
                        String res = Bingo.Bingo_one(BingoString);
                        // System.out.println("BingoString"+BingoString);

                        parameterType = "DTH";
                        parameterValue = mobileNo + "|" + amount + "|" + rechargeMobile + "|" + category + "|" + billerCode;
                        URL = getActivity().getResources().getString(R.string.billPayment) + "?mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU;

                        dthrecharge_url = getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU+"&Checksum="+res;


                        // System.out.println("dthURL ......" + dthrecharge_url);

                        CheckBalance checkBalance = new CheckBalance(getActivity());
                        if (checkBalance.getBalanceCheck(amount)) {

                            PopupBuilder popup = new PopupBuilder(getActivity());
                            popup.showPopup(checkBalance.getAmount(amount));

                            userInfoEditor.putString("transaction_url", dthrecharge_url);
                            userInfoEditor.putString("transaction_method", "POST2");
                            userInfoEditor.putString("transaction_flag", "1");
                            userInfoEditor.commit();

                        } else {
                            DTHRecharge dthRecharge = new DTHRecharge(getActivity());
                            dthRecharge.execute(dthrecharge_url);
                        }


                    }


                }
                } else {

                    alert.newUser();

                }

            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (connectionDetector.isConnectingToInternet()) {

                    String mpin = edtMpinDTH.getText().toString().trim();

                    if (mpin.trim().isEmpty()) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.mpin), Toast.LENGTH_LONG).show();
                    } else {

                        String dthrecharge_url = "";
                        String mobileNo;
                        String category;
                        String rechargeMobile;
                        String amount;
                        String billerCode;

                        if (radioPrepaidDTH.isChecked()) {

                            mobileNo = pref.getString("mobile_number", "");
                            category = Uri.encode("DTH Recharge", "utf-8");
                            rechargeMobile = txtNumberDTH.getText().toString();
                            amount = txtAmountDTH.getText().toString();
                            billerCode = Uri.encode(txtOperatorDTH.getText().toString().trim(), "utf-8");

                            dthrecharge_url = getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount;


//                            //// System.out.println("dthURL ......" + dthrecharge_url);
                        } else if (radioPostpaidDTH.isChecked()) {

                            mobileNo = pref.getString("mobile_number", "");
                            category = Uri.encode("DTH Recharge", "utf-8");
                            rechargeMobile = txtNumberDTH.getText().toString();
                            amount = txtAmountDTH.getText().toString();
                            billerCode = Uri.encode(txtOperatorDTH.getText().toString().trim(), "utf-8");

                            dthrecharge_url = getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount;


//                            //// System.out.println("dthURL ......" + dthrecharge_url);
                        }


                        DTHRecharge dthRecharge = new DTHRecharge(getActivity());
                        dthRecharge.execute(dthrecharge_url);
                    }

                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();
                }


            }
        });
        btnConfirmDTHDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper_dth.setDisplayedChild(0);
            }
        });

        txtOperatorDTH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    Intent i = new Intent(getActivity(), ServiceProviderListActivity.class);
                    i.putExtra("url", getResources().getString(R.string.service_provider));
                    i.putExtra("type", "DTH");
                    startActivityForResult(i, 2);

                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                }
            }
        });
        txtBrowsePlansDTH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {

                    //String url = "http://180.179.50.55/numberdemo/WebService/listDthByOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665&page=1&per_page=10&operator=" + Uri.encode(txtOperatorDTH.getText().toString().trim(), "utf-8");
                    String url = getResources().getString(R.string.browseplan) + "?Operator=" + Uri.encode(txtOperatorDTH.getText().toString().trim(), "utf-8") + "&Type=DTH";

                    Intent i = new Intent(getActivity(), BrowsePlanListActivity.class);
                    i.putExtra("url", url);
                    startActivityForResult(i, 1);

                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                }
            }
        });

        txtStateDTH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    String url = getResources().getString(R.string.states);

                    Intent i = new Intent(getActivity(), StateListActivity.class);
                    i.putExtra("url", url);
                    startActivityForResult(i, 5);
                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 2){

            try {


                txtOperatorDTH.setText(data.getStringExtra("result"));
                txtBrowsePlansDTH.setVisibility(View.GONE);

              //  linBrowsePlansMobile.setVisibility(View.VISIBLE);
              //  txtBrowsePlansDTH.setEnabled(true);
            }
            catch (Exception e)
            {

            }

        }
        else if(requestCode == 1){
            try
            {
//                //// System.out.println("Coming here" + data.getStringExtra("result"));

                String Stramt = data.getStringExtra("result");
                float amt = Float.parseFloat(Stramt);
                int amt1 = (int)amt;
                edtAmountDTH.setText(String.valueOf(amt1));
            }
            catch (Exception e)
            {

            }
        }
        else if(requestCode == 5)
        {
            try
            {
                txtStateDTH.setText(data.getStringExtra("result"));
            }
            catch (Exception e)
            {

            }
        }


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

                edtNumberDTH.setText("");
                edtAmountDTH.setText("");
                txtOperatorDTH.setText("");
                edtpromocode.setText("");
                chkFavDTH.setChecked(false);
                txtStateDTH.setText("");
                edtMpinDTH.setText("");
                txtBrowsePlansDTH.setVisibility(View.GONE);



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
    private class GetOperator extends AsyncTask<String, Void, String> {

        Context context;

        ArrayList<String> operatorName,stateName ;
        String firstName, lastName, walletBalance;

        public GetOperator(Context context) {
            this.context = context;
            operatorName = new ArrayList<String>();
            stateName = new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Operator...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


//                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

//                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        String status_code = jsonMainObj.getString("status_code");

                        if(status_code.trim().equalsIgnoreCase("200")) {


                            JSONArray jsonArray = jsonMainObj.getJSONArray("data");

//                            //// System.out.println("Length of jsonArray..............." + jsonArray.length());

                            String operator_names, new_operator_names;

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject obj = (JSONObject) jsonArray.get(i);
                                    operatorName.add(obj.getString("operator_name").toUpperCase());
                                    


                                }
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
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }



        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

//            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);

            if (result.equalsIgnoreCase("Success")) {
                //Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();

                txtOperatorDTH.setText(operatorName.get(0));
               // txtBrowsePlans.setText("Browse Plans\nof "+operatorName.get(0)+"\n"+stateName.get(0));


            } else if (result.equalsIgnoreCase("Failure")) {

                Toast.makeText(getActivity(),getResources().getString(R.string.apidown),Toast.LENGTH_LONG).show();;

            } else {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

        }

    }


    private class DTHRecharge extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

        String firstName, lastName, walletBalance;

        public DTHRecharge(Context context) {
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

//                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

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

//            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {

                String url = getResources().getString(R.string.promocode)+"?MDN="+ pref.getString("mobile_number", "")+"&PROMOCODE="+edtpromocode.getText().toString().trim()+"&Transaction=DTH";
                updatePromocode up = new updatePromocode(getActivity());
                up.execute(url);

                if(chkFavDTH.isChecked()) {
                    String addFavorites_url = getActivity().getResources().getString(R.string.addfavourites);
                    AddFavorites addFavorites = new AddFavorites(getActivity());
                    addFavorites.execute(addFavorites_url);
                }


                AlertBuilder alert = new AlertBuilder(getActivity());
                AlertDialog.Builder alertDialog = alert.showRetryAlert(responseMessage+getResources().getString(R.string.make_payment));
                openAlert(alertDialog);



            } else if (result.equalsIgnoreCase("Failure")) {
                AlertBuilder alert = new AlertBuilder(getActivity());
                alert.showAlert(getResources().getString(R.string.apidown));

            } else {

                AlertBuilder alert = new AlertBuilder(getActivity());
                alert.showAlert(result);
            }

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

//                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

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

//            //// System.out.println("Result" + result);

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

//                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

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
//            //// System.out.println("Result"+result);
        }

    }

}







