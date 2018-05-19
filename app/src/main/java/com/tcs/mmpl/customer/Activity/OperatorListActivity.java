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
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class OperatorListActivity extends Activity {

    ListView listDTH;

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_operatorlist);

        listDTH = (ListView)findViewById(R.id.listDTH);
        url = getIntent().getStringExtra("url");

        connectionDetector = new ConnectionDetector(getApplicationContext());
        if(connectionDetector.isConnectingToInternet())
        {
            //String operatorurl = "http://nexgenfmpl.com/numberdemo/WebService/listUniqDthOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665";
            GetOperator getOperator = new GetOperator(getApplicationContext());
            getOperator.execute(url);
        }
        else
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();;
        }

        listDTH.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String dthOperatorName = ((TextView)view).getText().toString();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",dthOperatorName);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
    }


    private class GetOperator extends AsyncTask<String, Void, String> {

        Context context;

        ArrayList<String> operatorName ;
        String firstName, lastName, walletBalance;

        public GetOperator(Context context) {
            this.context = context;
            operatorName = new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(OperatorListActivity.this);
            pDialog.setMessage("Loading Operator...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(OperatorListActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        String status_code = jsonMainObj.getString("status_code");

                        if(status_code.trim().equalsIgnoreCase("200")) {


                            JSONArray jsonArray = jsonMainObj.getJSONArray("data");

                            //// System.out.println("Length of jsonArray..............." + jsonArray.length());

                            String operator_names, new_operator_names;

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject obj = (JSONObject) jsonArray.get(i);
                                    operatorName.add(obj.getString("operator_name").toUpperCase());



                                }
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

                ArrayAdapter<String> adp=new ArrayAdapter<String> (getBaseContext(),
                        android.R.layout.simple_dropdown_item_1line,operatorName);
                adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

                listDTH.setAdapter(adp);

               // txtOperatorDTH.setText(operatorName.get(0));
                // txtBrowsePlans.setText("Browse Plans\nof "+operatorName.get(0)+"\n"+stateName.get(0));


            } else if (result.equalsIgnoreCase("Failure")) {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        OperatorListActivity.this).create();

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
                        OperatorListActivity.this).create();

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


}
