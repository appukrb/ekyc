package com.tcs.mmpl.customer.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.tcs.mmpl.customer.R.layout.popup_generate_otp;


/**
 * Created by hp on 06-06-2016.
 */
public class GenerateOTPAlert {

    private static final int MODE_PRIVATE = 1 ;
    private Context context;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public GenerateOTPAlert(Context context)
    {

        this.context = context;
        pref = context.getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();
    }

    public void openAlert() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(popup_generate_otp, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final EditText edtMpin = (EditText) popupView.findViewById(R.id.edittext_edit_popup);
        Button btnCancel = (Button)popupView.findViewById(R.id.button_pop_no);
        Button btnSubmit = (Button) popupView.findViewById(R.id.button_pop_yes);

        edtMpin.setTypeface(typeface);
        btnCancel.setTypeface(typeface);
        btnSubmit.setTypeface(typeface);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(context, context.getResources().getString(R.string.mpin), Toast.LENGTH_LONG).show();
                } else {
                    popupWindow.dismiss();
                    String generateOTPURL = context.getApplicationContext().getResources().getString(R.string.generateOTP) + "?MDN=" + pref.getString("mobile_number", "") +"&MPIN=" + edtMpin.getText().toString().trim();
                    GenerateOTP generateOTP = new GenerateOTP(context);
                    generateOTP.execute(generateOTPURL);
                }

            }
        });

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private class GenerateOTP extends AsyncTask<String, Void, String> {

        Context context;
        private ProgressDialog pDialog;
        public GenerateOTP(Context context) {
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

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {
                            return jsonMainObj.getString("responseMessage");

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            return jsonMainObj.getString("responseMessage");

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

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(context, context.getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

                AlertBuilder alert = new AlertBuilder(context);
                alert.showAlert(result);

            }

        }

    }

}
