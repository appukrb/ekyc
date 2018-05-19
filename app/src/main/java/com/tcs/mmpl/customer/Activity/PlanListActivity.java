package com.tcs.mmpl.customer.Activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.tcs.mmpl.customer.Adapter.PlanListAdapter;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.WebServiceHandler;
import com.tcs.mmpl.customer.utility.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PlanListActivity extends ListActivity {



    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    String url;
    PlanListAdapter adapter;
    ArrayList<String> amount, desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);

        url = getIntent().getStringExtra("url");

        connectionDetector = new ConnectionDetector(getApplicationContext());
        if(connectionDetector.isConnectingToInternet())
        {
            //String operatorurl = "http://nexgenfmpl.com/numberdemo/WebService/listUniqDthOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665";
            GetPlans getPlans = new GetPlans(getApplicationContext());
            getPlans.execute(url);
        }
        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();;
        }


    }


    private class GetPlans extends AsyncTask<String, Void, String> {

        Context context;

        ArrayList<String> operatorName ;
        String firstName, lastName, walletBalance;

        public GetPlans(Context context) {
            this.context = context;
            operatorName = new ArrayList<String>();
            desc = new ArrayList<String>();
            amount = new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PlanListActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(PlanListActivity.this);
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
                final ListView lstView = getListView();

                lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                adapter = new PlanListAdapter(PlanListActivity.this,
                        R.layout.viewtransaction_row_layout,lstView, amount, desc);
                setListAdapter(adapter);


            } else if (result.equalsIgnoreCase("Failure")) {

                Toast.makeText(getApplicationContext(),getResources().getString(R.string.apidown),Toast.LENGTH_LONG).show();;

            } else {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }

        }

    }


}
