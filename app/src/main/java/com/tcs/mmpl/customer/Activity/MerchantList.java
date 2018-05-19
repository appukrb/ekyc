package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.Adapter.MerchantNames;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MerchantList extends Activity {

    ListView listMerchant;
    ArrayList<String> merchantName,merchantNumber ;

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    String url,type;

    private EditText editsearch;

   // private  ArrayAdapter<String> adapter;

    private ListViewAdapter adapter;

    ArrayList<MerchantNames> arraylist = new ArrayList<MerchantNames>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_merchant_list);
        listMerchant = (ListView)findViewById(R.id.listMerchant);

        String url = getResources().getString(R.string.allMerchant);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        if(connectionDetector.isConnectingToInternet())
        {
            MerchantProvider merchantProvider = new MerchantProvider(getApplicationContext());
            merchantProvider.execute(url);
        }
        else
        {

            AlertDialog alertDialog = new AlertDialog.Builder(MerchantList.this).create();

            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.display_app_name));

            // Setting Dialog Message
            alertDialog.setMessage( getResources().getString(R.string.no_network));

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

        // Locate the EditText in listview_main.xml
        editsearch = (EditText) findViewById(R.id.search);

        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private class MerchantProvider extends AsyncTask<String, Void, String> {

        Context context;


        String firstName, lastName, walletBalance;

        public MerchantProvider(Context context) {
            this.context = context;
            merchantName = new ArrayList<String>();
            merchantNumber = new ArrayList<String>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MerchantList.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(MerchantList.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                jsonStr = "{ \"value\":"+jsonStr+"}";

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");

                        //// System.out.println("Length of jsonArray..............." + jsonArray.length());


                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = (JSONObject) jsonArray.get(i);

                                MerchantNames m = new MerchantNames(obj.getString("merchantName"),obj.getString("merchantNumber"));


                                merchantName.add(obj.getString("merchantName"));
                                merchantNumber.add(obj.getString("merchantNumber"));

                                arraylist.add(m);

                            }
                            return "Success";
                        }
                        else
                        {
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
                //Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();


                adapter = new ListViewAdapter(MerchantList.this, arraylist);
                

                // Binds the Adapter to the ListView
                listMerchant.setAdapter(adapter);

//                adapter =new ArrayAdapter<String> (getBaseContext(),
//                        android.R.layout.simple_dropdown_item_1line,merchantName);
//                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//
//                listMerchant.setAdapter(adapter);

                // txtOperatorDTH.setText(operatorName.get(0));
                // txtBrowsePlans.setText("Browse Plans\nof "+operatorName.get(0)+"\n"+stateName.get(0));


            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(MerchantList.this).create();

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

                AlertDialog alertDialog = new AlertDialog.Builder(MerchantList.this).create();

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
        private List<MerchantNames> merchantNamesList = null;
        private ArrayList<MerchantNames> arraylist;

        public ListViewAdapter(Context context,
                               List<MerchantNames> merchantNamesList) {
            mContext = context;
            this.merchantNamesList = merchantNamesList;
            inflater = LayoutInflater.from(mContext);
            this.arraylist = new ArrayList<MerchantNames>();
            this.arraylist.addAll(merchantNamesList);
        }

        public class ViewHolder {
            TextView txtMerchantName;

        }

        @Override
        public int getCount() {
            return merchantNamesList.size();
        }

        @Override
        public MerchantNames getItem(int position) {
            return merchantNamesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.listview_merchantitem, null);
                // Locate the TextViews in listview_item.xml
                holder.txtMerchantName = (TextView) view.findViewById(R.id.merchantName);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // Set the results into TextViews
            holder.txtMerchantName.setText(merchantNamesList.get(position).getMerchantName());



            // Listen for ListView Item Click
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // Send single item click data to SingleItemView Class
//                    Intent intent = new Intent(mContext, SingleItemView.class);
//                    // Pass all data rank
//                    intent.putExtra("rank",
//                            (worldpopulationlist.get(position).getRank()));
//                    // Pass all data country
//                    intent.putExtra("country",
//                            (worldpopulationlist.get(position).getCountry()));
//                    // Pass all data population
//                    intent.putExtra("population",
//                            (worldpopulationlist.get(position).getPopulation()));
//                    // Pass all data flag
//                    intent.putExtra("flag",
//                            (worldpopulationlist.get(position).getFlag()));
//                    // Start SingleItemView Class
//                    mContext.startActivity(intent);


                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("merchantNumber",merchantNamesList.get(position).getMerchantNumber());
                    returnIntent.putExtra("merchantName",merchantNamesList.get(position).getMerchantName());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });

            return view;
        }

        // Filter Class
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            merchantNamesList.clear();
            if (charText.length() == 0) {

                //// System.out.println(arraylist.size());
                merchantNamesList.addAll(arraylist);
            } else {
                for (MerchantNames wp : arraylist) {
                    if (wp.getMerchantName().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        merchantNamesList.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }


}
