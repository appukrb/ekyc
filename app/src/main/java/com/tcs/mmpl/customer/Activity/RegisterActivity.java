package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class RegisterActivity extends Activity {
    LinearLayout mainlinear;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    TextView txt_mobile,txt_mobile_number,txt_profile_picture;
    EditText edt_fullname,edt_emailid,edt_mPIN;
    Button btnRegConfirm,btnRegReset;
    CheckBox chk_favrite;
    LinearLayout linWelcome, linDetails;
    TextView txtWelcome,txtBalance;

    EditText edtpromocode;

    EditText edtFullName,edtEmailID,edtDate,edtLastName,edtMpin,edtRetypeMpin;
    private Spinner gender;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    CheckBox chkAgree;

    LinearLayout webview_linear;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Calendar cal;
    private int day;
    private int month;
    private int year;

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString="";
    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_new);
        mainlinear = (LinearLayout)findViewById(R.id.mainlinear);
        typeface=Typeface.createFromAsset(getApplicationContext().getAssets(),"helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

       // edtRegMobile = (EditText)findViewById(R.id.edtRegMobile);
        edtFullName = (EditText)findViewById(R.id.edtFullName);
        edtEmailID = (EditText)findViewById(R.id.edtEmailID);
        edtDate = (EditText)findViewById(R.id.edtDateOfBirth);
        edtpromocode = (EditText)findViewById(R.id.edtpromocode);
        edtLastName = (EditText)findViewById(R.id.edtLastName);
        edtMpin = (EditText)findViewById(R.id.edtMpin);
        edtRetypeMpin = (EditText)findViewById(R.id.edtRetypeMpin);
        gender = (Spinner)findViewById(R.id.gender);



        // Initializing a String Array
        String[] genderArray = new String[]{
                "Male",
                "Female"
        };

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,genderArray
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        gender.setAdapter(spinnerArrayAdapter);

        edtDate.setInputType(InputType.TYPE_NULL);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        chkAgree=(CheckBox)findViewById(R.id.chkAgree);

        btnRegConfirm = (Button) findViewById(R.id.btnRegConfirm);
        btnRegReset = (Button) findViewById(R.id.btnRegReset);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        imgProfile = (ImageView)findViewById(R.id.imgProfile);

        linWelcome = (LinearLayout) findViewById(R.id.linWelcome);
        linDetails = (LinearLayout) findViewById(R.id.linDetails);

        txtWelcome = (TextView)findViewById(R.id.txtWelcome);
        txtBalance = (TextView)findViewById(R.id.txtBalance);

        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);


        btnRegConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connectionDetector.isConnectingToInternet()) {

                      if(!edtFullName.getText().toString().trim().matches("[a-zA-Z ]+"))
                    {
                        AlertBuilder alert = new AlertBuilder(RegisterActivity.this);
                        alert.showAlert(getResources().getString(R.string.valid_first_name));

                    }
                    else if(!edtLastName.getText().toString().trim().matches("[a-zA-Z ]+") || edtLastName.getText().toString().trim().equalsIgnoreCase(""))
                    {
                        AlertBuilder alert = new AlertBuilder(RegisterActivity.this);
                        alert.showAlert(getResources().getString(R.string.valid_last_name));

                    }
                    else if(edtFullName.getText().toString().trim().equalsIgnoreCase("") )
                    {
                        AlertBuilder alert = new AlertBuilder(RegisterActivity.this);
                        alert.showAlert(getResources().getString(R.string.common_validation));

                    }
//                      else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmailID.getText().toString().trim()).matches())
//                      {
//                          AlertBuilder alert = new AlertBuilder(RegisterActivity.this);
//                          alert.showAlert(getResources().getString(R.string.invalid_email));
//                      }
                      else if(edtMpin.getText().toString().trim().equalsIgnoreCase("") || edtMpin.getText().toString().trim().length()<4 )
                      {
                          AlertBuilder alert = new AlertBuilder(RegisterActivity.this);
                          alert.showAlert(getResources().getString(R.string.mpin));

                      }
                      else if(edtRetypeMpin.getText().toString().trim().equalsIgnoreCase("") || edtRetypeMpin.getText().toString().trim().length()<4 || !edtMpin.getText().toString().trim().equalsIgnoreCase(edtRetypeMpin.getText().toString().trim()) )
                      {
                          AlertBuilder alert = new AlertBuilder(RegisterActivity.this);
                          alert.showAlert(getResources().getString(R.string.mpinnotmatching));

                      }
                    else {

                        if(chkAgree.isChecked()) {
//                            editor.putString("mobile_number", "91" + edtRegMobile.getText().toString());
//                            editor.commit();
                            Registration reg = new Registration(getApplicationContext());
                            reg.execute(getResources().getString(R.string.register));
                        }
                        else
                        {
                            AlertBuilder alert = new AlertBuilder(RegisterActivity.this);
                            alert.showAlert(getResources().getString(R.string.termsandcondition));

                        }
                    }
                } else {

                    editor.putInt("status", 0);
                    editor.commit();


                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                }

            }
        });

        btnRegReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edtFullName.setText("");
                edtEmailID.setText("");
                edtDate.setText("");
                edtpromocode.setText("");
                edtDate.setText("");
                edtLastName.setText("");
                edtMpin.setText("");
                edtRetypeMpin.setText("");
            }
        });
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);

                //// System.out.println("Image Decode String..............."+imgDecodableString);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                imgProfile.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
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
            edtDate.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
                    + selectedYear);
        }
    };


    private class Registration extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance,responseMessage="";

        public Registration(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("MDN", pref.getString("mobile_number", ""));
                jsonObject.accumulate("FirstName", Uri.encode(edtFullName.getText().toString(), "utf-8"));
                jsonObject.accumulate("EMailId", Uri.encode(edtEmailID.getText().toString(), "utf-8"));
                jsonObject.accumulate("DateOfBirth", Uri.encode(edtDate.getText().toString(),"utf-8"));
                jsonObject.accumulate("Gender", Uri.encode(gender.getSelectedItem().toString(),"utf-8"));
                jsonObject.accumulate("NewMPIN", Uri.encode(edtMpin.getText().toString(),"utf-8"));
                jsonObject.accumulate("LastName", Uri.encode(edtLastName.getText().toString(),"utf-8"));

                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                //// System.out.println(json);

                //// System.out.println(json);

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(RegisterActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                Log.d("json::::::::", jsonStr);
                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            userInfoEditor.putString("firstname", firstName);
                            userInfoEditor.putString("lastname", lastName);
                            userInfoEditor.putString("walletbalance", walletBalance);
                            userInfoEditor.putString("usertype", jsonMainObj.getString("userType"));
                            userInfoEditor.putBoolean("new_user_registered_newapp", true);
                            userInfoEditor.putString("emailId", jsonMainObj.getString("emailId"));
                            userInfoEditor.putString("account", jsonMainObj.getString("account"));
                            userInfoEditor.putString("profilepic", imgDecodableString);
                            userInfoEditor.commit();

                            responseMessage = jsonMainObj.getString("responseMessage");

                            if(jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                            {
                                new LocalNotification(RegisterActivity.this,jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationTitle)),jsonMainObj.getString(getApplicationContext().getResources().getString(R.string.notificationMessage))).sendNotification();
                            }

                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {

                            responseMessage = jsonMainObj.getString("responseMessage");
                            return "Failed";

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

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);

            if (result.equalsIgnoreCase("Success")) {

                userInfoEditor.putBoolean("new_user_registered_newapp", true);
                userInfoEditor.commit();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(responseMessage);
                // Setting OK Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        finish();
                        Intent i = new Intent(getApplicationContext(),HomeScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();


            } else if (result.equalsIgnoreCase("Failed")) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(responseMessage);
                // Setting OK Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();

                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();
            } else {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));
                // Setting OK Button
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();

                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();


            }

        }

    }


