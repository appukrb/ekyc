package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.StoresData;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreActivity extends Activity {

    StoresData storesData;
    ArrayList<StoresData> datas;
    private GoogleMap googleMap;
    private ListView listStores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        listStores = (ListView) findViewById(R.id.listStores);

        String brandstore_url = getResources().getString(R.string.brand_store_url) + "?accessToken=" + Uri.encode(getIntent().getStringExtra("accesstoken"), "utf-8") + "&hash=" + Uri.encode(getIntent().getStringExtra("hash"), "utf-8");

        //// System.out.println(brandstore_url);
        BrandStore brandStore = new BrandStore(getApplicationContext());
        brandStore.execute(brandstore_url);
    }


    private class BrandStore extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;


        public BrandStore(Context context) {
            this.context = context;

            datas = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StoreActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler webServiceHandler = new WebServiceHandler(StoreActivity.this);
                String jsonStr = webServiceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                //// System.out.println("Deno::::" + jsonStr);
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

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("stores");
                if (jsonArray.length() > 0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        if (jsonObject1.getString("latitude").equalsIgnoreCase("null") || jsonObject1.getString("longitude").equalsIgnoreCase("null")) {

                        } else {
                            StoresData storesData = new StoresData();
                            storesData.setAddressLine1(jsonObject1.getString("addressLine1"));
                            storesData.setLatitude(jsonObject1.getString("latitude"));
                            storesData.setLongitude(jsonObject1.getString("longitude"));
                            storesData.setCity(jsonObject1.getString("city"));
                            storesData.setState(jsonObject1.getString("state"));

                            storesData.setCountry(jsonObject1.getString("country"));
                            storesData.setPin(jsonObject1.getString("pin"));


                            datas.add(storesData);
                        }

                    }

                    ((TextView)findViewById(R.id.txtStores)).setVisibility(View.GONE);

                    listStores.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    StoreListAdapter adapter = new StoreListAdapter(StoreActivity.this, R.layout.stores_row_layout, listStores, datas);
                    listStores.setAdapter(adapter);

                } else {


                    ((TextView)findViewById(R.id.txtStores)).setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {

                e.printStackTrace();

            }


        }

    }

    public class StoreListAdapter extends ArrayAdapter<String> {

        private static final int MODE_PRIVATE = 0;
        Context context;
        ListView listView;
        int layout_id;

        ArrayList<StoresData> hotelSearchDetailsArrayList;

        public StoreListAdapter(Context context, int resource,
                                ListView lstView, ArrayList<StoresData> hotelSearchDetailsArrayList) {
            super(context, resource);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.layout_id = resource;
            this.listView = lstView;
            this.hotelSearchDetailsArrayList = hotelSearchDetailsArrayList;


        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return hotelSearchDetailsArrayList.size();
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            try {


                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View rowView = inflater.inflate(layout_id, null, true);


                ImageView imgLocation = (ImageView) rowView.findViewById(R.id.imgLocation);
                TextView txtAddress = (TextView) rowView.findViewById(R.id.txtAddress);
                TextView txtCity = (TextView) rowView.findViewById(R.id.txtCity);
                TextView txtState = (TextView) rowView.findViewById(R.id.txtState);


                txtAddress.setText(hotelSearchDetailsArrayList.get(position).getAddressLine1().trim());
                txtCity.setText(hotelSearchDetailsArrayList.get(position).getCity());
                txtState.setText(hotelSearchDetailsArrayList.get(position).getState() + "-" + hotelSearchDetailsArrayList.get(position).getPin());


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(getApplicationContext(), StoreMapActivity.class);
                        intent.putExtra("lat", hotelSearchDetailsArrayList.get(position).getLatitude());
                        intent.putExtra("log", hotelSearchDetailsArrayList.get(position).getLongitude());
                        intent.putExtra("address", hotelSearchDetailsArrayList.get(position).getAddressLine1());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                    }
                });

                imgLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), StoreMapActivity.class);
                        intent.putExtra("lat", hotelSearchDetailsArrayList.get(position).getLatitude());
                        intent.putExtra("log", hotelSearchDetailsArrayList.get(position).getLongitude());
                        intent.putExtra("address", hotelSearchDetailsArrayList.get(position).getAddressLine1());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                });


                return rowView;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                return null;
            }
        }

    }

}
