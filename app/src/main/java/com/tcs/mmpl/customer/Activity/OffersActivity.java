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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.Adapter.CropOptionAdapter;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.Adapter.OfferGridViewAdapter;
import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.CropOption;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.Offers;
import com.tcs.mmpl.customer.utility.SetNotificationCounter;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class OffersActivity extends Activity {
    FontClass fontclass = new FontClass();
    Typeface typeface;
    LinearLayout mainlinear;
    private Uri mImageCaptureUri;
    private static int RESULT_LOAD_IMG = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_GALLERY = 4;
    SharedPreferences userInfoPref;
    SharedPreferences.Editor userInfoEditor;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;

    ImageView offer1,offer2,offer3,offer4,offer5,offer6;

    MyDBHelper db;
    MyDBHelper dbHelper;
    ConnectionDetector connectionDetector;
    ImageView profileImage;
    LinearLayout linView1,linView2,linView3,linView4,linView5,linView6;
    TextView txtOffer1,txtOffer2,txtOffer3,txtOffer4,txtOffer5,txtOffer6;

    ArrayList<Offers> mGridData;
    private GridView mGridView;
    OfferGridViewAdapter mGridAdapter;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_offers);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        profileImage = (ImageView)findViewById(R.id.imgUser);


        dbHelper = new MyDBHelper(getApplicationContext());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(width/2,width/2);
        mGridView = (GridView) findViewById(R.id.offergridView);
        //Initialize with empty data
        mGridData = new ArrayList<Offers>();
        mGridAdapter = new OfferGridViewAdapter(OffersActivity.this, R.layout.offer_grid_layout,
                mGridData,width,layoutParams);

        mGridView.setAdapter(mGridAdapter);

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
               com.tcs.mmpl.customer.utility.Offers item = (Offers) parent.getItemAtPosition(position);


                try
                {
                    Intent i =new Intent(Intent.ACTION_VIEW, Uri.parse(item.getMerchantUrl()));
                    startActivity(i);
                }
                catch (Exception e)
                {

                }
            }
        });

        GetOffers g = new GetOffers(getApplicationContext());
        g.execute(getIntent().getStringExtra("url"));



        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
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

        connectionDetector = new ConnectionDetector(getApplicationContext());

        linView1 = (LinearLayout)findViewById(R.id.linView1);
        linView2 = (LinearLayout)findViewById(R.id.linView2);
        linView3 = (LinearLayout)findViewById(R.id.linView3);
        linView4 = (LinearLayout)findViewById(R.id.linView4);
        linView5 = (LinearLayout)findViewById(R.id.linView5);
        linView6 = (LinearLayout)findViewById(R.id.linView6);

        txtOffer1 = (TextView)findViewById(R.id.txtOffers1);
        txtOffer2 = (TextView)findViewById(R.id.txtOffers2);
        txtOffer3 = (TextView)findViewById(R.id.txtOffers3);
        txtOffer4 = (TextView)findViewById(R.id.txtOffers4);
        txtOffer5 = (TextView)findViewById(R.id.txtOffers5);
        txtOffer6 = (TextView)findViewById(R.id.txtOffers6);


        db =new MyDBHelper(getApplicationContext());
        mainlinear = (LinearLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        //call HamburgerMenu
        new HamburgerMenu(this,(DrawerLayout) findViewById(R.id.drawer_layout),(ExpandableListView) findViewById(R.id.left_drawer),(ImageView) findViewById(R.id.idmenuimg)).setHamburger();

        final ImageView dotimg = (ImageView) findViewById(R.id.iddotimg);

        dotimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(OffersActivity.this, dotimg);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //  Toast.makeText(HomeScreenActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(OffersActivity.this, WebActivity.class);
                        if (item.getTitle().toString().equalsIgnoreCase("FAQs")) {

                            i.putExtra("option", "FAQ");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("My Account")) {

                            Intent intent = new Intent(getApplicationContext(), MyAcount.class);
                            startActivity(intent);

                        }   else if (item.getTitle().toString().equalsIgnoreCase("Privacy Policy")) {

                            i.putExtra("option", "PRIVACY");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Terms & Conditions")) {
                            i.putExtra("option", "TNC");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Recharge & Pay")) {
                            Intent intent = new Intent(getApplicationContext(), Electricity_Payment.class);
                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Offers")) {
//                            Intent intent = new Intent(getApplicationContext(), LoadwalletActivity.class);
//                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Quick Transfer")) {
                            Intent intent = new Intent(getApplicationContext(), QuickTransferActivity.class);
                            startActivity(intent);
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
//                            Intent intent = new Intent(getApplicationContext(), LoadwalletActivity.class);
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
                                                Intent i = new Intent(OffersActivity.this, FavoriteActivity.class);
                                                startActivity(i);
                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();;
                                            }
                                        }
                                    }
        );

        offer1 = (ImageView)findViewById(R.id.offer1);
        offer2 = (ImageView)findViewById(R.id.offer2);
        offer3 = (ImageView)findViewById(R.id.offer3);
        offer4 = (ImageView)findViewById(R.id.offer4);
        offer5 = (ImageView)findViewById(R.id.offer5);
        offer6 = (ImageView)findViewById(R.id.offer6);


        int c=1;
