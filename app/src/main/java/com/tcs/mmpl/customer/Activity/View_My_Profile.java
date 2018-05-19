package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;
import com.tcs.mmpl.customer.utility.Profile;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.tcs.mmpl.customer.R.layout.popup_profile_update;


public class View_My_Profile extends Activity {

    RelativeLayout mainlinear;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;

    TextView txtUserName, txtUserNumber, txtEmail, txtAmountSpent, txtUserStatus;
    LinearLayout linProfile,linemail,lindob,linmpin,lindob1;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Calendar cal;
    private int day;
    private int month;
    private int year;

    EditText edittext_edit_dob,edittext_edit_emailid,edittext_edit_pin;

    String dob="",emailid,mpin,sucessAlart;
    Button button_Edit,button_pop_update;
    private String bannerName = "", bannerUrl = "";
    MyConnectionHelper db;
    ImageView edit,edit1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view__my__profile);
        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        linProfile = (LinearLayout) findViewById(R.id.linProfile);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtUserNumber = (TextView) findViewById(R.id.txtUserNumber);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtAmountSpent = (TextView) findViewById(R.id.txtAmountSpent);
        txtUserStatus = (TextView) findViewById(R.id.txtUserStatus);
        edit =(ImageView) findViewById(R.id.edit);
        edit1 =(ImageView) findViewById(R.id.edit1);
//        edittext_edit_pin =(EditText)findViewById(R.id.edittext_edit_pin);
//        edittext_edit_dob =(EditText)findViewById(R.id.edittext_edit_dob);
        button_pop_update =(Button)findViewById(R.id.button_pop_update);
        button_Edit =(Button)findViewById(R.id.button_Edit);

        ImageView imgBanner = (ImageView) findViewById(R.id.imgBanner);
        linemail = (LinearLayout) findViewById(R.id.linemail);
        lindob = (LinearLayout) findViewById(R.id.lindob);
        lindob1 = (LinearLayout) findViewById(R.id.lindob1);
        linmpin = (LinearLayout) findViewById(R.id.linmpin);

        if(dob.equalsIgnoreCase("NA")) {
            lindob1.setVisibility(View.VISIBLE);
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) View_My_Profile.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_profile_update, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                final EditText edittext_edit_dob = (EditText) popupView.findViewById(R.id.edittext_edit_dob);
                final EditText edittext_edit_emailid = (EditText) popupView.findViewById(R.id.edittext_edit_emailid);
                final EditText edittext_edit_pin = (EditText) popupView.findViewById(R.id.edittext_edit_pin);
                Button btnCancel = (Button)popupView.findViewById(R.id.button_pop_no);
                Button btnSubmit = (Button) popupView.findViewById(R.id.button_pop_yes);

                edittext_edit_dob.setTypeface(typeface);
                edittext_edit_emailid.setTypeface(typeface);
                edittext_edit_pin.setTypeface(typeface);
                edittext_edit_dob.setVisibility(View.GONE);

                btnCancel.setTypeface(typeface);
                btnSubmit.setTypeface(typeface);

                edittext_edit_dob.setText(dob);
                edittext_edit_emailid.setText(emailid);

                edittext_edit_dob.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fromDatePickerDialog.show();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                btnSubmit.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            emailid = edittext_edit_emailid.getText().toString();
//                            dob=edittext_edit_dob.getText().toString();
                            mpin=edittext_edit_pin.getText().toString();
                            if (emailid.trim().equalsIgnoreCase("")){
                                Toast.makeText(getApplicationContext(), "Please Enter Email ID", Toast.LENGTH_LONG).show();
                            }
