package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.Adapter.CropOptionAdapter;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.Contact;
import com.tcs.mmpl.customer.utility.CropOption;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.PopupBuilder;
import com.tcs.mmpl.customer.utility.SetNotificationCounter;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class QuickTransferActivity extends Activity {
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;
    private LinearLayout linWelcome, linDetails;
    private ImageView imgUser;
    private EditText edtMobileno,edtRemarks,edtAmount,edtMpin;
    private Button btn_confirm_mobile;
    MyDBHelper dbHelper;

    private Uri mImageCaptureUri;
    private static int RESULT_LOAD_IMG = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_GALLERY = 4;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;

    FontClass fontclass=new FontClass();
    Typeface typeface;
    private CheckBox chkFavMobile;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;

    ImageView profileImage;
    private LinearLayout linParent;
    private ImageView   dotimg ;
    private EditText edtpromocode;

    private static final String TAG = QuickTransfer.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID,parameterType,parameterValue,URL;




//    ExpandableListAdapter listAdapter;
//    ExpandableListView expListView;
//    List<String> listDataHeader;
//    HashMap<String, List<String>> listDataChild;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_quick_transfer);
        linParent = (LinearLayout)findViewById(R.id.linParent);
        typeface=Typeface.createFromAsset(getApplicationContext().getAssets(),"helvetica.otf");
        fontclass.setFont(linParent,typeface );
        dbHelper = new MyDBHelper(getApplicationContext());
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

        profileImage = (ImageView)findViewById(R.id.imgUser);
        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

            if(userInfoPref.getString("profilepic","").equalsIgnoreCase(""))
            {
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.dummy);
                profileImage.setImageBitmap(icon);
            }
            else
            {
                try {
                    Cursor c = dbHelper.fun_select_tbl_profileImage();

                    if (c.moveToNext()) {
                        profileImage.setImageBitmap(BitmapFactory.decodeByteArray(c.getBlob(0), 0, c.getBlob(0).length));
                    }
                    c.close();
                }
                catch (Exception e)
                {

                }
            }
        }
        else
        {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.dummy);
            profileImage.setImageBitmap(icon);
        }


        edtMobileno.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_RIGHT = 2;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = edtMobileno.getRight()
                            - edtMobileno.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();

                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
                        return true;
                    }
                }
                return false;
            }
        });

        //call HamburgerMenu
        new HamburgerMenu(this,(DrawerLayout) findViewById(R.id.drawer_layout),(ExpandableListView) findViewById(R.id.left_drawer),(ImageView) findViewById(R.id.idmenuimg)).setHamburger();