//        Cursor cursor = db.fun_select_tbl_Offers();
//        if(cursor.moveToFirst())
//        {
//            do {
//                switch (c)
//                {
//                    case 1 : linView1.setVisibility(View.VISIBLE);
//                          //   offer1.setImageBitmap(BitmapFactory.decodeByteArray(cursor.getBlob(1), 0, cursor.getBlob(1).length));
//                        Glide.with(OffersActivity.this).load(cursor.getString(2)).into(offer1);
//                             offer1.setOnClickListener(OnOfferClick(offer1,cursor.getString(3)));
//                             txtOffer1.setText(cursor.getString(4));
//                             break;
//
//                    case 2 :
//                        linView2.setVisibility(View.VISIBLE);
//                     //   offer2.setImageBitmap(BitmapFactory.decodeByteArray(cursor.getBlob(1), 0, cursor.getBlob(1).length));
//                        Glide.with(OffersActivity.this).load(cursor.getString(2)).into(offer2);
//                        offer2.setOnClickListener(OnOfferClick(offer2, cursor.getString(3)));
//                        txtOffer2.setText(cursor.getString(4));
//                        break;
//
//                    case 3 :
//                        linView3.setVisibility(View.VISIBLE);
//                      //  offer3.setImageBitmap(BitmapFactory.decodeByteArray(cursor.getBlob(1), 0, cursor.getBlob(1).length));
//                        Glide.with(OffersActivity.this).load(cursor.getString(2)).into(offer3);
//                        offer3.setOnClickListener(OnOfferClick(offer3, cursor.getString(3)));
//                        txtOffer3.setText(cursor.getString(4));
//                        break;
//
//                    case 4 :
//                        linView4.setVisibility(View.VISIBLE);
//                    //    offer4.setImageBitmap(BitmapFactory.decodeByteArray(cursor.getBlob(1), 0, cursor.getBlob(1).length));
//                        Glide.with(OffersActivity.this).load(cursor.getString(2)).into(offer4);
//                        offer4.setOnClickListener(OnOfferClick(offer4, cursor.getString(3)));
//                        txtOffer4.setText(cursor.getString(4));
//                        break;
//
//                    case 5 :
//                        linView5.setVisibility(View.VISIBLE);
//                       // offer5.setImageBitmap(BitmapFactory.decodeByteArray(cursor.getBlob(1), 0, cursor.getBlob(1).length));
//                        Glide.with(OffersActivity.this).load(cursor.getString(2)).into(offer5);
//                        offer5.setOnClickListener(OnOfferClick(offer5, cursor.getString(3)));
//                        txtOffer5.setText(cursor.getString(4));
//                        break;
//
//                    case 6 :
//                        linView6.setVisibility(View.VISIBLE);
//                        //offer6.setImageBitmap(BitmapFactory.decodeByteArray(cursor.getBlob(1), 0, cursor.getBlob(1).length));
//                        Glide.with(OffersActivity.this).load(cursor.getString(2)).into(offer6);
//                        offer6.setOnClickListener(OnOfferClick(offer6,cursor.getString(3)));
//                        txtOffer6.setText(cursor.getString(4));
//                        break;
//
//                    default:
//                }
//                c++;
//
//            }while(cursor.moveToNext());
//        }
//
//        try{
//            cursor.close();
//        }
//        catch (Exception e){
//
//        }

    }

    View.OnClickListener OnOfferClick(final ImageView img,final String url)
    {
        return  new View.OnClickListener(){

            public void onClick(View v)
            {
                Intent i =new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
        };
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
        Intent i = new Intent(OffersActivity.this, Shopping.class);
        startActivity(i);
    }
    public void openhome(View v)
    {
        Intent i = new Intent(OffersActivity.this, HomeScreenActivity.class);
        startActivity(i);
    }

    public void openinbox(View v)
    {
        Intent s5 =new Intent(getApplicationContext(),AlertActivity.class);
        startActivity(s5);

    }
    public void openMyAcount(View v)
    {
        Intent i = new Intent(OffersActivity.this, MyAcount.class);
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

    private class GetOffers extends AsyncTask<String, Void, String> {

        Context context;
        private ProgressDialog pDialog;

        ArrayList<String> id,url;
        String bannerURL;

        public GetOffers(Context context) {
            this.context = context;

            id = new ArrayList<String>();
            url = new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(OffersActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(OffersActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        //// System.out.println("1" );
                        if(jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success"))
                        {

                            JSONArray jsonArray = jsonMainObj.getJSONArray("lstGetOffersDetails");
                            //// System.out.println("2" );
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                //// System.out.println("3" );
                                Offers offers = new Offers();
                                offers.setCouponID(jsonObject.getString("couponID"));
                                offers.setCouponImage(jsonObject.getString("couponImage"));
                                offers.setMerchantUrl(jsonObject.getString("merchantUrl"));
                                offers.setMerchantAboutMe(jsonObject.getString("merchantAboutMe"));
                                //// System.out.println("4");
                                mGridData.add(offers);
                                //// System.out.println("5");
                            }

                        }





                        return "Success";

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failure";
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failure";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url"+e.getMessage());
                return "Failure";
            }



        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {





                mGridAdapter.setGridData(mGridData);



                try {
//                    for(int i=0;i<url.size();i++) {
//
//                        DownloadOfferImage DI = new DownloadOfferImage(getApplicationContext(),id.get(i));
//                        DI.execute(url.get(i));
//                    }
                }
                catch (Exception e)
                {

                }


            } else if (result.equalsIgnoreCase("Failure")) {


                // Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {


            }

        }

    }


}
