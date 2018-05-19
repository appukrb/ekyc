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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Activity.ServiceProviderListActivity;
import com.tcs.mmpl.customer.Activity.StateListActivity;
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

public class Landline extends Fragment
{
    private static final int MODE_PRIVATE = 0;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    private Button button;
    private ViewFlipper viewFlipper_landline;
    Spinner spnr_landline_oprator;
    String[] oprator_landline;

    EditText edtNumberLandline,edtAmountLandline,edtMpinLandline,edtbillDueDate_landline;
    TextView txtBrowsePlansLandline,txtOperatorLandline,txtNumberLandline,txtAmountLandline,txtConfirmLandlineDone,txtStateLandline;
    Button btnPaymentLandline,btnConfirmLandline,btnResetLandline,btnConfirmLandlineDone;
    CheckBox chkFavLandline;
    RadioButton radioPrepaidLandline,radioPostpaidLandline;
    ProgressDialog pDialog;
    EditText edtpromocode;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    ConnectionDetector connectionDetector;

    private static final String TAG = DTH.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    private String parameterType,parameterValue,URL;

    private LinearLayout billDueDate_landline;


    private Button btn_confirm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_landline,
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
                    if (viewFlipper_landline.getDisplayedChild() == 2) {
                        viewFlipper_landline.setDisplayedChild(1);
                    } else if (viewFlipper_landline.getDisplayedChild() == 1) {
                        viewFlipper_landline.setDisplayedChild(0);
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


        viewFlipper_landline = (ViewFlipper) rootView.findViewById(R.id.viewflipper_landline);
        fontclass.setFont(viewFlipper_landline,typeface );


        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        connectionDetector = new ConnectionDetector(getActivity());

        radioPrepaidLandline = (RadioButton)rootView.findViewById(R.id.radioPrepaidLandline);
        radioPostpaidLandline = (RadioButton)rootView.findViewById(R.id.radioPostpaidLandline);
        edtNumberLandline = (EditText)rootView.findViewById(R.id.edtNumberLandline);
        edtAmountLandline = (EditText)rootView.findViewById(R.id.edtAmountLandline);
        txtBrowsePlansLandline = (TextView)rootView.findViewById(R.id.txtBrowsePlansLandline);
        txtOperatorLandline = (TextView)rootView.findViewById(R.id.txtOperatorLandline);
        btnPaymentLandline = (Button) rootView.findViewById(R.id.btnPaymentLandline);
        chkFavLandline = (CheckBox)rootView.findViewById(R.id.chkFavLandline);
        edtpromocode = (EditText)rootView.findViewById(R.id.edtPromocode);
        txtStateLandline = (TextView)rootView.findViewById(R.id.txtStateLandline);

        btnPaymentLandline.setTypeface(btnPaymentLandline.getTypeface(), Typeface.BOLD);

        txtNumberLandline = (TextView)rootView.findViewById(R.id.txtNumberLandline);
        txtAmountLandline = (TextView)rootView.findViewById(R.id.txtAmountLandline);
        edtMpinLandline = (EditText)rootView.findViewById(R.id.edtMpinLandline);
        btnConfirmLandline = (Button)rootView.findViewById(R.id.btnConfirmLandline);
        btnResetLandline = (Button)rootView.findViewById(R.id.btnResetLandline);

        txtConfirmLandlineDone = (TextView)rootView.findViewById(R.id.txtConfirmLandlineDone);
        btnConfirmLandlineDone = (Button)rootView.findViewById(R.id.btnConfirmLandlineDone);

        edtbillDueDate_landline= (EditText)rootView.findViewById(R.id.edtbillDueDate_landline);
        billDueDate_landline=(LinearLayout)rootView.findViewById(R.id.billDueDate_landline);




    }

