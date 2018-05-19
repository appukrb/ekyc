package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Address;
import com.tcs.mmpl.customer.utility.Deals;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.RestaurantInvoiceList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RestaurantInvoiceActivity extends Activity {

    private RestaurantInvoiceList restaurantInvoiceList;

    private LinearLayout linParent;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;
    private String expiryDate="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_invoice);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        linParent = (LinearLayout) findViewById(R.id.linParent);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);

        expiryDate = getIntent().getStringExtra("expiry_date");


        restaurantInvoiceList = (RestaurantInvoiceList) getIntent().getSerializableExtra("invoice_details");

        TextView txtPaymentSuccess = (TextView)findViewById(R.id.txtPaymentSuccess);
        TextView txtHotelName = (TextView)findViewById(R.id.txtHotelName);
        TextView txtHotelAddress = (TextView)findViewById(R.id.txtHotelAddress);
        TextView txtCouponCode = (TextView)findViewById(R.id.txtCouponCode);
        TextView txtPurchaseDate = (TextView)findViewById(R.id.txtPurchaseDate);
        TextView txtExpiryDate = (TextView)findViewById(R.id.txtExpiryDate);
        TextView txtDeal = (TextView)findViewById(R.id.txtDeal);
        TextView txtAmount = (TextView)findViewById(R.id.txtAmount);

        txtPaymentSuccess.setText(Html.fromHtml("<b>"+restaurantInvoiceList.getCoupon_code()+"</b>"+ " has been paid successfully"));

        txtHotelName.setText(restaurantInvoiceList.getHotelName());
        Address address = restaurantInvoiceList.getAddress();
        txtHotelAddress.setText(address.getAddress());
        txtCouponCode.setText(restaurantInvoiceList.getCoupon_code());

        String purchaseDate="";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMM yyyy HH:mm");
        if(restaurantInvoiceList.getPaid_time().contains(".")) {
            String dateInString = restaurantInvoiceList.getPaid_time().split("\\.")[0].trim();

            try {

                Date date = formatter.parse(dateInString);
                purchaseDate = formatter1.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        else
        {
            purchaseDate = restaurantInvoiceList.getPaid_time();
        }
        txtPurchaseDate.setText(purchaseDate);
        Deals deals = restaurantInvoiceList.getDeal();
        txtExpiryDate.setText(expiryDate);
        txtDeal.setText(deals.getTitle());
        txtAmount.setText("Rs."+restaurantInvoiceList.getTotal_bill());


    }

    public void home(View v)
    {
        Intent intent = new Intent(getApplicationContext(),HomeScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



}
