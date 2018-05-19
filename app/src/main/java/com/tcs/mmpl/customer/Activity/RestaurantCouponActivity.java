package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Address;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.Deals;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.PopupBuilder;
import com.tcs.mmpl.customer.utility.RestaurantCouponList;
import com.tcs.mmpl.customer.utility.RestaurantInvoiceList;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestaurantCouponActivity extends Activity {

    FontClass fontclass = new FontClass();
    Typeface typeface;
    String expiryDate = "";
    private RestaurantCouponList restaurantCouponList;
    private LinearLayout linParent;
    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_coupon);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        linParent = (LinearLayout) findViewById(R.id.linParent);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);


        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        restaurantCouponList = (RestaurantCouponList) getIntent().getSerializableExtra("coupon_details");

        TextView txtHotelName = (TextView) findViewById(R.id.txtHotelName);
        TextView txtHotelAddress = (TextView) findViewById(R.id.txtHotelAddress);
        TextView txtOffer = (TextView) findViewById(R.id.txtOffer);
        TextView txtExpiryDate = (TextView) findViewById(R.id.txtExpiryDate);
        TextView txtCouponCode = (TextView) findViewById(R.id.txtCouponCode);

        txtHotelName.setText(restaurantCouponList.getHotelName());
        Address address = restaurantCouponList.getAddress();
        txtHotelAddress.setText(address.getAddress());
        Deals deals = restaurantCouponList.getDeal();
        txtOffer.setText(deals.getTitle());


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMM yyyy HH:mm");
        if (restaurantCouponList.getExpires_at().contains(".")) {
            String dateInString = restaurantCouponList.getExpires_at().split("\\.")[0].trim();

            try {

                Date date = formatter.parse(dateInString);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.HOUR_OF_DAY, 5);
                cal.add(Calendar.MINUTE, 5);

                date = formatter.parse(formatter.format(cal.getTime()));

                expiryDate = formatter1.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            expiryDate = restaurantCouponList.getExpires_at();
        }
        txtExpiryDate.setText(expiryDate);
        txtCouponCode.setText(Html.fromHtml("<b>" + restaurantCouponList.getCoupon_code() + "</b>"));

    }


    public void proceedOffer(View v) {
        LayoutInflater layoutInflater = (RestaurantCouponActivity.this).getLayoutInflater();
        View popupView = layoutInflater
                .inflate(R.layout.popup_favourite_mpin_layout, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        final EditText edtAmount = (EditText) popupView
                .findViewById(R.id.edtAmount);
        final EditText edtMpin = (EditText) popupView
                .findViewById(R.id.edittext_edit_popup);

        Button btnCancel = (Button) popupView.findViewById(R.id.button_pop_no);

        Button btnSubmit = (Button) popupView
                .findViewById(R.id.button_pop_yes);

        edtAmount.setTypeface(typeface);
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

                                             if (edtAmount.getText().toString().trim().equalsIgnoreCase("")) {
                                                 Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_amount), Toast.LENGTH_LONG).show();
                                             } else if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {
                                                 Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_mpin), Toast.LENGTH_LONG).show();
                                             } else {

                                                 String discount = restaurantCouponList.getDeal().getTitle().split("\\%")[0].trim();
                                                 String url = getResources().getString(R.string.ressyPayment_url) + "?MDN=" + pref.getString("mobile_number", "") + "&couponID=" + restaurantCouponList.getId() + "&amount=" + edtAmount.getText().toString().trim() + "&discount=" + discount + "&mPIN=" + edtMpin.getText().toString().trim();

                                                 String amount = edtAmount.getText().toString().trim();
                                                 popupWindow.dismiss();
                                                 CheckBalance checkBalance = new CheckBalance(RestaurantCouponActivity.this);
                                                 if (checkBalance.getBalanceCheck(amount)) {


                                                     PopupBuilder popup = new PopupBuilder(RestaurantCouponActivity.this);
                                                     popup.showPopup(checkBalance.getAmount(amount));

                                                     userInfoEditor.putString("transaction_url", url);
                                                     userInfoEditor.putString("transaction_method", "POST2");
                                                     userInfoEditor.putString("transaction_flag", "4");
                                                     userInfoEditor.putString("coupon_expiryDate", expiryDate);

                                                     userInfoEditor.commit();


                                                 } else {
                                                     //// System.out.println(url);
                                                     RessyPayment ressyPayment = new RessyPayment(getApplicationContext());
                                                     ressyPayment.execute(url);
                                                 }
                                             }

                                         }
                                     }

        );

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    public void cancelOffer(View v) {


        String cancel_coupon_url = getResources().getString(R.string.cancel_coupon_url)+"?couponID="+restaurantCouponList.getId();
        CancelCoupon cancelCoupon = new CancelCoupon(getApplicationContext());
        cancelCoupon.execute(cancel_coupon_url);
    }

    private class RessyPayment extends AsyncTask<String, Void, String> {

        RestaurantInvoiceList item = new RestaurantInvoiceList();
        private Context context;
        private ProgressDialog pDialog;

        public RessyPayment(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RestaurantCouponActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(RestaurantCouponActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);


                // Create a Pattern object
                Pattern r = Pattern.compile("[,]");
                // Now create matcher object.
                Matcher m;
                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("success").equalsIgnoreCase("true")) {


                            JSONObject obj = new JSONObject(jsonMainObj.getString("invoice"));
                            JSONObject jsonObjectRestaurant = new JSONObject(obj.getString("restaurant"));
                            item.setHotelName(jsonObjectRestaurant.getString("name"));
                            item.setTotal_bill(obj.getString("total_bill"));
                            item.setDiscount(obj.getString("discount"));
                            item.setNet_paid(obj.getString("net_paid"));
                            item.setTransaction_id(obj.getString("transaction_id"));
                            item.setPaid_time(obj.getString("paid_time"));


                            JSONObject jsonObject = new JSONObject(jsonObjectRestaurant.getString("address"));
                            Address address = new Address();
                            address.setAddress(jsonObject.getString("address"));
                            address.setLocality(jsonObject.getString("locality"));
                            address.setCity(jsonObject.getString("city"));
                            address.setLatitude(jsonObject.getString("latitude"));
                            address.setLongitude(jsonObject.getString("longitude"));
                            item.setAddress(address);


                            JSONObject jsonObjectCoupon = new JSONObject(obj.getString("coupon"));
                            item.setCoupon_code(jsonObjectCoupon.getString("coupon_code"));
                            item.setState(jsonObjectCoupon.getString("state"));

                            JSONObject jsonObjectDeals = new JSONObject(jsonObjectCoupon.getString("deal"));
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


            if (pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {

                finish();

                Intent intent = new Intent(getApplicationContext(), RestaurantInvoiceActivity.class);
                intent.putExtra("invoice_details", item);
                intent.putExtra("expiry_date", expiryDate);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(RestaurantCouponActivity.this).create();
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

                AlertDialog alertDialog = new AlertDialog.Builder(RestaurantCouponActivity.this).create();
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

    private class CancelCoupon extends AsyncTask<String, Void, String> {

        RestaurantInvoiceList item = new RestaurantInvoiceList();
        private Context context;
        private ProgressDialog pDialog;

        public CancelCoupon(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RestaurantCouponActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(RestaurantCouponActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);


                // Create a Pattern object
                Pattern r = Pattern.compile("[,]");
                // Now create matcher object.
                Matcher m;
                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("success").equalsIgnoreCase("true")) {


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


            if (pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {

                finish();


            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(RestaurantCouponActivity.this).create();
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

                AlertDialog alertDialog = new AlertDialog.Builder(RestaurantCouponActivity.this).create();
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
