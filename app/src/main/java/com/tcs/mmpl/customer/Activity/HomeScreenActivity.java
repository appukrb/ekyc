package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.service.MarketService;
import com.bumptech.glide.Glide;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tcs.mmpl.customer.Adapter.CropOptionAdapter;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.Adapter.SendNotficationClicked;
import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.BannerLayout;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.CropOption;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.KycList;
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
import java.util.concurrent.atomic.AtomicInteger;

public class HomeScreenActivity extends Activity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM";
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_GALLERY = 4;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static int RESULT_LOAD_IMG = 1;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    String jsonStr1;
    ImageView idmenuimg;
    String mdn, imei, imgBannertop1;
    Dialog dialog;
    LinearLayout linear, linParent;
    WebView wb;
    LinearLayout linWelcome, linDetails;
    TextView txtWelcome, txtBalance;
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;
    ImageView imgUser, imgMoneyTransfer, btn_load_money, imgBanner;
    Button btn_invite_friend;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    MyDBHelper dbHelper;
    MyConnectionHelper db;
    String SENDER_ID = "913581902163";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regid, networkType;
    ArrayList<KycList> kycLists;
    private Uri mImageCaptureUri;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;
    private ArrayList<String> list1 = new ArrayList<String>();
    private ArrayList<String> listurl = new ArrayList<String>();
    private ArrayList<String> list2 = new ArrayList<String>();
    private ArrayList<String> listimageid = new ArrayList<String>();
    private ImageView dotimg;
    private ImageView profileImage, imgTopBanner;
    private String bannerName = "", bannerUrl = "";

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_screen);


        dbHelper = new MyDBHelper(getApplicationContext());
        db = new MyConnectionHelper(getApplicationContext());

        linParent = (LinearLayout) findViewById(R.id.linParent);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);


        // btn_load_money = (ImageView) findViewById(R.id.btn_load_money);
        btn_invite_friend = (Button) findViewById(R.id.btn_invite_friend);
        imgMoneyTransfer = (ImageView) findViewById(R.id.imgMoneyTransfer);
        imgBanner = (ImageView) findViewById(R.id.imgBanner);
//        BannerLayout bannerLayout = (BannerLayout) findViewById(R.id.imgBanner);
        BannerLayout imgBannertop = (BannerLayout) findViewById(R.id.imgBannertop);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

//        linear = (LinearLayout) findViewById(R.id.webview_linear);
//        linear.setVisibility(View.GONE);
        linWelcome = (LinearLayout) findViewById(R.id.linWelcome);
        linDetails = (LinearLayout) findViewById(R.id.linDetails);

        txtWelcome = (TextView) findViewById(R.id.txtWelcome);
        txtBalance = (TextView) findViewById(R.id.txtBalance);

        dotimg = (ImageView) findViewById(R.id.iddotimg);
        profileImage = (ImageView) findViewById(R.id.imgUser);

        if(userInfoPref.getString("notify","false").trim().equalsIgnoreCase("true"))
        {

            // System.out.println("landing screen");
            String url = getResources().getString(R.string.saveNotification)+"?MSG="+userInfoPref.getString("msg","|");
            SendNotficationClicked sendNotficationClicked = new SendNotficationClicked(HomeScreenActivity.this, userInfoPref.getString("msg","|"));
            sendNotficationClicked.execute(url);
            userInfoEditor.putString("notify","false");
            userInfoEditor.commit();

        }

        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();
        if (c1.moveToNext()) {
            do {
//                if(c1.getString(3).equalsIgnoreCase("LAND"))
//                {
//
//                    Glide.with(HomeScreenActivity.this).load(c1.getString(1)).placeholder(R.drawable.default_banner).into(imgBanner);
//                    imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
//
//                }

                if (c1.getString(3).equalsIgnoreCase("ADVT")) {
                    listimageid.add(c1.getString(0));
                    list2.add(c1.getString(1));
                }
            } while (c1.moveToNext());

//            bannerLayout.setViewUrls(list1);
            imgBannertop.setViewUrls(list2);
        }

        c1.close();
        imgBannertop.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {

                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

                    if (connectionDetector.isConnectingToInternet()) {

                        imgBannertop1 = listimageid.get(position).toString();

                        String CarouselBannerDetailurl = getResources().getString(R.string.bannerlinknew) + "?BannerName=" + imgBannertop1;

                        //// System.out.println(CarouselBannerDetailurl);
//                CarouselBannerDetail CarouselBannerDetail1 = new CarouselBannerDetail(getApplicationContext());
//                CarouselBannerDetail1.execute(CarouselBannerDetailurl);
                        GetBannerLink getBannerLink = new GetBannerLink(getApplicationContext());
                        getBannerLink.execute(CarouselBannerDetailurl);
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }
                } else {
                    AlertBuilder alert = new AlertBuilder(HomeScreenActivity.this);
                    alert.newUser();
                }


            }
        });
        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {


                    if (userInfoPref.getString("kycstatus", "").equalsIgnoreCase("PENDING") || userInfoPref.getString("kycstatus", "").equalsIgnoreCase("REJECTED")) {
                        updateWallet(v);
                    }
                } else {
                    AlertBuilder alert = new AlertBuilder(HomeScreenActivity.this);
                    alert.newUser();
                }
