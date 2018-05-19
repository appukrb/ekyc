package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.Adapter.CropOptionAdapter;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.fragments.QuickTransfer;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.Contact;
import com.tcs.mmpl.customer.utility.CropOption;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.SetNotificationCounter;

import java.util.ArrayList;
import java.util.List;


public class MerchantPaymentNew extends Activity {
    FontClass fontclass = new FontClass();
    Typeface typeface;

    MyDBHelper dbHelper;
    private Uri mImageCaptureUri;
    private static int RESULT_LOAD_IMG = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_GALLERY = 4;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;
    ImageView imgBanner;
    MyConnectionHelper db;

    private EditText edtMerchantMobileNumber;
    private TextView txtMerchantName;
    private RadioButton radioMerchant,radioMerchantMobile;

    private static final String TAG = QuickTransfer.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final int REQUEST_CODE_MERCHANT_LIST = 5;
    private Uri uriContact;
    ImageView profileImage;

    private SharedPreferences pref,userInfoPref;
    private SharedPreferences.Editor editor,userInfoEditor;
    private ConnectionDetector connectionDetector;

    private String merchantNumber="",merchantName="",Isd_code;
    LinearLayout linParent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_merchant_payment_new);

        linParent = (LinearLayout) findViewById(R.id.linParent);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        db = new MyConnectionHelper(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        edtMerchantMobileNumber = (EditText)findViewById(R.id.edtMerchantMobileNumber);
        txtMerchantName = (TextView)findViewById(R.id.txtMerchantName);

        radioMerchant = (RadioButton)findViewById(R.id.radioMerchant);
        radioMerchantMobile = (RadioButton)findViewById(R.id.radioMerchantMobile);

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
        imgBanner =(ImageView)findViewById(R.id.imgBanner);

        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();

        if(c1.moveToNext())
        {

            do
            {
                //// System.out.println("********************************************************"+c1.getString(3)+" " +c1.getString(1));
                if(c1.getString(3).equalsIgnoreCase("MERCH"))
                {
                    Glide.with(MerchantPaymentNew.this).load(c1.getString(1)).placeholder(R.drawable.default_banner).into(imgBanner);
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

                if(isChecked)
                    radioMerchantMobile.setChecked(false);
            }
        });

        radioMerchantMobile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txtMerchantName.setEnabled(true);
                edtMerchantMobileNumber.setEnabled(false);

                if(isChecked)
                radioMerchant.setChecked(false);
            }
        });

        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(MerchantPaymentNew.this, FavoriteActivity.class);
                                            startActivity(i);
                                        }
                                    }
        );


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView idmenuimg = (ImageView)findViewById(R.id.idmenuimg);
        idmenuimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);

                }
                else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                //// System.out.println("mDwawer");

            }
        });

