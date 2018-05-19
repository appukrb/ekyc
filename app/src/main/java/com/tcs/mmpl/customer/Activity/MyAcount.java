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
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.Adapter.CropOptionAdapter;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.BannerLayout;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.CropOption;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.Purse;
import com.tcs.mmpl.customer.utility.SetNotificationCounter;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.tcs.mmpl.customer.R.layout.popup_generate_otp;


public class MyAcount extends Activity {

    LinearLayout viewmyprofile,linCheckBalance;
    LinearLayout loadmywallet;
    TextView txt_header_1,txt_header_2,txt_header_3,txt_header_4,txt_header_5,txt_header_6,txt_header_7;
    LinearLayout mainlinear;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    Button btn_load_money,btn_invite_friend;
    ConnectionDetector connectionDetector;
    ImageView profileImage;
    MyDBHelper dbHelper;
    private Uri mImageCaptureUri;
    private static int RESULT_LOAD_IMG = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_GALLERY = 4;

    LinearLayout linWelcome, linDetails,linView,linGenerateOTP;
    TextView txtWelcome,txtBalance;
    private ArrayList<String> list1 = new ArrayList<String>();

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    ImageView dotimg;
    ProgressDialog pDialog;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;


//    ImageView imgBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_acount);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        mainlinear = (LinearLayout)findViewById(R.id.mainlinear);
        typeface=Typeface.createFromAsset(getApplicationContext().getAssets(),"helvetica.otf");
        fontclass.setFont(mainlinear,typeface );

        dbHelper = new MyDBHelper(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        linWelcome = (LinearLayout) findViewById(R.id.linWelcome);
        linDetails = (LinearLayout) findViewById(R.id.linDetails);
        linGenerateOTP=(LinearLayout) findViewById(R.id.linGenerateOTP);

        txtWelcome = (TextView)findViewById(R.id.txtWelcome);
        txtBalance = (TextView)findViewById(R.id.txtBalance);
        profileImage = (ImageView)findViewById(R.id.imgUser);


        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
            txtBalance.setText(userInfoPref.getString("walletbalance", ""));

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


//        imgBanner = (ImageView)findViewById(R.id.imgBanner);
        BannerLayout bannerLayout = (BannerLayout) findViewById(R.id.imgBanner);

        String  banner = pref.getString("banner", "");
//        if(!banner.trim().isEmpty()) {
//            byte[] byteArray = Base64.decode(banner, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
//                    byteArray.length);
//            imgBanner.setImageBitmap(bitmap);
//            imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
//        }
        MyConnectionHelper db;

        db = new MyConnectionHelper(getApplicationContext());

        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();

        if(c1.moveToNext())
        {
            do
            {
//              List<bannersItem> list1 = new ArrayList<bannersItem>();
                //// System.out.println("********************************************************"+c1.getString(3)+" " +c1.getString(1));
                if(c1.getString(3).equalsIgnoreCase("ACCOUNT")) {
                    list1.add(c1.getString(1));
                }
            }while(c1.moveToNext());

            bannerLayout.setViewUrls(list1);
        }

        c1.close();


        bannerLayout.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String bannerURL = getResources().getString(R.string.bannerlink) + "?Type=ACCOUNT";
                GetBannerLink getBannerLink = new GetBannerLink(getApplicationContext());
                getBannerLink.execute(bannerURL);
            }
        });

//        imgBanner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                Intent re =new Intent(getApplicationContext(),Electricity_Payment.class);
//                re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(re);
//            }
//        });