//                String bannerURL = getResources().getString(R.string.bannerlink)+"?Type=LAND";
//                GetBannerLink getBannerLink = new GetBannerLink(getApplicationContext());
//                getBannerLink.execute(bannerURL);


            }
        });
//        banner = pref.getString("banner","");
//        if(!banner.trim().isEmpty()) {
//            byte[] byteArray = Base64.decode(banner, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
//                    byteArray.length);
//            imgBanner.setImageBitmap(bitmap);
//            imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
//        }
//       imgBanner.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               finish();
//               Intent re =new Intent(getApplicationContext(),Electricity_Payment.class);
//               re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//               startActivity(re);
//           }
//       });

//        bannerLayout.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
////                finish();
//                if(listurl.get(position).toString().equalsIgnoreCase("null"))
//                {
//                    Intent re =new Intent(getApplicationContext(),Electricity_Payment.class);
//                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(re);
//                }
//                else if(listurl.get(position).toString().equalsIgnoreCase("load"))
//                {
//                    Intent re =new Intent(getApplicationContext(),LoadMoneyActivity.class);
//                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(re);
//                }
//                else if(listurl.get(position).toString().equalsIgnoreCase("offers"))
//                {
//                    Intent re =new Intent(getApplicationContext(),OffersActivity.class);
//                    re.putExtra("url",getResources().getString(R.string.offers));
//                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(re);
//                }
//                else if(listurl.get(position).toString().equalsIgnoreCase("money"))
//                {
//                    Intent re =new Intent(getApplicationContext(),MoneyTransferActivity.class);
//                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(re);
//                }
//                else if(listurl.get(position).toString().equalsIgnoreCase("merch"))
//                {
//                    Intent re =new Intent(getApplicationContext(),MerchantTransferNew.class);
//                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(re);
//                }
//                else
//                {
////                    Toast.makeText(HomeScreenActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
//                    String url=  listurl.get(position);
////                    //// System.out.println(url);
//                    Intent i =new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(i);
//                }
//
//
//            }
//        });

        wb = new WebView(getApplicationContext());
        wb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

            //   txtWelcome.setText("Hi " + userInfoPref.getString("firstname", ""));
