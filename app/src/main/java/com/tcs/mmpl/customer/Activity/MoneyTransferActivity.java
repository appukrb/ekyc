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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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
import com.tcs.mmpl.customer.Adapter.CustomViewPagerAdapterLoad_Money;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.fragments.Manage_Beneficiary;
import com.tcs.mmpl.customer.fragments.TransferToWallet;
import com.tcs.mmpl.customer.utility.CropOption;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.PagerSlidingTabStrip;
import com.tcs.mmpl.customer.utility.SetNotificationCounter;

import java.util.ArrayList;
import java.util.List;


public class MoneyTransferActivity extends FragmentActivity {

    private Uri mImageCaptureUri;
    private static int RESULT_LOAD_IMG = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_GALLERY = 4;

    LinearLayout mainlinear;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    ViewPager viewPager;
    CustomViewPagerAdapterLoad_Money customViewPagerAdapter;
    Context context;
    String[] operationTypeList;
    ArrayList<String> opArrayList;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;

    private ArrayList<String> list1 = new ArrayList<String>();
//    private final Handler handler = new Handler();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter1 adapter;

    int index,flag;

    LinearLayout linWelcome, linDetails;
    TextView txtWelcome, txtBalance;
    MyConnectionHelper db;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    ProgressDialog pDialog;
    ImageView dotimg;
    MyDBHelper dbHelper;
    ImageView profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transfer);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainlinear = (LinearLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);


        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        linWelcome = (LinearLayout) findViewById(R.id.linWelcome);
        linDetails = (LinearLayout) findViewById(R.id.linDetails);

        txtWelcome = (TextView) findViewById(R.id.txtWelcome);
        txtBalance = (TextView) findViewById(R.id.txtBalance);
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
        ImageView imgBanner =(ImageView)findViewById(R.id.imgBanner);
        db = new MyConnectionHelper(getApplicationContext());
        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();
        if(c1.moveToNext())
        {
            do
            {
                //// System.out.println("********************************************************"+c1.getString(3)+" " +c1.getString(1));
                if(c1.getString(3).equalsIgnoreCase("MONEY"))
                {
                    Glide.with(MoneyTransferActivity.this).load(c1.getString(1)).placeholder(R.drawable.default_banner).into(imgBanner);
                    imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
            }while(c1.moveToNext());
        }

        c1.close();


        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent re = new Intent(getApplicationContext(), LoadMoneyActivity.class);
                re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(re);
            }
        });

//        BannerLayout bannerLayout = (BannerLayout) findViewById(R.id.imgBanner);
//        db= new MyConnectionHelper(getApplicationContext());
//
//
//        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();
//        if(c1.moveToNext())
//        {
//            do
//            {
////              List<bannersItem> list1 = new ArrayList<bannersItem>();
//                //// System.out.println("********************************************************"+c1.getString(3)+" " +c1.getString(1));
//                if(c1.getString(3).equalsIgnoreCase("MONEY"))
//                {
//                    list1.add(c1.getString(1));
//                }
//            }while(c1.moveToNext());
//
//            bannerLayout.setViewUrls(list1);
//        }
//
//        c1.close();
//
//        bannerLayout.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                finish();
//                Intent re =new Intent(getApplicationContext(),LoadMoneyActivity.class);
//                re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(re);
//            }
//        });
//        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
//
//            txtWelcome.setText("Welcome " + userInfoPref.getString("firstname", "") + " " + userInfoPref.getString("lastname", ""));
//            txtBalance.setText("Rs." + userInfoPref.getString("walletbalance", ""));
//        }

        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(MoneyTransferActivity.this, FavoriteActivity.class);
                                            startActivity(i);
                                        }
                                    }
        );

        //call HamburgerMenu
        new HamburgerMenu(this,(DrawerLayout) findViewById(R.id.drawer_layout),(ExpandableListView) findViewById(R.id.left_drawer),(ImageView) findViewById(R.id.idmenuimg)).setHamburger();

        dotimg = (ImageView) findViewById(R.id.iddotimg);

        dotimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MoneyTransferActivity.this, dotimg);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //  Toast.makeText(HomeScreenActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(MoneyTransferActivity.this, WebActivity.class);
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
                            //Intent intent = new Intent(getApplicationContext(), MoneyTransferActivity.class);
                            //intent.putExtra("index","0");
                           // startActivity(intent);
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

    /*  EditText ed=(EditText)findViewById(R.id.editText1);
        ed.setText("mPIN:");
      ed.setSelection(ed.getText().length());*/
        /*operationTypeList=getResources().getStringArray(R.array.operationTypeList);
        opArrayList=new ArrayList<String>(Arrays.asList(operationTypeList));*/

