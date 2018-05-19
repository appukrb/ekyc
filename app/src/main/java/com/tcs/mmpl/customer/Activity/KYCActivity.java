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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.KycList;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class KYCActivity extends Activity {

    private ImageView imgKyc, imgUpload, imgDocBack;
    private EditText edtName, edtLastName, edtEmailID, edtMother, edtPincode;
    private TextView txtDocument, txtDOB;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, SELECT_DOCUMENT = 2, SELECT_IMAGE = 3, SELECT_DOCUMENT_BACK = 6;
    private String userChoosenTask;
    private SharedPreferences pref, userInfoPref;
    private SharedPreferences.Editor editor, userInfoEditor;
    private String encodedImage = "", encodedPhotoImage = "", encodedDocBack = "";
    private ConnectionDetector connectionDetector;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Calendar cal;
    private int year, month, day;
    private CheckBox chkTerms;

    private String selectedPath1, selectedPath2, selectedPath3;

    private TextView txtImage, txtBackImage, txtFrontImage;

    private LinearLayout linMain, linFirstName, linLastName, linEmail, linDOB, linMother, linPincode, linIDProof, linDocument, linDocBack, linImage;
    private Button btnSubmit;

    private ArrayList<KycList> kycLists;
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {


            txtDOB.setText(selectedDay + " / " + (selectedMonth + 1) + " / "
                    + selectedYear);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_kyc);

        pref = getApplicationContext().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();
        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        connectionDetector = new ConnectionDetector(getApplicationContext());

        txtImage = (TextView) findViewById(R.id.txtImage);
        txtBackImage = (TextView) findViewById(R.id.txtBackImage);
        txtFrontImage = (TextView) findViewById(R.id.txtFrontImage);

        imgKyc = (ImageView) findViewById(R.id.imgKyc);
        imgUpload = (ImageView) findViewById(R.id.imgUpload);
        imgDocBack = (ImageView) findViewById(R.id.imgUploadDocBack);
        edtName = (EditText) findViewById(R.id.edtName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtEmailID = (EditText) findViewById(R.id.edtEmailID);
        edtMother = (EditText) findViewById(R.id.edtMother);
        edtPincode = (EditText) findViewById(R.id.edtPincode);
        txtDocument = (TextView) findViewById(R.id.txtDocument);
        chkTerms = (CheckBox) findViewById(R.id.chkTerms);
        txtDOB = (TextView) findViewById(R.id.txtDOB);

        linMain = (LinearLayout) findViewById(R.id.linMain);
        linFirstName = (LinearLayout) findViewById(R.id.linFirstName);
        linLastName = (LinearLayout) findViewById(R.id.linLastName);
        linEmail = (LinearLayout) findViewById(R.id.linEmail);
        linDOB = (LinearLayout) findViewById(R.id.linDOB);
        linMother = (LinearLayout) findViewById(R.id.linMother);
        linPincode = (LinearLayout) findViewById(R.id.linPincode);
        linIDProof = (LinearLayout) findViewById(R.id.linIDProof);
        linDocument = (LinearLayout) findViewById(R.id.linDocument);
        linDocBack = (LinearLayout) findViewById(R.id.linDocBack);
        linImage = (LinearLayout) findViewById(R.id.linImage);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        ((TextView) findViewById(R.id.txtTerms)).setText(Html.fromHtml("<u>I agree to the T&amp;C</u>"));

        kycLists = (ArrayList<KycList>) getIntent().getSerializableExtra("kyclist");

        if (kycLists.get(0).getKycStatus().trim().equalsIgnoreCase("REJECTED"))
        showUpdateScreen();
        else
            btnSubmit.setText(getResources().getString(R.string.button_submit));


        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                txtDOB.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        edtName.setText(userInfoPref.getString("firstname", ""));
        edtLastName.setText(userInfoPref.getString("lastname", ""));
        if (getIntent().getStringExtra("status").trim().equalsIgnoreCase("REJECTED")) {
            edtEmailID.setText(getIntent().getStringExtra("email"));
            edtEmailID.setEnabled(false);


        } else {
            edtEmailID.setText(userInfoPref.getString("emailId", ""));

        }

    }

    private void showUpdateScreen() {

        linIDProof.setVisibility(View.GONE);
        linFirstName.setVisibility(View.GONE);
        linLastName.setVisibility(View.GONE);
        linEmail.setVisibility(View.GONE);
        linDOB.setVisibility(View.GONE);
        linMother.setVisibility(View.GONE);
        linPincode.setVisibility(View.GONE);

        chkTerms.setChecked(true);
        btnSubmit.setText(getResources().getString(R.string.button_update));


        if (kycLists.get(0).getProfileStatus().trim().equalsIgnoreCase("REJECT"))
            linImage.setVisibility(View.VISIBLE);
        else {
            linImage.setVisibility(View.GONE);
            encodedPhotoImage = "NA";
        }

        if (kycLists.get(0).getPoaStatus().trim().equalsIgnoreCase("REJECT"))
            linDocument.setVisibility(View.VISIBLE);
        else {
            linDocument.setVisibility(View.GONE);
            encodedImage = "NA";

        }

        if (kycLists.get(0).getPoiStatus().trim().equalsIgnoreCase("REJECT"))
            linDocBack.setVisibility(View.VISIBLE);
        else {
            linDocBack.setVisibility(View.GONE);
            encodedDocBack = "NA";
        }

    }

    public void saveKyc(View v) {
        Button btnName = (Button) findViewById(v.getId());
        if (btnName.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.button_submit))) {
            AlertBuilder alertBuilder = new AlertBuilder(KYCActivity.this);
            if (edtName.getText().toString().trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.valid_first_name));
            } else if (edtLastName.getText().toString().trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.valid_last_name));
            } else if (edtEmailID.getText().toString().trim().equalsIgnoreCase("") || !android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmailID.getText().toString().trim()).matches()) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_email));
            } else if (txtDOB.getText().toString().trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_dob));
            } else if (edtMother.getText().toString().trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_mother_name));
            } else if (edtPincode.getText().toString().trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_pincode));
            } else if (txtDocument.getText().toString().trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_document));
            } else if (encodedImage.trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_front_image));
            } else if (encodedPhotoImage.trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_photo));
            } else if (encodedDocBack.trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_back_image));
            } else if (!chkTerms.isChecked()) {
                alertBuilder.showAlert(getResources().getString(R.string.terms_and_condition));
            } else {
                if (connectionDetector.isConnectingToInternet()) {
                    SaveKycDetails saveKycDetails = new SaveKycDetails(getApplicationContext());
                    saveKycDetails.execute();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }
            }
        } else if (btnName.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.button_update))) {



            AlertBuilder alertBuilder = new AlertBuilder(KYCActivity.this);

            if (encodedImage.trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_front_image));
            } else if (encodedPhotoImage.trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_photo));
            } else if (encodedDocBack.trim().equalsIgnoreCase("")) {
                alertBuilder.showAlert(getResources().getString(R.string.invalid_back_image));
            }
            else if (!chkTerms.isChecked()) {
                alertBuilder.showAlert(getResources().getString(R.string.terms_and_condition));
            }
            else
            {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("kycID", kycLists.get(0).getKycID().trim());
                    jsonObject.accumulate("profileStatus", kycLists.get(0).getProfileStatus().trim());
                    jsonObject.accumulate("poaStatus", kycLists.get(0).getPoaStatus().trim());
                    jsonObject.accumulate("poiStatus", kycLists.get(0).getPoiStatus().trim());
                    jsonObject.accumulate("profileImage", encodedPhotoImage);
                    jsonObject.accumulate("poaImage", encodedImage);
                    jsonObject.accumulate("poiImage", encodedDocBack);





                    if (connectionDetector.isConnectingToInternet()) {
                        UpdateKycDetails updateKycDetails = new UpdateKycDetails(getApplicationContext(),jsonObject);
                        updateKycDetails.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e)
                {

                }


            }




        }


    }

    public void choosePhoto(View v) {
        //  selectImage();
        galleryIntent();
    }

    public void chooseImage(View v) {
        galleryDocIntentFront();
    }

    public void chooseDocBack(View v) {
        galleryDocIntentBack();
    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(KYCActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //   boolean result=Utility.checkPermission(KYCActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    // if(result)
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    //  if(result)
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void galleryDocIntentFront() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_IMAGE);
    }

    private void galleryDocIntentBack() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_DOCUMENT_BACK);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void openKycDocument(View v) {
        Intent i = new Intent(getApplicationContext(), KycDocumentList.class);
        startActivityForResult(i, SELECT_DOCUMENT);

    }

    public void readMore(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(getResources().getString(R.string.kyc_html_url)));
        startActivity(i);
    }

    public void openDateofBirth(View v) {
        fromDatePickerDialog.show();


        // showDialog(0);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            Uri selectedImageUri = data.getData();
            if (requestCode == SELECT_FILE) {
                selectedPath1 = getPath(selectedImageUri);


                //// System.out.println("selectedPath1:::" + selectedPath1);
                onSelectImageFromGalleryResult(data);

                txtImage.setText("Photo : " + selectedPath1.substring(selectedPath1.lastIndexOf("/") + 1, selectedPath1.length()));
            } else if (requestCode == REQUEST_CAMERA)
                getPath(selectedImageUri);
            else if (requestCode == SELECT_DOCUMENT) {
                txtDocument.setText(data.getStringExtra("result"));
            } else if (requestCode == SELECT_IMAGE) {
                selectedPath2 = getPath(selectedImageUri);
                //// System.out.println("selectedPath2:::" + selectedPath2);
                txtFrontImage.setText("Proof Of Address : " + selectedPath2.substring(selectedPath2.lastIndexOf("/") + 1, selectedPath2.length()));
                onSelectFromGalleryResult(data);

            } else if (requestCode == SELECT_DOCUMENT_BACK) {
                selectedPath3 = getPath(selectedImageUri);
                //// System.out.println("selectedPath3:::" + selectedPath3);

                txtBackImage.setText("Proof Of Identity : " + selectedPath3.substring(selectedPath3.lastIndexOf("/") + 1, selectedPath3.length()));
                Bitmap bm = null;
                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

//                imgDocBack.setImageBitmap(bm);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
                encodedDocBack = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            }
        }

    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        encodedImage = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        imgKyc.setImageBitmap(bm);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
        encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    @SuppressWarnings("deprecation")
    private void onSelectImageFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        //   imgUpload.setImageBitmap(bm);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
        encodedPhotoImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private class SaveKycDetails extends AsyncTask<String, Void, String> {

        private Context context;
        private ProgressDialog pDialog;
        private String responseMessage = "";

        public SaveKycDetails(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(KYCActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("mdn", pref.getString("mobile_number", ""));
                jsonObject.accumulate("dob", txtDOB.getText().toString().trim());
                jsonObject.accumulate("firstName", edtName.getText().toString().trim());
                jsonObject.accumulate("lastName", edtLastName.getText().toString().trim());
                jsonObject.accumulate("docType", txtDocument.getText().toString().trim());
                jsonObject.accumulate("emailID", edtEmailID.getText().toString().trim());
                jsonObject.accumulate("mothermaidenname", edtMother.getText().toString().trim());
                jsonObject.accumulate("pincode", edtPincode.getText().toString().trim());
                jsonObject.accumulate("docImage",encodedImage);
                jsonObject.accumulate("photo",  encodedPhotoImage);
                jsonObject.accumulate("docBack", encodedDocBack);

                String json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                WebServiceHandler serviceHandler = new WebServiceHandler(KYCActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(getResources().getString(R.string.saveKycDetail), WebServiceHandler.POST);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);
                JSONObject jsonObject1 = new JSONObject(jsonStr);
                if (jsonObject1.getString("responseStatus").trim().equalsIgnoreCase("Success")) {
                    responseMessage = jsonObject1.getString("responseMessage");
                    return "Success";
                } else {
                    responseMessage = jsonObject1.getString("responseMessage");
                    return "Failure";
                }

            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failed";
            }


        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (result.trim().equalsIgnoreCase("Success")) {
                AlertBuilder alertBuilder = new AlertBuilder(KYCActivity.this);
                android.app.AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(responseMessage);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        encodedImage = "";
                        edtName.setText("");
                        edtLastName.setText("");
                        edtEmailID.setText("");
                        edtMother.setText("");
                        edtPincode.setText("");
                        txtDOB.setText("");
                        txtDocument.setText("");
                        finish();

                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();

            } else if (result.trim().equalsIgnoreCase("Failure")) {
                AlertBuilder alertBuilder = new AlertBuilder(KYCActivity.this);
                alertBuilder.showAlert(responseMessage);
            } else {
                AlertBuilder alertBuilder = new AlertBuilder(KYCActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));

            }
        }

    }

    private class UpdateKycDetails extends AsyncTask<String, Void, String> {

        private Context context;
        private ProgressDialog pDialog;
        private String responseMessage = "";
        private JSONObject jsonObject;

        public UpdateKycDetails(Context context,JSONObject jsonObject) {
            this.context = context;
            this.jsonObject = jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(KYCActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                String json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                WebServiceHandler serviceHandler = new WebServiceHandler(KYCActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(getResources().getString(R.string.updateKycDeatil), WebServiceHandler.POST);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);
                JSONObject jsonObject1 = new JSONObject(jsonStr);
                if (jsonObject1.getString("responseStatus").trim().equalsIgnoreCase("Success")) {
                    responseMessage = jsonObject1.getString("responseMessage");
                    return "Success";
                } else {
                    responseMessage = jsonObject1.getString("responseMessage");
                    return "Failure";
                }

            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failed";
            }


        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (result.trim().equalsIgnoreCase("Success")) {
                AlertBuilder alertBuilder = new AlertBuilder(KYCActivity.this);
                android.app.AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(responseMessage);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        finish();

                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();

            } else if (result.trim().equalsIgnoreCase("Failure")) {
                AlertBuilder alertBuilder = new AlertBuilder(KYCActivity.this);
                alertBuilder.showAlert(responseMessage);
            } else {
                AlertBuilder alertBuilder = new AlertBuilder(KYCActivity.this);
                alertBuilder.showAlert(getResources().getString(R.string.apidown));

            }
        }

    }

}
