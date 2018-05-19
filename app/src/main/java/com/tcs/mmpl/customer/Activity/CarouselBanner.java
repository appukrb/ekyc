package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class CarouselBanner extends Activity {

    FontClass fontclass = new FontClass();
    Typeface typeface;
    LinearLayout liradio;
    String Success,radiostring,message,count;
    Button btnMpinSubmit;
    String jsonStr1,msg,mobile_no,carouselbannerID,selectedOption;
    ProgressDialog pDialog;
    SharedPreferences userInfoPref,pref;
    RelativeLayout linParent;
    MyConnectionHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carousel_banner);
        linParent = (RelativeLayout) findViewById(R.id.linParent);
        liradio= (LinearLayout) findViewById(R.id.liradio);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(linParent, typeface);
        db = new MyConnectionHelper(getApplicationContext());
        Success= getIntent().getStringExtra("result");
//        carouselbannerID=getIntent().getStringExtra("carouselbannerID");
        //// System.out.println(carouselbannerID+"carouselbannerID-->");
        btnMpinSubmit =(Button)findViewById(R.id.btnMpinSubmit);
        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        mobile_no=pref.getString("mobile_number", "");
            btnMpinSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String CarouselBannerDetailurl = getResources().getString(R.string.Carousel_banner) + "?carouselbannerID="+carouselbannerID +"&Option="+ Uri.encode(selectedOption, "utf-8") +"&MDN="+mobile_no;
                   //// System.out.println("CarouselBannerDetailurl-___--->"+CarouselBannerDetailurl);
                    CarouselBannerDetail CarouselBannerDetail1 = new CarouselBannerDetail(getApplicationContext());
                    CarouselBannerDetail1.execute(CarouselBannerDetailurl);

                }
            });
        if (Success != null)
        {
            try
            {
                JSONObject jsonMainObj = new JSONObject(Success);
                if(jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success"))
                {
                    message=jsonMainObj.getString("message");
                    radiostring=jsonMainObj.getString("option");
                    carouselbannerID=jsonMainObj.getString("carouselbannerID");
                }
            }
            catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.apidown),Toast.LENGTH_SHORT).show();
                }
        } else
        {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_SHORT).show();
        }
        LinearLayout mainlinearLayout = new LinearLayout(this);

        mainlinearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView txt = new TextView(this);
        txt.setText(message);
        txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_text_size));
        txt.setTypeface(null, Typeface.BOLD);

        RadioGroup radio = new RadioGroup(this);
        RadioButton rb ;

        String[] optionAnswer = radiostring.split("#");
        //// System.out.println(radiostring);

        for (int j = 0; j < optionAnswer.length; j++) {
            rb = new RadioButton(this);
            rb.setId(j);
            if(j==0)
            {
                selectedOption = optionAnswer[j];
                rb.setChecked(true);
            }
            rb.setText(optionAnswer[j]);
            //// System.out.println(optionAnswer[j]);
            rb.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_text_size));
            rb.setOnClickListener(onRadioGroupClick(rb));
            radio.addView(rb);
        }
        mainlinearLayout.addView(txt);
        mainlinearLayout.addView(radio);


        liradio.addView(mainlinearLayout);

        ImageView imgBanner =(ImageView)findViewById(R.id.imgBanner);

        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();

        if(c1.moveToNext())
        {

            do
            {
                //// System.out.println("********************************************************"+c1.getString(3)+" " +c1.getString(1));
                if(c1.getString(3).equalsIgnoreCase("CARO"))
                {
                    Glide.with(CarouselBanner.this).load(c1.getString(1)).placeholder(R.drawable.default_banner).into(imgBanner);
                    imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
            }while(c1.moveToNext());
        }

        c1.close();


        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent re = new Intent(getApplicationContext(), LoadMoneyActivity.class);
                re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(re);
            }
        });

    }

    View.OnClickListener onRadioGroupClick(final RadioButton radioButton ) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                selectedOption=radioButton.getText().toString();

            }
        };

    }

    private class CarouselBannerDetail extends AsyncTask<String, Void, String> {
        Context context;
        ProgressDialog pDialog;
        String balance;

        public CarouselBannerDetail(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//             Showing progress dialog
            pDialog = new ProgressDialog(CarouselBanner.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
      }


        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(getApplicationContext());
                jsonStr1 = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr1);

                if (jsonStr1 != null || !jsonStr1.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr1);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {
                            msg=jsonMainObj.getString("responseMessage");
                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            return "Failure";

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

        protected void onPostExecute(final String result) {
            Log.d("Result::::", ">" + result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (result.equalsIgnoreCase("Success"))
            {

                View popupView = getLayoutInflater().inflate(R.layout.popup_delete_favourites, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                TextView update_dialog_txt = (TextView) popupView.findViewById(R.id.txtMessage);
                Button update_dialog_btn_Cancel =(Button)popupView.findViewById(R.id.btnNo);
                Button update_dialog_btn_ok = (Button) popupView.findViewById(R.id.btnYes);
                update_dialog_txt.setText(msg);
                update_dialog_btn_ok.setText("ok");
                update_dialog_btn_Cancel.setText("Cancel");
                update_dialog_btn_Cancel.setVisibility(View.GONE);
                update_dialog_txt.setTypeface(typeface);
                update_dialog_btn_ok.setTypeface(typeface);
                update_dialog_btn_Cancel.setTypeface(typeface);


                update_dialog_btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
                        popupWindow.dismiss();
                        Intent intent = new Intent(CarouselBanner.this,HomeScreenActivity.class);
                        startActivity(intent);
                    }
                });

                update_dialog_btn_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            } else if (result.equalsIgnoreCase("Failure"))
            {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.apidown),Toast.LENGTH_SHORT).show();
            }

        }
    }
}