/*
        viewPager = (ViewPager) findViewById(R.id.pager);
        customViewPagerAdapter = new CustomViewPagerAdapterLoad_Money(getSupportFragmentManager(),this);
        viewPager.setAdapter(customViewPagerAdapter);*/


        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs1);
       // tabs.setTextColor(Color.parseColor("#1268b1"));
        tabs.setTextColorResource(R.color.black);
        Typeface custom_font = Typeface.createFromAsset(getApplication().getAssets(), "helvetica-bold.ttf");
        tabs.setTypeface(custom_font, Typeface.NORMAL);
        pager = (ViewPager) findViewById(R.id.pager1);
        adapter = new MyPagerAdapter1(getSupportFragmentManager());
        index = Integer.parseInt(getIntent().getStringExtra("index"));
        if(index == 2)
        {
            flag = Integer.parseInt(getIntent().getStringExtra("flag"));
        }

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(Integer.parseInt(getIntent().getStringExtra("index")));
        tabs.setViewPager(pager);

    }

//    @Override
//    public void onBackPressed() {
//
//        finish();
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
        Intent i = new Intent(MoneyTransferActivity.this, Shopping.class);
        startActivity(i);
    }
    public void openhome(View v)
    {
        Intent i = new Intent(MoneyTransferActivity.this, HomeScreenActivity.class);
        startActivity(i);
    }
    public void openinbox(View v)
    {
        Intent s5 =new Intent(getApplicationContext(),AlertActivity.class);
        startActivity(s5);

    }

    public void openMyAcount(View v)
    {
        Intent i = new Intent(MoneyTransferActivity.this, MyAcount.class);
        startActivity(i);
    }
    @Override
    public void onResume(){
        super.onResume();

        new SetNotificationCounter(getApplicationContext(), (TextView)findViewById(R.id.txtNotificationCounter));

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
//            Toast.makeText(getApplicationContext(), "Activity not found", Toast.LENGTH_LONG).show();
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

    public class MyPagerAdapter1 extends FragmentPagerAdapter {

       /* private final String[] TITLES = { "Categories", "Home", "Top Paid", "Top Free", "Top Grossing", "Top New Paid",
                "Top New Free", "Trending" };*/

        public String tabtitles_loadMoney[];

        private int tabxml_loadMoney[] = {R.layout.fragment_tab_recharge_pay, R.layout.fragment_tab_money_transfer, R.layout.fragment_tab_offers, R.layout.fragment_tab_quick_transfer, R.layout.fragment_tab_transfer_to_bank, R.layout.fragment_tab_request_money, R.layout.fragment_tab_check_transaction, R.layout.fragment_tab_manage_beneficiary, R.layout.fragment_tab_transfer_to_wallet};


        public MyPagerAdapter1(FragmentManager fm) {
            super(fm);
            tabtitles_loadMoney = getResources().getStringArray(
                    R.array.moneyTransferList);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabtitles_loadMoney[position];
            // return TITLES[position];
        }

        @Override
        public int getCount() {
            return tabtitles_loadMoney.length;
            // return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {

            // return SuperAwesomeCardFragment.newInstance(position);

            switch (position) {

                case 0:
                    // Top Rated fragment activity
                    return new TransferToBank();

                case 1:
                    // Top Rated fragment activity
                    return new TransferToWallet();
                case 2:
                    // Games fragment activity
                    if(index == 2 && flag == 1)
                    return new Manage_Beneficiary().newInstance(1);
                    else
                        return new Manage_Beneficiary().newInstance(0);




            }

            return null;

        }

    }


}
