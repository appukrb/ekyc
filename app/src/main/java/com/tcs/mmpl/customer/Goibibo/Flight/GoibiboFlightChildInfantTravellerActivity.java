package com.tcs.mmpl.customer.Goibibo.Flight;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.FlightTraveller;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GoibiboFlightChildInfantTravellerActivity extends Activity {

    private EditText edtFirstName,edtLastName;
    private TextView txtDatefBirth;
    private int selected = 0;
    private GoibiboFlightDatabaseHelper goibiboFlightDatabaseHelper;
    private String tag;
    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;
    private RadioGroup radioTitle;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Calendar cal;
    private int day;
    private int month;
    private int year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_flight_child_infant_traveller);

        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        goibiboFlightDatabaseHelper = new GoibiboFlightDatabaseHelper(getApplicationContext());

        edtFirstName = (EditText)findViewById(R.id.edtFirstName);
        edtLastName = (EditText)findViewById(R.id.edtLastName);
        txtDatefBirth = (TextView)findViewById(R.id.txtDatefBirth);

        radioTitle = (RadioGroup)findViewById(R.id.radioTitle);

        selected = getIntent().getIntExtra("selected",0);
        tag = getIntent().getStringExtra("tag");

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                txtDatefBirth.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

    }


    public void add(View v)
    {
        AlertBuilder alertBuilder = new AlertBuilder(GoibiboFlightChildInfantTravellerActivity.this);
        if(edtFirstName.getText().toString().trim().equalsIgnoreCase(""))
        {
            alertBuilder.showAlert(getResources().getString(R.string.valid_first_name));
        }
        else if(edtLastName.getText().toString().trim().equalsIgnoreCase(""))
        {
            alertBuilder.showAlert(getResources().getString(R.string.valid_last_name));
        }
        else
        {
            RadioButton radioButton = (RadioButton)findViewById(radioTitle.getCheckedRadioButtonId());

            FlightTraveller flightTraveller = new FlightTraveller();
            flightTraveller.setFirstName(edtFirstName.getText().toString().trim());
            flightTraveller.setLastName(edtLastName.getText().toString().trim());
            flightTraveller.setDateOfBirth(txtDatefBirth.getText().toString().trim());
            flightTraveller.setTitle(radioButton.getText().toString().trim());
            flightTraveller.setEticketnumber("");
            if(selected == 2)
                flightTraveller.setType("C");
            else
            flightTraveller.setType("I");

            if(tag.equalsIgnoreCase("0")) {
                tag = goibiboFlightDatabaseHelper.insertFlightTraveller(flightTraveller);
            }
            else {
                goibiboFlightDatabaseHelper.updateFlightTravellerInfo(flightTraveller, tag);
            }



            String res = "Name: "+edtFirstName.getText().toString()+" " + edtLastName.getText().toString();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", res);
            returnIntent.putExtra("row_id", tag);
            setResult(selected, returnIntent);
            finish();
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void openDateofBirth(View v)
    {
        fromDatePickerDialog.show();


        // showDialog(0);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {


            txtDatefBirth.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
                    + selectedYear);
        }
    };



}
