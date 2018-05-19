package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.HotelBhavan;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HPCLActivity extends Activity {

    ConnectionDetector connectionDetector;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;
    private TextView txtSelectCity, txtTransactionError;
    private ArrayList<String> city;
    private ArrayList<HotelBhavan> shops;
    private ProgressBar progressBar;
    private LinearLayout linAddress,linAddressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hpcl);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");

        LinearLayout linParent = (LinearLayout) findViewById(R.id.linParent);
        fontclass.setFont(linParent, typeface);

        linAddress = (LinearLayout) findViewById(R.id.linAddress);
        linAddressList= (LinearLayout) findViewById(R.id.linAddressList);
        txtSelectCity = (TextView) findViewById(R.id.txtSelectCity);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtTransactionError = (TextView) findViewById(R.id.txtTransactionError);


        connectionDetector = new ConnectionDetector(getApplicationContext());

//        if (connectionDetector.isConnectingToInternet()) {
//
//            GetCity getCity = new GetCity(getApplicationContext());
//            getCity.execute(getResources().getString(R.string.getHPCLCity_url));
//        } else {
//            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
//        }


    }


    private void showCity(ArrayList<String> city) {

        if (city.size() > 0) {

            final String[] value = city.toArray(new String[city.size()]);
            new CityListDialog(HPCLActivity.this,value,txtSelectCity);

        } else {
            if (connectionDetector.isConnectingToInternet()) {

                GetCity getCity = new GetCity(getApplicationContext());
                getCity.execute(getResources().getString(R.string.getHPCLCity_url));
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }
        }
    }


    public class CityListDialog extends Dialog implements View.OnClickListener {

        private ListView list;
        private EditText filterText = null;
        ArrayAdapter<String> adapter = null;
        private static final String TAG = "CityList";

        public CityListDialog(Context context, String[] cityList, final TextView textView) {
            super(context);


            // custom dialog
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.layout_citylist);
            dialog.setTitle("");

            filterText = (EditText)dialog.findViewById(R.id.EditBox);
            filterText.addTextChangedListener(filterTextWatcher);
            list = (ListView) dialog.findViewById(R.id.List);
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cityList);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    Log.d(TAG, "Selected Item is = " + (String)list.getItemAtPosition(position));
                    textView.setText((String)list.getItemAtPosition(position));
                    dialog.dismiss();

                    linAddressList.removeAllViews();
                    String url = getResources().getString(R.string.getHCPLShop_url) + "?city=" + Uri.encode((String)list.getItemAtPosition(position));
                    GetShopAddress getShopAddress = new GetShopAddress(getApplicationContext());
                    getShopAddress.execute(url);
                }
            });

            dialog.show();
        }
        @Override
        public void onClick(View v) {

        }
        private TextWatcher filterTextWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                adapter.getFilter().filter(s);
            }
        };
        @Override
        public void onStop(){
            filterText.removeTextChangedListener(filterTextWatcher);
        }}


    private class GetCity extends AsyncTask<String, Void, String> {

        private Context context;
        private ProgressDialog pDialog;

        public GetCity(Context context) {
            this.context = context;

            city = new ArrayList<String>();


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HPCLActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(HPCLActivity.this);
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

                                city.add(obj.getString("city"));
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
                e.printStackTrace();
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);

            if (result.equalsIgnoreCase("Success")) {
                //Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();

                txtSelectCity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCity(city);
                    }
                });


            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(HPCLActivity.this).create();

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

                AlertDialog alertDialog = new AlertDialog.Builder(HPCLActivity.this).create();

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

    private class GetShopAddress extends AsyncTask<String, Void, String> {

        Context context;
        ProgressDialog pDialog;


        public GetShopAddress(Context context) {
            this.context = context;
            shops = new ArrayList<HotelBhavan>();

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


                WebServiceHandler serviceHandler = new WebServiceHandler(HPCLActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                jsonStr = "{ \"value\":" + jsonStr + "}";

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");
                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                HotelBhavan hotelBhavan = new HotelBhavan();
                                hotelBhavan.setShopName(jsonObject.getString("shopName"));
                                hotelBhavan.setShopCode(jsonObject.getString("shopCode"));

                                shops.add(hotelBhavan);
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

            progressBar.setVisibility(View.GONE);


            if (result.equalsIgnoreCase("Success")) {

                try {

                    ((TextView) findViewById(R.id.txtListOfPumps)).setVisibility(View.VISIBLE);
                    linAddress.setVisibility(View.VISIBLE);

                    for (int i = 0; i < shops.size(); i++) {

                        final HotelBhavan hotelBhavan = (HotelBhavan) shops.get(i);
                        View row = LayoutInflater.from(HPCLActivity.this).inflate(R.layout.layout_shop_details, null, true);
                        final TextView txtShopAddress = (TextView) row.findViewById(R.id.txtShopAddress);

                        View v = (View) row.findViewById(R.id.view);

                        txtShopAddress.setText(hotelBhavan.getShopName());

                        if (i == shops.size() - 1)
                            v.setVisibility(View.GONE);

                        linAddressList.addView(row);
                    }


                } catch (Exception e) {

                }
            } else if (result.equalsIgnoreCase("Failure")) {

                try {


                    txtTransactionError.setVisibility(View.VISIBLE);
                    txtTransactionError.setText(getResources().getString(R.string.apidown));


                } catch (Exception e) {
                }

            } else {

                try {


                    txtTransactionError.setVisibility(View.VISIBLE);
                    txtTransactionError.setText(result);
                } catch (Exception e) {
                }
            }

        }

    }


}
