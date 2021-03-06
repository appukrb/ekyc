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
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
import com.tcs.mmpl.customer.fragments.QuickTransfer;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.Contact;
import com.tcs.mmpl.customer.utility.CropOption;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.PhoneNumberVerifier;
import com.tcs.mmpl.customer.utility.PopupBuilder;
import com.tcs.mmpl.customer.utility.SetNotificationCounter;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HotelMerchantActivity extends Activity {
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int PICK_FROM_GALLERY = 4;
    private static final String TAG = QuickTransfer.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final int REQUEST_CODE_PICK_CONTACTS1 = 2;
    private static int RESULT_LOAD_IMG = 1;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    MyDBHelper dbHelper;
    EditText edtMerchantMobileNumber, edtMerchantAmount, edtMerchantAlternateMobileNumber, edtMerchantRemarks, edtMpin, edtMerchantAlternateName;
    TextView edtMerchantAlternateMobileNumber1;
    PhoneNumberVerifier.Countries country;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    ConnectionDetector connectionDetector;
    ImageView profileImage;
    LinearLayout linParent;
    private Uri mImageCaptureUri;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;
    private Uri uriContact;
    private String merchantNumber = "", amount = "", Isd_code = "+91", shopcode, merchantName;
    private int count = 10;
    private CheckBox chkFavMerchant;

    private String parameterType,parameterValue,URL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_merchant);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        linParent = (LinearLayout) findViewById(R.id.linParent);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);


        shopcode = getIntent().getStringExtra("shopcode");
        merchantNumber = getIntent().getStringExtra("merchantNumber");
        merchantName = getIntent().getStringExtra("merchantName");
        amount = getIntent().getStringExtra("amount");

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        profileImage = (ImageView) findViewById(R.id.imgUser);
        MyDBHelper dbHelper;
        dbHelper = new MyDBHelper(getApplicationContext());
        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

            if (userInfoPref.getString("profilepic", "").equalsIgnoreCase("")) {
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.dummy);
                profileImage.setImageBitmap(icon);
            } else {
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
        connectionDetector = new ConnectionDetector(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();
//
//        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
//        userInfoEditor = userInfoPref.edit();

        edtMerchantMobileNumber = (EditText) findViewById(R.id.edtMerchantMobileNumber);
        edtMerchantAmount = (EditText) findViewById(R.id.edtMerchantAmount);
        edtMerchantAlternateMobileNumber = (EditText) findViewById(R.id.edtMerchantAlternateMobileNumber);
//        edtMerchantRemarks = (EditText)findViewById(R.id.edtMerchantRemarks);
//        edtMerchantAlternateName = (EditText)findViewById(R.id.edtMerchantAlternateName);
        edtMerchantAlternateMobileNumber1 = (TextView) findViewById(R.id.edtMerchantAlternateMobileNumber1);
        chkFavMerchant = (CheckBox) findViewById(R.id.chkFavMerchant);
        edtMpin = (EditText) findViewById(R.id.edtMpin);

        edtMerchantMobileNumber.setText(merchantNumber);
        edtMerchantAlternateMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(count)});

        if (amount.equalsIgnoreCase("350")) {
            edtMerchantAmount.setText("350");
            edtMerchantAmount.setEnabled(false);
        } else {
            edtMerchantAmount.setEnabled(true);
        }


        edtMerchantAlternateMobileNumber.setOnTouchListener(new View.OnTouchListener() {
            // final int DRAWABLE_LEFT = 0;
            // final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;

            //  final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = edtMerchantAlternateMobileNumber.getRight()
                            - edtMerchantAlternateMobileNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    // when EditBox has padding, adjust leftEdge like
                    // leftEdgeOfRightDrawable -= getResources().getDimension(R.dimen.edittext_padding_left_right);
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        // clicked on clear icon
                        //edt_mobile.setText("123");

                        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS1);

                        return true;
                    }
                }
                return false;
            }
        });
        edtMerchantAlternateMobileNumber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupView = getLayoutInflater().inflate(R.layout.phonenumberverifier_popup, null);
                ArrayAdapter<PhoneNumberVerifier.Countries> adapter = new ArrayAdapter<PhoneNumberVerifier.Countries>(HotelMerchantActivity.this, android.R.layout.simple_dropdown_item_1line, PhoneNumberVerifier.Countries.values());
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

                ListView ListView1 = (ListView) popupView.findViewById(R.id.listview);
                ListView1.setAdapter(adapter);
                ListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        country = PhoneNumberVerifier.Countries.values()[i];
                        count = country.getAllowableToLength();
                        Isd_code = "+" + String.valueOf(country.getCountryCode());

                        edtMerchantAlternateMobileNumber1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(count)});
                        edtMerchantAlternateMobileNumber1.setText(Isd_code);
                        popupWindow.dismiss();
                    }
                });
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 20, 20);

            }
        });

        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(HotelMerchantActivity.this, FavoriteActivity.class);
                                            startActivity(i);
                                        }
                                    }
        );


        //call HamburgerMenu
        new HamburgerMenu(this, (DrawerLayout) findViewById(R.id.drawer_layout), (ExpandableListView) findViewById(R.id.left_drawer), (ImageView) findViewById(R.id.idmenuimg)).setHamburger();


        final ImageView dotimg = (ImageView) findViewById(R.id.iddotimg);
        dotimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(HotelMerchantActivity.this, dotimg);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //  Toast.makeText(HomeScreenActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(HotelMerchantActivity.this, WebActivity.class);
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
        } else if (requestCode == REQUEST_CODE_PICK_CONTACTS1 && resultCode == RESULT_OK) {
            uriContact = data.getData();
            retrieveAlternateContactNumber();
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

    public void hotelMerchantPayment(View v) {
        AlertBuilder alert = new AlertBuilder(HotelMerchantActivity.this);
        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
            if (connectionDetector.isConnectingToInternet()) {

                String mdn = pref.getString("mobile_number", "");
                String payeeMobile = "91" + edtMerchantMobileNumber.getText().toString().trim();
                String Amount = edtMerchantAmount.getText().toString().trim();
                String mpin = edtMpin.getText().toString().trim();
//                String remarks = Uri.encode(edtMerchantRemarks.getText().toString().trim(), "utf-8");
                String remarks = Uri.encode(merchantName, "utf-8");
//                String alternateNo = "91"+edtMerchantAlternateMobileNumber.getText().toString().trim();
                String alternateNo = "";
                String alternateName = shopcode;

                if (edtMerchantMobileNumber.getText().toString().trim().equalsIgnoreCase("") || edtMerchantMobileNumber.getText().toString().trim().length() < 10) {
                    alert.showAlert(getResources().getString(R.string.invalid_mobile));
                } else if (edtMerchantAmount.getText().toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_amount));
                }
//                else if(edtMerchantAlternateName.getText().toString().trim().equalsIgnoreCase(""))
//                {
//                    alert.showAlert(getResources().getString(R.string.invalid_merchant_alternate_name));
//                }
//                else if(edtMerchantAlternateMobileNumber.getText().toString().trim().equalsIgnoreCase("") || edtMerchantAlternateMobileNumber.getText().toString().trim().length()<10)
//                {
//                    alert.showAlert(getResources().getString(R.string.invalid_mobile));
//                }
                else if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_mpin));
                } else if (payeeMobile.trim().equalsIgnoreCase("") || mpin.trim().equalsIgnoreCase("") || Amount.trim().equalsIgnoreCase("")) {

                    alert.showAlert(getResources().getString(R.string.validation));

                } else {
                    String HotelMerchantActivityUrl = getResources().getString(R.string.hotels_merchant_payment_url) + "?mobileNo=" + mdn + "&payeeMobileNo=" + payeeMobile + "&Amount=" + Amount + "&Remark=" + remarks + "&alterMobileNo=" + alternateNo + "&nickName=" + alternateName + "&MPIN=" + mpin;


//                    // System.out.println(HotelMerchantActivityUrl);
                    CheckBalance checkBalance = new CheckBalance(HotelMerchantActivity.this);
                    if (checkBalance.getBalanceCheck(Amount)) {

                        PopupBuilder popup = new PopupBuilder(HotelMerchantActivity.this);
                        popup.showPopup(checkBalance.getAmount(Amount));
                        userInfoEditor.putString("transaction_url", HotelMerchantActivityUrl);
                        userInfoEditor.putString("transaction_method", "POST2");
                        userInfoEditor.putString("transaction_flag", "1");
                        userInfoEditor.commit();

                    } else {
                        parameterType = Uri.encode(merchantName,"utf-8");
                        parameterValue = mdn + "|" + Amount + "|" + edtMerchantMobileNumber.getText().toString().trim() + "|" + Uri.encode("Merchant Payment","utf-8") + "|" + "";
                        URL = getResources().getString(R.string.hotels_merchant_payment_url) + "?mobileNo=" + mdn + "&payeeMobileNo=" + payeeMobile + "&Amount=" + Amount + "&Remark=" + remarks + "&alterMobileNo=" + alternateNo + "&nickName=" + alternateName;



                        HotelMerchantActivityAsync HotelMerchantActivityAsync = new HotelMerchantActivityAsync(getApplicationContext());
                        HotelMerchantActivityAsync.execute(HotelMerchantActivityUrl);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }

        } else {
            alert.newUser();
        }


    }

    public void generateOTP(View v) {
        GenerateOTPAlert generateOTPAlert = new GenerateOTPAlert(this);
        generateOTPAlert.openAlert();

    }

    public void openAlert(View v) {
        Intent i = new Intent(HotelMerchantActivity.this, Shopping.class);
        startActivity(i);
    }

    public void openhome(View v) {
        Intent i = new Intent(HotelMerchantActivity.this, HomeScreenActivity.class);
        startActivity(i);
    }

    public void openinbox(View v) {
        Intent s5 = new Intent(getApplicationContext(), AlertActivity.class);
        startActivity(s5);

    }

    public void openMyAcount(View v) {
        Intent i = new Intent(HotelMerchantActivity.this, MyAcount.class);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();

        new SetNotificationCounter(getApplicationContext(), (TextView) findViewById(R.id.txtNotificationCounter));

    }

    private void openAlertDialog(AlertDialog.Builder alertDialog) {

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

                dialog.cancel();


                edtMerchantAmount.setText("");
//                edtMerchantAlternateMobileNumber.setText("");
//                edtMerchantAlternateName.setText("");
//                edtMerchantRemarks.setText("");
                edtMpin.setText("");
                chkFavMerchant.setChecked(false);


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

    private void retrieveAlternateContactNumber() {

        try {
            String id = uriContact.getLastPathSegment();

            Contact contact = new Contact(HotelMerchantActivity.this, edtMerchantAlternateMobileNumber, Isd_code, count);
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
//                edtMerchantAlternateMobileNumber.setText(contactNumber);
//            }
        } catch (NullPointerException ex) {


        } catch (Exception e) {
            // TODO: handle exception
        }


    }

    private void retrieveContactNumber() {

        try {
            String id = uriContact.getLastPathSegment();

            Contact contact = new Contact(HotelMerchantActivity.this, edtMerchantMobileNumber);
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
//                edtMerchantMobileNumber.setText(contactNumber);
//            }
        } catch (NullPointerException ex) {


        } catch (Exception e) {
            // TODO: handle exception
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

    private class HotelMerchantActivityAsync extends AsyncTask<String, Void, String> {

        Context context;

        String responseMessage;
        ProgressDialog pDialog;

        public HotelMerchantActivityAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HotelMerchantActivity.this);
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

//                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {
                            responseMessage = jsonMainObj.getString("responseMessage");

                            if (jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true")) {
                                new LocalNotification(HotelMerchantActivity.this, jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationTitle)), jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationMessage))).sendNotification();
                            }
                            return "Success";

                        } else {
                            responseMessage = jsonMainObj.getString("responseMessage");
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


            if (result.equalsIgnoreCase("Success")) {

                if(chkFavMerchant.isChecked())
                {
                    String addFavorites_url = getResources().getString(R.string.addfavourites);
                    AddFavorites addFavorites = new AddFavorites(HotelMerchantActivity.this);
                    addFavorites.execute(addFavorites_url);
                }

                AlertBuilder alert = new AlertBuilder(HotelMerchantActivity.this);
                AlertDialog.Builder alertDialog = alert.showRetryAlert(responseMessage + getResources().getString(R.string.make_payment));
                openAlertDialog(alertDialog);
            } else {
                AlertBuilder alert = new AlertBuilder(HotelMerchantActivity.this);
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
                jsonObject.accumulate("parameterType", parameterType);
                jsonObject.accumulate("prarameterValue", parameterValue);
                jsonObject.accumulate("URL", URL);

                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                // System.out.println("json: >>>>>>>>>>>" + json);

                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(HotelMerchantActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

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

        }

    }

}
