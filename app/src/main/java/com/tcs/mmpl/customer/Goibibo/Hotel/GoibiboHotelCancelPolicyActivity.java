package com.tcs.mmpl.customer.Goibibo.Hotel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.Goibibo.Hotel.Pojo.RoomDetails;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;

import org.json.JSONObject;

public class GoibiboHotelCancelPolicyActivity extends Activity {

    private String query,hc,rtc,rpc,fwdp,ibp,offercode;

    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;

    private TextView txtCancelPolicy,txtHotelPolicy;

    private RoomDetails roomDetails;
    private LinearLayout linMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_hotel_cancel_policy);

        linMain = (LinearLayout)findViewById(R.id.linMain);

        txtCancelPolicy = (TextView)findViewById(R.id.txtCancelPolicy);
        txtHotelPolicy = (TextView)findViewById(R.id.txtHotelPolicy);

        roomDetails = (RoomDetails) getIntent().getSerializableExtra("room_details");
        GoibiboPref = getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();


        query = GoibiboPref.getString("hotel_query", "");
        hc = GoibiboPref.getString("hotel_code", "");
        rtc = Uri.encode(roomDetails.getRtc(),"utf-8");
        rpc = Uri.encode(roomDetails.getRpc(),"utf-8");
        fwdp = Uri.encode(roomDetails.getFwdp(),"utf-8");
        ibp = Uri.encode(roomDetails.getIbp(),"utf-8");
        offercode = Uri.encode(roomDetails.getOffercode(),"utf-8");

        String cancelPolicyUrl = "http://pp.goibibobusiness.com/api/hotels/b2b/get_hotel_cancel_policy/?query=" + query + "&hc="+hc+"&rtc="+rtc+"&rpc="+rpc+"&fwdp="+fwdp+"&ibp="+ibp+"&offercode="+offercode;

        //// System.out.println(cancelPolicyUrl);
        HotelCancelPolicy hotelCancelPolicy = new HotelCancelPolicy(getApplicationContext());
        hotelCancelPolicy.execute(cancelPolicyUrl);
    }

    public void closePolicy(View v)
    {
        finish();
    }

    private class HotelCancelPolicy extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;

        public HotelCancelPolicy(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboHotelCancelPolicyActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboHotelCancelPolicyActivity.this);
                String jsonStr = goibiboServiceHandler.makeServiceCall(arg0[0].toString(), GoibiboServiceHandler.POST2);
                return jsonStr;


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            try {



                JSONObject jsonObject = new JSONObject(result);


                if (jsonObject.getString("success").equalsIgnoreCase("true")) {

                    String data = jsonObject.getString("data");
                    JSONObject jsonData = new JSONObject(data);
                        linMain.setVisibility(View.VISIBLE);

                    String cancelPolicy = jsonData.getString("cancelPolicy");
                    cancelPolicy = cancelPolicy.substring(1,cancelPolicy.length()-1);



                    if(cancelPolicy.contains(","))
                    {
                        String[] splitCancel = cancelPolicy.split(",");
                        String strCancel = "";
                        for(int i= 0;i<splitCancel.length;i++)
                        {
                            strCancel += splitCancel[i].substring(1,splitCancel[i].length()-1) + "<br>";
                        }
                        txtCancelPolicy.setText(Html.fromHtml(strCancel));
                    }
                    else
                    {
                        txtCancelPolicy.setText(Html.fromHtml(cancelPolicy));
                    }

                    String hotelPolicy = jsonData.getString("hotelPolicy");
                    hotelPolicy = hotelPolicy.substring(1,hotelPolicy.length()-1);

                    if(hotelPolicy.contains(","))
                    {
                        String[] splitHotel = hotelPolicy.split(",");
                        String strHotel = "";
                        for(int i= 0;i<splitHotel.length;i++)
                        {
                            strHotel += splitHotel[i].substring(1,splitHotel[i].length()-1) + "<br>";
                        }
                        txtHotelPolicy.setText(Html.fromHtml(strHotel));
                    }
                    else
                    {
                        txtHotelPolicy.setText(Html.fromHtml(hotelPolicy));
                    }



                } else {

                    AlertBuilder alertBuilder = new AlertBuilder(GoibiboHotelCancelPolicyActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
            catch (Exception e)
            {

            }


        }

    }


}
