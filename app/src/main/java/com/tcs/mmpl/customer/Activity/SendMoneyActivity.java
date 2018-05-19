package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.Contact;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.PopupBuilder;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class SendMoneyActivity extends Activity {

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;
    EditText edtMobileno,edtRemarks,edtAmount,edtMpin;
    Button btn_confirm_mobile;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    CheckBox chkFavMobile;

    RelativeLayout linParent;

    EditText edtpromocode;
    ImageView imgBanner;
    MyConnectionHelper db;
    private static final String TAG = QuickTransfer.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID,parameterType,parameterValue,URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        linParent = (RelativeLayout)findViewById(R.id.linParent);
        typeface= Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent,typeface );

        edtMobileno = (EditText) findViewById(R.id.edtMobileNumber);
        edtAmount = (EditText) findViewById(R.id.edtAmount);
        edtRemarks = (EditText) findViewById(R.id.edtRemarks);
        edtMpin = (EditText) findViewById(R.id.edtMpin);
        edtpromocode = (EditText)findViewById(R.id.edtPromocode);
        btn_confirm_mobile=(Button)findViewById(R.id.btn_confirm_mobile);
        chkFavMobile = (CheckBox)findViewById(R.id.chkFavMobile);

        btn_confirm_mobile.setTypeface(btn_confirm_mobile.getTypeface(),Typeface.BOLD);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

