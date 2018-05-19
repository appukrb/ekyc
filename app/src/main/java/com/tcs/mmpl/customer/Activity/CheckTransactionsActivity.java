package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.Adapter.TransactionAdapter;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.PagerSlidingTabStrip;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.tcs.mmpl.customer.R.layout.popup_mpin_layout;


public class CheckTransactionsActivity extends FragmentActivity {

    private static final int MODE_PRIVATE = 0 ;
    FontClass fontclass=new FontClass();
    Typeface typeface;

    private RadioGroup RadioGroupTwo;
    private RadioButton rbPrepaid, rbPostpaid, rbCheck, rbToday, rbYesterday, rbStatement, rbMini,rbLastFive,radioTransactionHistory;

    Integer pos1,pos2,total;
    // int pos1,pos2,total;
    private Button button;
    String buttonSelected;
    private ViewFlipper viewFlipper_checktransactions;

    Activity activity;
    ListView lstView;
    //TransactionAdapter adapter;
    // private Button btn_confirm;
    //  private RadioButton mini_statement;.


    TransactionAdapter adapter;
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;

    LinearLayout mainlinear,linEmail;
    EditText edtEmailID,edtFromDate,edtToDate;


    private SimpleDateFormat dateFormatter;
    private DatePickerDialog fromDatePickerDialog,toDatePickerDialog;
    private Calendar cal;
    private int day;
    private int month;
    private int year;

    TabHost tabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_transactions);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();


        mainlinear = (LinearLayout) findViewById(R.id.mainlinear);
        linEmail = (LinearLayout)findViewById(R.id.linEmail);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);
        button = (Button)findViewById(R.id.idsubmitbutton);
        edtEmailID = (EditText)findViewById(R.id.edtEmailID);

        RadioGroupTwo = (RadioGroup)findViewById(R.id.group2);
        rbToday = (RadioButton)findViewById(R.id.idToday);
        rbYesterday = (RadioButton)findViewById(R.id.idYesterday);
        rbStatement = (RadioButton)findViewById(R.id.idstatement);
        rbMini = (RadioButton)findViewById(R.id.idministatement);
        rbLastFive = (RadioButton)findViewById(R.id.idlastfive);
        radioTransactionHistory = (RadioButton)findViewById(R.id.radioTransactionHistory);
        edtFromDate = (EditText)findViewById(R.id.edtFromDate);
        edtToDate = (EditText)findViewById(R.id.edtToDate);

        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        Calendar newCalendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtFromDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtToDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        rbStatement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    linEmail.setVisibility(View.VISIBLE);
                    edtEmailID.setText(userInfoPref.getString("emailId", ""));
                }
                else
                    linEmail.setVisibility(View.GONE);

