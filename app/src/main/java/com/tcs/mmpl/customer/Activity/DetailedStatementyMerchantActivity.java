package com.tcs.mmpl.customer.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DetailedStatementyMerchantActivity extends AppCompatActivity {
    private static final int MODE_PRIVATE = 0;
    //    private static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
    private EditText edtEmailID,edtFromDate,edtToDate;
    private Button btnDetailStatement;
    private LinearLayout mainlinear;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    private SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;

    private SimpleDateFormat dateFormatter;
    private DatePickerDialog fromDatePickerDialog,toDatePickerDialog;
    private Calendar cal;
    private int day;
    private int month;
    private int year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_statementy_merchant);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        mainlinear = (LinearLayout)findViewById(R.id.mainlinear);

        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        edtEmailID = (EditText)findViewById(R.id.edtEmailID);
        edtFromDate = (EditText)findViewById(R.id.edtFromDate);
        edtToDate = (EditText)findViewById(R.id.edtToDate);
        btnDetailStatement = (Button)findViewById(R.id.btnDetailStatement);

        edtEmailID.setText(userInfoPref.getString("emailId", ""));

        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        Calendar newCalendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        fromDatePickerDialog = new DatePickerDialog(DetailedStatementyMerchantActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtFromDate.setText(dateFormatter.format(newDate.getTime()));

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(DetailedStatementyMerchantActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtToDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFromDate();
            }
        });

        edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openToDate();
            }
        });

        btnDetailStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtFromDate.getText().toString().trim().equalsIgnoreCase(""))
                {
                    AlertBuilder alert = new AlertBuilder(DetailedStatementyMerchantActivity.this);
                    alert.showAlert(getResources().getString(R.string.invalid_FromDate));
                }
                else if (edtToDate.getText().toString().trim().equalsIgnoreCase(""))
                {
                    AlertBuilder alert = new AlertBuilder(DetailedStatementyMerchantActivity.this);
                    alert.showAlert(getResources().getString(R.string.invalid_ToDate));
                }
                try{

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                    String fromDate,toDate,Currentdate;
                    fromDate=edtFromDate.getText().toString().trim();
                    toDate=edtToDate.getText().toString().trim();

                    Calendar c = Calendar.getInstance();
                    Currentdate= formatter.format(c.getTime());
                    Date datefrom = formatter.parse(fromDate);
                    Date dateto = formatter.parse(toDate);
                    Date datecurrent = formatter.parse(Currentdate);

                    if (datefrom.compareTo(datecurrent)>0 )
                    {
                        AlertBuilder alert = new AlertBuilder(DetailedStatementyMerchantActivity.this);
                        alert.showAlert(getResources().getString(R.string.invalid_FromDate));
                    }
                    else if(dateto.compareTo(datecurrent)>0 )
                    {
                        AlertBuilder alert = new AlertBuilder(DetailedStatementyMerchantActivity.this);
                        alert.showAlert(getResources().getString(R.string.invalid_ToDate));
                    }
                    else if(datefrom.compareTo(dateto)>0 )
                    {
                        AlertBuilder alert = new AlertBuilder(DetailedStatementyMerchantActivity.this);
                        alert.showAlert(getResources().getString(R.string.fromdatemorethentodate));
                    }

                    else{
                        openDetailedStatement();
                    }


                }catch (ParseException e1){
                    e1.printStackTrace();
                }

            }
        });
    }

    public void openDetailedStatement() {


        if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {


            boolean flag = android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmailID.getText().toString().trim()).matches();
            if(edtFromDate.getText().toString().trim().equalsIgnoreCase(""))
            {
                AlertBuilder alert = new AlertBuilder(DetailedStatementyMerchantActivity.this);
                alert.showAlert(getResources().getString(R.string.invalid_date));
            }
            else if (edtToDate.getText().toString().trim().equalsIgnoreCase(""))
            {
                AlertBuilder alert = new AlertBuilder(DetailedStatementyMerchantActivity.this);
                alert.showAlert(getResources().getString(R.string.invalid_date));
            }
            else if (flag) {
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(DetailedStatementyMerchantActivity.this.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_mpin_layout, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                final EditText edtMpin = (EditText) popupView.findViewById(R.id.edittext_edit_popup);
                Button btnCancel = (Button) popupView.findViewById(R.id.button_pop_no);
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

                        if (edtMpin.getText().toString().trim().equalsIgnoreCase("")|| edtMpin.getText().toString().trim().length() < 4) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.mpin), Toast.LENGTH_LONG).show();
                        }
                        else {
                            popupWindow.dismiss();
                            String getDetailStatementURL = getApplicationContext().getResources().getString(R.string.detailStatement) + "?MDN=" + pref.getString("mobile_number", "") + "&emailId=" + edtEmailID.getText().toString().trim() + "&Mpin=" + edtMpin.getText().toString().trim() + "&fromDate=" + edtFromDate.getText().toString().trim() + "&toDate=" + edtToDate.getText().toString().trim();
                            GetDetailStatement getDetailStatement = new GetDetailStatement(DetailedStatementyMerchantActivity.this);
                            getDetailStatement.execute(getDetailStatementURL);
                        }

                    }
                });

                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            }
            else
            {
                AlertBuilder alert = new AlertBuilder(DetailedStatementyMerchantActivity.this);
                alert.showAlert(getResources().getString(R.string.invalid_email));
            }

        } else {
            AlertBuilder alert = new AlertBuilder(DetailedStatementyMerchantActivity.this);
            alert.newUser();
        }
    }

    public void openFromDate()
    {
        fromDatePickerDialog.show();
    }

    public void openToDate()
    {
        toDatePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {


            edtFromDate.setText(selectedDay + " / " + (selectedMonth + 1) + " / " + selectedYear);
            edtToDate.setText(selectedDay + " / " + (selectedMonth + 1) + " / " + selectedYear);
        }
    };
    private class GetDetailStatement extends AsyncTask<String, Void, String> {

        Context context;
        ProgressDialog pDialog;
        public GetDetailStatement(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DetailedStatementyMerchantActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(DetailedStatementyMerchantActivity.this);
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


                Toast.makeText(DetailedStatementyMerchantActivity.this, getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

                AlertBuilder alert = new AlertBuilder(DetailedStatementyMerchantActivity.this);
                alert.showAlert(result);

            }

        }

    }
}