//        ImageView profileImage = (ImageView)findViewById(R.id.imgUser);
//        MyDBHelper dbHelper;
//        dbHelper = new MyDBHelper(getApplicationContext());
//        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
//
////            if(userInfoPref.getString("profilepic","").equalsIgnoreCase(""))
////            {
////                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.dummy);
////                profileImage.setImageBitmap(icon);
////            }
////            else
////            {
////                try {
////                    Cursor c = dbHelper.fun_select_tbl_profileImage();
////
////                    if (c.moveToNext()) {
////                        profileImage.setImageBitmap(BitmapFactory.decodeByteArray(c.getBlob(0), 0, c.getBlob(0).length));
////                    }
////                    c.close();
////                }
////                catch (Exception e)
////                {
////
////                }
////            }
//        }
//        else
//        {
//            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.dummy);
//            profileImage.setImageBitmap(icon);
//        }

        imgBanner = (ImageView)findViewById(R.id.imgBanner);
        db = new MyConnectionHelper(getApplicationContext());

        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();

        if(c1.moveToNext())
        {

            do
            {
                //// System.out.println("********************************************************"+c1.getString(3)+" " +c1.getString(1));
                if(c1.getString(3).equalsIgnoreCase("LOAD"))
                {
                    Glide.with(SendMoneyActivity.this).load(c1.getString(1)).into(imgBanner);
                    imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }



            }while(c1.moveToNext());
        }

        c1.close();


        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bannerURL = getResources().getString(R.string.bannerlink) + "?Type=LOAD";
                GetBannerLink getBannerLink = new GetBannerLink(getApplicationContext());
                getBannerLink.execute(bannerURL);
            }
        });
        edtMobileno.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_RIGHT = 2;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = edtMobileno.getRight()
                            - edtMobileno.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
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
        btn_confirm_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertBuilder alert = new AlertBuilder(SendMoneyActivity.this);

                if(userInfoPref.getBoolean("new_user_registered_newapp",false)) {
                    if (connectionDetector.isConnectingToInternet()) {


                        String mobileNo1 = pref.getString("mobile_number", "");
                        String mobileNo2 = "91" + edtMobileno.getText().toString();
                        String amt = edtAmount.getText().toString();
                        String mpin = edtMpin.getText().toString();
//                        String remarks = edtRemarks.getText().toString();
                        String remarks= Uri.encode(edtRemarks.getText().toString().trim(),"utf-8");
                        String userType = userInfoPref.getString("usertype", "");

                        parameterType = "Send Money";
                        parameterValue = mobileNo1 + "|" + amt + "|" + mobileNo2 + "|" + remarks ;

                        if(edtMobileno.getText().toString().trim().equalsIgnoreCase(""))
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
                        else if (edtMobileno.getText().toString().trim().equalsIgnoreCase("") || amt.trim().equalsIgnoreCase("") || mpin.trim().equalsIgnoreCase("")) {
                           alert.showAlert( getResources().getString(R.string.validation));

                        } else {

                            URL =getResources().getString(R.string.quickPay) + "?mobileNo1=" + mobileNo1 + "&mobileNo2=" + mobileNo2 + "&Amount=" + amt + "&userType=" + userType + "&Remarks=" + remarks;

                            String quickPay = getResources().getString(R.string.quickPay) + "?mobileNo1=" + mobileNo1 + "&mobileNo2=" + mobileNo2 + "&Amount=" + amt + "&userType=" + userType + "&Remarks=" + remarks + "&MPIN=" + mpin;

                            CheckBalance checkBalance = new CheckBalance(SendMoneyActivity.this);
                            if(checkBalance.getBalanceCheck(amt))
                            {

                                PopupBuilder popup = new PopupBuilder(SendMoneyActivity.this);
                                popup.showPopup(checkBalance.getAmount(amt));

                                userInfoEditor.putString("transaction_url", quickPay);
                                userInfoEditor.putString("transaction_method","POST2");
                                userInfoEditor.putString("transaction_flag","1");
                                userInfoEditor.commit();

                            }
                            else {
                                QuickTransfer quickTransfer = new QuickTransfer(getApplicationContext());
                                quickTransfer.execute(quickPay);
                            }
                        }
                    } else {


                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                    }
                }
                else
                {
                    alert.newUser();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {

        finish();
        Intent i = new Intent(getApplicationContext(),HomeScreenActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

    }
    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    class DrawerItemCustomAdapter extends BaseAdapter {
        private Context context;
        private List<ObjectDrawerItem> list;

        public DrawerItemCustomAdapter(Context context, List<ObjectDrawerItem> list) {
            this.context = context;
            this.list = list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {

            return list.get(i);
        }

        @Override
        public long getItemId(int i) {

            return i;
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.listview_item_row, parent, false);
            TextView textViewName = (TextView) row.findViewById(R.id.textViewName);
            Typeface custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
            textViewName.setTypeface(custom_font);
            final ObjectDrawerItem objectDrawerItem = list.get(position);
            textViewName.setText(objectDrawerItem.getName());


            return (row);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            //  Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            // retrieveContactName();
            retrieveContactNumber();
            //retrieveContactPhoto();

        }


    }

    private void retrieveContactNumber() {

        try
        {

            String id = uriContact.getLastPathSegment();

            Contact contact = new Contact(SendMoneyActivity.this,edtMobileno);
            contact.getContactNumber(id);

//            String contactNumber = null;
//
//            // getting contacts ID
//            Cursor cursorID = getContentResolver().query(uriContact,
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
//            Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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
//                edtMobileno.setText(contactNumber);
//            }
        }
        catch(NullPointerException ex)
        {


        }
        catch (Exception e) {
            // TODO: handle exception
        }


    }

    private class QuickTransfer extends AsyncTask<String, Void, String> {

        Context context;

        String responseMessage;

        public QuickTransfer(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SendMoneyActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
//                JSONObject jsonObject = new JSONObject();



                WebServiceHandler serviceHandler = new WebServiceHandler(context);
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());

                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {
                            responseMessage = jsonMainObj.getString("responseMessage");

                            if(jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                            {
                                new LocalNotification(SendMoneyActivity.this,jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationTitle)),jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationMessage))).sendNotification();
                            }
                            return "Success";
                        }
                        else
                        {
                            responseMessage = jsonMainObj.getString("responseMessage");
                            return "Failure";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failure";
                    }
                }
                else {
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
            AlertBuilder alert = new AlertBuilder(SendMoneyActivity.this);


            if (result.equalsIgnoreCase("Success")) {


                String url = getResources().getString(R.string.promocode)+"?MDN="+ pref.getString("mobile_number", "")+"&PROMOCODE="+edtpromocode.getText().toString().trim()+"&Transaction=QuickTransfer";
                updatePromocode up = new updatePromocode(getApplicationContext());
                up.execute(url);

                if(chkFavMobile.isChecked()) {
                    String addFavorites_url = getApplicationContext().getResources().getString(R.string.addfavourites);
                    AddFavorites addFavorites = new AddFavorites(getApplicationContext());
                    addFavorites.execute(addFavorites_url);
                }
                alert.showAlert(responseMessage);

            }
            else {
                alert.showAlert(responseMessage);
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

                WebServiceHandler serviceHandler = new WebServiceHandler(SendMoneyActivity.this, se);
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


                WebServiceHandler serviceHandler = new WebServiceHandler(SendMoneyActivity.this);
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

            return "Success";
        }

        @Override
        protected void onPostExecute(String result) {

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);

        }

    }

    private class GetBannerLink extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;
        private ProgressDialog pDialog;
        private String activityName;
        private String urlLink;

        public GetBannerLink(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(HomeScreenActivity.this);
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler serviceHandler = new WebServiceHandler(getApplication());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                JSONObject jsonObject = new JSONObject(jsonStr);

                activityName = jsonObject.getString("activityName");
                urlLink = jsonObject.getString("externalURL");


                return "Success";

            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure1";
            }



        }

        @Override
        protected void onPostExecute(String result) {


//            if(pDialog.isShowing())
//                pDialog.dismiss();

            //// System.out.println(activityName);
            //// System.out.println(urlLink);
            if(!activityName.equalsIgnoreCase("NA") && !urlLink.equalsIgnoreCase("NA"))
            {
                try {

                    String className = getPackageName()+".Activity."+activityName;
                    Class<?> myClass = Class.forName(className);
                    //Activity activity = (Activity) myClass.newInstance();
                    Intent re = new Intent(getApplicationContext(),myClass);
                    re.putExtra("option", urlLink);
                    startActivity(re);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    //// System.out.println("exception "+e.getMessage());
                }
            }
            else if(!activityName.equalsIgnoreCase("NA") && urlLink.equalsIgnoreCase("NA"))
            {
                try {
                    String className = getPackageName()+".Activity."+activityName;
                    Class<?> myClass = Class.forName(className);
                    //Activity activity = (Activity) myClass.newInstance();
                    Intent re = new Intent(getApplicationContext(),myClass);
                    startActivity(re);
                }
                catch (Exception e)
                {

                }

            }
            else if(activityName.equalsIgnoreCase("NA") && !urlLink.equalsIgnoreCase("NA"))
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink)));
            }



        }

    }


}