    private void onClick() {
        // TODO Auto-generated method stub



        edtNumberLandline.setOnTouchListener(new View.OnTouchListener() {
            // final int DRAWABLE_LEFT = 0;
            // final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;

            //  final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = edtNumberLandline.getRight()
                            - edtNumberLandline.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
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

        btnPaymentLandline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                AlertBuilder alert = new AlertBuilder(getActivity());
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                String landlineNumber = edtNumberLandline.getText().toString().trim();

                if (landlineNumber.toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_landline_number));
                } else if (txtOperatorLandline.getText().toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_landline_operator));
                } else if (edtAmountLandline.getText().toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_amount));
                }
                else if (edtMpinLandline.getText().toString().trim().equalsIgnoreCase(""))
                {
                    alert.showAlert(getResources().getString(R.string.invalid_mpin));
                }
                else if (edtNumberLandline.getText().toString().trim().equalsIgnoreCase("") || edtAmountLandline.getText().toString().trim().equalsIgnoreCase("") || txtOperatorLandline.getText().toString().equalsIgnoreCase("") || edtMpinLandline.getText().toString().trim().equalsIgnoreCase("")) {

                    alert.showAlert(getResources().getString(R.string.validation));
                } else {

                    String newLandlineNumber = landlineNumber.replaceFirst("^0+(?!$)", "");

                  //  if (newLandlineNumber.length() == 10) {




                            String landlinerecharge_url = "";
                            String mobileNo;
                            String category;
                            String rechargeMobile;
                            String amount;
                            String billerCode;
                            String mpin;
                            String region;
                            String BU = "OTHERS";


                            mobileNo = pref.getString("mobile_number", "");
                            category = Uri.encode("Landline Payments", "utf-8");
                            rechargeMobile = edtNumberLandline.getText().toString().trim();
                            amount = edtAmountLandline.getText().toString().trim();
                            billerCode = Uri.encode(txtOperatorLandline.getText().toString().trim(), "utf-8");
                            mpin = edtMpinLandline.getText().toString();
//                            region = Uri.encode(txtStateLandline.getText().toString().trim(), "utf-8");
                            String category1=Uri.decode(category);
                            String BingoString = mobileNo + "|" + ""+"|"+mpin + "|" + category1+ "|"+""+"|"+""+"|"+""+"|"+billerCode+"|"+rechargeMobile+"|"+amount+"|"+""+"|"+BU;
                            String res = Bingo.Bingo_one(BingoString);
                            // System.out.println("BingoString"+BingoString);

                            parameterType = "Landline";
                            parameterValue = mobileNo + "|" + amount + "|" + rechargeMobile + "|" + category + "|" + billerCode;
                            URL = getActivity().getResources().getString(R.string.billPayment) + "?mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + newLandlineNumber + "&Amount=" + amount +  "&BU=" + BU;

                            landlinerecharge_url = getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + newLandlineNumber + "&Amount=" + amount + "&BU=" + BU+"&Checksum="+res;


                            //// System.out.println("URL ......" + landlinerecharge_url);

                            CheckBalance checkBalance = new CheckBalance(getActivity());
                            if(checkBalance.getBalanceCheck(amount))
                            {

                                PopupBuilder popup = new PopupBuilder(getActivity());
                                popup.showPopup(checkBalance.getAmount(amount));

                                userInfoEditor.putString("transaction_url", landlinerecharge_url);
                                userInfoEditor.putString("transaction_method","POST2");
                                userInfoEditor.putString("transaction_flag","1");
                                userInfoEditor.commit();

                            }
                            else {
                                LandlineRecharge landlineRecharge = new LandlineRecharge(getActivity());
                                landlineRecharge.execute(landlinerecharge_url);
                            }

//                            txtNumberLandline.setText(edtNumberLandline.getText().toString().trim());
//                            txtAmountLandline.setText(edtAmountLandline.getText().toString().trim());
//
//                            viewFlipper_landline.setDisplayedChild(1);

//                    } else {
//                        Toast.makeText(getActivity(), "Please Enter STD Code and Number", Toast.LENGTH_LONG).show();
//                    }


                }

                } else {

                    alert.newUser();

                }
            }
        });




        txtOperatorLandline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionDetector.isConnectingToInternet())
                {

                    Intent i =new Intent(getActivity(),ServiceProviderListActivity.class);
                    i.putExtra("url",getResources().getString(R.string.service_provider));
                    i.putExtra("type","LANDLINE");
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

        txtStateLandline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectionDetector.isConnectingToInternet())
                {
                    //String url = "http://nexgenfmpl.com/numberdemo/WebService/listDthByOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665&page=1&per_page=10&operator="+ Uri.encode(txtOperatorDTH.getText().toString().trim(), "utf-8");

                    String url = getResources().getString(R.string.states);

                    Intent i =new Intent(getActivity(),StateListActivity.class);
                    i.putExtra("url",url);
                    startActivityForResult(i, 5);
//                    String operatorurl = "http://nexgenfmpl.com/numberdemo/WebService/listUniqDthOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665";
//                    GetOperator getOperator = new GetOperator(getActivity());
//                    getOperator.execute(operatorurl);
                }
                else {
                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();;
                }
            }
        });



        edtNumberLandline.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                //// System.out.println(s + ":::::::::::::::::: " + s.length());
                if (s.length() == 10 && txtOperatorLandline.getText().toString().trim().equalsIgnoreCase("TATA WALKY")) {
                    if (connectionDetector.isConnectingToInternet()) {

                        String mobile_number = edtNumberLandline.getText().toString().trim();

                        mobile_number = edtNumberLandline.getText().toString().trim().replaceFirst("^0", "");

                        //// System.out.println(mobile_number);

                        if (mobile_number.length() == 10) {


                            String tata_walky_url = getResources().getString(R.string.outstandingamount) + "?mobileNo=" + pref.getString("mobile_number", "") + "&rechMobile=" + edtNumberLandline.getText().toString().trim();
                            Tata_Walky tata_walky = new Tata_Walky(getActivity());
                            tata_walky.execute(tata_walky_url);
                        } else {

                        }

                    } else {

                        Toast.makeText(getActivity(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }
                } else {
                    edtAmountLandline.setText("");
                    billDueDate_landline.setVisibility(View.GONE);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
        else if(requestCode == 2){

            try {

                txtOperatorLandline.setText(data.getStringExtra("result"));

                if(data.getStringExtra("result").equalsIgnoreCase("TATA WALKY") && edtNumberLandline.length() == 10)
                {
                    String tata_walky_url = getResources().getString(R.string.outstandingamount) + "?mobileNo=" + pref.getString("mobile_number", "") + "&rechMobile=" + edtNumberLandline.getText().toString().trim();
                    Tata_Walky tata_walky = new Tata_Walky(getActivity());
                    tata_walky.execute(tata_walky_url);
                }
                else
                {
                    edtAmountLandline.setText("");
                    billDueDate_landline.setVisibility(View.GONE);
                }
            }
            catch (Exception e)
            {

            }

        }

        else if(requestCode == 5)
        {
            try
            {
                txtStateLandline.setText(data.getStringExtra("result"));
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

                edtNumberLandline.setText("");
                edtAmountLandline.setText("");
                txtOperatorLandline.setText("");
                edtpromocode.setText("");
                chkFavLandline.setChecked(false);
                txtStateLandline.setText("");
                edtMpinLandline.setText("");




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

            Contact contact = new Contact(getActivity(),edtNumberLandline);
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
//                edtNumberLandline.setText(contactNumber);
//            }
        }
        catch(NullPointerException ex)
        {


        }
        catch (Exception e) {
            // TODO: handle exception
        }


    }


    private class LandlineRecharge extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

        String firstName, lastName, walletBalance;

        public LandlineRecharge(Context context) {
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

                String url = getResources().getString(R.string.promocode)+"?MDN="+ pref.getString("mobile_number", "")+"&PROMOCODE="+edtpromocode.getText().toString().trim()+"&Transaction=Landline";
                updatePromocode up = new updatePromocode(getActivity());
                up.execute(url);

                if(chkFavLandline.isChecked())
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

       /* public View getCustomView(int position, View convertView,
                                  ViewGroup parent)
        {
            // return super.getView(position, convertView, parent);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            convertView = inflater.inflate(R.layout.textfile, parent, false);
            TextView label = (TextView) convertView.findViewById(R.id.txting);

            Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
            label.setTypeface(custom_font);

            label.setText(location[position]);
            return convertView;
        }*/
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
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

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

    private class Tata_Walky extends AsyncTask<String, Void, String> {
        Context context;
        ProgressDialog pDialog;

        public Tata_Walky(Context context) {
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


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        if(jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success"))
                        {
                            //// System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");


                            editor.putString("balance", jsonMainObj.getString("balance"));
                            editor.putString("billDueDate1", jsonMainObj.getString("billDueDate"));
                            editor.commit();

                            return "Success";

                        }else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {


                            editor.putString("responseMessage", jsonMainObj.getString("responseMessage"));
                            editor.commit();
                            return "failure";
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


        protected void onPostExecute(final String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.d("Result::::", ">" + result);

            if (result.equalsIgnoreCase("Success"))
            {
                String DueDate,AmountMobile;
                DueDate=pref.getString("billDueDate1","");
                AmountMobile=pref.getString("balance","");
                //// System.out.println("inside onPostExecute" + DueDate + AmountMobile);

                edtAmountLandline.setText(AmountMobile);

                if(!DueDate.trim().equalsIgnoreCase("NULL"))
                {
                    edtbillDueDate_landline.setText(DueDate);
                    edtbillDueDate_landline.setEnabled(false);
                    billDueDate_landline.setVisibility(View.VISIBLE);
                }

            } else if (result.equalsIgnoreCase("Failure"))
            {

                String responseMessage1 = pref.getString("responseMessage","");
                //// System.out.println(responseMessage1+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                Toast.makeText(getActivity(),responseMessage1,Toast.LENGTH_LONG).show();

            }

//            edtAmountMobile.setText(jsonMainObj.getString("balance"));
//            String responseMessage1 = pref.getString("responseMessage","");
//            String edtbillDueDate1=(jsonMainObj.getString("billDueDate"));
//            //// System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+edtbillDueDate1);
//
//            billDueDate.setVisibility(View.VISIBLE);
//            edtbillDueDate.setText(edtbillDueDate1);
//            edtbillDueDate.setEnabled(false);
//            //// System.out.println(jsonMainObj.getString("balance")+jsonMainObj.getString("billDueDate")+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");



        }
    }

}