//                            else if(dob.trim().equalsIgnoreCase("NA")||(dob.trim().equalsIgnoreCase("")))
//                            {
//                                Toast.makeText(getApplicationContext(), "Please Enter Date Of Birth", Toast.LENGTH_LONG).show();
//                            }
                            else if(mpin.trim().equalsIgnoreCase("NA")||(mpin.trim().equalsIgnoreCase("")))
                            {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.mpin), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                // System.out.println(emailid+"emailid"+dob+"dob");
                                String profile_url = getResources().getString(R.string.profileupdate_url) + "?MDN=" + pref.getString("mobile_number", "") +
                                        "&userID=" + userInfoPref.getString("userID", "")+ "&dob=" +dob+ "&emailid=" + emailid+ "&mpin=" +mpin+"&kycStatus="+userInfoPref.getString("kycstatus", "");
                                // System.out.println(profile_url+"profile_url");
                                UpdateProfile updateProfile = new UpdateProfile(getApplicationContext());
                                updateProfile.execute(profile_url);
                            }
                            popupWindow.dismiss();
                    }
                });

                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

            }
        });

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) View_My_Profile.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(popup_profile_update, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                edittext_edit_dob = (EditText) popupView.findViewById(R.id.edittext_edit_dob);
                final EditText edittext_edit_emailid = (EditText) popupView.findViewById(R.id.edittext_edit_emailid);
                final EditText edittext_edit_pin = (EditText) popupView.findViewById(R.id.edittext_edit_pin);
                Button btnCancel = (Button)popupView.findViewById(R.id.button_pop_no);
                Button btnSubmit = (Button) popupView.findViewById(R.id.button_pop_yes);

                edittext_edit_dob.setTypeface(typeface);
                edittext_edit_emailid.setTypeface(typeface);
                edittext_edit_pin.setTypeface(typeface);

                btnCancel.setTypeface(typeface);
                btnSubmit.setTypeface(typeface);

                edittext_edit_dob.setText(dob);
                edittext_edit_emailid.setText(emailid);
                edittext_edit_emailid.setVisibility(View.GONE);

                edittext_edit_dob.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fromDatePickerDialog.show();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                btnSubmit.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        emailid = edittext_edit_emailid.getText().toString();
                            dob=edittext_edit_dob.getText().toString();
                            mpin=edittext_edit_pin.getText().toString();


//                        if (emailid.trim().equalsIgnoreCase("")){
//                            Toast.makeText(getApplicationContext(), "Please Enter Email ID", Toast.LENGTH_LONG).show();
//                        }
                        if(dob.trim().equalsIgnoreCase("NA")||(dob.trim().equalsIgnoreCase("")))
                        {
                            Toast.makeText(getApplicationContext(), "Please Enter Date Of Birth", Toast.LENGTH_LONG).show();
                        }
                        else if(mpin.trim().equalsIgnoreCase("NA")||(mpin.trim().equalsIgnoreCase("")))
                        {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.mpin), Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            // System.out.println(emailid+"emailid"+dob+"dob");
                            String profile_url = getResources().getString(R.string.profileupdate_url) + "?MDN=" + pref.getString("mobile_number", "") +
                                    "&userID=" + userInfoPref.getString("userID", "")+ "&dob=" +dob+ "&emailid=" + emailid+ "&mpin=" + mpin+"&kycStatus=" + userInfoPref.getString("kycstatus", "");
                            // System.out.println(profile_url+"profile_url");
                            UpdateProfile updateProfile = new UpdateProfile(getApplicationContext());
                            updateProfile.execute(profile_url);
                        }
                        popupWindow.dismiss();



                    }
                });

                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

            }
        });


