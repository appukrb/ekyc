package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.CouponDetails;
import com.tcs.mmpl.customer.utility.Deals;
import com.tcs.mmpl.customer.utility.GPSTracker;
import com.tcs.mmpl.customer.utility.RestaurantList;
import com.tcs.mmpl.customer.utility.SetNotificationCounter;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RessyActivity extends Activity {


    private ArrayList<RestaurantList> listitems = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private double latitude=1,longitude=1;
    private EditText edtSearch;
    private MyAdapter adapter;
    private Button btnCoupons;

    private ConnectionDetector connectionDetector;
    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;
    private TextView txtComingSoon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ressy);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        txtComingSoon = (TextView)findViewById(R.id.txtComingSoon);
        edtSearch = (EditText)findViewById(R.id.edtSearch);
        btnCoupons = (Button)findViewById(R.id.btnCoupons);
        edtSearch.addTextChangedListener(filterTextWatcher);


        connectionDetector = new ConnectionDetector(getApplicationContext());
        GPSTracker gps = new GPSTracker(this);

        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        }


        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(RessyActivity.this, FavoriteActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                        }
                                    }
        );


        //call HamburgerMenu
        new HamburgerMenu(this,(DrawerLayout) findViewById(R.id.drawer_layout),(ExpandableListView) findViewById(R.id.left_drawer),(ImageView) findViewById(R.id.idmenuimg)).setHamburger();


        String url = getResources().getString(R.string.getRestaurantList_url) + "?latittude=" +latitude +"&longitude="+longitude;

        GetRestaurantList getRestaurantList = new GetRestaurantList(getApplicationContext());
        getRestaurantList.execute(url);



    }

    public void openhome(View v)
    {
        Intent i = new Intent(RessyActivity.this, HomeScreenActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    public void openinbox(View v)
    {
        Intent s5 =new Intent(getApplicationContext(),AlertActivity.class);
        s5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(s5);

    }

    @Override
    public void onResume(){
        super.onResume();

        new SetNotificationCounter(getApplicationContext(), (TextView)findViewById(R.id.txtNotificationCounter));

    }

    public void myCoupons(View v)
    {
        if(connectionDetector.isConnectingToInternet())
        {
            String url = getResources().getString(R.string.getmycoupon_url)+"?MDN="+pref.getString("mobile_number","");
            GetMyCoupons getMyCoupons = new GetMyCoupons(getApplicationContext());
            getMyCoupons.execute(url);
        }
        else
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();
        }
    }

    private class GetMyCoupons extends AsyncTask<String, Void, String> {

        private Context context;
        private String responseMessage="";
        private ArrayList<CouponDetails> couponDetailsArrayList = new ArrayList<>();

        private ProgressDialog pDialog;

        public GetMyCoupons(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RessyActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(RessyActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                if(!jsonStr.trim().equalsIgnoreCase(""))
                {
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    if(jsonObject.getString("responseStatus").trim().equalsIgnoreCase("Success"))
                    {

                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for(int i=0;i<jsonArray.length();i++)
                        {

                            JSONObject jsonCoupon = jsonArray.getJSONObject(i);
                            CouponDetails couponDetails = new CouponDetails();
                            couponDetails.setCouponId(jsonCoupon.getString("couponId"));
                            couponDetails.setCouponstatus(jsonCoupon.getString("couponstatus"));
                            couponDetails.setConsumedDate(jsonCoupon.getString("consumedDate"));
                            couponDetails.setExpiryDate(jsonCoupon.getString("expiryDate"));
                            couponDetails.setRestName(jsonCoupon.getString("restName"));

                            couponDetailsArrayList.add(couponDetails);

                        }

                        return "Success";

                    }
                    else if(jsonObject.getString("responseStatus").trim().equalsIgnoreCase("Failure"))
                    {

                        responseMessage = jsonObject.getString("responseMessage");

                        return "Failure";

                    }
                    else
                    {
                        return "Failed";
                    }
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                e.printStackTrace();
                return "Failed";

            }


            return "Failed";
        }

        @Override
        protected void onPostExecute(String result) {

            if(pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {

                Intent intent = new Intent(getApplicationContext(),MyCouponsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("my_coupons", couponDetailsArrayList);
                startActivity(intent);


            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(RessyActivity.this).create();
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(responseMessage);
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

                AlertDialog alertDialog = new AlertDialog.Builder(RessyActivity.this).create();
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

            }

        }

    }

    private class GetRestaurantList extends AsyncTask<String, Void, String> {

        private Context context;
        private String respMessage="";

        public GetRestaurantList(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressBar.setVisibility(View.VISIBLE);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(RessyActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);
                listitems.clear();

                // Create a Pattern object
                Pattern r = Pattern.compile("[,]");
                // Now create matcher object.
                Matcher m;
                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if(jsonMainObj.getString("success").equalsIgnoreCase("true")) {


                            JSONArray jsonArray = jsonMainObj.getJSONArray("deals");

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    RestaurantList item = new RestaurantList();

                                    JSONObject obj = (JSONObject) jsonArray.get(i);
                                    item.setId(obj.getString("id"));
                                    JSONObject jsonObjectRestaurant = new JSONObject(obj.getString("restaurant"));

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
                                    ArrayList<String> menuCardList = new ArrayList<>();
                                    if (menu_card.equalsIgnoreCase("")) {

                                        menuCardList.add("NA");
                                        item.setMenuCardList(menuCardList);

                                    } else if (m.find()) {
                                        Log.i("menu_card", menu_card);
                                        String[] menu_cardArray = menu_card.split(",");

                                        for(int k = 0;k<menu_cardArray.length;k++) {
                                            String newmenu_card = menu_cardArray[k].replaceAll("\\\\", "");
                                            menuCardList.add(newmenu_card);
                                        }
                                        item.setMenuCardList(menuCardList);
                                    } else {

                                        menu_card = menu_card.replaceAll("\\\\", "");
                                        menuCardList.add(menu_card);

                                        item.setMenuCardList(menuCardList);
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


                                    item.setHotelName(jsonObjectRestaurant.getString("name"));
                                    JSONObject jsonObject = new JSONObject(jsonObjectRestaurant.getString("address"));
                                    item.setHotelAddress(jsonObject.getString("address"));
                                    item.setLocality(jsonObject.getString("locality"));
                                    item.setCity(jsonObject.getString("city"));


                                    Deals deals = new Deals();
                                    deals.setId(obj.getString("id"));
                                    deals.setRestaurant_id(obj.getString("restaurant_id"));
                                    deals.setLocality(obj.getString("locality"));
                                    deals.setCity(obj.getString("city"));
                                    deals.setTitle(obj.getString("title"));
                                    deals.setTerms_and_conditions(obj.getString("terms_and_conditions"));
                                    deals.setStart_time(obj.getString("start_time"));
                                    deals.setEnd_time(obj.getString("end_time"));
                                    deals.setNotes(obj.getString("notes"));


                                    item.setDeal(deals);
                                    listitems.add(item);

                                }
                                return "Success";
                            } else {
                                return "Failure";
                            }

                        }
                        else if(jsonMainObj.getString("success").equalsIgnoreCase("flag"))
                        {


                            respMessage =  jsonMainObj.getString("message");
                            return "flag";

                        }
                        else if(jsonMainObj.getString("success").equalsIgnoreCase("false"))
                        {

                            return jsonMainObj.getString("message");

                        }
                        else {
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
            progressBar.setVisibility(View.GONE);

            if(result.trim().equalsIgnoreCase("flag")) {

                txtComingSoon.setVisibility(View.VISIBLE);
                txtComingSoon.setText(respMessage);

            }
            else if (result.equalsIgnoreCase("Success")) {
                txtComingSoon.setVisibility(View.GONE);

                btnCoupons.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.VISIBLE);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager MyLayoutManager = new LinearLayoutManager(RessyActivity.this);
                MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                if (listitems.size() > 0 & recyclerView != null) {

                    adapter = new MyAdapter(listitems,context);
                    recyclerView.setAdapter(adapter);
                }
                recyclerView.setLayoutManager(MyLayoutManager);


            } else if (result.equalsIgnoreCase("Failure")) {
                txtComingSoon.setVisibility(View.GONE);


                AlertDialog alertDialog = new AlertDialog.Builder(RessyActivity.this).create();
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
                        finish();

                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            } else {
                txtComingSoon.setVisibility(View.GONE);

                AlertDialog alertDialog = new AlertDialog.Builder(RessyActivity.this).create();
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
                        finish();

                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            }

        }

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<RestaurantList> list = new ArrayList<>();
        private ArrayList<RestaurantList> filterList = new ArrayList<>();


        private Context context;

        public MyAdapter(ArrayList<RestaurantList> Data,Context context) {
            list = Data;
            this.context = context;
            filterList.addAll(list);

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_ressy, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.titleTextView.setText(filterList.get(position).getHotelName());
            holder.txtHotelAddress.setText(filterList.get(position).getHotelAddress());
            holder.txtHotelCuisine.setText(filterList.get(position).getHotelCuisine());
            Picasso.with(RessyActivity.this).load(filterList.get(position).getHotelImage()).error(R.drawable.restaurant_background).into(holder.coverImageView);

            holder.coverImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(getApplicationContext(), RestaurantDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("hotel_details", filterList.get(position));
                    startActivity(intent);

                    edtSearch.setText("");

                }
            });
//            holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
//            holder.coverImageView.setTag(list.get(position).getImageResourceId());

        }

        @Override
        public int getItemCount() {
//            return list.size();
            return (null != filterList ? filterList.size() : 0);
        }


        // Do Search...
        public void filter(final String text) {

            // Searching could be complex..so we will dispatch it to a different thread...
            new Thread(new Runnable() {
                @Override
                public void run() {

                    // Clear the filter list
                    filterList.clear();

                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {

                        filterList.addAll(list);

                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (RestaurantList item : list) {
                            if (item.getHotelName().toLowerCase().contains(text.toLowerCase()) ) {
                                // Adding Matched items
                                filterList.add(item);
                            }
                        }
                    }

                    // Set on UI Thread
                    (RessyActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Notify the List that the DataSet has changed...
                           adapter.notifyDataSetChanged();
                        }
                    });

                }
            }).start();

        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView, txtHotelAddress, txtHotelCuisine;
        public ImageView coverImageView;


        public MyViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            txtHotelAddress = (TextView) v.findViewById(R.id.txtHotelAddress);
            txtHotelCuisine = (TextView) v.findViewById(R.id.txtHotelCuisine);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);

        }
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {


            try {
                adapter.filter(s.toString());
            }
            catch (Exception e)
            {

            }
        }
    };

}

