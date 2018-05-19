package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ChooseOperatorMobile extends Activity {


    ListView listDTH;

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose_operator_mobile);
        listDTH = (ListView)findViewById(R.id.listDTH);
        url = getIntent().getStringExtra("url");

        connectionDetector = new ConnectionDetector(getApplicationContext());
        if(connectionDetector.isConnectingToInternet())
        {
            //String operatorurl = "http://nexgenfmpl.com/numberdemo/WebService/listUniqDthOperator?api_key=448e4b049ae3d31a8484e48f8c439b85644b4665";
            ChooseOperator getOperator = new ChooseOperator(getApplicationContext());
            getOperator.execute(url);
        }
        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();;
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


    private class ChooseOperator extends AsyncTask<String, Void, String> {

        Context context;

        ArrayList<String> operatorName;

        String firstName, lastName, walletBalance;

        public ChooseOperator(Context context) {
            this.context = context;
            operatorName = new ArrayList<String>();


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ChooseOperatorMobile.this);
            pDialog.setMessage("Loading Operator...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        String data = jsonMainObj.getString("data");

                        //// System.out.println("Length of jsonArray..............."+data);

                        String operator_names,new_operator_names;

                        JSONObject jsonMainObj1 = new JSONObject(data);

                            operator_names = jsonMainObj1.getString("operator_name");

                            new_operator_names = operator_names.replaceAll("\\[","").replaceAll("\\]","").replaceAll("\"", "");

                            //// System.out.println("New Operator Name................."+new_operator_names );

                            String[] splitArray = new_operator_names.split(",");

                            for(int i=0;i<splitArray.length;i++)
                            {

                                operatorName.add(splitArray[i]);
                            }

                            return "Success";





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

            } else if (result.equalsIgnoreCase("Failure")) {

                Toast.makeText(getApplicationContext(),getResources().getString(R.string.apidown),Toast.LENGTH_LONG).show();;

            } else {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }

        }

    }


}
