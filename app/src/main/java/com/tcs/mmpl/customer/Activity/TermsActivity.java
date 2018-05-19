package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;

public class TermsActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);



        TextView txtTerms = (TextView)findViewById(R.id.txtTerms);
        txtTerms.setText(Html.fromHtml(getIntent().getStringExtra("terms")));
    }


}