//    private class updatePromocode extends AsyncTask<String, Void, String> {
//
//        Context context;
//
//        String firstName, lastName, walletBalance;
//
//        public updatePromocode(Context context) {
//            this.context = context;
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Showing progress dialog
////            pDialog = new ProgressDialog(RegisterActivity.this);
////            pDialog.setMessage(getResources().getString(R.string.loading));
////            pDialog.setCancelable(false);
////            pDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... arg0) {
//
//            try {
//                // 3. build jsonObject
////                JSONObject jsonObject = new JSONObject();
////
////                jsonObject.accumulate("MDN", pref.getString("mobile_number",""));
////                jsonObject.accumulate("FirstName", Uri.encode(edtFullName.getText().toString(), "utf-8"));
////                jsonObject.accumulate("EMailId", Uri.encode(edtEmailID.getText().toString(),"utf-8"));
////                jsonObject.accumulate("DateOfBirth", Uri.encode(edtDate.getText().toString(),"utf-8"));
////
////
////
////                // 4. convert JSONObject to JSON to String
////                String json = jsonObject.toString();
////
////                // 5. set json to StringEntity
////                StringEntity se = new StringEntity(json);
//
//                WebServiceHandler serviceHandler = new WebServiceHandler(RegisterActivity.this);
//                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
//
//                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);
//
//                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
//
//                } else {
//                    Log.e("ServiceHandler", "Couldn't get any data from the url");
//                    return "Failure";
//                }
//
//
//            } catch (Exception e) {
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
//                return "Failure";
//            }
//
//         return "Sucess";
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            if (pDialog.isShowing())
//                pDialog.dismiss();
//
//            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);
//
//
//        }
//
//    }
//    private class Registration extends AsyncTask<String, Void, String> {
//
//        Context context;
//
//        String firstName, lastName, walletBalance;
//
//        public Registration(Context context) {
//            this.context = context;
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Showing progress dialog
//            pDialog = new ProgressDialog(RegisterActivity.this);
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... arg0) {
//
//            try {
//                // 3. build jsonObject
//                JSONObject jsonObject = new JSONObject();
//
//                jsonObject.accumulate("MDN", pref.getString("mobile_number", ""));
//                jsonObject.accumulate("FirstName", Uri.encode(edtFullName.getText().toString(), "utf-8"));
//                jsonObject.accumulate("EMailId", Uri.encode(edtEmailID.getText().toString(), "utf-8"));
//                jsonObject.accumulate("DateOfBirth", Uri.encode(edtDate.getText().toString(),"utf-8"));
//                jsonObject.accumulate("Gender", Uri.encode(gender.getSelectedItem().toString(),"utf-8"));
//                jsonObject.accumulate("NewMPIN", Uri.encode(edtMpin.getText().toString(),"utf-8"));
//                jsonObject.accumulate("LastName", Uri.encode(edtLastName.getText().toString(),"utf-8"));
//
//
//
//
//
//                // 4. convert JSONObject to JSON to String
//                String json = jsonObject.toString();
//
//                //// System.out.println(json);
//
//                // 5. set json to StringEntity
//                StringEntity se = new StringEntity(json);
//
//                WebServiceHandler serviceHandler = new WebServiceHandler(RegisterActivity.this, se);
//                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);
//
//                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);
//
//                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
//                    try {
//                        JSONObject jsonMainObj = new JSONObject(jsonStr);
//
//                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {
//
//                            userInfoEditor.putString("firstname", firstName);
//                            userInfoEditor.putString("lastname", lastName);
//                            userInfoEditor.putString("walletbalance", walletBalance);
//                            userInfoEditor.putString("usertype", jsonMainObj.getString("userType"));
//                            userInfoEditor.putBoolean("new_user_registered_newapp", true);
//                            userInfoEditor.putString("emailId", jsonMainObj.getString("emailId"));
//                            userInfoEditor.putString("account", jsonMainObj.getString("account"));
//                            userInfoEditor.putString("profilepic", imgDecodableString);
//                            userInfoEditor.commit();
//
//                            return "Success";
//
//                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
//                            return jsonMainObj.getString("responseMessage");
//
//                        } else {
//                            return "Failure";
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        return "Failure";
//                    }
//                } else {
//                    Log.e("ServiceHandler", "Couldn't get any data from the url");
//                    return "Failure";
//                }
//
//
//            } catch (Exception e) {
//                Log.e("ServiceHandler", "Couldn't get any data from the url");
//                return "Failure";
//            }
//
//
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            if (pDialog.isShowing())
//                pDialog.dismiss();
//
//            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);
//
//            if (result.equalsIgnoreCase("Success")) {
//
//                userInfoEditor.putBoolean("new_user_registered_newapp", true);
//                userInfoEditor.commit();
//
//                //        String mobilenumber =  pref.getString("mobile_number", "").substring(2, pref.getString("mobile_number", "").length());
//
////                String url = getResources().getString(R.string.promocode)+"?MDN="+mobilenumber+"&PROMOCODE="+edtpromocode.getText().toString().trim()+"&Transaction=REGISTRATION";
////                updatePromocode up = new updatePromocode(getApplicationContext());
////                up.execute(url);
//
////                finish();
////                Intent i= new Intent(RegisterActivity.this,ConfirmMobileActivity.class);
////                i.putExtra("mobile_number",edtRegMobile.getText().toString());
////                startActivity(i);
//
//
//            } else if (result.equalsIgnoreCase("Failure")) {
//
//
//
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();
//
//
//            } else {
//
//
//
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//            }
//
//        }
//
//    }

}
