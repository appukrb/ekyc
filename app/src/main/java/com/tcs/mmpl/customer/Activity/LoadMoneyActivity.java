package com.tcs.mmpl.customer.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
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
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.Adapter.CropOptionAdapter;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.CropOption;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.KycList;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.Purse;
import com.tcs.mmpl.customer.utility.SetNotificationCounter;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoadMoneyActivity extends TabActivity {
    LinearLayout mainlinear;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    TextView txtBalance,txtUpgrade;

    private Uri mImageCaptureUri;
    private static int RESULT_LOAD_IMG = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_GALLERY = 4;

    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    TabHost tabHost;


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;
    ConnectionDetector connectionDetector;
    ImageView profileImage;
    MyDBHelper dbHelper;
    ArrayList<KycList> kycLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load_money);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();
        profileImage = (ImageView)findViewById(R.id.imgUser);

        dbHelper = new MyDBHelper(getApplicationContext());
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

        txtUpgrade = (TextView)findViewById(R.id.txtUpgrade);
        mainlinear = (LinearLayout) findViewById(R.id.LinearLayout01);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        txtUpgrade.setTypeface(txtUpgrade.getTypeface(),Typeface.BOLD);

        txtBalance = (TextView) findViewById(R.id.txtBalance);
        if(userInfoPref.getString("kycstatus","").equalsIgnoreCase("PENDING") || userInfoPref.getString("kycstatus","").equalsIgnoreCase("REJECTED"))
            txtUpgrade.setVisibility(View.VISIBLE);
        else
            txtUpgrade.setVisibility(View.GONE);

        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {


            txtBalance.setText(userInfoPref.getString("walletbalance", ""));
        }


              tabHost = getTabHost();


        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");




        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator(createTabIndicator("Load Money"));
        tab1.setContent(new Intent(this, LoadwalletActivity.class));

        tab2.setIndicator(createTabIndicator("Send Money"));
        tab2.setContent(new Intent(this, SendMoneyActivity.class));




        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.label);
            tv.setTypeface(typeface);


        }

        ((TextView) tabHost.getCurrentTabView().findViewById(R.id.label)).setTextColor(Color.parseColor("#FFFFFF"));



        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                TextView tv = (TextView) tabHost.getCurrentTabView().findViewById(R.id.label);
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    ((TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.label)).setTextColor(getResources().getColor(R.color.black));
                }
                //for Selected Tab
                tv.setTextColor(Color.parseColor("#FFFFFF"));


            }
        });



        //call HamburgerMenu
        new HamburgerMenu(this,(DrawerLayout) findViewById(R.id.drawer_layout),(ExpandableListView) findViewById(R.id.left_drawer),(ImageView) findViewById(R.id.idmenuimg)).setHamburger();

        final ImageView  dotimg = (ImageView) findViewById(R.id.iddotimg);

        dotimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(LoadMoneyActivity.this, dotimg);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //  Toast.makeText(HomeScreenActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(LoadMoneyActivity.this, WebActivity.class);
                        if (item.getTitle().toString().equalsIgnoreCase("FAQs")) {

                            i.putExtra("option", "FAQ");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Privacy Policy")) {

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
                            Intent intent = new Intent(getApplicationContext(), QuickTransferActivity.class);
                            startActivity(intent);
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

                        } else if (item.getTitle().toString().equalsIgnoreCase("My Account")) {
                            Intent intent = new Intent(getApplicationContext(), MyAcount.class);
                            startActivity(intent);
                        }
                        else if (item.getTitle().toString().equalsIgnoreCase("Load Wallet")) {
//                            Intent intent = new Intent(getApplicationContext(), LoadMoneyActivity.class);
//                            startActivity(intent);
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
        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(connectionDetector.isConnectingToInternet()) {
                                                Intent i = new Intent(LoadMoneyActivity.this, FavoriteActivity.class);
                                                startActivity(i);
                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();;
                                            }
                                        }
                                    }
        );

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
//
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != RESULT_OK) return;
//
//        switch (requestCode) {
//
//
//            case PICK_FROM_FILE:
//                mImageCaptureUri = data.getData();
//
//                doCrop();
//
//                break;
//
//            case CROP_FROM_CAMERA:
//                Bundle extras = data.getExtras();
//
//                if (extras != null) {
//                    Bitmap photo = extras.getParcelable("data");
//                    profileImage.setImageBitmap(photo);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//
//                    userInfoEditor.putString("profilepic", "1");
//                    userInfoEditor.commit();
//
//                    Cursor c = dbHelper.fun_select_tbl_profileImage();
//
//                    if(c.moveToNext())
//                    {
//                        dbHelper.updateProfileImage(stream.toByteArray());
//                    }
//                    else {
//                        dbHelper.fun_insert_tbl_profileImage(stream.toByteArray());
//                    }
//
//                    c.close();
//                }
//
//                File f = new File(mImageCaptureUri.getPath());
//
//                if (f.exists()) f.delete();
//
//                break;
//
//            case PICK_FROM_GALLERY:
//
//                mImageCaptureUri = data.getData();
//                File f1 = new File(mImageCaptureUri.getPath());
//                Bitmap bitmap = null;
//                try {
//                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageCaptureUri));
//                    profileImage.setImageBitmap(bitmap);
//                }
//                catch (Exception e)
//                {
//
//                }
//        }
//    }

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

    public void updateWallet(View v)
    {

        if(userInfoPref.getBoolean("new_user_registered_newapp",false)) {

            if (connectionDetector.isConnectingToInternet()) {
                String url = getResources().getString(R.string.getKycStatus) + "?MDN=" + pref.getString("mobile_number", "");

                //// System.out.println(url);

                UpgradeWallet upgradeWallet = new UpgradeWallet(getApplicationContext());
                upgradeWallet.execute(url);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    LoadMoneyActivity.this).create();

            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.display_app_name));

            // Setting Dialog Message
            alertDialog.setMessage("Please register with mRupee to continue.");

            // Setting Icon to Dialog
            // alertDialog.setIcon(R.drawable.tick);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(i);
                }
            });

            alertDialog.setCancelable(false);
            // Showing Alert Message
            alertDialog.show();
        }


    }


    public void openWallet(View v)
    {
        if (connectionDetector.isConnectingToInternet()) {
            GetPurse getPurse = new GetPurse(getApplicationContext());
            getPurse.execute(getResources().getString(R.string.purse_url) + "?MDN=" + pref.getString("mobile_number", ""));
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
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


    private View createTabIndicator(String label) {
        View tabIndicator = getLayoutInflater().inflate(R.layout.tabindicator, null);
        TextView tv = (TextView) tabIndicator.findViewById(R.id.label);
        tv.setText(label);
        return tabIndicator;
    }

    public void generateOTP(View v)
    {
        GenerateOTPAlert generateOTPAlert = new GenerateOTPAlert(this);
        generateOTPAlert.openAlert();

    }

    public void openAlert(View v)
    {
        Intent i = new Intent(LoadMoneyActivity.this, Shopping.class);
        startActivity(i);
    }
    public void openinbox(View v)
    {
        Intent s5 =new Intent(getApplicationContext(),AlertActivity.class);
        startActivity(s5);

    }

    public void openhome(View v)
    {
        Intent i = new Intent(LoadMoneyActivity.this, HomeScreenActivity.class);
        startActivity(i);
    }

    public void openMyAcount(View v)
    {
        Intent i = new Intent(LoadMoneyActivity.this, MyAcount.class);
        startActivity(i);
    }

    @Override
    public void onResume(){
        super.onResume();

        new SetNotificationCounter(getApplicationContext(), (TextView)findViewById(R.id.txtNotificationCounter));

    }

    private class UpgradeWallet extends AsyncTask<String, Void, String> {

        Context context;

        String responseMessage="";
        ProgressDialog pDialog;
        private String kycStatus="",emailid = "";


        public UpgradeWallet(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoadMoneyActivity.this);
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


                Log.i("json::",jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        kycLists = new ArrayList<KycList>();
                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            KycList kycList = new KycList();
                            kycList.setKycStatus(jsonMainObj.getString("kycStatus"));
                            kycList.setEmailID(jsonMainObj.getString("emailID"));
                            kycList.setKycID(jsonMainObj.getString("kycID"));
                            kycList.setDob(jsonMainObj.getString("dob"));
                            kycList.setFirstName(jsonMainObj.getString("firstName"));
                            kycList.setLastName(jsonMainObj.getString("lastName"));
                            kycList.setPincode(jsonMainObj.getString("pincode"));
                            kycList.setMotherMaiden(jsonMainObj.getString("motherMaiden"));
                            kycList.setProfileStatus(jsonMainObj.getString("profileStatus"));
                            kycList.setPoaStatus(jsonMainObj.getString("poaStatus"));
                            kycList.setPoiStatus(jsonMainObj.getString("poiStatus"));
                            kycLists.add(kycList);

                            responseMessage = jsonMainObj.getString("responseMessage");
                            kycStatus = jsonMainObj.getString("kycStatus");
                            emailid = jsonMainObj.getString("emailID");
                            return "Success";

                        }

                        else  if(jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE"))
                        {

                            KycList kycList = new KycList();
                            kycList.setKycStatus(jsonMainObj.getString("kycStatus"));
                            kycList.setEmailID(jsonMainObj.getString("emailID"));
                            kycList.setKycID(jsonMainObj.getString("kycID"));
                            kycList.setDob(jsonMainObj.getString("dob"));
                            kycList.setFirstName(jsonMainObj.getString("firstName"));
                            kycList.setLastName(jsonMainObj.getString("lastName"));
                            kycList.setPincode(jsonMainObj.getString("pincode"));
                            kycList.setMotherMaiden(jsonMainObj.getString("motherMaiden"));
                            kycList.setProfileStatus(jsonMainObj.getString("profileStatus"));
                            kycList.setPoaStatus(jsonMainObj.getString("poaStatus"));
                            kycList.setPoiStatus(jsonMainObj.getString("poiStatus"));
                            kycLists.add(kycList);

                            responseMessage = jsonMainObj.getString("responseMessage");
                            kycStatus = jsonMainObj.getString("kycStatus");
                            emailid = jsonMainObj.getString("emailID");

                            return "Failure";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failed";
                    }
                }
                else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failed";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failed";
            }


            return "Failed";
        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {

                if(kycStatus.trim().equalsIgnoreCase("REJECTED"))
                {
                    AlertBuilder alertBuilder = new AlertBuilder(LoadMoneyActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(responseMessage);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            Intent i = new Intent(getApplicationContext(),KYCActivity.class);
                            i.putExtra("status","REJECTED");
                            i.putExtra("email",emailid);
                            i.putExtra("kyclist",kycLists);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();



                }
                else
                {
                    AlertBuilder alertBuilder = new AlertBuilder(LoadMoneyActivity.this);
                    alertBuilder.showAlert(responseMessage);
                }
            }
            else if(result.trim().equalsIgnoreCase("Failure"))
            {
                Intent i = new Intent(getApplicationContext(),KYCActivity.class);
                i.putExtra("status","");
                i.putExtra("email",emailid);
                i.putExtra("kyclist",kycLists);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();
            }

        }



    }
    private class GetPurse extends AsyncTask<String, Void, String> {

        Context context;

        ProgressDialog pDialog;
        private String responseMessage = "";
        ArrayList<Purse> purseArrayList ;


        public GetPurse(Context context) {
            this.context = context;

            purseArrayList = new ArrayList<Purse>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoadMoneyActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            String jsonStr = "";

            try {

                WebServiceHandler serviceHandler = new WebServiceHandler(getApplication());
                jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                try {
                    if (jsonStr.trim().equalsIgnoreCase(""))
                        return "Failed";
                    else {
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        if (jsonObject.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            responseMessage = jsonObject.getString("responseMessage");

                            JSONArray jsonArray = jsonObject.getJSONArray("purse");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                Purse purse = new Purse();
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                purse.setPurseImage(jsonObject1.getString("purseImage"));
                                purse.setPurseName(jsonObject1.getString("purseName"));
                                purse.setPurseBalance(jsonObject1.getString("purseBalance"));
                                purse.setPurseTextColor(jsonObject1.getString("purseTextColor"));
                                purse.setPurseBackgroundColor(jsonObject1.getString("purseBackgroundColor"));

                                purseArrayList.add(purse);


                            }


                            return "Success";
                        } else {
                            responseMessage = jsonObject.getString("responseMessage");
                            return "Failure";
                        }
                    }
                } catch (JSONException ex) {
                    return "Failed";
                }
            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failed";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {
                Intent i = new Intent(getApplicationContext(), PurseSubBalanceActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("purseArrayList", purseArrayList);
                startActivity(i);
            } else if (result.equalsIgnoreCase("Failure")) {

                AlertBuilder alertBuilder = new AlertBuilder(LoadMoneyActivity.this);
                alertBuilder.showAlert(responseMessage);
            } else {
                AlertBuilder alertBuilder = new AlertBuilder(LoadMoneyActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));
            }

        }

    }

}