//        mDrawerList = (ListView) findViewById(R.id.left_drawer);
//
//        List<ObjectDrawerItem> list = new ArrayList<ObjectDrawerItem>();
//        ObjectDrawerItem objectDrawerItem1 = new ObjectDrawerItem(getResources().getString(R.string.home));
//        ObjectDrawerItem objectDrawerItem2= new ObjectDrawerItem(getResources().getString(R.string.load_money));
//        ObjectDrawerItem objectDrawerItem3 = new ObjectDrawerItem(getResources().getString(R.string.recharge));
//        ObjectDrawerItem objectDrawerItem4 = new ObjectDrawerItem(getResources().getString(R.string.check_transaction));
//        ObjectDrawerItem objectDrawerItem5 = new ObjectDrawerItem(getResources().getString(R.string.shopping));
//        ObjectDrawerItem objectDrawerItem6 = new ObjectDrawerItem(getResources().getString(R.string.money_transfer));
//        ObjectDrawerItem objectDrawerItem11 = new ObjectDrawerItem(getResources().getString(R.string.quick_transfer));
//        ObjectDrawerItem objectDrawerItem7 = new ObjectDrawerItem(getResources().getString(R.string.coupons));
//        ObjectDrawerItem objectDrawerItem8 = new ObjectDrawerItem(getResources().getString(R.string.steal));
//        ObjectDrawerItem objectDrawerItem9 = new ObjectDrawerItem(getResources().getString(R.string.inbox));
//        ObjectDrawerItem objectDrawerItem10 = new ObjectDrawerItem(getResources().getString(R.string.help));
//
//        list.add(objectDrawerItem1);
//        list.add(objectDrawerItem2);
//        list.add(objectDrawerItem3);
//        list.add(objectDrawerItem4);
//        list.add(objectDrawerItem5);
//        list.add(objectDrawerItem6);
//        list.add(objectDrawerItem11);
//        list.add(objectDrawerItem7);
//        list.add(objectDrawerItem8);
//        list.add(objectDrawerItem9);
//        list.add(objectDrawerItem10);
//
//
//
//        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(getApplicationContext(),list);
//
//        mDrawerList.setAdapter(adapter);
//        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                int itemPosition = position;
//
//                switch (position) {
//                    case 0:
//                        Intent h = new Intent(getApplicationContext(), HomeScreenActivity.class);
//                        startActivity(h);
//                        break;
//                    case 1:
//                        Intent i = new Intent(getApplicationContext(), LoadMoneyActivity.class);
//                        startActivity(i);
//                        break;
//                    case 2:
//                        Intent re = new Intent(getApplicationContext(), Electricity_Payment.class);
//                        startActivity(re);
//                        break;
//                    case 3:
//                        Intent v = new Intent(getApplicationContext(), CheckTransactionsActivity.class);
//                        startActivity(v);
//                        break;
//                    case 4:
//                        Intent s = new Intent(getApplicationContext(), OffersActivity.class);
//                        startActivity(s);
//                        break;
//                    case 5:
//                        Intent m = new Intent(getApplicationContext(), MoneyTransferActivity.class);
//                        m.putExtra("index", "0");
//                        startActivity(m);
//                        break;
//                    case 6:
//                        Intent s2 =new Intent(getApplicationContext(),QuickTransferActivity.class);
//                        startActivity(s2);
//                        break;
//                    case 7:
//                        Intent s3 =new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.deals)));
//                        startActivity(s3);
//                        break;
//                    case 8:
//                        Intent s4 = new Intent(getApplicationContext(), OffersActivity.class);
//                        startActivity(s4);
//                        break;
//                    case 9:
//                        Intent s5 = new Intent(getApplicationContext(), AlertActivity.class);
//                        startActivity(s5);
//                        break;
//                    case 10:
//                        Intent s6 = new Intent(getApplicationContext(), Shopping.class);
//                        startActivity(s6);
//                        break;
//
//                }
//
//                mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
//                    @Override
//                    public void onDrawerClosed(View drawerView) {
//                        super.onDrawerClosed(drawerView);
//
//                    }
//                });
//                mDrawerLayout.closeDrawer(mDrawerList);
//            }
//        });


        final ImageView  dotimg = (ImageView) findViewById(R.id.iddotimg);
        dotimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MerchantPaymentNew.this, dotimg);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //  Toast.makeText(HomeScreenActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(MerchantPaymentNew.this, WebActivity.class);
                        if (item.getTitle().toString().equalsIgnoreCase("FAQs")) {
                            i.putExtra("option", "FAQ");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("My Account")) {
                            Intent intent = new Intent(getApplicationContext(), MyAcount.class);
                            startActivity(intent);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Offers")) {
                            Intent intent = new Intent(getApplicationContext(), OffersActivity.class);
                            intent.putExtra("url",getResources().getString(R.string.offers));
                            startActivity(intent);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Privacy Policy")) {

                            i.putExtra("option", "PRIVACY");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Terms & Conditions")) {
                            i.putExtra("option", "TNC");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("About Us")) {

                            i.putExtra("option", "ABOUT");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Contact Us")) {

                            i.putExtra("option", "CONTACT");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Recharge & Pay")) {
                            Intent intent = new Intent(getApplicationContext(), Electricity_Payment.class);
                            startActivity(intent);
                        }   else if (item.getTitle().toString().equalsIgnoreCase("Quick Transfer")) {
//                            Intent intent = new Intent(getApplicationContext(), QuickTransferActivity.class);
//                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Request Money")) {
                            Intent intent = new Intent(getApplicationContext(), RequestMoneyActivity.class);
                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Money Transfer")) {
                            Intent intent = new Intent(getApplicationContext(), MoneyTransferActivity.class);
                            intent.putExtra("index", "0");
                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Manage Beneficiary")) {
                            Intent intent = new Intent(getApplicationContext(), MoneyTransferActivity.class);
                            intent.putExtra("index", "3");
                            startActivity(intent);

                        } else if (item.getTitle().toString().equalsIgnoreCase("My Account")) {
                            Intent intent = new Intent(getApplicationContext(), MyAcount.class);
                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Load Wallet")) {
                            Intent intent = new Intent(getApplicationContext(), LoadMoneyActivity.class);
                            startActivity(intent);
                        }
                        else if(item.getTitle().toString().equalsIgnoreCase("Feedback")){
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
    }


    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK)
        {
            uriContact = data.getData();
            retrieveContactNumber();
        }
        else if(requestCode == REQUEST_CODE_MERCHANT_LIST)
        {
            try {
                txtMerchantName.setText(data.getStringExtra("merchantName"));
                merchantNumber = data.getStringExtra("merchantNumber");
                merchantName = data.getStringExtra("merchantName");

                if(!txtMerchantName.getText().toString().trim().equalsIgnoreCase("") && txtMerchantName.getText().toString().trim().equalsIgnoreCase("SKILLANGELS"))
                {
                    Intent i = new Intent(getApplicationContext(),SkillAngels.class);
                    i.putExtra("merchantNumber",merchantNumber);
                    i.putExtra("merchantName",merchantName);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else if(!txtMerchantName.getText().toString().trim().equalsIgnoreCase(""))
                {
                    Intent i = new Intent(getApplicationContext(),MerchantPayment.class);
                    i.putExtra("merchantNumber",merchantNumber);
                    i.putExtra("amount","");
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else
                {
                    AlertBuilder alert = new AlertBuilder(MerchantPaymentNew.this);
                    alert.showAlert(getResources().getString(R.string.invalid_merchant_type));
                }


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

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    public void merchantPayment(View v)
    {   if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

             if(radioMerchant.isChecked())
            {

                if(!txtMerchantName.getText().toString().trim().equalsIgnoreCase("") && txtMerchantName.getText().toString().trim().equalsIgnoreCase("SKILLANGELS"))
                {
                    Intent i = new Intent(getApplicationContext(),SkillAngels.class);
                    i.putExtra("merchantNumber",merchantNumber);
                    i.putExtra("merchantName",merchantName);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else if(!txtMerchantName.getText().toString().trim().equalsIgnoreCase(""))
                {
                    Intent i = new Intent(getApplicationContext(),MerchantPayment.class);
                    i.putExtra("merchantNumber",merchantNumber);
                    i.putExtra("amount","");
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else
                {
                    AlertBuilder alert = new AlertBuilder(MerchantPaymentNew.this);
                    alert.showAlert(getResources().getString(R.string.invalid_merchant_type));
                }

            }
            else if(radioMerchantMobile.isChecked())
            {

                if(!edtMerchantMobileNumber.getText().toString().trim().equalsIgnoreCase("") && edtMerchantMobileNumber.getText().toString().trim().length() == 10)
                {
                    Intent i = new Intent(getApplicationContext(),MerchantPayment.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("merchantNumber", edtMerchantMobileNumber.getText().toString().trim());
                    i.putExtra("amount","");
                    startActivity(i);
                }
                else
                {
                    AlertBuilder alert = new AlertBuilder(MerchantPaymentNew.this);
                    alert.showAlert(getResources().getString(R.string.invalid_mobile));
                }
            }

    } else {
        AlertBuilder alert = new AlertBuilder(MerchantPaymentNew.this);
        alert.newUser();
    }



    }

    public void selectMerchant(View v)
    {
        if(connectionDetector.isConnectingToInternet()) {
            Intent i = new Intent(getApplicationContext(), MerchantList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(i, REQUEST_CODE_MERCHANT_LIST);
        }
        else
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();
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
        Intent i = new Intent(MerchantPaymentNew.this, Shopping.class);
        startActivity(i);
    }
    public void openhome(View v)
    {
        Intent i = new Intent(MerchantPaymentNew.this, HomeScreenActivity.class);
        startActivity(i);
    }
    public void openinbox(View v)
    {
        Intent s5 =new Intent(getApplicationContext(),AlertActivity.class);
        startActivity(s5);

    }
    public void openMyAcount(View v)
    {
        Intent i = new Intent(MerchantPaymentNew.this, MyAcount.class);
        startActivity(i);
    }
    @Override
    public void onResume(){
        super.onResume();

        new SetNotificationCounter(getApplicationContext(), (TextView)findViewById(R.id.txtNotificationCounter));

    }
    private void openAlertDialog(AlertDialog.Builder alertDialog)
    {

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                dialog.cancel();
                edtMerchantMobileNumber.setText("");


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
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

            Contact contact = new Contact(MerchantPaymentNew.this,edtMerchantMobileNumber);
            contact.getContactNumber(id);
        }
        catch(NullPointerException ex)
        {


        }
        catch (Exception e) {
            // TODO: handle exception
        }


    }


}