//        // get the listview
//        expListView = (ExpandableListView) findViewById(R.id.exp_left_drawer);
//
//        // preparing list data
//        prepareListData();
//
//        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
//
//        // setting list adapter
//        expListView.setAdapter(listAdapter);

        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(QuickTransferActivity.this, FavoriteActivity.class);
                                            startActivity(i);
                                        }
                                    }
        );



        dotimg = (ImageView) findViewById(R.id.iddotimg);
        dotimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(QuickTransferActivity.this, dotimg);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        Intent i = new Intent(QuickTransferActivity.this, WebActivity.class);
                        if (item.getTitle().toString().equalsIgnoreCase("FAQs")) {
                            i.putExtra("option", "FAQ");
                            startActivity(i);
                        }
                        else if (item.getTitle().toString().equalsIgnoreCase("My Account")) {
                            Intent intent = new Intent(getApplicationContext(), MyAcount.class);
                            startActivity(intent);
                        }

                        else if (item.getTitle().toString().equalsIgnoreCase("Privacy Policy")) {
                            i.putExtra("option", "PRIVACY");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Terms & Conditions")) {
                            i.putExtra("option", "TNC");
                            startActivity(i);
                        }
                        else if (item.getTitle().toString().equalsIgnoreCase("About Us")) {
                            i.putExtra("option", "ABOUT");
                            startActivity(i);
                        }
                        else if (item.getTitle().toString().equalsIgnoreCase("Contact Us")) {
                            i.putExtra("option", "CONTACT");
                            startActivity(i);

                        }else if (item.getTitle().toString().equalsIgnoreCase("Recharge & Pay")) {
                            Intent intent = new Intent(getApplicationContext(), Electricity_Payment.class);
                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Offers")) {
                            Intent intent = new Intent(getApplicationContext(), OffersActivity.class);
                            intent.putExtra("url",getResources().getString(R.string.offers));
                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Quick Transfer")) {
//                            Intent intent = new Intent(getApplicationContext(), QuickTransferActivity.class);
//                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Request Money")) {
                            Intent intent = new Intent(getApplicationContext(), RequestMoneyActivity.class);
                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Money Transfer")) {
                            Intent intent = new Intent(getApplicationContext(), MoneyTransferActivity.class);
                            intent.putExtra("index","0");
                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Manage Beneficiary")) {
                            Intent intent = new Intent(getApplicationContext(), MoneyTransferActivity.class);
                            intent.putExtra("index","3");
                            startActivity(intent);

                        }
                        else if (item.getTitle().toString().equalsIgnoreCase("Load Wallet")) {
                            Intent intent = new Intent(getApplicationContext(), LoadMoneyActivity.class);
                            startActivity(intent);
                        }else if(item.getTitle().toString().equalsIgnoreCase("Feedback")){
                            Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                            startActivity(intent);
                        }else if(item.getTitle().toString().equalsIgnoreCase("Rate Us")){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tcs.mmpl.customer")));
                        }



                        return true;
                    }
                });

                popup.show();
            }
        });
        btn_confirm_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertBuilder alert = new AlertBuilder(QuickTransferActivity.this);
                if(userInfoPref.getBoolean("new_user_registered_newapp",false)) {
                    if (connectionDetector.isConnectingToInternet()) {

                        String mobileNo1 = pref.getString("mobile_number", "");
                        String mobileNo2 = "91" + edtMobileno.getText().toString();
                        String amt = edtAmount.getText().toString();
                        String mpin = edtMpin.getText().toString();

                        String remarks = Uri.encode(edtRemarks.getText().toString().trim(),"utf-8");
                        String userType = userInfoPref.getString("usertype", "");

                        parameterType = "Quick Transfer";
                        parameterValue = mobileNo1 + "|" + amt + "|" + mobileNo2 + "|" + remarks ;

                        if(edtMobileno.getText().toString().trim().equalsIgnoreCase("") || edtMobileno.getText().toString().trim().length()<10)
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
                            alert.showAlert(getResources().getString(R.string.validation));
                        } else {
                            URL =getResources().getString(R.string.quickPay) + "?mobileNo1=" + mobileNo1 + "&mobileNo2=" + mobileNo2 + "&Amount=" + amt + "&userType=" + userType + "&Remarks=" + remarks;


                            String secretParameter = mobileNo1.trim() + "|" + mobileNo2.trim()+"|"+mpin.trim() +"|" +amt.trim();
                            String res = Bingo.Bingo_one(secretParameter);

                            String quickPay = getResources().getString(R.string.quickPay) + "?mobileNo1=" + mobileNo1 + "&mobileNo2=" + mobileNo2 + "&Amount=" + amt + "&userType=" + userType + "&Remarks=" + remarks + "&MPIN=" + mpin+"&Checksum="+res;

                            CheckBalance checkBalance = new CheckBalance(QuickTransferActivity.this);
                            if(checkBalance.getBalanceCheck(amt))
                            {

                                PopupBuilder popup = new PopupBuilder(QuickTransferActivity.this);
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
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
    public void openhome(View v)
    {
        Intent i = new Intent(QuickTransferActivity.this, HomeScreenActivity.class);
        startActivity(i);
    }
    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 50);
        intent.putExtra("aspectY", 50);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);

        try {
            // Start the Intent
            startActivityForResult(intent, PICK_FROM_GALLERY);
        }
        catch (ActivityNotFoundException ae)
        {

        }
        catch (Exception e)
        {

        }
    }

    public void cropImage(View v)
    {
        Intent intent = new Intent();
        // call android default gallery
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // ******** code for crop image
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 50);
        intent.putExtra("aspectY", 50);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);

        try {

            intent.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(intent,
                    "Complete action using"), PICK_FROM_GALLERY);

        } catch (ActivityNotFoundException e) {

        }
    }
    public void loadImage(View v)
    {

//        Intent intent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
////        Intent intent = new Intent();
////        intent.setType("image/*");
////        intent.setAction(Intent.ACTION_GET_CONTENT);
//
//        try {
//
//            startActivityForResult(intent, PICK_FROM_FILE);
//            // startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(getApplicationContext(),"Activity not found",Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();
            retrieveContactNumber();
        }


    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

        int size = list.size();

        // size = 0;
        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
