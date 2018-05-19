package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.HotelBhavan;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HotelLocationActivity extends Activity {


    private String merchantNumber = "", merchantName = "",city="";
    private ListView listHotel;
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_location);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        merchantName = getIntent().getStringExtra("merchantName");
        merchantNumber = getIntent().getStringExtra("merchantNumber");

        listHotel = (ListView) findViewById(R.id.listHotel);

        city = getIntent().getStringExtra("city");

        String url = getResources().getString(R.string.hotels_url)+"?city="+city+"&location=";

        connectionDetector = new ConnectionDetector(getApplicationContext());
        if (connectionDetector.isConnectingToInternet()) {
            HotelDetails hotelDetails = new HotelDetails(getApplicationContext());
            hotelDetails.execute(url);
        } else {

            AlertDialog alertDialog = new AlertDialog.Builder(HotelLocationActivity.this).create();

            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.display_app_name));

            // Setting Dialog Message
            alertDialog.setMessage(getResources().getString(R.string.no_network));

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

    private class HotelDetails extends AsyncTask<String, Void, String> {

        Context context;
        String firstName, lastName, walletBalance;
        private ProgressDialog pDialog;
        private ArrayList<HotelBhavan> hotelBhavans;

        public HotelDetails(Context context) {
            this.context = context;

            hotelBhavans = new ArrayList<>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HotelLocationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(HotelLocationActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                jsonStr = "{ \"value\":" + jsonStr + "}";

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");

                        //// System.out.println("Length of jsonArray..............." + jsonArray.length());


                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = (JSONObject) jsonArray.get(i);

                                HotelBhavan hotelBhavan = new HotelBhavan();
                                hotelBhavan.setShopName(obj.getString("shopName"));
                                hotelBhavan.setShopCode(obj.getString("shopCode"));

                                hotelBhavans.add(hotelBhavan);

                            }
                            return "Success";
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

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);

            if (result.equalsIgnoreCase("Success")) {


                ListViewAdapter adapter = new ListViewAdapter(HotelLocationActivity.this, hotelBhavans);
                // Binds the Adapter to the ListView
                listHotel.setAdapter(adapter);



            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(HotelLocationActivity.this).create();

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

                AlertDialog alertDialog = new AlertDialog.Builder(HotelLocationActivity.this).create();

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

            }

        }

    }


    public class ListViewAdapter extends BaseAdapter {

        // Declare Variables
        Context mContext;
        LayoutInflater inflater;

        private ArrayList<HotelBhavan> hotelBhavans;

        public ListViewAdapter(Context context,
                               ArrayList<HotelBhavan> hotelBhavans) {
            mContext = context;
        ;
            inflater = LayoutInflater.from(mContext);
            this.hotelBhavans = hotelBhavans;

        }

        @Override
        public int getCount() {
            return hotelBhavans.size();
        }

        @Override
        public HotelBhavan getItem(int position) {
            return hotelBhavans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.layout_hotel_location, null);
                // Locate the TextViews in listview_item.xml
                holder.txtHotelNames = (TextView) view.findViewById(R.id.txtHotelNames);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // Set the results into TextViews
            holder.txtHotelNames.setText(hotelBhavans.get(position).getShopName());


            // Listen for ListView Item Click
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Intent i = new Intent(mContext, HotelMerchantActivity.class);
                    i.putExtra("merchantNumber", merchantNumber);
                    i.putExtra("merchantName",merchantName);
                    i.putExtra("amount", "");
                    i.putExtra("shopcode", hotelBhavans.get(position).getShopCode());
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            });

            return view;
        }



        public class ViewHolder {
            TextView txtHotelNames;

        }

    }

}
