package com.tcs.mmpl.customer.Activity;

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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.Adapter.CropOptionAdapter;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.Adapter.SendNotficationClicked;
import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.fragments.DTH;
import com.tcs.mmpl.customer.fragments.Datacard;
import com.tcs.mmpl.customer.fragments.ElectriCity;
import com.tcs.mmpl.customer.fragments.Gas;
import com.tcs.mmpl.customer.fragments.Landline;
import com.tcs.mmpl.customer.fragments.Mobile;
import com.tcs.mmpl.customer.utility.CropOption;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.PagerSlidingTabStrip;
import com.tcs.mmpl.customer.utility.SetNotificationCounter;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Electricity_Payment extends FragmentActivity {
  //  ViewPager viewPager;
  //  CustomViewPagerAdapter customViewPagerAdapter;
    Context context;
    LinearLayout mainlinear;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    MyConnectionHelper db;

    private Uri mImageCaptureUri;
    private static int RESULT_LOAD_IMG = 1;
    private static final int CROP_FROM_CAMERA = 8;
    private static final int PICK_FROM_FILE = 9;
    private static final int PICK_FROM_GALLERY = 10;

    ImageView profileImage;
    private final Handler handler = new Handler();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    TextView txtWelcome,txtBalance;
    ImageView dotimg;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;

    MyDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_electricity__payment);
        mainlinear = (LinearLayout)findViewById(R.id.mainlinear);
        typeface=Typeface.createFromAsset(getApplicationContext().getAssets(),"helvetica.otf");
        fontclass.setFont(mainlinear,typeface );
        db = new MyConnectionHelper(getApplicationContext());
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
       // tabs.setTextColor(Color.parseColor("#1268b1"));
        tabs.setTextColorResource(R.color.black);
        ImageView imgBanner =(ImageView)findViewById(R.id.imgBanner);

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        profileImage = (ImageView)findViewById(R.id.imgUser);
//        MyDBHelper dbHelper;
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

        if(userInfoPref.getString("notify","false").trim().equalsIgnoreCase("true"))
        {
            String url = getResources().getString(R.string.saveNotification)+"?MSG="+userInfoPref.getString("msg","|");
            SendNotficationClicked sendNotficationClicked = new SendNotficationClicked(Electricity_Payment.this, userInfoPref.getString("msg","|"));
            sendNotficationClicked.execute(url);
            userInfoEditor.putString("notify","false");
            userInfoEditor.commit();

        }

        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();

        if(c1.moveToNext())
        {

            do
            {
                //// System.out.println("********************************************************"+c1.getString(3)+" " +c1.getString(1));
                if(c1.getString(3).equalsIgnoreCase("BILL"))
                {
                    Glide.with(Electricity_Payment.this).load(c1.getString(1)).placeholder(R.drawable.default_banner).into(imgBanner);
                    imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
            }while(c1.moveToNext());
        }

        c1.close();


        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bannerURL = getResources().getString(R.string.bannerlink)+"?Type=BILL";
                GetBannerLink getBannerLink = new GetBannerLink(getApplicationContext());
                getBannerLink.execute(bannerURL);
            }
        });
//   new AppRater(HomeScreenActivity.this).demo();

        //App rating code
//        AppRater appRater = new AppRater(Electricity_Payment.this);
//        appRater.setDaysBeforePrompt(0);
//        appRater.setLaunchesBeforePrompt(0);
//        appRater.setPhrases(R.string.rate_title,
//                R.string.rate_explanation,
//                R.string.rate_now,
//                R.string.rate_later,
//                R.string.rate_never);
//        appRater.show();

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        txtWelcome = (TextView)findViewById(R.id.txtWelcome);
        txtBalance = (TextView)findViewById(R.id.txtBalance);



        Typeface custom_font = Typeface.createFromAsset(getApplication().getAssets(), "helvetica-bold.ttf");

        tabs.setTypeface(custom_font,Typeface.NORMAL);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(Electricity_Payment.this, FavoriteActivity.class);
                                            startActivity(i);
                                        }
                                    }
        );
