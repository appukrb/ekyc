package com.tcs.mmpl.customer.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Hamburger.HamburgerMenu;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.fragments.NewIssueFragment;
import com.tcs.mmpl.customer.fragments.PastIssuesFragment;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.CallJavascript;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.PagerSlidingTabStrip;
import com.tcs.mmpl.customer.utility.SetNotificationCounter;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class SelfHelpActivity extends FragmentActivity {
    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private LinearLayout linPagerSlidingTabStrip,linWebVIew;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_self_help);


        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        linPagerSlidingTabStrip = (LinearLayout)findViewById(R.id.linPagerSlidingTabStrip);
        linWebVIew = (LinearLayout)findViewById(R.id.linWebVIew);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);




        ImageView idfavimg = (ImageView) findViewById(R.id.imgFavorite);
        idfavimg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(getApplicationContext(), FavoriteActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                            startActivity(i);
                                        }
                                    }
        );

        //call HamburgerMenu
        new HamburgerMenu(this,(DrawerLayout) findViewById(R.id.drawer_layout),(ExpandableListView) findViewById(R.id.left_drawer),(ImageView) findViewById(R.id.idmenuimg)).setHamburger();


        GetSHMHTML getSHMHTML = new GetSHMHTML(getApplicationContext());
        getSHMHTML.execute(getResources().getString(R.string.shm_down_url));

    }

    @Override
    public void onResume(){
        super.onResume();

        new SetNotificationCounter(getApplicationContext(), (TextView)findViewById(R.id.txtNotificationCounter));

    }

    public void openhome(View v) {
        Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(i);
    }
    public void openinbox(View v) {
        Intent s5 =new Intent(getApplicationContext(),AlertActivity.class);
        s5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(s5);

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public String tabtitles[];


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabtitles = getResources().getStringArray(
                    R.array.helpCategory);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabtitles[position];
        }

        @Override
        public int getCount() {
            return tabtitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    return new NewIssueFragment();
                case 1:
                    return new PastIssuesFragment();



            }

            return null;

        }

    }


    private class GetSHMHTML extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

        //        String firstName, lastName, walletBalance;
        ProgressDialog pDialog;

        public GetSHMHTML(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressBar.setVisibility(View.VISIBLE);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);
        }

        @Override
        protected String doInBackground(String... arg0) {

            String jsonStr = "";

            try {

                WebServiceHandler serviceHandler = new WebServiceHandler(getApplication());
                jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

               try
               {
                   JSONObject jsonObject = new JSONObject(jsonStr);
                   if(jsonObject.getString("responseStatus").trim().equalsIgnoreCase("SUCCESS"))
                   {
                       return "Success";

                   }
                   else  if(jsonObject.getString("responseStatus").trim().equalsIgnoreCase("FAILURE"))
                   {
                       responseMessage = jsonObject.getString("responseMessage").trim();

                       return "Failure";

                   }
                   else
                   {
                       return "Failed";

                   }
               }
               catch (JSONException e)
               {
                   return "Failed";

               }
            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            
            try {
                if(result.trim().equalsIgnoreCase("Success"))
                {

                    progressBar.setVisibility(View.GONE);

                    linPagerSlidingTabStrip.setVisibility(View.VISIBLE);
                    linWebVIew.setVisibility(View.GONE);

                    PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
                    tabs.setTextColorResource(R.color.black);
                    Typeface custom_font = Typeface.createFromAsset(getApplication().getAssets(), "helvetica-bold.ttf");
                    tabs.setTypeface(custom_font,Typeface.NORMAL);
                    ViewPager pager = (ViewPager) findViewById(R.id.pager);
                    MyPagerAdapter adapter1 = new MyPagerAdapter(getSupportFragmentManager());

                    pager.setAdapter(adapter1);

                    final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                            getResources().getDisplayMetrics());
                    pager.setPageMargin(pageMargin);

                    tabs.setViewPager(pager);
                }
                else if(result.equalsIgnoreCase("Failure")) {

                    progressBar.setVisibility(View.GONE);

                    linPagerSlidingTabStrip.setVisibility(View.GONE);
                    linWebVIew.setVisibility(View.VISIBLE);


                    WebView wb = new WebView(getApplicationContext());
                    wb.addJavascriptInterface(new CallJavascript(SelfHelpActivity.this), "Android");
                    WebSettings webSettings = wb.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    wb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    wb.clearCache(true);

                    wb.loadUrl(responseMessage);
                    linWebVIew.removeAllViews();
                    linWebVIew.addView(wb);
                }
                else
                {
                    progressBar.setVisibility(View.GONE);

                    AlertBuilder alertBuilder = new AlertBuilder(SelfHelpActivity.this);
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
