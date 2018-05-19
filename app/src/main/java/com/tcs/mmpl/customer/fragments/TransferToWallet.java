package com.tcs.mmpl.customer.fragments;

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
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Activity.TransferToBank;
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

/**
 * Created by Admin on 9/16/2015.
 */
public class TransferToWallet extends Fragment {

    private static final int MODE_PRIVATE = 0 ;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    private Button btnSendToWallet;
    private EditText edtMobile,edtAmount,edtComment,edtMpin,edtpromocode;
    private ViewFlipper viewFlipper_transfer_to_wallet;
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;
    private Button confirm,btn_transfer;
    Activity activity;
    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    private static final String TAG = TransferToBank.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID,parameterType,parameterValue,URL;

    private CheckBox chkFavMobile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_transfer_to_wallet,
                container, false);
        typeface=Typeface.createFromAsset(getActivity().getAssets(),"helvetica.otf");
        activity = getActivity();

        init(rootView);

        onClick();


        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK ))
                {
                    if(viewFlipper_transfer_to_wallet.getDisplayedChild() == 2)
                    {
                        viewFlipper_transfer_to_wallet.setDisplayedChild(1);
                    }
                   /* else if(viewFlipper_transfer_to_wallet.getDisplayedChild() == 2)
                    {
                        viewFlipper_transfer_to_wallet.setDisplayedChild(1);
                    }*/
                    else if(viewFlipper_transfer_to_wallet.getDisplayedChild() == 1)
                    {
                        viewFlipper_transfer_to_wallet.setDisplayedChild(0);
                    }
                    else
                    {
                        getActivity().finish();
//                        Intent i = new Intent(getActivity(),HomeScreenActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(i);
                    }

                    return true;
                }
                return false;
            }
        } );

        return rootView;
    }

   private void onClick()
    {

        edtMobile.setOnTouchListener(new View.OnTouchListener() {
            // final int DRAWABLE_LEFT = 0;
            // final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;

            //  final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = edtMobile.getRight()
                            - edtMobile.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
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


        btnSendToWallet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertBuilder alert = new AlertBuilder(getActivity());
                viewFlipper_transfer_to_wallet.setDisplayedChild(1);

                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                if (connectionDetector.isConnectingToInternet()) {

                    String mobileNo1 = pref.getString("mobile_number", "");
                    String mobileNo2 = "91" + edtMobile.getText().toString();
                    String amt = edtAmount.getText().toString();
                    String mpin = edtMpin.getText().toString();
                    String remarks= Uri.encode(edtComment.getText().toString().trim(), "utf-8");
                    String userType = userInfoPref.getString("usertype", "");


                    parameterType = "Wallet to Wallet";
                    parameterValue = mobileNo1 + "|" + amt + "|" + mobileNo2 + "|" + remarks;


                    if(edtMobile.getText().toString().trim().equalsIgnoreCase("") || edtMobile.getText().toString().trim().length()<10)
                    {
                        alert.showAlert(getResources().getString(R.string.invalid_mobile));
                    }
                    else if(edtAmount.getText().toString().trim().equalsIgnoreCase(""))
                    {
                        alert.showAlert(getResources().getString(R.string.invalid_amount));
                    }
                    else if(edtMpin.getText().toString().trim().equalsIgnoreCase(""))
                    {
                        alert.showAlert(getResources().getString(R.string.invalid_mpin));
                    }
                    else if (edtMobile.getText().toString().trim().equalsIgnoreCase("") || amt.trim().equalsIgnoreCase("") || mpin.trim().equalsIgnoreCase("")) {
                        alert.showAlert(getResources().getString(R.string.validation));

                    } else {



                            URL = getResources().getString(R.string.quickPay) + "?mobileNo1=" + mobileNo1 + "&mobileNo2=" + mobileNo2 + "&Amount=" + amt + "&userType=" + userType + "&Remarks=" + remarks;

                        String secretParameter = mobileNo1.trim() + "|" + mobileNo2.trim()+"|"+mpin.trim() +"|" +amt.trim();
                        String res = Bingo.Bingo_one(secretParameter);

                        String wallettowallet_url = getResources().getString(R.string.quickPay) + "?mobileNo1=" + mobileNo1 + "&mobileNo2=" + mobileNo2 + "&Amount=" + amt + "&userType=" + userType + "&Remarks=" + remarks + "&MPIN=" + mpin+"&Checksum="+res;

                            CheckBalance checkBalance = new CheckBalance(getActivity());
                            if(checkBalance.getBalanceCheck(amt))
                            {

                                PopupBuilder popup = new PopupBuilder(getActivity());
                                popup.showPopup(checkBalance.getAmount(amt));

                                userInfoEditor.putString("transaction_url", wallettowallet_url);
                                userInfoEditor.putString("transaction_method","POST2");
                                userInfoEditor.putString("transaction_flag","1");
                                userInfoEditor.commit();

                            }
                            else {
                                WalletToWallet wallettowallet = new WalletToWallet(getActivity());
                                wallettowallet.execute(wallettowallet_url);
                            }

                    }

                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();
                }

                } else {
                    alert.newUser();
                }
            }
        });


    }

    private void init(View rootView) {


        connectionDetector = new ConnectionDetector(getActivity());

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        edtMobile =(EditText)  rootView.findViewById(R.id.edtMobileNumber);
        edtAmount =(EditText)  rootView.findViewById(R.id.edtAmount);
        edtComment =(EditText)  rootView.findViewById(R.id.edtComment);
        edtMpin =(EditText)  rootView.findViewById(R.id.edtMpin);
        edtpromocode = (EditText)rootView.findViewById(R.id.edtPromocode);
        btnSendToWallet = (Button) rootView.findViewById(R.id.btnSendToWallet);
        chkFavMobile = (CheckBox)rootView.findViewById(R.id.chkFavMobile);
        viewFlipper_transfer_to_wallet = (ViewFlipper) rootView.findViewById(R.id.viewflipper_transfert_to_wallet);
        fontclass.setFont(viewFlipper_transfer_to_wallet, typeface);

        btnSendToWallet.setTypeface(btnSendToWallet.getTypeface(), Typeface.BOLD);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == getActivity().RESULT_OK) {
            //  Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            // retrieveContactName();
            retrieveContactNumber();
            //retrieveContactPhoto();

        }
        else if(resultCode == getActivity().RESULT_OK){

            //// System.out.println("Coming here"+data.getStringExtra("result"));

            edtMobile.setText(data.getStringExtra("result"));

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

                edtMobile.setText("");
                edtAmount.setText("");
                edtMpin.setText("");
                edtComment.setText("");
                edtpromocode.setText("");
                chkFavMobile.setChecked(false);



            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                dialog.cancel();
                getActivity().finish();
                Intent i = new Intent(activity, HomeScreenActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(i);

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

            Contact contact = new Contact(getActivity(),edtMobile);
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
//                edtMobile.setText(contactNumber);
//            }
        }
        catch(NullPointerException ex)
        {


        }
        catch (Exception e) {
            // TODO: handle exception
        }


    }

    private class WalletToWallet extends AsyncTask<String, Void, String> {

        Context context;

        String responseMessage;

        public WalletToWallet(Context context) {
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
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                WebServiceHandler serviceHandler = new WebServiceHandler(context);
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());

                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            responseMessage = jsonMainObj.getString("responseMessage");

                            if(jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                            {
                                new LocalNotification(getActivity(),jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationTitle)),jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationMessage))).sendNotification();
                            }
                            return "Success";

                        } else {
                            responseMessage = jsonMainObj.getString("responseMessage");
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


            if (result.equalsIgnoreCase("Success")) {

                if(chkFavMobile.isChecked()) {
                    String addFavorites_url = getActivity().getResources().getString(R.string.addfavourites);
                    AddFavorites addFavorites = new AddFavorites(getActivity());
                    addFavorites.execute(addFavorites_url);
                }

                String url =  getActivity().getResources().getString(R.string.promocode)+"?MDN="+ pref.getString("mobile_number", "")+"&PROMOCODE="+edtpromocode.getText().toString().trim()+"&Transaction=TransferToWallet";
                updatePromocode up = new updatePromocode(getActivity());
                up.execute(url);

                AlertBuilder alert = new AlertBuilder(getActivity());
                AlertDialog.Builder alertDialog = alert.showRetryAlert(responseMessage+getResources().getString(R.string.make_payment));
                openAlert(alertDialog);
            }
            else {
                AlertBuilder alert = new AlertBuilder(getActivity());
              alert.showAlert(responseMessage);
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

                //// System.out.println(json);
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                //// System.out.println(json);

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