//                if(isChecked) {
//                    if (userInfoPref.getString("emailId", "").trim().equalsIgnoreCase(""))
//                        linEmail.setVisibility(View.VISIBLE);
//                    else
//                        linEmail.setVisibility(View.GONE);
//                }
//                else
//                    linEmail.setVisibility(View.GONE);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                    int selectedId2 = RadioGroupTwo.getCheckedRadioButtonId();
                    RadioButton radioButton2 = (RadioButton) findViewById(selectedId2);
                    //  //// System.out.println("selected_2=" + radioButton2.getText());
                    pos2 = RadioGroupTwo.indexOfChild(radioButton2);
                    String transurl = "";

                    if (pos2.equals(0)) {
                        transurl = getApplicationContext().getResources().getString(R.string.transactionDetails) + "?MDN=" + pref.getString("mobile_number", "") + "&userType=C&toDate=Today&fromDate=Today";
                    } else if (pos2.equals(1)) {
                        transurl = getApplicationContext().getResources().getString(R.string.transactionDetails) + "?MDN=" + pref.getString("mobile_number", "") + "&userType=C&toDate=Yesterday&fromDate=Yesterday";
                    } else if (pos2.equals(2)) {
                        transurl = getApplicationContext().getResources().getString(R.string.account_statement) + "?MDN=" + pref.getString("mobile_number", "");
                    }

                    // String transurl = " https://app.mrupee.in:8443/mRupeeServiceNew/getAllTranscationDetail?MDN=" + pref.getString("mobile_number", "") + "&userType=C&toDate=24/09/2015&fromDate=10/09/2015";
                    if (connectionDetector.isConnectingToInternet()) {

                        if (pos2.equals(3)) {

//                            boolean flag=false;
//                            if (!userInfoPref.getString("emailId", "").trim().equalsIgnoreCase(""))
//                                flag = android.util.Patterns.EMAIL_ADDRESS.matcher(userInfoPref.getString("emailId", "").trim()).matches();
//                            else
//                                flag = android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmailID.getText().toString().trim()).matches();

                            boolean flag = android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmailID.getText().toString().trim()).matches();
                            if (flag == true) {

                                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                                View popupView = layoutInflater
                                        .inflate(popup_mpin_layout, null);
                                final PopupWindow popupWindow = new PopupWindow(popupView,
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                final EditText edtMpin = (EditText) popupView
                                        .findViewById(R.id.edittext_edit_popup);


                                Button btnCancel = (Button) popupView.findViewById(R.id.button_pop_no);

                                Button btnSubmit = (Button) popupView
                                        .findViewById(R.id.button_pop_yes);

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
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.mpin), Toast.LENGTH_LONG).show();
                                        } else {
                                            popupWindow.dismiss();
                                            String getDetailStatementURL = getApplicationContext().getResources().getString(R.string.detailStatement) + "?MDN=" + pref.getString("mobile_number", "") + "&emailId=" + edtEmailID.getText().toString().trim() + "&Mpin=" + edtMpin.getText().toString().trim() + "&fromDate=" + edtFromDate.getText().toString().trim() + "&toDate=" + edtToDate.getText().toString().trim();
                                            GetDetailStatement getDetailStatement = new GetDetailStatement(getApplicationContext());
                                            getDetailStatement.execute(getDetailStatementURL);
                                        }

                                    }
                                });

                                popupWindow.setOutsideTouchable(false);
                                popupWindow.setFocusable(true);
                                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                            } else {
                                AlertBuilder alert = new AlertBuilder(CheckTransactionsActivity.this);
                                alert.showAlert(getResources().getString(R.string.invalid_email));
                            }
                        } else if (pos2.equals(4)) {

                            transurl = getApplicationContext().getResources().getString(R.string.account_statement) + "?MDN=" + pref.getString("mobile_number", "");
                            GetTransaction trans = new GetTransaction(getApplicationContext());
                            trans.execute(transurl);

//                            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
//                                    .getSystemService(LAYOUT_INFLATER_SERVICE);
//                            View popupView = layoutInflater
//                                    .inflate(popup_mpin_layout, null);
//                            final PopupWindow popupWindow = new PopupWindow(popupView,
//                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                            final EditText edtMpin = (EditText) popupView
//                                    .findViewById(R.id.edittext_edit_popup);
//
//
//                            Button btnCancel = (Button)popupView.findViewById(R.id.button_pop_no);
//
//                            Button btnSubmit = (Button) popupView
//                                    .findViewById(R.id.button_pop_yes);
//
//                            edtMpin.setTypeface(typeface);
//                            btnCancel.setTypeface(typeface);
//                            btnSubmit.setTypeface(typeface);
//
//                            btnCancel.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    popupWindow.dismiss();
//                                }
//                            });
//
//                            btnSubmit.setOnClickListener(new Button.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {
//                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.mpin),Toast.LENGTH_LONG).show();
//                                    } else {
//                                        popupWindow.dismiss();
//                                        String statementURL = getApplicationContext().getResources().getString(R.string.ministatement) + "?MDN=" + pref.getString("mobile_number", "") + "&accountNo="+userInfoPref.getString("account","") + "&Mpin=" + edtMpin.getText().toString().trim();
//                                        GetTransaction trans = new GetTransaction(getApplicationContext());
//                                        trans.execute(statementURL);
//                                    }
//
//                                }
//                            });
//
//                            popupWindow.setOutsideTouchable(false);
//                            popupWindow.setFocusable(true);
//                            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        } else if (pos2.equals(2)) {
                            GetAccountStatement getAccountStatement = new GetAccountStatement(getApplicationContext());
                            getAccountStatement.execute(transurl);
                        } else {
                            GetTransaction trans = new GetTransaction(getApplicationContext());
                            trans.execute(transurl);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }


                } else {
                    AlertBuilder alert = new AlertBuilder(CheckTransactionsActivity.this);
                    alert.newUser();

                }
            }
        });



        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColorResource(R.color.black);
        Typeface custom_font = Typeface.createFromAsset(getApplication().getAssets(), "helvetica-bold.ttf");

        tabs.setTypeface(custom_font, Typeface.NORMAL);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter adapter1 = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter1);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);

    }
    private View createTabIndicator(String label) {
        View tabIndicator = getLayoutInflater().inflate(R.layout.tabindicator, null);
        TextView tv = (TextView) tabIndicator.findViewById(R.id.label);
        tv.setText(label);
        return tabIndicator;
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

    public void openFromDate(View v)
    {
        fromDatePickerDialog.show();
    }

    public void openToDate(View v)
    {
        toDatePickerDialog.show();
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {


            edtFromDate.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
                    + selectedYear);
            edtToDate.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
                    + selectedYear);
        }
    };

    private class GetTransaction extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;
        ArrayList<String> desc,amount,date,month,day,year,type;

        public GetTransaction(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CheckTransactionsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(CheckTransactionsActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            JSONArray jsonArray = jsonMainObj
                                    .getJSONArray("transctionDetaillist");
                            //// System.out.println("I am here>>>>>>>>"+ jsonArray.length());

                            desc =new ArrayList<String>();
                            amount = new ArrayList<String>();
                            date = new ArrayList<String>();
                            day = new ArrayList<String>();
                            month = new ArrayList<String>();
                            year = new ArrayList<String>();
                            type = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject j1 = jsonArray.getJSONObject(i);
                                //// System.out.println("Category..............."+j1.getString("category"));
                                desc.add(j1.getString("category"));
                                amount.add(j1.getString("amount"));
                                date.add(j1.getString("date"));
                                day.add(j1.getString("day"));
                                month.add(j1.getString("month"));
                                year.add(j1.getString("year"));
                                type.add(j1.getString("type"));
                            }

                            return "Success";

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

            if (result.equalsIgnoreCase("Success")) {

                Intent i = new Intent(getApplicationContext(),ViewTransactionActivity.class);
                i.putExtra("amount",amount);
                i.putExtra("desc",desc);
                i.putExtra("date",date);
                i.putExtra("day",day);
                i.putExtra("month",month);
                i.putExtra("year",year);
                i.putExtra("type",type);

                startActivity(i);


            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {


                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }

        }

    }

    private class GetAccountStatement extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;
        ArrayList<String> desc,amount,date,month,day,year,type;

        public GetAccountStatement(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CheckTransactionsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(CheckTransactionsActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            JSONArray jsonArray = jsonMainObj
                                    .getJSONArray("transctionDetaillist");
                            //// System.out.println("I am here>>>>>>>>"+ jsonArray.length());

                            desc =new ArrayList<String>();
                            amount = new ArrayList<String>();
                            date = new ArrayList<String>();
                            day = new ArrayList<String>();
                            month = new ArrayList<String>();
                            year = new ArrayList<String>();
                            type = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject j1 = jsonArray.getJSONObject(i);
                                //// System.out.println("Category..............."+j1.getString("category"));
                                desc.add(j1.getString("category"));
                                amount.add(j1.getString("amount"));
                                date.add(j1.getString("date"));
                                day.add(j1.getString("day"));
                                month.add(j1.getString("month"));
                                year.add(j1.getString("year"));

                            }

                            return "Success";

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

            if (result.equalsIgnoreCase("Success")) {

                Intent i = new Intent(getApplicationContext(),ViewTransactionActivity.class);
                i.putExtra("amount",amount);
                i.putExtra("desc",desc);
                i.putExtra("date",date);
                i.putExtra("day",day);
                i.putExtra("month",month);
                i.putExtra("year",year);
                i.putExtra("type",type);


                startActivity(i);


            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {


                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }

        }

    }


    private class GetDetailStatement extends AsyncTask<String, Void, String> {

        Context context;
        public GetDetailStatement(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CheckTransactionsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(CheckTransactionsActivity.this);
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


                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

                AlertBuilder alert = new AlertBuilder(CheckTransactionsActivity.this);
                alert.showAlert(result);

            }

        }

    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        public String tabtitles[];


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabtitles = getResources().getStringArray(
                    R.array.view_transaction);
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
                    return new AddedTransactionActivity();
                case 1:
                    return new ViewTransactionActivity();
                case 2:
                    return new DetailedStatementActivity();

            }

            return null;

        }

    }
}