//        Update();
        button_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                button_pop_update.setVisibility(View.VISIBLE);
                linemail.setVisibility(View.VISIBLE);
                linmpin.setVisibility(View.VISIBLE);
                button_Edit.setVisibility(View.GONE);
                if(dob.equalsIgnoreCase("NA")) {
                    lindob.setVisibility(View.VISIBLE);
                }
            }
        });

        button_pop_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailid = edittext_edit_emailid.getText().toString();
                dob=edittext_edit_dob.getText().toString();
                mpin=edittext_edit_pin.getText().toString();
                if (emailid.trim().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(), "Please Enter Email ID", Toast.LENGTH_LONG).show();
                } else if(dob.trim().equalsIgnoreCase("NA")||(dob.trim().equalsIgnoreCase("")))
                {
                    Toast.makeText(getApplicationContext(), "Please Enter Date Of Birth", Toast.LENGTH_LONG).show();
                }else if(mpin.trim().equalsIgnoreCase("NA")||(mpin.trim().equalsIgnoreCase("")))
                {
                    Toast.makeText(getApplicationContext(), "Please Enter mpin", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // System.out.println(emailid+"emailid"+dob+"dob");
                    String profile_url = getResources().getString(R.string.profileupdate_url) + "?MDN=" + pref.getString("mobile_number", "") +
                            "&userID=" + userInfoPref.getString("userID", "")+ "&dob=" +dob+ "&emailid=" + emailid+ "&mpin=" + mpin+"&kycStatus=" + userInfoPref.getString("kycstatus", "");
                    // System.out.println(profile_url+"profile_url");
                    UpdateProfile updateProfile = new UpdateProfile(getApplicationContext());
                    updateProfile.execute(profile_url);
                }
            }
        });


        db = new MyConnectionHelper(getApplicationContext());

        Cursor c1 = db.fun_selectDistinct_tbl_multibanner();

        if (c1.moveToNext()) {

            do {

                if(userInfoPref.getString("usertype","").equalsIgnoreCase("M"))
                {
                    if (c1.getString(3).equalsIgnoreCase("RETPROFILE")) {
                        bannerName=c1.getString(0);
                        bannerUrl=c1.getString(1);
                        break;
                    }
                }
                else
                {


                    if (c1.getString(3).equalsIgnoreCase("LAND")) {
                        bannerName=c1.getString(0);
                        bannerUrl=c1.getString(1);
                        break;
                    }
                }

            } while (c1.moveToNext());
        }

        c1.close();
//        String banner = pref.getString("banner", "");
        // System.out.println(bannerUrl+"/n bannerUrl /n"+bannerName+"/n bannerName /n");

        Glide.with(View_My_Profile.this).load(bannerUrl).into(imgBanner);
        imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
