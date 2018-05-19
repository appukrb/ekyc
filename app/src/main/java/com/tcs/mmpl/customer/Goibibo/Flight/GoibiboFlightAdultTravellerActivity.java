package com.tcs.mmpl.customer.Goibibo.Flight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.FlightTraveller;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;

public class GoibiboFlightAdultTravellerActivity extends Activity {


    private EditText edtFirstName,edtLastName;
    private int selected = 0;
    private GoibiboFlightDatabaseHelper goibiboFlightDatabaseHelper;
    private String tag;
    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;
    private RadioGroup radioTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_flight_adult_traveller);
        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        goibiboFlightDatabaseHelper = new GoibiboFlightDatabaseHelper(getApplicationContext());

        edtFirstName = (EditText)findViewById(R.id.edtFirstName);
        edtLastName = (EditText)findViewById(R.id.edtLastName);


        radioTitle = (RadioGroup)findViewById(R.id.radioTitle);

        selected = getIntent().getIntExtra("selected",0);
        tag = getIntent().getStringExtra("tag");




    }


    public void add(View v)
    {
        AlertBuilder alertBuilder = new AlertBuilder(GoibiboFlightAdultTravellerActivity.this);
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
            flightTraveller.setTitle(radioButton.getText().toString().trim());
            flightTraveller.setEticketnumber("");
            flightTraveller.setDateOfBirth("");
            flightTraveller.setType("A");

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


}