//            txtBalance.setText( userInfoPref.getString("walletbalance", ""));

            if (userInfoPref.getString("profilepic", "").equalsIgnoreCase("")) {
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.dummy);
                profileImage.setImageBitmap(icon);
            } else {
//                //// System.out.println("Coming else part");
//                profileImage.setImageBitmap(BitmapFactory
//                        .decodeFile(userInfoPref.getString("profilepic","")));
                try {
                    Cursor c = dbHelper.fun_select_tbl_profileImage();

                    if (c.moveToNext()) {
                        profileImage.setImageBitmap(BitmapFactory.decodeByteArray(c.getBlob(0), 0, c.getBlob(0).length));
                    }
                    c.close();
                } catch (Exception e) {

                }
            }
        } else {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.dummy);
            profileImage.setImageBitmap(icon);

        }
        if (connectionDetector.isConnectingToInternet()) {

            networkType = getNetworkClass(getApplicationContext());
            GetUserStatus getUserStatus = new GetUserStatus(getApplicationContext());
            getUserStatus.execute(getResources().getString(R.string.getUserStatus));
        } else {

            editor.putInt("status", 0);
            editor.commit();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

        }


        //call HamburgerMenu
        new HamburgerMenu(this, (DrawerLayout) findViewById(R.id.drawer_layout), (ExpandableListView) findViewById(R.id.left_drawer), (ImageView) findViewById(R.id.idmenuimg)).setHamburger();

        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

                                                if (connectionDetector.isConnectingToInternet()) {
                                                    Intent i = new Intent(HomeScreenActivity.this, FavoriteActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                AlertBuilder alert = new AlertBuilder(HomeScreenActivity.this);
                                                alert.newUser();
                                            }
                                        }
                                    }
        );
        dotimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(HomeScreenActivity.this, dotimg);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //  Toast.makeText(HomeScreenActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(HomeScreenActivity.this, WebActivity.class);
                        if (item.getTitle().toString().equalsIgnoreCase("FAQs")) {

                            i.putExtra("option", "FAQ");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("My Account")) {

                            Intent intent = new Intent(getApplicationContext(), MyAcount.class);
                            startActivity(intent);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Offers")) {

                            Intent intent = new Intent(getApplicationContext(), OffersActivity.class);
                            intent.putExtra("url", getResources().getString(R.string.offers));
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
                            Intent intent = new Intent(getApplicationContext(), LoadMoneyActivity.class);
                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Feedback")) {
                            Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                            startActivity(intent);
                        } else if (item.getTitle().toString().equalsIgnoreCase("Rate Us")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tcs.mmpl.customer")));
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });


        imgMoneyTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent i = new Intent(HomeScreenActivity.this, RegisterActivity.class);
                Intent i = new Intent(HomeScreenActivity.this, MoneyTransferActivity.class);
                i.putExtra("index", "0");
                startActivity(i);
            }
        });


        ImageView billpay = (ImageView) findViewById(R.id.idbillpay);
        billpay.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent i = new Intent(HomeScreenActivity.this, Electricity_Payment.class);
                                           startActivity(i);
                                       }
                                   }
        );
        ImageView idquickimg = (ImageView) findViewById(R.id.idquickimg);
        idquickimg.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent i = new Intent(HomeScreenActivity.this, QuickTransferActivity.class);
                                              startActivity(i);
                                          }
                                      }
        );

        btn_invite_friend.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     Intent i = new Intent(HomeScreenActivity.this, Invite_Friend_Activity.class);
                                                     startActivity(i);
                                                 }
                                             }
        );
        ImageView idmerchantpayment = (ImageView) findViewById(R.id.idmerchantpayment);
        idmerchantpayment.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     Intent i = new Intent(HomeScreenActivity.this, MerchantTransferNew.class);
                                                     startActivity(i);
                                                 }
                                             }
        );
        ImageView offers = (ImageView) findViewById(R.id.offers);
        offers.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                                              Intent i = new Intent(HomeScreenActivity.this, GCIActivity.class);
                                              startActivity(i);

                                          } else {
                                              AlertBuilder alert = new AlertBuilder(HomeScreenActivity.this);
                                              alert.newUser();
                                          }

