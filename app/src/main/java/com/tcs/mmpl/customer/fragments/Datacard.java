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
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.Activity.BrowsePlanListActivity;
import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Activity.ServiceProviderListActivity;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.Contact;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.PopupBuilder;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class Datacard extends Fragment {

    private static final int MODE_PRIVATE = 0;
    private Button btnPaymentDatacard, btnConfirmDatacardDone,btnConfirmDatacard,btnResetDatacard;
    private EditText edtNumberDatcard, edtAmountDatacard, edtMpinDatcard;
    private TextView txtNumberDatacard, txtAmountDatacard, txtConfirmDatacardDone, txtOperatorDatacard;

    FontClass fontclass = new FontClass();
    Typeface typeface;
    private ViewFlipper viewflipper_datacard;


    RadioButton radioPrepaidDatacard,radioPostpaidDatacard;
    ProgressDialog pDialog;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    ConnectionDetector connectionDetector;

    private static final String TAG = DTH.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    private String parameterType,parameterValue,URL;
    private CheckBox chkFavDatacard;
    private TextView txtBrowsePlansDataCard;
    private RadioGroup radioGroupDatacard;


    EditText edtpromocode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_data,
                container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");

        init(rootView);

        onClick();


        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)) {
                    if (viewflipper_datacard.getDisplayedChild() == 2) {
                        viewflipper_datacard.setDisplayedChild(1);
                    } else if (viewflipper_datacard.getDisplayedChild() == 1) {
                        viewflipper_datacard.setDisplayedChild(0);
                    } else {
                        getActivity().finish();
//                        Intent i = new Intent(getActivity(),HomeScreenActivity.class);
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

        radioGroupDatacard.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_prepaid_datacard)
                {
                    if(!txtOperatorDatacard.getText().toString().equalsIgnoreCase("") && !edtNumberDatcard.getText().toString().equalsIgnoreCase(""))
                        txtBrowsePlansDataCard.setVisibility(View.VISIBLE);
                }
                else if( checkedId == R.id.radio_postpaid_datacard)
                {
                    txtBrowsePlansDataCard.setVisibility(View.GONE);
                }
            }
        });

        txtBrowsePlansDataCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    //String url = "http://nexgenfmpl.com/numberdemo/WebService/listDthByOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665&page=1&per_page=10&operator="+ Uri.encode(txtOperatorDTH.getText().toString().trim(), "utf-8");

                    // String url = "http://180.179.50.55/numberdemo/WebService/listDataCardByOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665&page=1&per_page=10&operator=" + Uri.encode(txtOperatorDatacard.getText().toString().trim(), "utf-8");
                    String url = getResources().getString(R.string.browseplan) + "?Operator=" + Uri.encode(txtOperatorDatacard.getText().toString().trim(), "utf-8") + "&Type=DATACARD" + "&MDN=" + edtNumberDatcard.getText().toString().trim() + "&State=Delhi";
                    Intent i = new Intent(getActivity(), BrowsePlanListActivity.class);
                    i.putExtra("url", url);
                    startActivityForResult(i, 4);
