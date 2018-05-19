package com.tcs.mmpl.customer.Activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.WebServiceHandler;
import com.tcs.mmpl.customer.Adapter.BrowsePlanChild;
import com.tcs.mmpl.customer.Adapter.BrowsePlanMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class BrowsePlanTabActivity extends TabActivity {

    Intent genaralIntent;
    MyDBHelper dbHelper;
    Handler oHandler;
    TabHost tabHost;
    ProgressDialog pDialog;
    ArrayList<BrowsePlanMain> ClasA_list;
    SharedPreferences sharedPreferences, sharedPreferences1;
    Editor editor, editor1;


    String operator_name,state,mdn,category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browse_plan_tab);

        operator_name = getIntent().getStringExtra("operator_name");
        state = getIntent().getStringExtra("state");
        mdn = getIntent().getStringExtra("mdn");
        category = Uri.encode(getIntent().getStringExtra("category"),"utf-8");

        tabHost = getTabHost();
        ClasA_list= new ArrayList<BrowsePlanMain>();

        sharedPreferences = getApplicationContext().getSharedPreferences(
                "bestfit", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sharedPreferences1 = getApplicationContext().getSharedPreferences(
                "name", MODE_PRIVATE);
        editor1 = sharedPreferences1.edit();




        AddonDataASYNC addonDataASYNC = new AddonDataASYNC(BrowsePlanTabActivity.this);
        try
        {

            String url = getResources().getString(R.string.getMobileBrowsePlan_url)+"?Operator="+ Uri.encode(operator_name,"utf-8")+"&State="+ Uri.encode(state,"utf-8")+"&MDN="+mdn+"&Category="+category;

            //// System.out.println(url);
            addonDataASYNC.execute(url);
        }
        catch(Exception e)
        {

            AlertDialog alertDialog = new AlertDialog.Builder(
                    BrowsePlanTabActivity.this).create();

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
          //  Toast.makeText(BrowsePlanTabActivity.this, getResources().getString(R.string.apidown), Toast.LENGTH_SHORT).show();

        }

        tabHost.setOnTabChangedListener( new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                TextView tv=(TextView)tabHost.getCurrentTabView().findViewById(R.id.label);
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    ((TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.label)).setTextColor(getResources().getColor(R.color.black));
                }
                //for Selected Tab
                tv.setTextColor(Color.parseColor("#FFFFFF"));


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

    private View createTabIndicator(String label,int width,int size) {
        View tabIndicator = getLayoutInflater().inflate(R.layout.tabindicatornew, null);
        TextView tv = (TextView) tabIndicator.findViewById(R.id.label);
        if(width != 0)
        {
            if(size == 1)
            {
                tv.setWidth(width);
            }
            else if(size == 2)
            {
                tv.setWidth(width / 2);
            }
            else if(size == 3)
            {
                tv.setWidth(width / 3);
            }

            tv.setPadding(0,0,0,0);
        }

        tv.setText(label);
        return tabIndicator;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        finish();
    }

    private class AddonDataASYNC extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

        public AddonDataASYNC(Context context) {
            this.context =context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(BrowsePlanTabActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @SuppressLint("DefaultLocale")
        @Override
        protected String doInBackground(String... arg0) {

            // Creating service handler class instance
            WebServiceHandler serviceHandler=new WebServiceHandler(BrowsePlanTabActivity.this);
            // reg_url=reg_url+"?"+"MDN="+editText1.getText().toString()+"&Service_Type="+string_image_clicked+"&IMEI="+"0000000000000"+"&OS="+"Android"+"&Alternate_Number="+editText1.getText().toString();
            // Making a request to url and getting response
            String jsonStr;
            try
            {
                jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
                Log.d("Response: ", "> " + jsonStr);

            }
            catch(Exception e)
            {


                return "Failed";
            }

            if (jsonStr != null)
            {

                try
                {
                    JSONObject jsonMainObj = new JSONObject(jsonStr);
                    //// System.out.println(jsonMainObj);

                    if(jsonMainObj.getString("status").equalsIgnoreCase("SUCCESS")) {
                        JSONArray jsonArray = jsonMainObj.getJSONArray("getMobileBrowsePlan");
                        //// System.out.println("json arry length: " + jsonArray.length());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            BrowsePlanMain classA = new BrowsePlanMain();
                            ArrayList<BrowsePlanChild> ClsB_list = new ArrayList<BrowsePlanChild>();
                            JSONObject object = jsonArray.getJSONObject(i);
                            //// System.out.println(object.getString("category"));
                            classA.setSegName(object.getString("category"));
                            JSONArray jsonArray2 = object.getJSONArray("plan");
                            //// System.out.println("json arry2 length: " + jsonArray2.length());
                            for (int j = 0; j < jsonArray2.length(); j++) {
                                BrowsePlanChild classB = new BrowsePlanChild();
                                JSONObject object2 = jsonArray2.getJSONObject(j);
                                classB.setPackName(object2.getString("description"));
                                classB.setPackId(object2.getString("amount"));
                                ClsB_list.add(classB);
                                classB = null;
                            }
                            classA.setPackDetails(ClsB_list);
                            ClasA_list.add(classA);
                            classA = null;
                        }

                        return "SUCCESS";
                    }
                    else if(jsonMainObj.getString("status").equalsIgnoreCase("FAILURE"))
                    {
                        responseMessage = jsonMainObj.getString("responseMessage");

                        return "FAILURE";
                    }


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    return "Failure";
                }

            }
            else
            {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return null;
            }
            return "Success";
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            if(result.equalsIgnoreCase("Success"))
            {
                //// System.out.println("on post...");
                //// System.out.println("Size::::::::::"+ClasA_list.size());

                String mobileNumber="Mobile Number - "+sharedPreferences.getString("BESTFIT_MDN", "");

                SimpleDateFormat sdf =  new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                Date d= new Date(); //Get system date

                //Convert Date object to string
                String strDate = " Date - "+sdf.format(d);
                //// System.out.println("Formated String is " + strDate);

                EasyTracker tracker = EasyTracker.getInstance(getApplicationContext());
                tracker.set(Fields.customDimension(10),"Addon Packs : "+strDate);
                tracker.send(MapBuilder.createAppView().build());

                int width,height;
//                Display display = getWindowManager().getDefaultDisplay();
//                if(Build.VERSION.SDK_INT>=13)
//                {
//                    Point size = new Point();
//                    display.getSize(size);
//                      width = size.x;
//                      height = size.y;
//                }
//                else {
//
//                      width = display.getWidth(); // deprecated
//                      height = display.getHeight();  // deprecated
//                }

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                width = metrics.widthPixels;

                //// System.out.println("Width:::::::"+width);


                if(ClasA_list.size() == 1)
                {

                    for (int i = 0; i < ClasA_list.size(); i++) {
                        TabSpec smsspec = tabHost.newTabSpec(ClasA_list.get(i).getSegName());
                        smsspec.setIndicator(createTabIndicator(ClasA_list.get(i).getSegName(), width,1));
                        genaralIntent = new Intent(BrowsePlanTabActivity.this, BrowsePlanTabListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("selected_tab", ClasA_list.get(i).getSegName()).putExtra("dataObj", ClasA_list);
                        smsspec.setContent(genaralIntent);

                        tabHost.addTab(smsspec);
                    }

                }
                else if(ClasA_list.size() == 2)
                {

                    for (int i = 0; i < ClasA_list.size(); i++) {
                        TabSpec smsspec = tabHost.newTabSpec(ClasA_list.get(i).getSegName());
                        smsspec.setIndicator(createTabIndicator(ClasA_list.get(i).getSegName(), width,2));
                        genaralIntent = new Intent(BrowsePlanTabActivity.this, BrowsePlanTabListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("selected_tab", ClasA_list.get(i).getSegName()).putExtra("dataObj", ClasA_list);
                        smsspec.setContent(genaralIntent);

                        tabHost.addTab(smsspec);
                    }

                }
                else if(ClasA_list.size() == 3)
                {

                    for (int i = 0; i < ClasA_list.size(); i++) {
                        TabSpec smsspec = tabHost.newTabSpec(ClasA_list.get(i).getSegName());
                        smsspec.setIndicator(createTabIndicator(ClasA_list.get(i).getSegName(), width,3));
                        genaralIntent = new Intent(BrowsePlanTabActivity.this, BrowsePlanTabListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("selected_tab", ClasA_list.get(i).getSegName()).putExtra("dataObj", ClasA_list);
                        smsspec.setContent(genaralIntent);

                        tabHost.addTab(smsspec);
                    }

                }
                else {

                    for (int i = 0; i < ClasA_list.size(); i++) {
                        TabSpec smsspec = tabHost.newTabSpec(ClasA_list.get(i).getSegName());
                        smsspec.setIndicator(createTabIndicator(ClasA_list.get(i).getSegName(), 0,0));
                        genaralIntent = new Intent(BrowsePlanTabActivity.this, BrowsePlanTabListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("selected_tab", ClasA_list.get(i).getSegName()).putExtra("dataObj", ClasA_list);
                        smsspec.setContent(genaralIntent);

                        tabHost.addTab(smsspec);
                    }
                }


                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    ((TextView) tabHost.getTabWidget().getChildAt(i).findViewById(R.id.label)).setTextColor(getResources().getColor(R.color.black));

                }
                //for Selected Tab
                ((TextView) tabHost.getCurrentTabView().findViewById(R.id.label)).setTextColor(Color.parseColor("#FFFFFF"));
            }
            else if(result.equalsIgnoreCase("FAILURE"))
            {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        BrowsePlanTabActivity.this).create();

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
              //  Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();
            }
            else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        BrowsePlanTabActivity.this).create();

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
                //  Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();
            }

        }


    }


}