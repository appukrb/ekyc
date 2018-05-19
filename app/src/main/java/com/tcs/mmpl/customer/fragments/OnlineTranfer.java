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
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.Activity.TransferToBank;
import com.tcs.mmpl.customer.Activity.WebActivity;
import com.tcs.mmpl.customer.Adapter.GridViewAdapter;
import com.tcs.mmpl.customer.Adapter.MerchantImageAndName;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class OnlineTranfer extends Fragment {





    private static final int MODE_PRIVATE = 0 ;
    FontClass fontclass=new FontClass();
    Typeface typeface;

    private ViewFlipper viewflipper_merchant_transfer;
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    Activity activity;
    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    private static final String TAG = TransferToBank.class.getSimpleName();

    private String contactID,parameterType,parameterValue,URL;

    private GridView mGridView;
    private TextView txtMerchantName;


    private MyDBHelper dbHelper;
    private MyConnectionHelper db;

    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final int REQUEST_CODE_MERCHANT_LIST = 5;
    private Uri uriContact;

    private String merchantNumber="",merchantName="",Isd_code;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_online_tranfer,
                container, false);
        typeface=Typeface.createFromAsset(getActivity().getAssets(),"helvetica.otf");
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

        String url = getResources().getString(R.string.category);
        CategoryList categoryList = new CategoryList(getActivity());
        categoryList.execute(url);

        return rootView;
    }

    private void onClick()
    {





    }

    private void init(View rootView) {


//        connectionDetector = new ConnectionDetector(getActivity());
//
//        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
//        editor = pref.edit();
//
//        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
//        userInfoEditor = userInfoPref.edit();
//
//        edtMobile =(EditText)  rootView.findViewById(R.id.edtMobileNumber);
//        edtAmount =(EditText)  rootView.findViewById(R.id.edtAmount);
//        edtComment =(EditText)  rootView.findViewById(R.id.edtComment);
//        edtMpin =(EditText)  rootView.findViewById(R.id.edtMpin);
//        edtpromocode = (EditText)rootView.findViewById(R.id.edtPromocode);
//        btnSendToWallet = (Button) rootView.findViewById(R.id.btnSendToWallet);
//        chkFavMobile = (CheckBox)rootView.findViewById(R.id.chkFavMobile);
//        viewFlipper_transfer_to_wallet = (ViewFlipper) rootView.findViewById(R.id.viewflipper_transfert_to_wallet);
//        fontclass.setFont(viewFlipper_transfer_to_wallet, typeface);
//
//        btnSendToWallet.setTypeface(btnSendToWallet.getTypeface(), Typeface.BOLD);


        LinearLayout linParent = (LinearLayout)rootView.findViewById(R.id.linParent);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        connectionDetector = new ConnectionDetector(getActivity());


         mGridView = (GridView)rootView.findViewById(R.id.gridView);

        viewflipper_merchant_transfer = (ViewFlipper) rootView.findViewById(R.id.viewflipper_merchant_transfer);
        fontclass.setFont(viewflipper_merchant_transfer, typeface);



    }

//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//
//        if(requestCode == 5)
//        {
//            try
//            {
//                txtCategory.setText(data.getStringExtra("result"));
//
//                String url = getResources().getString(R.string.onlinemerchant)+"?category="+ Uri.encode(data.getStringExtra("result"),"UTF-8");
//                MerchantImage merchantImage = new MerchantImage(getActivity());
//                merchantImage.execute(url);
//
//            }
//            catch (Exception e)
//            {
//
//            }
//        }
//
//
//
//
//
//    }



    private class CategoryList extends AsyncTask<String, Void, String> {

        ArrayList<MerchantImageAndName> mGridData;
        Context context;
        ArrayList<String> category;


        public CategoryList(Context context) {
            this.context = context;
            category = new ArrayList<String>();

            mGridData  = new ArrayList<MerchantImageAndName>();
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

                //// System.out.println(jsonStr);

                jsonStr = "{\"value\":" + jsonStr + "}";

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");

                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                MerchantImageAndName merchantImageAndName = new MerchantImageAndName();

                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                merchantImageAndName.setCategoryName((obj.getString("category").trim()));
                                merchantImageAndName.setImage(obj.getString("iconUrl"));

                                mGridData.add(merchantImageAndName);
                            }

                            return "Success";
                        } else
                            return "Failure";


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
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int width = metrics.widthPixels-20;

                LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(width/2,width/2);

                //Initialize with empty data

                GridViewAdapter mGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, mGridData,width,layoutParams);
                mGridView.setAdapter(mGridAdapter);


                //Grid view click event
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        //Get item at position
                        MerchantImageAndName item = (MerchantImageAndName) parent.getItemAtPosition(position);
//                        String url = getResources().getString(R.string.onlinemerchant)+"?category="+ Uri.encode(item.getCategoryName(),"UTF-8");
//                        Intent intent = new Intent(getActivity(),MerchantCategoryList.class);
//                        intent.putExtra("url",url);
//                        getActivity().startActivity(intent);

                        Intent i = new Intent(getActivity(), WebActivity.class);
                        i.putExtra("option", Uri.encode(item.getCategoryName(),"UTF-8"));
                        startActivity(i);




                    }
                });






            } else if (result.equalsIgnoreCase("Failure")) {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        getActivity()).create();

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
                        getActivity().finish();
                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

                // Toast.makeText(getApplicationContext(),getResources().getString(R.string.apidown),Toast.LENGTH_LONG).show();;

            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        getActivity()).create();

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
                        getActivity().finish();
                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            }

        }

    }


}
