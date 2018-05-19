package com.tcs.mmpl.customer.Goibibo.Bus;

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

import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusTraveller;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;


public class GoibiboBusTravellerActivity extends Activity {

    private EditText edtFirstName,edtMiddleName,edtLastName,edtAge;
    private int selected = 0;
    private GoibiboBusDatabaseHelper goibiboBusDatabaseHelper;
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
        setContentView(R.layout.activity_goibibo_bus_traveller);

        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        goibiboBusDatabaseHelper = new GoibiboBusDatabaseHelper(getApplicationContext());

        edtFirstName = (EditText)findViewById(R.id.edtFirstName);
        edtMiddleName = (EditText)findViewById(R.id.edtMiddleName);
        edtLastName = (EditText)findViewById(R.id.edtLastName);
        edtAge = (EditText)findViewById(R.id.edtAge);

        radioTitle = (RadioGroup)findViewById(R.id.radioTitle);

        selected = getIntent().getIntExtra("selected",0);
        tag = getIntent().getStringExtra("tag");

    }


    public void add(View v)
    {
        AlertBuilder alertBuilder = new AlertBuilder(GoibiboBusTravellerActivity.this);
        if(edtFirstName.getText().toString().trim().equalsIgnoreCase(""))
        {
            alertBuilder.showAlert(getResources().getString(R.string.valid_first_name));
        }
        else if(edtLastName.getText().toString().trim().equalsIgnoreCase(""))
        {
            alertBuilder.showAlert(getResources().getString(R.string.valid_last_name));
        }
        else if(edtAge.getText().toString().trim().equalsIgnoreCase(""))
        {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_age));
        }
        else
        {
            RadioButton radioButton = (RadioButton)findViewById(radioTitle.getCheckedRadioButtonId());

            BusTraveller busTraveller = new BusTraveller();
            busTraveller.setFirstname(edtFirstName.getText().toString().trim());
            busTraveller.setMiddlename(edtMiddleName.getText().toString().trim());
            busTraveller.setLastname(edtLastName.getText().toString().trim());
            busTraveller.setAge(edtAge.getText().toString().trim());
            busTraveller.setTitle(radioButton.getText().toString().trim());

            if(tag.equalsIgnoreCase("0")) {
                tag = goibiboBusDatabaseHelper.insertBusTraveller(busTraveller);
            }
            else {
               goibiboBusDatabaseHelper.updateBusTravellerInfo(busTraveller,tag);
            }

            String res = "Name: "+edtFirstName.getText().toString()+" " + edtLastName.getText().toString() + "\nAge: " + edtAge.getText().toString();
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