//                    String operatorurl = "http://nexgenfmpl.com/numberdemo/WebService/listUniqDthOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665";
//                    GetOperator getOperator = new GetOperator(getActivity());
//                    getOperator.execute(operatorurl);
                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                }
            }
        });

        edtNumberDatcard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                //// System.out.println(s + ":::::::::::::::::: " + s.length());
                if (s.length() > 0) {
                    if(radioPrepaidDatacard.isChecked()  && !txtOperatorDatacard.getText().toString().equalsIgnoreCase(""))
                        txtBrowsePlansDataCard.setVisibility(View.VISIBLE);
                    else
                        txtBrowsePlansDataCard.setVisibility(View.GONE);
                } else {
                    txtBrowsePlansDataCard.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        txtStateDataCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (connectionDetector.isConnectingToInternet()) {
//                    //String url = "http://nexgenfmpl.com/numberdemo/WebService/listDthByOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665&page=1&per_page=10&operator="+ Uri.encode(txtOperatorDTH.getText().toString().trim(), "utf-8");
//
//                    String url = getResources().getString(R.string.states);
//
//                    Intent i = new Intent(getActivity(), StateListActivity.class);
//                    i.putExtra("url", url);
//                    startActivityForResult(i, 5);
////                    String operatorurl = "http://nexgenfmpl.com/numberdemo/WebService/listUniqDthOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665";
////                    GetOperator getOperator = new GetOperator(getActivity());
////                    getOperator.execute(operatorurl);
//                } else {
//                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
//                    ;
//                }
//            }
//        });


        edtNumberDatcard.setOnTouchListener(new View.OnTouchListener() {
            // final int DRAWABLE_LEFT = 0;
            // final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;

            //  final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = edtNumberDatcard.getRight()
                            - edtNumberDatcard.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    // when EditBox has padding, adjust leftEdge like
                    // leftEdgeOfRightDrawable -= getResources().getDimension(R.dimen.edittext_padding_left_right);
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        // clicked on clear icon
                        //edt_mobile.setText("123");

                        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);

                        return true;
                    }
                }
                return false;
            }
        });


        btnPaymentDatacard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                AlertBuilder alert = new AlertBuilder(getActivity());
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                if (edtNumberDatcard.getText().toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_datacard_number));
                } else if (txtOperatorDatacard.getText().toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_datacard_operator));
                }
