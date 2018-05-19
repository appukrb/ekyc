package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.Adapter.ObjectDrawerItem;
import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.fragments.QuickTransfer;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.GenerateOTPAlert;
import com.tcs.mmpl.customer.utility.MyDBHelper;

import java.util.ArrayList;
import java.util.List;


public class SBCTerms extends Activity {
    FontClass fontclass = new FontClass();
    Typeface typeface;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<ObjectDrawerItem> list;



    private static final String TAG = QuickTransfer.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;

    private SharedPreferences pref,userInfoPref;
    private SharedPreferences.Editor editor,userInfoEditor;
    private ConnectionDetector connectionDetector;
    private String merchantNumber="",merchantName="";

    LinearLayout linParent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sbcterms);

        linParent = (LinearLayout) findViewById(R.id.linParent);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();


        MyDBHelper dbHelper = new MyDBHelper(getApplicationContext());
        ImageView profileImage = (ImageView)findViewById(R.id.imgUser);
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

//        edtMerchantMobileNumber.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                txtMerchantName.setText("");
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        edtMerchantMobileNumber.setOnTouchListener(new View.OnTouchListener() {
//            // final int DRAWABLE_LEFT = 0;
//            // final int DRAWABLE_TOP = 1;
//            final int DRAWABLE_RIGHT = 2;
//
//            //  final int DRAWABLE_BOTTOM = 3;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    int leftEdgeOfRightDrawable = edtMerchantMobileNumber.getRight()
//                            - edtMerchantMobileNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
//                    // when EditBox has padding, adjust leftEdge like
//                    // leftEdgeOfRightDrawable -= getResources().getDimension(R.dimen.edittext_padding_left_right);
//                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
//
//                        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
//
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });


        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(SBCTerms.this, FavoriteActivity.class);
                                            startActivity(i);
                                        }
                                    }
        );


        //call HamburgerMenu
        new HamburgerMenu(this,(DrawerLayout) findViewById(R.id.drawer_layout),(ExpandableListView) findViewById(R.id.left_drawer),(ImageView) findViewById(R.id.idmenuimg)).setHamburger();



        final ImageView  dotimg = (ImageView) findViewById(R.id.iddotimg);
        dotimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(SBCTerms.this, dotimg);

                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //  Toast.makeText(HomeScreenActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(SBCTerms.this, WebActivity.class);
                        if (item.getTitle().toString().equalsIgnoreCase("FAQs")) {
                            i.putExtra("option", "FAQ");
                            startActivity(i);

                        } else if (item.getTitle().toString().equalsIgnoreCase("My Account")) {
                            Intent intent = new Intent(getApplicationContext(), MyAcount.class);
                            startActivity(intent);

                        } else if (item.getTitle().toString().equalsIgnoreCase("Offers")) {
                            Intent intent = new Intent(getApplicationContext(), OffersActivity.class);
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


        LinearLayout linWebVIew = (LinearLayout)findViewById(R.id.linWebVIew);

        WebView wb = new WebView(getApplicationContext());
        wb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            wb.loadUrl("file:///android_asset/" + "sbcterms.html");
            linWebVIew.addView(wb);

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


    public void generateOTP(View v)
    {
        GenerateOTPAlert generateOTPAlert = new GenerateOTPAlert(this);
        generateOTPAlert.openAlert();

    }
    public void openAlert(View v)
    {
        Intent i = new Intent(SBCTerms.this, Shopping.class);
        startActivity(i);
    }


    public void openOffers(View v)
    {
            Intent i = new Intent(SBCTerms.this, OffersActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
    }

}
