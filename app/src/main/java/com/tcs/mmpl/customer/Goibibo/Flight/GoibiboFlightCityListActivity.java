package com.tcs.mmpl.customer.Goibibo.Flight;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.AirportList;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class GoibiboFlightCityListActivity extends Activity {

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    String url;
    private ListView listCity;
    private EditText edtSearchCity;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_flight_city_list);
        url = getResources().getString(R.string.flight_airport_url);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        listCity = (ListView)findViewById(R.id.listCity);
        edtSearchCity = (EditText)findViewById(R.id.edtSearchCity);
        if(connectionDetector.isConnectingToInternet())
        {
            GetCity getCity = new GetCity(getApplicationContext());
            getCity.execute(url);
        }
        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();;
        }

//        listCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//            }
//        });

          }

    private class GetCity extends AsyncTask<String, Void, String> {

        Context context;

        ArrayList<AirportList> list;
        public GetCity(Context context) {
            this.context = context;

            list = new ArrayList<>();


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboFlightCityListActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(GoibiboFlightCityListActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
                jsonStr = "{\"value\":"+jsonStr+"}";
                //// System.out.println(jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");

                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                AirportList airportList = new AirportList();
                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                airportList.setCity(obj.getString("city"));
                                airportList.setCode(obj.getString("code"));

                                list.add(airportList);

                            }
                            return "Success";
                        }

                        else
                            return "Failure";


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

                adapter=new ListViewAdapter(getBaseContext(),list);
                listCity.setAdapter(adapter);


                edtSearchCity.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                        // When user changed the Text

                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        String text = edtSearchCity.getText().toString().toLowerCase(Locale.getDefault());
                        adapter.filter(text);
                    }
                });




            } else if (result.equalsIgnoreCase("Failure")) {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        GoibiboFlightCityListActivity.this).create();

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

                AlertDialog alertDialog = new AlertDialog.Builder(
                        GoibiboFlightCityListActivity.this).create();

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

    public class ListViewAdapter extends BaseAdapter {

        // Declare Variables
        Context mContext;
        LayoutInflater inflater;
        private ArrayList<AirportList> list = null;
        private ArrayList<AirportList> arraylist;


        public ListViewAdapter(Context context,
                               ArrayList<AirportList> list) {
            mContext = context;
            this.list = list;
            inflater = LayoutInflater.from(mContext);
            this.arraylist = new ArrayList<AirportList>();
            this.arraylist.addAll(list);

        }

        public class ViewHolder {
            TextView txtCity;
            TextView txtCode;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public AirportList getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.view_airport_layout, null);
                // Locate the TextViews in listview_item.xml
                holder.txtCity = (TextView) view.findViewById(R.id.txtCity);
                holder.txtCode = (TextView) view.findViewById(R.id.txtCode);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // Set the results into TextViews
            holder.txtCity.setText(list.get(position).getCity());
            holder.txtCode.setText(list.get(position).getCode());

            // Listen for ListView Item Click
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {


                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",list.get(position).getCode());
                    returnIntent.putExtra("city",list.get(position).getCity());
                    setResult(RESULT_OK, returnIntent);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);

                    finish();
                }
            });

            return view;
        }

        // Filter Class
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            list.clear();
            if (charText.length() == 0) {
                list.addAll(arraylist);
            } else {
                for (AirportList wp : arraylist) {
                    if (wp.getCity().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        list.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

}
