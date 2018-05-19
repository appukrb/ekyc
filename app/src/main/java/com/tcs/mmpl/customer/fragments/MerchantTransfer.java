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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.Activity.HotelMerchantActivity;
import com.tcs.mmpl.customer.Activity.MerchantList;
import com.tcs.mmpl.customer.Activity.MerchantPayment;
import com.tcs.mmpl.customer.Activity.SkillAngels;
import com.tcs.mmpl.customer.Activity.TransferToBank;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.CityListDialog;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.Contact;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Admin on 9/16/2015.
 */
public class MerchantTransfer extends Fragment {

    private static final int MODE_PRIVATE = 0;
    private static final String TAG = TransferToBank.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final int REQUEST_CODE_MERCHANT_LIST = 5;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;
    Activity activity;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    private ViewFlipper viewflipper_merchant_transfer;
    private String contactID, parameterType, parameterValue, URL;
    private EditText edtMerchantMobileNumber;
    private TextView txtMerchantName, txtSelectCity;
    private RadioButton radioMerchant, radioMerchantMobile;
    private Button btnMerchantConfirm;
    private MyDBHelper dbHelper;
    private MyConnectionHelper db;
    private Uri uriContact;

    private LinearLayout linCity;

    private String merchantNumber = "", merchantName = "", Isd_code;

    private String selected_city;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_merchant_transfer, container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
        activity = getActivity();

        init(rootView);

        onClick();


        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
//        rootView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)) {
//                    if (viewFlipper_transfer_to_wallet.getDisplayedChild() == 2) {
//                        viewFlipper_transfer_to_wallet.setDisplayedChild(1);
//                    }
//                   /* else if(viewFlipper_transfer_to_wallet.getDisplayedChild() == 2)
//                    {
//                        viewFlipper_transfer_to_wallet.setDisplayedChild(1);
//                    }*/
//                    else if (viewFlipper_transfer_to_wallet.getDisplayedChild() == 1) {
//                        viewFlipper_transfer_to_wallet.setDisplayedChild(0);
//                    } else {
//                        getActivity().finish();
//                        Intent i = new Intent(getActivity(), HomeScreenActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(i);
//                    }
//
//                    return true;
//                }
//                return false;
//            }
//        });