//        ImageView home = (ImageView) findViewById(R.id.home);
//        home.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent i = new Intent(Electricity_Payment.this, HomeScreenActivity.class);
//                                        startActivity(i);
//                                    }
//                                }
//        );

        //call HamburgerMenu
        new HamburgerMenu(this,(DrawerLayout) findViewById(R.id.drawer_layout),(ExpandableListView) findViewById(R.id.left_drawer),(ImageView) findViewById(R.id.idmenuimg)).setHamburger();



        dotimg = (ImageView) findViewById(R.id.iddotimg);
        dotimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Electricity_Payment.this, dotimg);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //  Toast.makeText(HomeScreenActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(Electricity_Payment.this, WebActivity.class);
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
//                            Intent intent = new Intent(getApplicationContext(), Electricity_Payment.class);
//                            startActivity(intent);
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
        //TextView textView_header1 = (TextView) findViewById(R.id.label);

       /* viewPager = (ViewPager) findViewById(R.id.pager);
        customViewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(customViewPagerAdapter);*/

       // PagerSlidingTabStrip mPagerTabStrip = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        for (int i = 0; i < tabs.getChildCount(); ++i)
        {
            View nextChild = tabs.getChildAt(i);
            if (nextChild instanceof TextView)
            {
                TextView textViewToConvert = (TextView) nextChild;
                //  textViewToConvert.setTextColor(getResources().getColor(R.color.primary_text_color_dark_gray));

/*
                Typeface custom_font = Typeface.createFromAsset(getApplication().getAssets(), "helvetica-bold.ttf");
                textViewToConvert.setTypeface(custom_font);
*/

               /* mPagerTabStrip.setTabIndicatorColorResource(R.color.orange);
                mPagerTabStrip.setDrawFullUnderline(false);*/
            }
        }



        /*PagerTabStrip mPagerTabStrip = (PagerTabStrip) findViewById(R.id.pageTabStrip);
        for (int i = 0; i < mPagerTabStrip.getChildCount(); ++i)
        {
            View nextChild = mPagerTabStrip.getChildAt(i);
            if (nextChild instanceof TextView)
            {
                TextView textViewToConvert = (TextView) nextChild;
                //  textViewToConvert.setTextColor(getResources().getColor(R.color.primary_text_color_dark_gray));

                Typeface custom_font = Typeface.createFromAsset(getApplication().getAssets(), "helvetica-bold.ttf");
                textViewToConvert.setTypeface(custom_font);

                mPagerTabStrip.setTabIndicatorColorResource(R.color.orange);
                mPagerTabStrip.setDrawFullUnderline(false);
            }
        }*/

//        if(userInfoPref.getBoolean("new_user_registered_newapp",false)) {
//
////            txtWelcome.setText("Welcome " + userInfoPref.getString("firstname", "") + " " + userInfoPref.getString("lastname", ""));
////            txtBalance.setText("Rs." + userInfoPref.getString("walletbalance", ""));
//        }

    }

//    @Override
//    public void onBackPressed() {
//
//      finish();
//        Intent i = new Intent(getApplicationContext(),HomeScreenActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);
//
//    }



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

    public void generateOTP(View v)
    {
        GenerateOTPAlert generateOTPAlert = new GenerateOTPAlert(this);
        generateOTPAlert.openAlert();

    }

    public void openAlert(View v)
    {
        Intent i = new Intent(Electricity_Payment.this, Shopping.class);
        startActivity(i);
    }

    public void openinbox(View v)
    {
        Intent s5 =new Intent(getApplicationContext(),AlertActivity.class);
        startActivity(s5);

    }
    public void openhome(View v)
    {
        Intent i = new Intent(Electricity_Payment.this, HomeScreenActivity.class);

        startActivity(i);
    }

    public void openMyAcount(View v)
    {
        Intent i = new Intent(Electricity_Payment.this, MyAcount.class);
        startActivity(i);
    }
    @Override
    public void onResume(){
        super.onResume();

        new SetNotificationCounter(getApplicationContext(), (TextView)findViewById(R.id.txtNotificationCounter));

    }
    public class MyPagerAdapter extends FragmentPagerAdapter
    {

       /* private final String[] TITLES = { "Categories", "Home", "Top Paid", "Top Free", "Top Grossing", "Top New Paid",
                "Top New Free", "Trending" };*/

        public String tabtitles[];

        private int tabxml[] = { R.layout.fragment_tab_mobile, R.layout.fragment_tab_dth ,R.layout.fragment_tab_landline,
                R.layout.fragment_tab_datacard, R.layout.fragment_tab,
                R.layout.fragment_tab_gas};




        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabtitles = getResources().getStringArray(
                    R.array.operationTypeList);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return tabtitles[position];
           // return TITLES[position];
        }

        @Override
        public int getCount()
        {
            return tabtitles.length;
           // return TITLES.length;
        }

        @Override
        public Fragment getItem(int position)
        {

           // return SuperAwesomeCardFragment.newInstance(position);

            switch (position)
            {

                case 0:
                    // Top Rated fragment activity
                    return new Mobile();
                case 1:
                    // Games fragment activity
                    return new DTH();

                case 2:
                    // Top Rated fragment activity
                    return new Landline();
                case 3:
                    // Games fragment activity
                    return new Datacard();
                case 4:
                    // Movies fragment activity
                    return new ElectriCity();

                case 5:
                    // Movies fragment activity
                    return new Gas();

//                case 6:
//                    // Movies fragment activity
//                    return new MoneyTransfer();
//
//                case 7:
//                    // Movies fragment activity
//                    return new Offers();
//                case 8:
//                    // Movies fragment activity
//                    return new RequestMoney();

            }

            return null;

        }

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
//            // Showing progress dialog
//            pDialog = new ProgressDialog(Electricity_Payment.this);
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler serviceHandler = new WebServiceHandler(getApplication());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println(jsonStr);
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

//
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