//        if(userInfoPref.getBoolean("new_user_registered_newapp",false)) {
//
//            txtWelcome.setText("Welcome " + userInfoPref.getString("firstname", "") + " " + userInfoPref.getString("lastname", ""));
//            txtBalance.setText("Rs." + userInfoPref.getString("walletbalance", ""));
//        }


        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(MyAcount.this, FavoriteActivity.class);
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
                PopupMenu popup = new PopupMenu(MyAcount.this, dotimg);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //  Toast.makeText(HomeScreenActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(MyAcount.this, WebActivity.class);
                        if (item.getTitle().toString().equalsIgnoreCase("FAQs")) {

                            i.putExtra("option", "FAQ");
                            startActivity(i);

                        }
                        else if (item.getTitle().toString().equalsIgnoreCase("My Account")) {

//                            Intent intent = new Intent(getApplicationContext(), MyAcount.class);
//                            startActivity(intent);

                        }
                        else if (item.getTitle().toString().equalsIgnoreCase("Offers")) {

                            Intent intent = new Intent(getApplicationContext(), OffersActivity.class);
                            intent.putExtra("url",getResources().getString(R.string.offers));
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
                        }   else if (item.getTitle().toString().equalsIgnoreCase("Quick Transfer")) {
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
                            //Intent intent = new Intent(getApplicationContext(), MyAcount.class);
                           // startActivity(intent);
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


        /*txt_header_1 = (TextView) findViewById(R.id.txt_header_1);
        txt_header_2 = (TextView) findViewById(R.id.txt_header_2);
        txt_header_3 = (TextView) findViewById(R.id.txt_header_3);
        txt_header_4 = (TextView) findViewById(R.id.txt_header_4);
        txt_header_5 = (TextView) findViewById(R.id.txt_header_5);
        txt_header_6 = (TextView) findViewById(R.id.txt_header_6);
        txt_header_7 = (TextView) findViewById(R.id.txt_header_7);

        btn_load_money = (Button) findViewById(R.id.btn_load_money);
        btn_invite_friend = (Button) findViewById(R.id.btn_invite_friend);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "helvetica.otf");

        txt_header_1.setTypeface(custom_font);
        txt_header_2.setTypeface(custom_font);
        txt_header_3.setTypeface(custom_font);
        txt_header_4.setTypeface(custom_font);
        txt_header_5.setTypeface(custom_font);
        txt_header_6.setTypeface(custom_font);
        txt_header_7.setTypeface(custom_font);

        btn_load_money.setTypeface(custom_font);
        btn_invite_friend.setTypeface(custom_font);*/

        linCheckBalance =(LinearLayout)findViewById(R.id.linCheckBalance);
        linCheckBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

                    String url = getResources().getString(R.string.loadwalletStatement)+ "?MDN="+pref.getString("mobile_number", "");
                    WalletStatementAsync w =new WalletStatementAsync(getApplicationContext());
                    w.execute(url);
                }
                else
                {
                    AlertBuilder alert = new AlertBuilder(MyAcount.this);
                    alert.newUser();

                }
            }
        });

        viewmyprofile =  (LinearLayout) findViewById(R.id.idview_my_profile);
        viewmyprofile.setOnClickListener(new View.OnClickListener() {
                                        @Override public void onClick(View v) {

                                            if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                                                Intent i = new Intent(MyAcount.this, View_My_Profile.class);
                                                startActivity(i);
                                            }
                                            else
                                            {
                                                AlertBuilder alert = new AlertBuilder(MyAcount.this);
                                                alert.newUser();
                                            }
                                        }
                                    }
        );


        loadmywallet =  (LinearLayout) findViewById(R.id.idloadmywallet);
        loadmywallet.setOnClickListener(new View.OnClickListener() {
                                             @Override public void onClick(View v) {
                                                 Intent i= new Intent(MyAcount.this,LoadMoneyActivity.class);
                    i.putExtra("index", "0");
                    startActivity(i);
                                             }
                                         }
        );

        linView= (LinearLayout)findViewById(R.id.linView);
        linView.setOnClickListener(new View.OnClickListener() {
                                            @Override public void onClick(View v) {
                                                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                                                    Intent i = new Intent(MyAcount.this, CheckTransactionsActivity.class);
                                                    startActivity(i);
                                                }
                                                else
                                                {
                                                    AlertBuilder alert = new AlertBuilder(MyAcount.this);
                                                    alert.newUser();
                                                }
                                            }
                                        }
        );


        linGenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater
                        .inflate(popup_generate_otp, null);
                final PopupWindow popupWindow = new PopupWindow(popupView,
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                final EditText edtMpin = (EditText) popupView
                        .findViewById(R.id.edittext_edit_popup);


                Button btnCancel = (Button)popupView.findViewById(R.id.button_pop_no);

                Button btnSubmit = (Button) popupView
                        .findViewById(R.id.button_pop_yes);

                edtMpin.setTypeface(typeface);
                btnCancel.setTypeface(typeface);
                btnSubmit.setTypeface(typeface);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                btnSubmit.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.mpin),Toast.LENGTH_LONG).show();
                        } else {
                            popupWindow.dismiss();
                            String generateOTPURL = getApplicationContext().getResources().getString(R.string.generateOTP) + "?MDN=" + pref.getString("mobile_number", "") +"&MPIN=" + edtMpin.getText().toString().trim();
                            GenerateOTP generateOTP = new GenerateOTP(getApplicationContext());
                            generateOTP.execute(generateOTPURL);
                        }

                    }
                });

                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
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

    public void generateOTP(View v)
    {
        GenerateOTPAlert generateOTPAlert = new GenerateOTPAlert(this);
        generateOTPAlert.openAlert();

    }
    public void openAlert(View v)
    {
        Intent i = new Intent(MyAcount.this, Shopping.class);
        startActivity(i);
    }
    public void openhome(View v)
    {
        Intent i = new Intent(MyAcount.this, HomeScreenActivity.class);
        startActivity(i);
    }

    public void openinbox(View v)
    {
        Intent s5 =new Intent(getApplicationContext(),AlertActivity.class);
        startActivity(s5);

    }
    public void openMyAcount(View v)
    {
        Intent i = new Intent(MyAcount.this, MyAcount.class);
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

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);

        try {

            startActivityForResult(intent, PICK_FROM_FILE);
            // startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Activity not found",Toast.LENGTH_LONG).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {


            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();

                doCrop();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    profileImage.setImageBitmap(photo);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    userInfoEditor.putString("profilepic", "1");
                    userInfoEditor.commit();

                    Cursor c = dbHelper.fun_select_tbl_profileImage();

                    if(c.moveToNext())
                    {
                        dbHelper.updateProfileImage(stream.toByteArray());
                    }
                    else {
                        dbHelper.fun_insert_tbl_profileImage(stream.toByteArray());
                    }

                    c.close();
                }

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists()) f.delete();

                break;

            case PICK_FROM_GALLERY:

                mImageCaptureUri = data.getData();
                File f1 = new File(mImageCaptureUri.getPath());
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageCaptureUri));
                    profileImage.setImageBitmap(bitmap);
                }
                catch (Exception e)
                {

                }
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
    public void changeMpin(View v)
    {
        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
            Intent i = new Intent(getApplicationContext(), ChangeMPINActivity.class);
            i.putExtra("status", "0");
            startActivity(i);
        }
        else
        {
            AlertBuilder alert = new AlertBuilder(MyAcount.this);
            alert.newUser();
        }
    }

    private class GetUserStatus extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        public GetUserStatus(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MyAcount.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("MDN", pref.getString("mobile_number", ""));
//                jsonObject.accumulate("IMEI", "123456");


                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(MyAcount.this, se);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            firstName = jsonMainObj.getString("firstName");
                            lastName = jsonMainObj.getString("lastName");
                            walletBalance = jsonMainObj.getString("walletBalance");

                            userInfoEditor.putString("firstname", firstName);
                            userInfoEditor.putString("lastname", lastName);
                            userInfoEditor.putString("walletbalance", walletBalance);
                            userInfoEditor.putString("usertype", jsonMainObj.getString("userType"));
                            userInfoEditor.putBoolean("new_user_registered_newapp", true);
                            userInfoEditor.putString("emailId", jsonMainObj.getString("emailId"));
                            userInfoEditor.putString("account", jsonMainObj.getString("account"));
                            userInfoEditor.putString("kycstatus",jsonMainObj.getString("kycstatus"));
                            userInfoEditor.putString("billdeskFlag", jsonMainObj.getString("billdeskFlag"));
                            userInfoEditor.putString("userID", jsonMainObj.getString("userID"));
                            userInfoEditor.commit();


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

                Intent i = new Intent(MyAcount.this, CheckBalanceActivity.class);
                i.putExtra("balance",userInfoPref.getString("walletbalance","0.0"));
                startActivity(i);

            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }

        }

    }
    private class WalletStatementAsync extends AsyncTask<String, Void, String> {

        Context context;
        int flag = 0;

        private ArrayList<String> amount,transid,date;


        public WalletStatementAsync(Context context) {
            this.context = context;

            amount = new ArrayList<String>();
            transid = new ArrayList<String>();
            date =new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MyAcount.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);




                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {


                        JSONObject jsonMainObj = new JSONObject(jsonStr);



                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success")) {

                            JSONArray jsonArray = jsonMainObj.getJSONArray("data");

                            if (jsonArray.length() > 0) {


                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject j1 = jsonArray.getJSONObject(i);
                                    amount.add(j1.getString("amount"));
                                    date.add(j1.getString("date"));
                                    transid.add(j1.getString("transcationID"));
                                }


                            }

                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {

                            return jsonMainObj.getString("responseMessage");


                        } else {

                            return "Failure";
                        }

                    } catch (JSONException e) {
                        editor.putInt("flag", 0);
                        editor.commit();
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

                Intent intent = new Intent(getApplicationContext(), WalletStatement.class);
                intent.putExtra("amount", amount);
                intent.putExtra("transid", transid);
                intent.putExtra("date", date);
                startActivity(intent);


            } else if (result.equalsIgnoreCase("Failure")) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();

            } else {
                AlertBuilder alert =new AlertBuilder(MyAcount.this);
                alert.showAlert(result);

            }

        }

    }

    private class GenerateOTP extends AsyncTask<String, Void, String> {

        Context context;




        public GenerateOTP(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MyAcount.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(MyAcount.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

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

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

                AlertBuilder alert = new AlertBuilder(MyAcount.this);
                alert.showAlert(result);

            }

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

            pDialog = new ProgressDialog(MyAcount.this);
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

                AlertBuilder alertBuilder = new AlertBuilder(MyAcount.this);
                alertBuilder.showAlert(responseMessage);
            } else {
                AlertBuilder alertBuilder = new AlertBuilder(MyAcount.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));
            }

        }

    }
}