//        if(!banner.trim().isEmpty()) {
//            byte[] byteArray = Base64.decode(banner, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
//                    byteArray.length);
//            imgBanner.setImageBitmap(bitmap);
//            imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
//        }

        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bannerURL = getResources().getString(R.string.bannerlink)+"?Type="+bannerName;
                GetBannerLink getBannerLink = new GetBannerLink(getApplicationContext());
                getBannerLink.execute(bannerURL);
            }
        });


        String profile_url = getResources().getString(R.string.profile_url) + "?MDN=" + pref.getString("mobile_number", "")
                + "&userID=" + userInfoPref.getString("userID", "")+ "&kycStatus=" + userInfoPref.getString("kycstatus", "");

        // System.out.println("profile_url"+profile_url);
        // System.out.println("kycStatus"+userInfoPref.getString("kycstatus", ""));
        GetProfile gb = new GetProfile(getApplicationContext());
        gb.execute(profile_url);

        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edittext_edit_dob.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


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

    private class GetProfile extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private Profile profile;

        public GetProfile(Context context) {
            this.context = context;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(View_My_Profile.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(View_My_Profile.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

              // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {


                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success")) {

                            profile = new Profile();
                            profile.setFirstName(jsonMainObj.getString("firstName"));
                            profile.setLastname(jsonMainObj.getString("lastname"));
                            profile.setEmail(jsonMainObj.getString("email"));
                            profile.setAmount(jsonMainObj.getString("amount"));
                            profile.setMobileNumber(jsonMainObj.getString("mobileNumber"));
                            profile.setAccountType(jsonMainObj.getString("accountType"));
                            profile.setDob(jsonMainObj.getString("dob"));
                            return "Success";
                        } else if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {
                            return jsonMainObj.getString("responseMessage");
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

            return "Failure";

        }

        @Override
        protected void onPostExecute(String result) {

            if(pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {
                linProfile.setVisibility(View.VISIBLE);

                if(!profile.getMobileNumber().trim().equalsIgnoreCase(""))
                txtUserNumber.setText(profile.getMobileNumber());
                else
                    txtUserNumber.setText("-");

                if(!profile.getDob().trim().equalsIgnoreCase(""))
//                    edittext_edit_dob.setText(profile.getDob());
                    dob=profile.getDob();

//                    edittext_edit_dob.setText("");
                    if(!profile.getEmail().trim().equalsIgnoreCase(""))
                        txtEmail.setText(profile.getEmail());
                    else
                        txtEmail.setText("-");
//                if(!profile.getEmail().trim().equalsIgnoreCase(""))
//                    txtEmail.setText("my mail id ");
//                else
//                    txtEmail.setText("-");

                emailid=profile.getEmail();
                // System.out.println("dob"+dob+"emailid"+emailid);

                if(!profile.getFirstName().trim().equalsIgnoreCase(""))
                    txtUserName.setText(profile.getFirstName().substring(0, 1).toUpperCase() + profile.getFirstName().substring(1));
                else
                    txtUserName.setText("-");


                if(!profile.getAmount().trim().equalsIgnoreCase(""))
                txtAmountSpent.setText(Html.fromHtml(profile.getAmount()));
                else
                    txtAmountSpent.setText("-");

                if(!profile.getAccountType().trim().equalsIgnoreCase(""))
                    txtUserStatus.setText(profile.getAccountType());
                else
                    txtUserStatus.setText("-");

            } else if (result.equalsIgnoreCase("Failure")) {

                AlertBuilder alertBuilder = new AlertBuilder(View_My_Profile.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));

            } else {
                AlertBuilder alertBuilder = new AlertBuilder(View_My_Profile.this);
                alertBuilder.showAlert(result);
            }

        }

    }

    private class UpdateProfile extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private Profile profile;

        public UpdateProfile(Context context) {
            this.context = context;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(View_My_Profile.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(View_My_Profile.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {


                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success")) {
                            sucessAlart=(jsonMainObj.getString("responseMessage"));
                            profile = new Profile();
                            profile.setFirstName(jsonMainObj.getString("firstName"));
                            profile.setLastname(jsonMainObj.getString("lastname"));
                            profile.setEmail(jsonMainObj.getString("email"));
                            profile.setAmount(jsonMainObj.getString("amount"));
                            profile.setMobileNumber(jsonMainObj.getString("mobileNumber"));
                            profile.setAccountType(jsonMainObj.getString("accountType"));
                            return "Success";
                        } else if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {
                            return jsonMainObj.getString("responseMessage");
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

            return "Failure";

        }

        @Override
        protected void onPostExecute(String result) {

            if(pDialog.isShowing())
                pDialog.dismiss();

            if (result.equalsIgnoreCase("Success")) {
                linProfile.setVisibility(View.VISIBLE);

                AlertBuilder alertBuilder = new AlertBuilder(View_My_Profile.this);
                alertBuilder.showAlert(sucessAlart);

                if(!profile.getMobileNumber().trim().equalsIgnoreCase(""))
                    txtUserNumber.setText(profile.getMobileNumber());
                else
                    txtUserNumber.setText("-");

                if(!profile.getDob().trim().equalsIgnoreCase(""))
//                    edittext_edit_dob.setText(profile.getDob());
                    dob=profile.getDob();

//                    edittext_edit_dob.setText("");
                if(!profile.getEmail().trim().equalsIgnoreCase(""))
                    txtEmail.setText(profile.getEmail());
                else
                    txtEmail.setText("-");
//                if(!profile.getEmail().trim().equalsIgnoreCase(""))
//                    txtEmail.setText("my mail id ");
//                else
//                    txtEmail.setText("-");

                emailid=profile.getEmail();
                // System.out.println("dob"+dob+"emailid"+emailid);

                if(!profile.getFirstName().trim().equalsIgnoreCase(""))
                    txtUserName.setText(profile.getFirstName().substring(0, 1).toUpperCase() + profile.getFirstName().substring(1));
                else
                    txtUserName.setText("-");


                if(!profile.getAmount().trim().equalsIgnoreCase(""))
                    txtAmountSpent.setText(Html.fromHtml(profile.getAmount()));
                else
                    txtAmountSpent.setText("-");

                if(!profile.getAccountType().trim().equalsIgnoreCase(""))
                    txtUserStatus.setText(profile.getAccountType());
                else
                    txtUserStatus.setText("-");
//                linProfile.setVisibility(View.VISIBLE);
//                button_pop_update.setVisibility(View.GONE);
//
//                if(!profile.getMobileNumber().trim().equalsIgnoreCase(""))
//                    txtUserNumber.setText(profile.getMobileNumber());
//                else
//                    txtUserNumber.setText("-");
//
//
//                if(!profile.getEmail().trim().equalsIgnoreCase(""))
//                    txtEmail.setText(profile.getEmail());
//                else
//                    txtEmail.setText("-");
//
//                if(!profile.getFirstName().trim().equalsIgnoreCase(""))
//                    txtUserName.setText(profile.getFirstName().substring(0, 1).toUpperCase() + profile.getFirstName().substring(1));
//                else
//                    txtUserName.setText("-");
//
//
//                if(!profile.getAmount().trim().equalsIgnoreCase(""))
//                    txtAmountSpent.setText(Html.fromHtml(profile.getAmount()));
//                else
//                    txtAmountSpent.setText("-");
//
//                if(!profile.getAccountType().trim().equalsIgnoreCase(""))
//                    txtUserStatus.setText(profile.getAccountType());
//                else
//                    txtUserStatus.setText("-");

            } else if (result.equalsIgnoreCase("Failure")) {

                AlertBuilder alertBuilder = new AlertBuilder(View_My_Profile.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));

            } else {
                AlertBuilder alertBuilder = new AlertBuilder(View_My_Profile.this);
                alertBuilder.showAlert(result);
            }

        }

    }

    private class GetBannerLink extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;
        private ProgressDialog pDialog;
        private String activityName;
        private String urlLink;

        public GetBannerLink(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(HomeScreenActivity.this);
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler serviceHandler = new WebServiceHandler(getApplication());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
                // System.out.println(jsonStr);
                JSONObject jsonObject = new JSONObject(jsonStr);

                activityName = jsonObject.getString("activityName");
                urlLink = jsonObject.getString("externalURL");


                return "Success";

            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure1";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (!activityName.equalsIgnoreCase("NA") && !urlLink.equalsIgnoreCase("NA")) {
                try {

                    String className = getPackageName() + ".Activity." + activityName;
                    Class<?> myClass = Class.forName(className);
                    //Activity activity = (Activity) myClass.newInstance();
                    Intent re = new Intent(getApplicationContext(), myClass);
                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    re.putExtra("option", urlLink);
                    startActivity(re);
                } catch (Exception e) {
                    e.printStackTrace();
                    //// System.out.println("exception " + e.getMessage());
                }
            } else if (!activityName.equalsIgnoreCase("NA") && urlLink.equalsIgnoreCase("NA")) {
                try {
                    String className = getPackageName() + ".Activity." + activityName;
                    Class<?> myClass = Class.forName(className);
                    //Activity activity = (Activity) myClass.newInstance();
                    Intent re = new Intent(getApplicationContext(), myClass);
                    re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(re);
                } catch (Exception e) {

                }

            } else if (activityName.equalsIgnoreCase("NA") && !urlLink.equalsIgnoreCase("NA")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink)));
            }


        }

    }
}
