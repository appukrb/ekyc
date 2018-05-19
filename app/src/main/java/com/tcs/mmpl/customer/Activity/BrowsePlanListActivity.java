package com.tcs.mmpl.customer.Activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.tcs.mmpl.customer.Adapter.BrowsePlanListAdapter;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BrowsePlanListActivity extends ListActivity {

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browse_plan_list);
        url = getIntent().getStringExtra("url");

        connectionDetector = new ConnectionDetector(getApplicationContext());
        if(connectionDetector.isConnectingToInternet())
        {
            //String operatorurl = "http://nexgenfmpl.com/numberdemo/WebService/listUniqDthOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665";
            BrowsePlan browsePlan = new BrowsePlan(getApplicationContext());
            browsePlan.execute(url);
        }
        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();;
        }


    }


    private class BrowsePlan extends AsyncTask<String, Void, String> {

        Context context;

        ArrayList<String> amount,description,validity ;
        String firstName, lastName, walletBalance;
        private String responseMessage;

        public BrowsePlan(Context context) {
            this.context = context;
            amount = new ArrayList<String>();
            description = new ArrayList<String>();
            validity = new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(BrowsePlanListActivity.this);
            pDialog.setMessage("Loading Operator...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(BrowsePlanListActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        String responseStatus = jsonMainObj.getString("responseStatus");

                        if(responseStatus.trim().equalsIgnoreCase("SUCCESS")) {


                            JSONArray jsonArray = jsonMainObj.getJSONArray("data");



                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject obj = (JSONObject) jsonArray.get(i);
                                    amount.add(obj.getString("amount").toUpperCase());
                                    description.add(obj.getString("description").toUpperCase());
                                   // validity.add(obj.getString("validity").toUpperCase());



                                }
                            }

                            return "Success";
                        }
                        else if(responseStatus.trim().equalsIgnoreCase("FAILURE"))
                        {
                            responseMessage = jsonMainObj.getString("responseMessage");
                            return "Failure";
                        }
                        else
                        {
                            return "Failed";
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failed";
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failed";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failed";
            }



        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);

            if (result.equalsIgnoreCase("Success")) {
                //Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();

//                ArrayAdapter<String> adp=new ArrayAdapter<String> (getBaseContext(),
//                        android.R.layout.simple_dropdown_item_1line,operatorName);
//                adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//
//                listDTH.setAdapter(adp);

                ListView lstView = getListView();

                lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                BrowsePlanListAdapter adapter = new BrowsePlanListAdapter(BrowsePlanListActivity.this,
                        R.layout.browse_plan_layout,lstView, amount, description,validity);
                setListAdapter(adapter);


                // txtOperatorDTH.setText(operatorName.get(0));
                // txtBrowsePlans.setText("Browse Plans\nof "+operatorName.get(0)+"\n"+stateName.get(0));


            } else if (result.equalsIgnoreCase("Failure")) {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        BrowsePlanListActivity.this).create();

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
                        finish();
                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

               // Toast.makeText(getApplicationContext(),getResources().getString(R.string.apidown),Toast.LENGTH_LONG).show();;

            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        BrowsePlanListActivity.this).create();

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
