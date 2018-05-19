package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Address;
import com.tcs.mmpl.customer.utility.Deals;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.RestaurantCouponList;
import com.tcs.mmpl.customer.utility.RestaurantList;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestaurantDetailsActivity extends Activity implements ViewPager.OnPageChangeListener{


    private RestaurantList restaurantList;
    private ImageView coverImageView;
    private TextView txtAddress, txtCuisine, txtAvailOffer, txtOffer, txtCall;
    private LinearLayout linDeals;
    private Deals deals;

    FontClass fontclass = new FontClass();
    Typeface typeface;
    private LinearLayout linParent;


    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;






    protected View view;

    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;
//    private int[] mImageResources = {
//            R.drawable.ic_launcher,
//            R.drawable.ic_info,
//            R.drawable.next,
//            R.drawable.previous,
//            R.drawable.call
//    };

    private ArrayList<String> menuCardList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        linParent = (LinearLayout) findViewById(R.id.linParent);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        restaurantList = (RestaurantList) getIntent().getSerializableExtra("hotel_details");
        deals = restaurantList.getDeal();
        menuCardList=restaurantList.getMenuCardList();

        linDeals = (LinearLayout) findViewById(R.id.linDeals);

        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtCuisine = (TextView) findViewById(R.id.txtCuisine);
        txtAvailOffer = (TextView) findViewById(R.id.txtAvailOffer);
        txtOffer = (TextView) findViewById(R.id.txtOffer);
        txtCall = (TextView) findViewById(R.id.txtCall);

        coverImageView = (ImageView) findViewById(R.id.coverImageView);

        Picasso.with(RestaurantDetailsActivity.this).load(restaurantList.getHotelImage()).error(R.drawable.restaurant_background).into(coverImageView);
        txtAddress.setText(restaurantList.getHotelName() + "\n" + restaurantList.getHotelAddress() + "\n" + restaurantList.getLocality() + "\n" + restaurantList.getCity());
        txtCuisine.setText(restaurantList.getHotelCuisine());
        txtCall.setText(restaurantList.getPhone());

        if (!deals.getTitle().trim().equalsIgnoreCase("")) {
            linDeals.setVisibility(View.VISIBLE);
            txtOffer.setText(deals.getTitle());
        } else
            linDeals.setVisibility(View.GONE);


    }

    public void openMenu1(View v) {
        // custom dialog
//        final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
//        dialog.setContentView(R.layout.layout_menucard);
//        dialog.setTitle("");
//
//
////        int gallery_grid_Images[]={R.drawable.ic_info,R.drawable.ic_launcher
////        };
////
////        final ViewFlipper viewFlipper = (ViewFlipper) dialog.findViewById(R.id.flipper);
////        for(int i=0;i<gallery_grid_Images.length;i++)
////        {
////
////            ImageView image = new ImageView(getApplicationContext());
////            image.setBackgroundResource(gallery_grid_Images[i]);
////            viewFlipper.addView(image);
////        }
////        viewFlipper.startFlipping();
////        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////
////                switch (event.getAction()) {
////
////                    case MotionEvent.ACTION_DOWN:
////                        viewFlipper.showNext();
////                        break;
////
////                    case MotionEvent.ACTION_UP:
////                        viewFlipper.showPrevious();
////                        break;
////
////                }
////                return false;
////            }
////        });
//
//        ImageView imgMenu = (ImageView) dialog.findViewById(R.id.imgMenu);
//        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
//
//        Picasso.with(RestaurantDetailsActivity.this).load(restaurantList.getMenuCard()).into(imgMenu);
//
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//        dialog.show();
    }

    public void openMenu(View v)
    {

        if(menuCardList.size() == 1)
        {
            if(menuCardList.get(0).equalsIgnoreCase("NA"))
            {
                Toast.makeText(getApplicationContext(),"Menu is not available",Toast.LENGTH_LONG).show();
            }
            else
            {
                setReference();

            }
        }
        else
        {
            setReference();

        }

    }


    public void setReference() {

        final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
        dialog.setContentView(R.layout.layout_menucard);
        dialog.setTitle("");


        intro_images = (ViewPager) dialog.findViewById(R.id.pager_introduction);

        pager_indicator = (LinearLayout) dialog.findViewById(R.id.viewPagerCountDots);
//
//        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
//
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });

        mAdapter = new ViewPagerAdapter(RestaurantDetailsActivity.this, menuCardList);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        setUiPageViewController();

        dialog.show();
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        ArrayList<String> menuCardList;

        public ViewPagerAdapter(Context mContext, ArrayList<String> menuCardList) {
            this.mContext = mContext;
            this.menuCardList = menuCardList;
        }

        @Override
        public int getCount() {
            return menuCardList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item, container, false);

            ImageView imgMenu = (ImageView) itemView.findViewById(R.id.img_pager_item);

                    Picasso.with(RestaurantDetailsActivity.this).load(menuCardList.get(position)).into(imgMenu);


            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }


    public void openDial(View v) {

        if (!txtCall.getText().toString().trim().equalsIgnoreCase("-") && !txtCall.getText().toString().trim().equalsIgnoreCase("NA")) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + restaurantList.getPhone()));
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Contact number is not available",Toast.LENGTH_LONG).show();

        }
    }

    public void openAvailOffer(View v) {

        String url = getResources().getString(R.string.getCouponDetail_url) + "?MDN=" + pref.getString("mobile_number", "") + "&dealID=" + restaurantList.getId() + "&emailID=" + userInfoPref.getString("emailId", "");
        GetCoupons getCoupons = new GetCoupons(getApplicationContext());
        getCoupons.execute(url);
    }


    private class GetCoupons extends AsyncTask<String, Void, String> {

        private Context context;
        private ProgressDialog pDialog;
        RestaurantCouponList item = new RestaurantCouponList();

        public GetCoupons(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RestaurantDetailsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(RestaurantDetailsActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);


                // Create a Pattern object
                Pattern r = Pattern.compile("[,]");
                // Now create matcher object.
                Matcher m;
                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("success").equalsIgnoreCase("true")) {



                            JSONObject obj = new JSONObject(jsonMainObj.getString("coupon"));
                            item.setId(obj.getString("id"));
                            JSONObject jsonObjectRestaurant = new JSONObject(obj.getString("restaurant"));
                            item.setHotelName(jsonObjectRestaurant.getString("name"));
                            String photos = jsonObjectRestaurant.getString("photos");
                            photos = photos.substring(1, photos.length() - 1);
                            photos = photos.replaceAll("\"", "");
                            photos = photos.trim();

                            m = r.matcher(photos);
                            if (photos.equalsIgnoreCase("")) {
                                item.setHotelImage("NA");
                            } else if (m.find()) {
                                Log.i("photo", photos);
                                String[] photoArray = photos.split(",");
                                String newPhoto = photoArray[0].replaceAll("\\\\", "");
                                item.setHotelImage(newPhoto);
                            } else {
                                photos = photos.replaceAll("\\\\", "");
                                item.setHotelImage(photos);
                            }


                            String menu_card = jsonObjectRestaurant.getString("menu_card");
                            menu_card = menu_card.substring(1, menu_card.length() - 1);
                            menu_card = menu_card.replaceAll("\"", "");
                            menu_card = menu_card.trim();
                            m = r.matcher(menu_card);
                            if (menu_card.equalsIgnoreCase("")) {
                                item.setMenuCard("NA");
                            } else if (m.find()) {
                                Log.i("menu_card", menu_card);
                                String[] menu_cardArray = menu_card.split(",");
                                String newmenu_card = menu_cardArray[0].replaceAll("\\\\", "");
                                item.setMenuCard(newmenu_card);
                            } else {

                                menu_card = menu_card.replaceAll("\\\\", "");
                                item.setMenuCard(menu_card);
                            }

                            String phone = jsonObjectRestaurant.getString("phone");
                            phone = phone.substring(1, phone.length() - 1);
                            phone = phone.replaceAll("\"", "");
                            phone = phone.trim();
                            m = r.matcher(phone);
                            if (phone.equalsIgnoreCase("")) {
                                item.setPhone("NA");
                            } else if (m.find()) {
                                String[] phoneArray = phone.split(",");
                                item.setPhone(phoneArray[0].trim());
                            } else {
                                item.setPhone(phone.trim());
                            }

                            String cuisines = jsonObjectRestaurant.getString("cuisines");
                            cuisines = cuisines.substring(1, cuisines.length() - 1);
                            cuisines = cuisines.replaceAll("\"", "");
                            cuisines = cuisines.trim();
                            item.setHotelCuisine(cuisines);



                            JSONObject jsonObject = new JSONObject(jsonObjectRestaurant.getString("address"));
                            Address address = new Address();
                            address.setAddress(jsonObject.getString("address"));
                            address.setLocality(jsonObject.getString("locality"));
                            address.setCity(jsonObject.getString("city"));
                            address.setLatitude(jsonObject.getString("latitude"));
                            address.setLongitude(jsonObject.getString("longitude"));
                            item.setAddress(address);


                            JSONObject jsonObjectDeals = new JSONObject(obj.getString("deal"));
                            Deals deals = new Deals();
                            deals.setId(jsonObjectDeals.getString("id"));
                            deals.setRestaurant_id(jsonObjectDeals.getString("restaurant_id"));
                            deals.setLocality(jsonObjectDeals.getString("locality"));
                            deals.setCity(jsonObjectDeals.getString("city"));
                            deals.setTitle(jsonObjectDeals.getString("title"));
                            deals.setTerms_and_conditions(jsonObjectDeals.getString("terms_and_conditions"));
                            deals.setStart_time(jsonObjectDeals.getString("start_time"));
                            deals.setEnd_time(jsonObjectDeals.getString("end_time"));
                            deals.setNotes(jsonObjectDeals.getString("notes"));

                            item.setDeal(deals);

                            item.setCoupon_code(obj.getString("coupon_code"));
                            item.setState(obj.getString("state"));
                            item.setExpires_at(obj.getString("expires_at"));

                            return "Success";


                        } else if (jsonMainObj.getString("success").equalsIgnoreCase("false")) {
                            return jsonMainObj.getString("message");
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
                e.printStackTrace();
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {


            if(pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {


                finish();
                Intent intent = new Intent(getApplicationContext(),RestaurantCouponActivity.class);
                intent.putExtra("coupon_details",item);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(RestaurantDetailsActivity.this).create();
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();


                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(RestaurantDetailsActivity.this).create();
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(result);
                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();


                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            }

        }

    }

}