//                else if (txtStateDataCard.getText().toString().trim().equalsIgnoreCase("")) {
//                    alert.showAlert(getResources().getString(R.string.invalid_state));
//                }
                else if (edtAmountDatacard.getText().toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_amount));
                } else if (edtMpinDatcard.getText().toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_mpin));
                } else if (edtNumberDatcard.getText().toString().trim().equalsIgnoreCase("") || edtAmountDatacard.getText().toString().trim().equalsIgnoreCase("") || txtOperatorDatacard.getText().toString().equalsIgnoreCase("")  || edtMpinDatcard.getText().toString().trim().isEmpty()) {
                    alert.showAlert(getResources().getString(R.string.validation));
                } else {
                    if (radioPrepaidDatacard.isChecked() || radioPostpaidDatacard.isChecked()) {



                            String datacardrecharge_url = "";
                            String mobileNo;
                            String category;
                            String rechargeMobile;
                            String amount;
                            String billerCode;
                            String mpin;
                            String region;
                            String BU;


                            mobileNo = pref.getString("mobile_number", "");
                            category = Uri.encode("DataCard Recharge", "utf-8");
                            rechargeMobile = edtNumberDatcard.getText().toString();
                            amount = edtAmountDatacard.getText().toString();
                            billerCode = Uri.encode(txtOperatorDatacard.getText().toString().trim(), "utf-8");
                            mpin = edtMpinDatcard.getText().toString();
                            region = "";
                            if (radioPrepaidDatacard.isChecked()) {
                                BU = "PREPAID";
                                parameterType = "Prepaid DataCard";
                            } else {
                                BU = "POSTPAID";
                                parameterType = "Postpaid DataCard";
                            }

                            parameterValue = mobileNo + "|" + amount + "|" + rechargeMobile + "|" + category + "|" + billerCode;

                            String category1=Uri.decode(category);
                            String BingoString = mobileNo + "|" + ""+"|"+mpin + "|" + category1+ "|" +""+"|"+""+"|"+""+"|"+billerCode+"|"+rechargeMobile+"|"+amount+"|"+""+"|"+BU;
                            String res = Bingo.Bingo_one(BingoString);

                            URL = getActivity().getResources().getString(R.string.billPayment) + "?mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&Region=" + region + "&BU=" + BU;

                            datacardrecharge_url = getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&Region=" + region + "&BU=" + BU+"&Checksum="+res;

//                            // System.out.println("URL......" + datacardrecharge_url);

                            CheckBalance checkBalance = new CheckBalance(getActivity());
                            if (checkBalance.getBalanceCheck(amount)) {

                                PopupBuilder popup = new PopupBuilder(getActivity());
                                popup.showPopup(checkBalance.getAmount(amount));

                                userInfoEditor.putString("transaction_url", datacardrecharge_url);
                                userInfoEditor.putString("transaction_method", "POST2");
                                userInfoEditor.putString("transaction_flag", "1");
                                userInfoEditor.commit();

                            } else {
                                DatacardRecharge datacardRecharge = new DatacardRecharge(getActivity());
                                datacardRecharge.execute(datacardrecharge_url);
                            }
//
//                        txtNumberDatacard.setText(edtNumberDatcard.getText().toString().trim());
//                        txtAmountDatacard.setText(edtAmountDatacard.getText().toString().trim());
//
//                        viewflipper_datacard.setDisplayedChild(1);

                    } else {
                        alert.showAlert(getResources().getString(R.string.validation));
                    }


                }
                } else {

                    alert.newUser();
                }
            }
        });


        btnConfirmDatacardDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewflipper_datacard.setDisplayedChild(0);
            }
        });

        txtOperatorDatacard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {

                    Intent i = new Intent(getActivity(), ServiceProviderListActivity.class);
                    i.putExtra("url", getResources().getString(R.string.service_provider));
                    i.putExtra("type", "DATACARD");
                    startActivityForResult(i, 2);

                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                }
            }
        });





    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//        //// System.out.println("RequestCode:::::"+requestCode+"ResultCode:::::::::"+resultCode);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS ) {
            //  Log.d(TAG, "Response: " + data.toString());
            try {
                uriContact = data.getData();

                // retrieveContactName();
                retrieveContactNumber();
                //retrieveContactPhoto();
            }
            catch (Exception e)
            {

            }

        }

        else if(requestCode == 2 ){

            try {



                txtOperatorDatacard.setText(data.getStringExtra("result"));
                if(radioPrepaidDatacard.isChecked()  && !edtNumberDatcard.getText().toString().equalsIgnoreCase(""))
                txtBrowsePlansDataCard.setVisibility(View.VISIBLE);
            }
            catch (Exception e)
            {

            }

        }
        else if(requestCode == 5)
        {
            try
            {
                if(radioPrepaidDatacard.isChecked() && !txtOperatorDatacard.getText().toString().toString().equalsIgnoreCase("") && !edtNumberDatcard.getText().toString().equalsIgnoreCase(""))
                    txtBrowsePlansDataCard.setVisibility(View.VISIBLE);
            }
            catch (Exception e)
            {

            }
        }

        else
        {
            try
            {


                String Stramt = data.getStringExtra("result");
                float amt = Float.parseFloat(Stramt);
                int amt1 = (int)amt;
                edtAmountDatacard.setText(String.valueOf(amt1));
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

                edtNumberDatcard.setText("");
                edtAmountDatacard.setText("");
                txtOperatorDatacard.setText("");
                edtpromocode.setText("");
                chkFavDatacard.setChecked(false);
              //  txtStateDataCard.setText("");
                edtMpinDatcard.setText("");
                txtBrowsePlansDataCard.setVisibility(View.GONE);
                radioPrepaidDatacard.setChecked(true);




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


    private void retrieveContactNumber() {

        try
        {

            String id = uriContact.getLastPathSegment();

            Contact contact = new Contact(getActivity(),edtNumberDatcard);
            contact.getContactNumber(id);

//            String contactNumber = null;
//
//            // getting contacts ID
//            Cursor cursorID = getActivity().getContentResolver().query(uriContact,
//                    new String[]{ContactsContract.Contacts._ID},
//                    null, null, null);
//
//            if (cursorID.moveToFirst()) {
//
//                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
//            }
//
//            cursorID.close();
//
//            Log.d(TAG, "Contact ID: " + contactID);
//
//            // Using the contact ID now we will get contact phone number
//            Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
//
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
//                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
//                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
//
//                    new String[]{contactID},
//                    null);
//
//            if (cursorPhone.moveToFirst()) {
//                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                //// System.out.println("First......... "+contactNumber);
//                try
//                {
//                    //contactNumber = contactNumber.replaceFirst("^0+(?!$)", "");
//
//                    contactNumber = contactNumber.replaceAll("\\s","");
//                    contactNumber = contactNumber.replaceFirst("^0", "");
//                    //// System.out.println("Removing zero......... "+contactNumber);
//                }
//                catch(Exception e)
//                {
//                    //// System.out.println("Error Here in contact");
//                }
//                if(contactNumber.contains("+91"))
//                {
//                    //// System.out.println("Searching +91......... "+contactNumber);
//                    contactNumber = contactNumber.replaceAll("\\+91", "");
//                    //// System.out.println("Removing +91......... "+contactNumber);
//                }
//            }
//
//            cursorPhone.close();
//
//            Log.d(TAG, "Contact Phone Number: " + contactNumber);
//            if(!contactNumber.equalsIgnoreCase("null")&& contactNumber != null)
//            {
//                edtNumberDatcard.setText(contactNumber);
//            }
        }
        catch(NullPointerException ex)
        {


        }
        catch (Exception e) {
            // TODO: handle exception
        }


    }

    private void init(View rootView) {
        // TODO Auto-generated method stub

        viewflipper_datacard = (ViewFlipper) rootView.findViewById(R.id.viewflipper_datacard);
        fontclass.setFont(viewflipper_datacard, typeface);

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        connectionDetector = new ConnectionDetector(getActivity());

        txtOperatorDatacard = (TextView) rootView.findViewById(R.id.txtOperatorDatacard);
        edtNumberDatcard = (EditText) rootView.findViewById(R.id.edtNumberDatacard);
        edtAmountDatacard = (EditText) rootView.findViewById(R.id.edtAmountDatacard);
        btnPaymentDatacard = (Button) rootView.findViewById(R.id.btnPaymentDatacard);
        chkFavDatacard = (CheckBox)rootView.findViewById(R.id.chkFavDatacard);
        edtpromocode = (EditText)rootView.findViewById(R.id.edtPromocode);
        txtBrowsePlansDataCard = (TextView)rootView.findViewById(R.id.txtBrowsePlansDataCard);
//        txtStateDataCard = (TextView)rootView.findViewById(R.id.txtStateDataCard);
        radioPrepaidDatacard = (RadioButton)rootView.findViewById(R.id.radio_prepaid_datacard);
        radioPostpaidDatacard = (RadioButton)rootView.findViewById(R.id.radio_postpaid_datacard);
        radioGroupDatacard = (RadioGroup)rootView.findViewById(R.id.radioGroup_datacard);

        btnPaymentDatacard.setTypeface(btnPaymentDatacard.getTypeface(), Typeface.BOLD);

        edtMpinDatcard = (EditText) rootView.findViewById(R.id.edtMpinDatacard);
        txtNumberDatacard = (TextView) rootView.findViewById(R.id.txtNumberDatacard);
        txtAmountDatacard = (TextView) rootView.findViewById(R.id.txtAmountDatacard);
        btnConfirmDatacard = (Button)rootView.findViewById(R.id.btnConfirmDatacard);
        btnResetDatacard = (Button)rootView.findViewById(R.id.btnResetDatacard);

        txtConfirmDatacardDone = (TextView) rootView.findViewById(R.id.txtConfirmDatacardDone);
        btnConfirmDatacardDone = (Button) rootView.findViewById(R.id.btnConfirmDatacardDone);



    }


    private class DatacardRecharge extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

        String firstName, lastName, walletBalance;

        public DatacardRecharge(Context context) {
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

                String url = getResources().getString(R.string.promocode)+"?MDN="+ pref.getString("mobile_number", "")+"&PROMOCODE="+edtpromocode.getText().toString().trim()+"&Transaction=DataCard";
                updatePromocode up = new updatePromocode(getActivity());
                up.execute(url);


                if(chkFavDatacard.isChecked())
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

            if (pDialog.isShowing())
                pDialog.dismiss();

//            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);


        }

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }


}