        return rootView;
    }

    private void onClick() {

        edtMerchantMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtMerchantName.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        edtMerchantMobileNumber.setOnTouchListener(new View.OnTouchListener() {
            // final int DRAWABLE_LEFT = 0;
            // final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;

            //  final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = edtMerchantMobileNumber.getRight()
                            - edtMerchantMobileNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    // when EditBox has padding, adjust leftEdge like
                    // leftEdgeOfRightDrawable -= getResources().getDimension(R.dimen.edittext_padding_left_right);
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {

                        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);

                        return true;
                    }
                }
                return false;
            }
        });


        radioMerchant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txtMerchantName.setEnabled(false);
                edtMerchantMobileNumber.setText("");
                edtMerchantMobileNumber.setEnabled(true);
                linCity.setVisibility(View.GONE);
                if (isChecked)
                    radioMerchantMobile.setChecked(false);
            }
        });

        radioMerchantMobile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txtMerchantName.setEnabled(true);
                edtMerchantMobileNumber.setEnabled(false);


                if (isChecked)
                    radioMerchant.setChecked(false);
            }
        });


        btnMerchantConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioMerchant.isChecked()) {

                    if (!txtMerchantName.getText().toString().trim().equalsIgnoreCase("") && txtMerchantName.getText().toString().trim().equalsIgnoreCase("SKILLANGELS")) {
                        Intent i = new Intent(getActivity(), SkillAngels.class);
                        i.putExtra("merchantNumber", merchantNumber);
                        i.putExtra("merchantName", merchantName);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    } else if (!txtMerchantName.getText().toString().trim().equalsIgnoreCase("") && txtMerchantName.getText().toString().trim().equalsIgnoreCase("ADYAR ANAND BHAVAN")) {


//                            if(txtSelectCity.getText().toString().trim().equalsIgnoreCase(""))
//                            {
//                                AlertBuilder alert = new AlertBuilder(getActivity());
//                                alert.showAlert(getResources().getString(R.string.invalid_city));
//                            }
//                            else {
//
//                                Intent i = new Intent(getActivity(), HotelLocationActivity.class);
//                                i.putExtra("merchantNumber", merchantNumber);
//                                i.putExtra("merchantName", merchantName);
//                                i.putExtra("city", Uri.encode(txtSelectCity.getText().toString().trim(),"utf-8"));
//                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(i);
//                            }



                            Intent i = new Intent(getActivity(), HotelMerchantActivity.class);
                            i.putExtra("merchantNumber", merchantNumber);
                            i.putExtra("merchantName",merchantName);
                            i.putExtra("amount", "");
                            i.putExtra("shopcode", "");
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);



                    } else if (!txtMerchantName.getText().toString().trim().equalsIgnoreCase("")) {
                        Intent i = new Intent(getActivity(), MerchantPayment.class);
                        i.putExtra("merchantNumber", merchantNumber);
                        i.putExtra("merchantName", merchantName);
                        i.putExtra("amount", "");
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    } else {
                        AlertBuilder alert = new AlertBuilder(getActivity());
                        alert.showAlert(getResources().getString(R.string.invalid_merchant_type));
                    }

                } else if (radioMerchantMobile.isChecked()) {

                    if (!edtMerchantMobileNumber.getText().toString().trim().equalsIgnoreCase("") && edtMerchantMobileNumber.getText().toString().trim().length() == 10) {
                        Intent i = new Intent(getActivity(), MerchantPayment.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("merchantNumber", edtMerchantMobileNumber.getText().toString().trim());
                        i.putExtra("merchantName", merchantName);
                        i.putExtra("amount", "");
                        startActivity(i);
                    } else {
                        AlertBuilder alert = new AlertBuilder(getActivity());
                        alert.showAlert(getResources().getString(R.string.invalid_mobile));
                    }
                }


            }
        });

        txtMerchantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connectionDetector.isConnectingToInternet()) {
                    Intent i = new Intent(getActivity(), MerchantList.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(i, REQUEST_CODE_MERCHANT_LIST);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void init(View rootView) {

        LinearLayout linParent = (LinearLayout) rootView.findViewById(R.id.linParent);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        connectionDetector = new ConnectionDetector(getActivity());

        db = new MyConnectionHelper(getActivity());

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        edtMerchantMobileNumber = (EditText) rootView.findViewById(R.id.edtMerchantMobileNumber);
        txtMerchantName = (TextView) rootView.findViewById(R.id.txtMerchantName);
        txtSelectCity = (TextView) rootView.findViewById(R.id.txtSelectCity);
        linCity = (LinearLayout)rootView.findViewById(R.id.linCity);
        btnMerchantConfirm = (Button) rootView.findViewById(R.id.btnMerchantConfirm);

        radioMerchant = (RadioButton) rootView.findViewById(R.id.radioMerchant);
        radioMerchantMobile = (RadioButton) rootView.findViewById(R.id.radioMerchantMobile);

        dbHelper = new MyDBHelper(getActivity());

        viewflipper_merchant_transfer = (ViewFlipper) rootView.findViewById(R.id.viewflipper_merchant_transfer);
        fontclass.setFont(viewflipper_merchant_transfer, typeface);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == getActivity().RESULT_OK) {
            uriContact = data.getData();
            retrieveContactNumber();
        } else if (requestCode == REQUEST_CODE_MERCHANT_LIST) {
            try {
                txtMerchantName.setText(data.getStringExtra("merchantName"));
                merchantNumber = data.getStringExtra("merchantNumber");
                merchantName = data.getStringExtra("merchantName");

                if (!txtMerchantName.getText().toString().trim().equalsIgnoreCase("") && txtMerchantName.getText().toString().trim().equalsIgnoreCase("SKILLANGELS")) {
                    linCity.setVisibility(View.GONE);
                    Intent i = new Intent(getActivity(), SkillAngels.class);
                    i.putExtra("merchantNumber", merchantNumber);
                    i.putExtra("merchantName", merchantName);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
//                else if (!txtMerchantName.getText().toString().trim().equalsIgnoreCase("") && txtMerchantName.getText().toString().trim().equalsIgnoreCase("ADYAR ANAND BHAVAN")) {
////                    if (connectionDetector.isConnectingToInternet()) {
////
////                        GetHotelCity getHotelCity = new GetHotelCity(getActivity());
////                        getHotelCity.execute(getActivity().getResources().getString(R.string.getAdyarCity_url));
////                    }
//                    linCity.setVisibility(View.GONE);
//                    Intent i = new Intent(getActivity(), HotelMerchantActivity.class);
//                    i.putExtra("merchantNumber", merchantNumber);
//                    i.putExtra("merchantName",merchantName);
//                    i.putExtra("amount", "");
//                    i.putExtra("shopcode", "");
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//
//
//                }
//                else if (!txtMerchantName.getText().toString().trim().equalsIgnoreCase("")) {
//                    linCity.setVisibility(View.GONE);
//                    Intent i = new Intent(getActivity(), MerchantPayment.class);
//                    i.putExtra("merchantNumber", merchantNumber);
//                    i.putExtra("merchantName", merchantName);
//                    i.putExtra("amount", "");
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//                }
//                else {
//                    linCity.setVisibility(View.GONE);
//
//                    AlertBuilder alert = new AlertBuilder(getActivity());
//                    alert.showAlert(getResources().getString(R.string.invalid_merchant_type));
//                }


            } catch (Exception e) {

            }
        }

    }


    private void retrieveContactNumber() {

        try {
            String id = uriContact.getLastPathSegment();

            Contact contact = new Contact(getActivity(), edtMerchantMobileNumber);
            contact.getContactNumber(id);
        } catch (NullPointerException ex) {


        } catch (Exception e) {
            // TODO: handle exception
        }


    }

    private void showCity(ArrayList<String> city) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Select City");
        final String[] value = city.toArray(new String[city.size()]);
        builder.setItems(value, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedText = Arrays.asList(value).get(which);
                txtSelectCity.setText(selectedText.trim());
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private class GetHotelCity extends AsyncTask<String, Void, String> {

        ArrayList<String> city;

        private Context context;
        private ProgressDialog pDialog;

        public GetHotelCity(Context context) {
            this.context = context;
            city = new ArrayList<>();

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

                jsonStr = "{ \"value\":" + jsonStr + "}";

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");

                        //// System.out.println("Length of jsonArray..............." + jsonArray.length());


                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = (JSONObject) jsonArray.get(i);

                                city.add(obj.getString("city"));
                            }
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

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);

            if (result.equalsIgnoreCase("Success")) {

                linCity.setVisibility(View.GONE);

                txtSelectCity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        showCity(city);
                          String[] value = city.toArray(new String[city.size()]);
                       new CityListDialog(getActivity(),value,txtSelectCity);
                    }
                });




            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

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

                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

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

                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            }

        }

    }

}




