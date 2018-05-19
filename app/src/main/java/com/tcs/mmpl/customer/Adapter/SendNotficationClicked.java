package com.tcs.mmpl.customer.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.tcs.mmpl.customer.utility.WebServiceHandler;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hp on 2017-02-22.
 */

public class SendNotficationClicked extends AsyncTask<String, Void, String> {

    Context context;
    private ProgressDialog pDialog;
    private SharedPreferences pref;

    private String msg;

    public SendNotficationClicked(Context context, String msg) {
        this.context = context;
        this.msg = msg;
        pref = context.getSharedPreferences("userstatus", MODE_PRIVATE);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
    }

    @Override
    protected String doInBackground(String... arg0) {

        try {
            // 3. build jsonObject
//                JSONObject jsonObject = new JSONObject();
//
//                jsonObject.accumulate("MDN", pref.getString("mobile_number", ""));
//                jsonObject.accumulate("MSG", msg);
//
//
//                // 4. convert JSONObject to JSON to String
//                String json = jsonObject.toString();
//
//                // 5. set json to StringEntity
//                StringEntity se = new StringEntity(json);


            WebServiceHandler serviceHandler = new WebServiceHandler(context);
            String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

            //// System.out.println(arg0[0].toString());
            Log.d("notification click", jsonStr);


        } catch (Exception e) {
            e.printStackTrace();

        }

        return "success";
    }

    @Override
    protected void onPostExecute(String result) {


    }

}