//                                                 GiftVoucher giftVoucher = new GiftVoucher(HomeScreenActivity.this);
//                                                 giftVoucher.execute(getResources().getString(R.string.gift_url)+"?MDN="+pref.getString("mobile_number",""));


                                      }
                                  }
        );

        if (connectionDetector.isConnectingToInternet()) {

            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                regid = getRegistrationId(getApplicationContext());

                //// System.out.println(regid);
                if (regid.equalsIgnoreCase("")) {
                    registerInBackground();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Please update google play service", Toast.LENGTH_LONG).show();
                Log.i(TAG, "No valid Google Play Services APK found.");
            }
        }

        mdn = pref.getString("mobile_number", "");
        imei = getIMEI(getApplicationContext());

    }

    public String getOS() {
        String OS = "";
        try {
            OS = Build.VERSION.RELEASE;
        } catch (Exception e) {
            OS = "";
        }


        return "Android " + OS;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    public String getIMEI(Context context) {
        String IMEI = "";

        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            IMEI = mTelephonyManager.getDeviceId();
        } catch (Exception e) {
            IMEI = "";
        }

        return IMEI;
    }

    public String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }

    private boolean checkPlayServices() {

        try {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (resultCode != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    finish();
                }
                return false;
            }

        } catch (Exception e) {
//            //// System.out.println("in catch block of chckplayservice method");
            e.printStackTrace();
        }
        return true;
    }

    private String getRegistrationId(Context context) {

        String registrationId = "";
        try {
            final SharedPreferences prefs = getGcmPreferences(context);
            registrationId = prefs.getString(PROPERTY_REG_ID, "");
            if (registrationId.equalsIgnoreCase("")) {
                Log.i(TAG, "Registration not found.");
                return "";
            }
            // Check if app was updated; if so, it must clear the registration ID
            // since the existing regID is not guaranteed to work with the new
            // app version.
            int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
            int currentVersion = getAppVersion(getApplicationContext());
            if (registeredVersion != currentVersion) {
                // Log.i(TAG, "App version changed.");
                return "";
            }

        } catch (Exception e) {
//            //// System.out.println("In getRegistrationId");
            e.printStackTrace();
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String jsonStr = null;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(SENDER_ID);

//                    //// System.out.println("mssgg==" + regid);
                    Log.i("regid", regid);
                    storeRegistrationId(getApplicationContext(), regid);


                    TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    String imei = mngr.getDeviceId();

                    String url = getResources().getString(R.string.updateGCMDetails) + "?MDN=" + pref.getString("mobile_number", "") + "&IMEI=" + imei + "&GCM=" + regid;
//                    //// System.out.println("URL::::::::::::::>>>>>>>>>>"+url);


                    // Creating service handler class instance
                    WebServiceHandler serviceHandler = new WebServiceHandler(
                            HomeScreenActivity.this);

                    // Making a request to url and getting response
                    jsonStr = serviceHandler.makeServiceCall(url,
                            WebServiceHandler.POST2);

//                    //// System.out.println("Response::::::::::::::>>>>>>>>>>"+jsonStr);

                    return "Success";

                } catch (Exception ex) {
//                    //// System.out.println("In doInbackground");
                    ex.printStackTrace();
                    return "Failure";
                }

            }

            @Override
            protected void onPostExecute(String result) {


//                //// System.out.println("Inside postexecute method::::::"+result);
            }
        }.execute(null, null, null);
    }

    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(HomeScreenActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


    private void storeRegistrationId(Context context, String regId) {
        try {

            final SharedPreferences prefs = getGcmPreferences(context);
            int appVersion = getAppVersion(context);
            Log.i(TAG, "Saving regId on app version " + appVersion);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_REG_ID, regId);
            editor.putInt(PROPERTY_APP_VERSION, appVersion);
            editor.commit();
        } catch (Exception e) {
//            //// System.out.println("In storeRegistraionId");
            e.printStackTrace();
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

    public void generateOTP(View v) {
        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

            if (connectionDetector.isConnectingToInternet()) {
                GenerateOTPAlert generateOTPAlert = new GenerateOTPAlert(this);
                generateOTPAlert.openAlert();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }
        } else {
            AlertBuilder alert = new AlertBuilder(HomeScreenActivity.this);
            alert.newUser();
        }

    }

    public void openAlert(View v) {
        Intent i = new Intent(HomeScreenActivity.this, Shopping.class);
        startActivity(i);
    }

    public void openinbox(View v) {
        Intent s5 = new Intent(getApplicationContext(), AlertActivity.class);
        startActivity(s5);

    }

    public void openLoadMoney(View v) {
        Intent i = new Intent(HomeScreenActivity.this, LoadMoneyActivity.class);
        i.putExtra("index", "0");
        startActivity(i);
    }

    public void openWallet(View v) {

        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
            if (connectionDetector.isConnectingToInternet()) {
                GetPurse getPurse = new GetPurse(getApplicationContext());
                getPurse.execute(getResources().getString(R.string.purse_url) + "?MDN=" + pref.getString("mobile_number", ""));
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }
        } else {
            AlertBuilder alert = new AlertBuilder(HomeScreenActivity.this);
            alert.newUser();
        }
    }

    public void openMyAcount(View v) {
        Intent i = new Intent(HomeScreenActivity.this, MyAcount.class);
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
        } catch (ActivityNotFoundException ae) {

        } catch (Exception e) {

        }
    }

    public void cropImage(View v) {
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

    public void loadImage(View v) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);

        try {

            startActivityForResult(intent, PICK_FROM_FILE);
            // startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Activity not found", Toast.LENGTH_LONG).show();
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

                    if (c.moveToNext()) {
                        dbHelper.updateProfileImage(stream.toByteArray());
                    } else {
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
                } catch (Exception e) {

                }
        }
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

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
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        try {
                            startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Activity is not found", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

    private void showRateDialog() {

        if (userInfoPref.getString("rate_us_status", "NOTDONE").trim().equalsIgnoreCase("NOTDONE")) {

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(HomeScreenActivity.this, R.style.StackedAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.rate_title));
            builder.setMessage(userInfoPref.getString("rate_message", getResources().getString(R.string.rate_explanation)));
            builder.setPositiveButton(getResources().getString(R.string.rate_now), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Write your code here to execute after dialog closed
                    dialog.cancel();
                    userInfoEditor.putString("rate_us_status", "DONE");
                    userInfoEditor.commit();
                    final String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+ appPackageName)));
                    }

                    String rateURL = getResources().getString(R.string.rateus) + "?MDN=" + pref.getString("mobile_number", "") + "&rateStatus=RATE";
                    RateUs rateUs = new RateUs(getApplicationContext());
                    rateUs.execute(rateURL);

                }
            });
            builder.setNeutralButton(getResources().getString(R.string.rate_later), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    userInfoEditor.putString("rate_us_status", "DONE");
                    userInfoEditor.commit();
                    String rateURL = getResources().getString(R.string.rateus) + "?MDN=" + pref.getString("mobile_number", "") + "&rateStatus=LATER";
                    RateUs rateUs = new RateUs(getApplicationContext());
                    rateUs.execute(rateURL);
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.rate_never), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();

                    userInfoEditor.putString("rate_us_status", "DONE");
                    userInfoEditor.commit();

                    String rateURL = getResources().getString(R.string.rateus) + "?MDN=" + pref.getString("mobile_number", "") + "&rateStatus=NOTHANKS";
                    RateUs rateUs = new RateUs(getApplicationContext());
                    rateUs.execute(rateURL);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            try{
                final Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                LinearLayout linearLayout = (LinearLayout) button.getParent();
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            } catch(Exception ex){
                //ignore it
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        new SetNotificationCounter(getApplicationContext(), (TextView) findViewById(R.id.txtNotificationCounter));

    }

    public void updateWallet(View v) {

        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

            if (connectionDetector.isConnectingToInternet()) {
                String url = getResources().getString(R.string.getKycStatus) + "?MDN=" + pref.getString("mobile_number", "");

                //// System.out.println(url);

                UpgradeWallet upgradeWallet = new UpgradeWallet(getApplicationContext());
                upgradeWallet.execute(url);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    HomeScreenActivity.this).create();

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

    private class GetUserStatus extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance, responseMessage;

        public GetUserStatus(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HomeScreenActivity.this);
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
                jsonObject.accumulate("IMEI", getIMEI(getApplicationContext()));
                jsonObject.accumulate("Model", getDeviceName());
                jsonObject.accumulate("OS", getOS());
                jsonObject.accumulate("Network", getNetworkClass(getApplicationContext()));


                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(HomeScreenActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                Log.d("response", jsonStr);


                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            firstName = jsonMainObj.getString("firstName");
                            lastName = jsonMainObj.getString("lastName");
                            walletBalance = jsonMainObj.getString("walletBalance");
                            bannerName = jsonMainObj.getString("bannerName");
                            bannerUrl = jsonMainObj.getString("bannerDownloadLink");
                            userInfoEditor.putString("firstname", firstName);
                            userInfoEditor.putString("lastname", lastName);
                            userInfoEditor.putString("walletbalance", walletBalance);
                            userInfoEditor.putString("usertype", jsonMainObj.getString("userType"));
                            userInfoEditor.putBoolean("new_user_registered_newapp", true);
                            userInfoEditor.putString("emailId", jsonMainObj.getString("emailId"));
                            userInfoEditor.putString("account", jsonMainObj.getString("account"));
                            userInfoEditor.putString("kycstatus", jsonMainObj.getString("kycstatus"));
                            userInfoEditor.putString("billdeskFlag", jsonMainObj.getString("billdeskFlag"));
                            userInfoEditor.putString("userID", jsonMainObj.getString("userID"));
                            userInfoEditor.commit();

                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            bannerName = jsonMainObj.getString("bannerName");
                            bannerUrl = jsonMainObj.getString("bannerDownloadLink");
                            return jsonMainObj.getString("responseMessage");

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("BLOCK1")) {
                            responseMessage = jsonMainObj.getString("responseMessage");

                            return "BLOCK1";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("BLOCK2")) {
                            responseMessage = jsonMainObj.getString("responseMessage");
                            return "BLOCK2";

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

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + bannerUrl);

            Glide.with(HomeScreenActivity.this).load(bannerUrl).placeholder(R.drawable.default_banner).into(imgBanner);
            imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);


            if (result.equalsIgnoreCase("Success")) {


                showRateDialog();
//                AppRater appRater = new AppRater(HomeScreenActivity.this);
//                appRater.setDaysBeforePrompt(3);
//                appRater.setLaunchesBeforePrompt(7);
//                appRater.setPhrases(R.string.rate_title, R.string.rate_explanation, R.string.rate_now, R.string.rate_later, R.string.rate_never);
//                appRater.show();

                editor.putInt("status", 2);
                editor.putBoolean("status_flag", false);
                editor.putBoolean("got_mobile_number_newapp", true);
                editor.commit();

                linWelcome.setVisibility(View.VISIBLE);
                linDetails.setVisibility(View.VISIBLE);

//                txtBalance.setText(userInfoPref.getString("walletbalance", ""));


            } else if (result.equalsIgnoreCase("Failure")) {

                editor.putInt("status", 0);
                editor.putBoolean("status_flag", true);
                editor.putBoolean("got_mobile_number_newapp", true);
                editor.commit();

                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

                    //  txtWelcome.setText("Hi " + userInfoPref.getString("firstname", ""));
//                    txtBalance.setText( userInfoPref.getString("walletbalance", ""));
                }

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else if (result.equalsIgnoreCase("BLOCK1")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeScreenActivity.this);
                alertDialogBuilder.setTitle("Application Blocked");
                alertDialogBuilder
                        .setMessage(responseMessage)
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        String Bolcked_url = getApplicationContext().getResources().getString(R.string.updateDeviceStatus) + "?MDN=" + mdn + "&IMEI=" + imei + "&ACTION=" + "BLOCK1";
                                        Bolcked Bolcked1 = new Bolcked(getApplicationContext());
                                        Bolcked1.execute(Bolcked_url);
                                    }
                                })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            } else if (result.equalsIgnoreCase("BLOCK2")) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeScreenActivity.this);
