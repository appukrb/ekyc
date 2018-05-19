package com.tcs.mmpl.customer.Goibibo.Bus;

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
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GoibiboBusCityListActivity extends Activity {


    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    String url;
    private ListView listCity;
    private EditText edtSearchCity;
    private   ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bus_city_list);

        url = getResources().getString(R.string.bus_city_url);

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

        listCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String cityName = ((TextView) view).getText().toString();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", cityName);
                setResult(RESULT_OK, returnIntent);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                finish();
            }
        });

        edtSearchCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                GoibiboBusCityListActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private class GetCity extends AsyncTask<String, Void, String> {

        Context context;
        ArrayList<String> cityName ;
        public GetCity(Context context) {
            this.context = context;
            cityName = new ArrayList<String>();


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboBusCityListActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(GoibiboBusCityListActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
                jsonStr = "{\"value\":"+jsonStr+"}";
                //// System.out.println(jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");

                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                cityName.add(obj.getString("city"));
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

               adapter=new ArrayAdapter<String> (getBaseContext(),
                        android.R.layout.simple_dropdown_item_1line,cityName);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

                listCity.setAdapter(adapter);



            } else if (result.equalsIgnoreCase("Failure")) {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        GoibiboBusCityListActivity.this).create();

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
                        GoibiboBusCityListActivity.this).create();

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


}
