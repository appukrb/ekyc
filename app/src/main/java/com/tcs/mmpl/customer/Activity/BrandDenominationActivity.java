package com.tcs.mmpl.customer.Activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.R;

public class BrandDenominationActivity extends TabActivity {


    private ImageView imgBrand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_brand_denomination);

        imgBrand = (ImageView)findViewById(R.id.imgBrand);


        Picasso.with(this).load(getIntent().getStringExtra("image")).placeholder(R.drawable.backgroud_default_image).into(imgBrand);




        TabHost tabHost = getTabHost();

        // Tab for Photos

        TabHost.TabSpec denominations = tabHost.newTabSpec("Amount");
        // setting Title and Icon for the Tab
        denominations.setIndicator("Amount", getResources().getDrawable(R.drawable.deno));
        Intent denoIntent = new Intent(this, DenominationActivity.class);
        denoIntent.putExtra("hash",getIntent().getStringExtra("hash"));
        denoIntent.putExtra("image",getIntent().getStringExtra("image"));
        denoIntent.putExtra("accesstoken",getIntent().getStringExtra("accesstoken"));
        denoIntent.putExtra("name",getIntent().getStringExtra("name"));
        denoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        denominations.setContent(denoIntent);

        // Tab for Store
        TabHost.TabSpec store = tabHost.newTabSpec("Store");
        store.setIndicator("Store", getResources().getDrawable(R.drawable.store));
        Intent storeIntent = new Intent(this, StoreActivity.class);
        storeIntent.putExtra("hash",getIntent().getStringExtra("hash"));
        storeIntent.putExtra("accesstoken", getIntent().getStringExtra("accesstoken"));
        store.setContent(storeIntent);

        // Tab for Brand
        TabHost.TabSpec terms = tabHost.newTabSpec("Terms");
        terms.setIndicator(getResources().getString(R.string.terms), getResources().getDrawable(R.drawable.terms));
        Intent termsIntent = new Intent(this, TermsActivity.class);
        termsIntent.putExtra("terms",getIntent().getStringExtra("terms"));
        terms.setContent(termsIntent);

        // Adding all TabSpec to TabHost
        tabHost.addTab(denominations);
        tabHost.addTab(store);
        tabHost.addTab(terms);
    }


}