//                alertDialogBuilder.setTitle("Application Blocked");
//                alertDialogBuilder
//                        .setMessage(getResources().getString(R.string.block2))
//                        .setCancelable(false)
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//
//                                        String Bolcked_url=getApplicationContext().getResources().getString(R.string.updateDeviceStatus) + "?MDN="+mdn+"&IMEI="+imei+"&ACTION="+"BLOCK2";
//                                        Bolcked Bolcked1 = new Bolcked(getApplicationContext());
//                                        Bolcked1.execute(Bolcked_url);
//                                    }
//                                })
//
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                                android.os.Process.killProcess(android.os.Process.myPid());
//                                System.exit(1);
//                            }
//                        });
//
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();

                AlertBuilder alertBuilder = new AlertBuilder(HomeScreenActivity.this);
                AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(responseMessage);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();


            } else {

                editor.putInt("status", 1);
                editor.putBoolean("status_flag", false);
                editor.putBoolean("got_mobile_number_newapp", true);
                editor.commit();

                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                    // txtWelcome.setText("Hi " + userInfoPref.getString("firstname", ""));
//                    txtBalance.setText( userInfoPref.getString("walletbalance", ""));
                }

            }

            try {
                MarketService ms = new MarketService(HomeScreenActivity.this);
                ms.level(MarketService.REVISION).checkVersion();
            } catch (Exception e) {

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

    private class CarouselBannerDetail extends AsyncTask<String, Void, String> {
        Context context;
        ProgressDialog pDialog;
        String balance;

        public CarouselBannerDetail(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(getApplicationContext());
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();
        }


        @Override
        protected String doInBackground(String... arg0) {

            try {


//                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                jsonStr1 = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
//                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr1);

                if (jsonStr1 != null) {
//                    //// System.out.println("Response: >>>>>>>>>>>" + jsonStr1);
                    return "Success";
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failure";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }

        }


        protected void onPostExecute(final String result) {
            Log.d("Result::::", ">" + result);

            if (result.equalsIgnoreCase("Success")) {
                Intent intent = new Intent(HomeScreenActivity.this, CarouselBanner.class);
                intent.putExtra("result", jsonStr1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("carouselbannerID",imgBannertop1);
                startActivity(intent);

            } else if (result.equalsIgnoreCase("Failure")) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class Bolcked extends AsyncTask<String, Void, String> {

        Context context;
        String firstName, ToastMessage;

        public Bolcked(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HomeScreenActivity.this);
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

                return jsonStr;

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
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

            } else if (result.equalsIgnoreCase("Failure")) {
                // Toast.makeText(HomeScreenActivity.this,ToastMessage,Toast.LENGTH_LONG).show();
            }

        }

    }

    private class RateUs extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

//        String firstName, lastName, walletBalance;

        public RateUs(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler serviceHandler = new WebServiceHandler(getApplication());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
//                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);


                return "Success";

            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure1";
            }


        }

        @Override
        protected void onPostExecute(String result) {


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

            if (!activityName.equalsIgnoreCase("NA") && !urlLink.equalsIgnoreCase("NA")) {
                try {

                    String className = getPackageName() + ".Activity." + activityName;
                    Class<?> myClass = Class.forName(className);
                    //Activity activity = (Activity) myClass.newInstance();
                    Intent re = new Intent(getApplicationContext(), myClass);
                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    re.putExtra("option", urlLink);
                    startActivity(re);
                } catch (Exception e) {
                    e.printStackTrace();
                    //// System.out.println("exception " + e.getMessage());
                }
            } else if (!activityName.equalsIgnoreCase("NA") && urlLink.equalsIgnoreCase("NA")) {
                try {
                    String className = getPackageName() + ".Activity." + activityName;
                    Class<?> myClass = Class.forName(className);
                    //Activity activity = (Activity) myClass.newInstance();
                    Intent re = new Intent(getApplicationContext(), myClass);
                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(re);
                } catch (Exception e) {

                }

            } else if (activityName.equalsIgnoreCase("NA") && !urlLink.equalsIgnoreCase("NA")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink)));
            }


        }

    }

    private class GetPurse extends AsyncTask<String, Void, String> {

        Context context;

        ProgressDialog pDialog;
        ArrayList<Purse> purseArrayList;
        private String responseMessage = "";


        public GetPurse(Context context) {
            this.context = context;

            purseArrayList = new ArrayList<Purse>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(HomeScreenActivity.this);
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

                Log.d("response", jsonStr);

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

                                if (jsonObject1.getString("purseName").trim().equalsIgnoreCase("Cash")) {
                                    userInfoEditor.putString("walletbalance", jsonObject1.getString("purseBalance").trim());
                                    userInfoEditor.commit();
                                }
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

                AlertBuilder alertBuilder = new AlertBuilder(HomeScreenActivity.this);
                alertBuilder.showAlert(responseMessage);
            } else {
                AlertBuilder alertBuilder = new AlertBuilder(HomeScreenActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));
            }
        }

    }

    private class UpgradeWallet extends AsyncTask<String, Void, String> {

        Context context;

        String responseMessage = "";
        ProgressDialog pDialog;
        private String kycStatus = "", emailid = "";


        public UpgradeWallet(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HomeScreenActivity.this);
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
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());

                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

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

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {

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
                } else {
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

                if (kycStatus.trim().equalsIgnoreCase("REJECTED")) {
                    AlertBuilder alertBuilder = new AlertBuilder(HomeScreenActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(responseMessage);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            Intent i = new Intent(getApplicationContext(), KYCActivity.class);
                            i.putExtra("status", "REJECTED");
                            i.putExtra("email", emailid);
                            i.putExtra("kyclist", kycLists);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();


                } else {
                    AlertBuilder alertBuilder = new AlertBuilder(HomeScreenActivity.this);
                    alertBuilder.showAlert(responseMessage);
                }
            } else if (result.trim().equalsIgnoreCase("Failure")) {
                Intent i = new Intent(getApplicationContext(), KYCActivity.class);
                i.putExtra("status", "");
                i.putExtra("email", emailid);
                i.putExtra("kyclist", kycLists);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();
            }

        }


    }

}
//if (userInfoPref.getString("rate_us_status", "NOTDONE").trim().equalsIgnoreCase("NOTDONE")) {
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeScreenActivity.this);
//
//        // Setting Dialog Title
//        alertDialog.setTitle(getResources().getString(R.string.rate_title));
//        // Setting Dialog Message
//        alertDialog.setMessage(userInfoPref.getString("rate_message", getResources().getString(R.string.rate_explanation)));
//        // Setting Icon to Dialog
//        // alertDialog.setIcon(R.drawable.tick);
//        // Setting OK Button
//
//
//        alertDialog.setNegativeButton(getResources().getString(R.string.rate_now), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // Write your code here to execute after dialog closed
//                dialog.cancel();
//                userInfoEditor.putString("rate_us_status", "DONE");
//                userInfoEditor.commit();
//                final String appPackageName = getPackageName();
//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+ appPackageName)));
//                }
//
//                String rateURL = getResources().getString(R.string.rateus) + "?MDN=" + pref.getString("mobile_number", "") + "&rateStatus=RATE";
//                RateUs rateUs = new RateUs(getApplicationContext());
//                rateUs.execute(rateURL);
//
//            }
//        });
//
//        alertDialog.setPositiveButton(getResources().getString(R.string.rate_later), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // Write your code here to execute after dialog closed
//                dialog.cancel();
//                userInfoEditor.putString("rate_us_status", "DONE");
//                userInfoEditor.commit();
//                String rateURL = getResources().getString(R.string.rateus) + "?MDN=" + pref.getString("mobile_number", "") + "&rateStatus=LATER";
//                RateUs rateUs = new RateUs(getApplicationContext());
//                rateUs.execute(rateURL);
//
//
//            }
//        });
//        alertDialog.setNeutralButton(getResources().getString(R.string.rate_never), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // Write your code here to execute after dialog closed
//                dialog.cancel();
//
//                userInfoEditor.putString("rate_us_status", "DONE");
//                userInfoEditor.commit();
//
//                String rateURL = getResources().getString(R.string.rateus) + "?MDN=" + pref.getString("mobile_number", "") + "&rateStatus=NOTHANKS";
//                RateUs rateUs = new RateUs(getApplicationContext());
//                rateUs.execute(rateURL);
//
//            }
//        });
//
//
//        // Showing Alert Message
//        alertDialog.setCancelable(false);
//        alertDialog.show();
//
//
//    }