//            intent.setData(mImageCaptureUri);
//
//            intent.putExtra("outputX", 200);
//            intent.putExtra("outputY", 200);
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("scale", true);
//            intent.putExtra("return-data", true);
//            Intent i = new Intent(intent);
//            startActivityForResult(i, PICK_FROM_GALLERY);
            return;
        } else {
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res	= list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent= new Intent(intent);

                    co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) {
                        try {
                            startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"Activity is not found",Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (mImageCaptureUri != null ) {
                            getContentResolver().delete(mImageCaptureUri, null, null );
                            mImageCaptureUri = null;
                        }
                    }
                } );

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
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

    public void generateOTP(View v)
    {
        GenerateOTPAlert generateOTPAlert = new GenerateOTPAlert(this);
        generateOTPAlert.openAlert();

    }
    public void openAlert(View v)
    {
        Intent i = new Intent(QuickTransferActivity.this, Shopping.class);
        startActivity(i);
    }

    private void openAlertDialog(AlertDialog.Builder alertDialog)
    {
        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.tick);
        // Setting OK Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                edtMobileno.setText("");
                edtAmount.setText("");
                edtMpin.setText("");
                edtpromocode.setText("");
                chkFavMobile.setChecked(false);
                edtRemarks.setText("");
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
                Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

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

            Contact contact = new Contact(QuickTransferActivity.this,edtMobileno);
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
            pDialog = new ProgressDialog(QuickTransferActivity.this);
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
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            responseMessage = jsonMainObj.getString("responseMessage");


                            if(jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                            {
                                new LocalNotification(QuickTransferActivity.this,jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationTitle)),jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationMessage))).sendNotification();
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


            if (result.equalsIgnoreCase("Success")) {
                String url = getResources().getString(R.string.promocode)+"?MDN="+ pref.getString("mobile_number", "")+"&PROMOCODE="+edtpromocode.getText().toString().trim()+"&Transaction=QuickTransfer";
                updatePromocode up = new updatePromocode(getApplicationContext());
                up.execute(url);

                if(chkFavMobile.isChecked()) {
                    String addFavorites_url = getApplicationContext().getResources().getString(R.string.addfavourites);
                    AddFavorites addFavorites = new AddFavorites(getApplicationContext());
                    addFavorites.execute(addFavorites_url);
                }


                AlertBuilder alert = new AlertBuilder(QuickTransferActivity.this);
                AlertDialog.Builder alertDialog = alert.showRetryAlert(responseMessage + getResources().getString(R.string.make_transfer));
                openAlertDialog(alertDialog);

            }
            else {
                Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_LONG).show();
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

                WebServiceHandler serviceHandler = new WebServiceHandler(QuickTransferActivity.this, se);
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
                WebServiceHandler serviceHandler = new WebServiceHandler(QuickTransferActivity.this);
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

        }

    }

    public void openinbox(View v)
    {
        Intent s5 =new Intent(getApplicationContext(),AlertActivity.class);
        startActivity(s5);

    }

    @Override
    public void onResume(){
        super.onResume();

        new SetNotificationCounter(getApplicationContext(), (TextView)findViewById(R.id.txtNotificationCounter));

    }


