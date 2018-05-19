package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Address;
import com.tcs.mmpl.customer.utility.CouponDetails;
import com.tcs.mmpl.customer.utility.Deals;
import com.tcs.mmpl.customer.utility.RestaurantCouponList;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCouponsActivity extends Activity {

    private ArrayList<CouponDetails> couponDetailsArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupons);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        couponDetailsArrayList = (ArrayList<CouponDetails>) getIntent().getSerializableExtra("my_coupons");


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(MyCouponsActivity.this);
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (couponDetailsArrayList.size() > 0 & recyclerView != null) {

            CouponAdapter adapter = new CouponAdapter(couponDetailsArrayList,getApplicationContext());
            recyclerView.setAdapter(adapter);
        }
        recyclerView.setLayoutManager(MyLayoutManager);


    }
    public class CouponAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<CouponDetails> list = new ArrayList<>();


        private Context context;

        public CouponAdapter(ArrayList<CouponDetails> Data,Context context) {
            list = Data;
            this.context = context;


        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_my_coupons, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.txtHotelName.setText(list.get(position).getRestName());
            holder.txtConsumedDate.setText("Consumed on "+list.get(position).getConsumedDate());
            holder.txtExpriyDate.setText("Expires on "+list.get(position).getExpiryDate());

            holder.linMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String url = getResources().getString(R.string.getOfflineCouponDetail_url) + "?couponID=" + list.get(position).getCouponId();


                    GetCoupons getCoupons = new GetCoupons(getApplicationContext());
                    getCoupons.execute(url);
                }
            });
//            holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
//            holder.coverImageView.setTag(list.get(position).getImageResourceId());

        }

        @Override
        public int getItemCount() {
//            return list.size();
            return (null != list ? list.size() : 0);
        }

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtHotelName, txtExpriyDate, txtConsumedDate;
        public LinearLayout linMain;


        public MyViewHolder(View v) {
            super(v);
            txtHotelName = (TextView) v.findViewById(R.id.txtHotelName);
            txtConsumedDate = (TextView) v.findViewById(R.id.txtConsumedDate);
            txtExpriyDate = (TextView) v.findViewById(R.id.txtExpiryDate);
            linMain= (LinearLayout)v.findViewById(R.id.linMain);

        }
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
            pDialog = new ProgressDialog(MyCouponsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(MyCouponsActivity.this);
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


                AlertDialog alertDialog = new AlertDialog.Builder(MyCouponsActivity.this).create();
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

                AlertDialog alertDialog = new AlertDialog.Builder(MyCouponsActivity.this).create();
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
