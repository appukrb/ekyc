package com.tcs.mmpl.customer.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.tcs.mmpl.customer.Activity.WebActivity;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

/**
 * Created by hp on 20-05-2016.
 */

public class GiftVoucher extends AsyncTask<String, Void, String> {

    private Context context;

    private ProgressDialog pDialog;
    private String res = "";

    public GiftVoucher(Context context) {
        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getResources().getString(R.string.loading));
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected String doInBackground(String... arg0) {

        try {


            WebServiceHandler serviceHandler = new WebServiceHandler(context);
            String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);





            if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                try {
                    res = jsonStr;

                        return "Success";

                    }

                 catch (Exception e) {
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

//            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

        if (result.equalsIgnoreCase("Success")) {

            Intent i = new Intent(context, WebActivity.class);
            i.putExtra("option", "LINK");
            i.putExtra("url", res);
            context.startActivity(i);




        } else if (result.equalsIgnoreCase("Failure")) {


            Toast.makeText(context, context.getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


        }
    }

}