//
//    private void prepareListData() {
//        listDataHeader = new ArrayList<String>();
//        listDataChild = new HashMap<String, List<String>>();
//
//        // Adding child data
//        listDataHeader.add("Top 250");
//        listDataHeader.add("Now Showing");
//        listDataHeader.add("Coming Soon..");
//
//        // Adding child data
//        List<String> top250 = new ArrayList<String>();
//        top250.add("The Shawshank Redemption");
//        top250.add("The Godfather");
//        top250.add("The Godfather: Part II");
//        top250.add("Pulp Fiction");
//        top250.add("The Good, the Bad and the Ugly");
//        top250.add("The Dark Knight");
//        top250.add("12 Angry Men");
//
//        List<String> nowShowing = new ArrayList<String>();
//        nowShowing.add("The Conjuring");
//        nowShowing.add("Despicable Me 2");
//        nowShowing.add("Turbo");
//        nowShowing.add("Grown Ups 2");
//        nowShowing.add("Red 2");
//        nowShowing.add("The Wolverine");
//
//        List<String> comingSoon = new ArrayList<String>();
//        comingSoon.add("2 Guns");
//        comingSoon.add("The Smurfs 2");
//        comingSoon.add("The Spectacular Now");
//        comingSoon.add("The Canyons");
//        comingSoon.add("Europa Report");
//
//        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
//        listDataChild.put(listDataHeader.get(1), nowShowing);
//        listDataChild.put(listDataHeader.get(2), comingSoon);
//    }
//
//
//    public class ExpandableListAdapter extends BaseExpandableListAdapter {
//
//        private Context _context;
//        private List<String> _listDataHeader; // header titles
//        // child data in format of header title, child title
//        private HashMap<String, List<String>> _listDataChild;
//
//        public ExpandableListAdapter(Context context, List<String> listDataHeader,
//                                     HashMap<String, List<String>> listChildData) {
//            this._context = context;
//            this._listDataHeader = listDataHeader;
//            this._listDataChild = listChildData;
//        }
//
//        @Override
//        public Object getChild(int groupPosition, int childPosititon) {
//            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
//                    .get(childPosititon);
//        }
//
//        @Override
//        public long getChildId(int groupPosition, int childPosition) {
//            return childPosition;
//        }
//
//        @Override
//        public View getChildView(int groupPosition, final int childPosition,
//                                 boolean isLastChild, View convertView, ViewGroup parent) {
//
//            final String childText = (String) getChild(groupPosition, childPosition);
//
//            if (convertView == null) {
//                LayoutInflater infalInflater = (LayoutInflater) this._context
//                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                convertView = infalInflater.inflate(R.layout.list_item, null);
//            }
//
//            TextView txtListChild = (TextView) convertView
//                    .findViewById(R.id.lblListItem);
//
//            txtListChild.setText(childText);
//            return convertView;
//        }
//
//        @Override
//        public int getChildrenCount(int groupPosition) {
//            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
//                    .size();
//        }
//
//        @Override
//        public Object getGroup(int groupPosition) {
//            return this._listDataHeader.get(groupPosition);
//        }
//
//        @Override
//        public int getGroupCount() {
//            return this._listDataHeader.size();
//        }
//
//        @Override
//        public long getGroupId(int groupPosition) {
//            return groupPosition;
//        }
//
//        @Override
//        public View getGroupView(int groupPosition, boolean isExpanded,
//                                 View convertView, ViewGroup parent) {
//            String headerTitle = (String) getGroup(groupPosition);
//            if (convertView == null) {
//                LayoutInflater infalInflater = (LayoutInflater) this._context
//                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                convertView = infalInflater.inflate(R.layout.list_group, null);
//            }
//
//            TextView lblListHeader = (TextView) convertView
//                    .findViewById(R.id.lblListHeader);
//            lblListHeader.setTypeface(null, Typeface.BOLD);
//            lblListHeader.setText(headerTitle);
//
//            return convertView;
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return false;
//        }
//
//        @Override
//        public boolean isChildSelectable(int groupPosition, int childPosition) {
//            return true;
//        }
//    }

}
