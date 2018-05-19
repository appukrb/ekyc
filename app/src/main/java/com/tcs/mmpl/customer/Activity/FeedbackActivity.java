package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class FeedbackActivity extends Activity {

    FontClass fontclass = new FontClass();
    Typeface typeface;

    RelativeLayout linParent;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    ConnectionDetector connectionDetector;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;

    Button feedback_btn_Cancel,feedback_btn_send;
    EditText feedback_txt;
    private ImageView dotimg;

    String responseMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        linParent = (RelativeLayout) findViewById(R.id.linParent);
        dotimg = (ImageView) findViewById(R.id.iddotimg);

        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        feedback_btn_send = (Button) findViewById(R.id.feedback_btn_send);
        feedback_btn_Cancel = (Button) findViewById(R.id.feedback_btn_Cancel);
        feedback_txt=(EditText)findViewById(R.id.feedback_txt);


        feedback_btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(feedback_txt.getText().toString().trim().equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(),"please enter feedback",Toast.LENGTH_LONG).show();
                }else{
                    String mobile_number=pref.getString("mobile_number", "");
                    String  Email= userInfoPref.getString("emailId", "");

                    //// System.out.println("email__________"+Email);
                    String Feedback= Uri.encode(feedback_txt.getText().toString().trim(),"utf-8");

                    String feedbackUrl = getResources().getString(R.string.feedback) + "?MDN=" + mobile_number + "&Email=" + Email + "&Feedback=" + Feedback ;
                    //// System.out.println(feedbackUrl);
                    FeedBack feedBack = new FeedBack(getApplicationContext());
                    feedBack.execute(feedbackUrl);
                }

            }
        });

        feedback_btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i =new Intent(getApplicationContext(),HomeScreenActivity.class);
                startActivity(i);
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
    private class FeedBack extends AsyncTask<String, Void, String> {
        Context context;
        ProgressDialog pDialog;


        public FeedBack(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FeedbackActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();



                WebServiceHandler serviceHandler = new WebServiceHandler(context);
                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());

                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {
                            responseMessage = jsonMainObj.getString("responseMessage");
                            return "Success";

                        }

                        else
                        {
                            responseMessage = jsonMainObj.getString("responseMessage");
                            return "Failure";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Failure";
                    }
                }
                else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failure";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }



        }

        protected void onPostExecute(final String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.d("Result::::", ">" + result);

            if (result.equalsIgnoreCase("Success"))
            {
//                String responseMessage = pref.getString("responseMessage","");
                Toast.makeText(getApplicationContext(),responseMessage,Toast.LENGTH_LONG).show();
                Intent i =new Intent(getApplicationContext(),HomeScreenActivity.class);
                startActivity(i);

            }
            else
            {
//                String responseMessage1 = pref.getString("responseMessage","");
                Toast.makeText(getApplicationContext(),responseMessage,Toast.LENGTH_LONG).show();
            }
        }


    }

//    class DrawerItemCustomAdapter extends BaseAdapter {
//        private Context context;
//        private List<ObjectDrawerItem> list;
//
//        public DrawerItemCustomAdapter(Context context, List<ObjectDrawerItem> list) {
//            this.context = context;
//            this.list = list;
//        }
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//
//            return list.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//
//            return i;
//        }
//
//
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            LayoutInflater inflater = getLayoutInflater();
//            View row;
//            row = inflater.inflate(R.layout.listview_item_row, parent, false);
//            TextView textViewName = (TextView) row.findViewById(R.id.textViewName);
//            Typeface custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
//            textViewName.setTypeface(custom_font);
//            final ObjectDrawerItem objectDrawerItem = list.get(position);
//            textViewName.setText(objectDrawerItem.getName());
//
//
//            return (row);
//        }
//    }


}