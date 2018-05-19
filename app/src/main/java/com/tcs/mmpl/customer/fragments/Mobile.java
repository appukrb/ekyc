package com.tcs.mmpl.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.Activity.BrowsePlanTabActivity;
import com.tcs.mmpl.customer.Activity.HomeScreenActivity;
import com.tcs.mmpl.customer.Activity.PlanTypeList;
import com.tcs.mmpl.customer.Activity.ServiceProviderListActivity;
import com.tcs.mmpl.customer.Adapter.CropOptionAdapter;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.Contact;
import com.tcs.mmpl.customer.utility.CropOption;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.PopupBuilder;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 9/10/2015.
 */
public class Mobile extends Fragment {
    FontClass fontclass=new FontClass();
    Typeface typeface;

    private static final int MODE_PRIVATE = 0;
    private Button button;
    private ViewFlipper viewFlipper_mobile;
    private ProgressDialog pDialog;

    private Button btn_confirm,payment_mobile,btn_confirm_mobile_done;
    ImageView imgContacts;
    TextView btnOperator,btnChangeOperator;
    private Activity activity;
    private ConnectionDetector connectionDetector;

    ArrayList<String> operatorName,stateName ;

    private EditText edt_mobile,edtAmountMobile,edtMpin,edtpromocode,edtbillDueDate;
    private Spinner spinnerOperator;

    private RadioGroup radioGroup_mobile;
    private TextView txt_proceed_mobilenumber,txt_proceed_amount,txtSuccessRecharge,txtBrowsePlans,txtPlanType;

    private CheckBox chkQuickMmobile,chkFavMobile;

    private RadioButton radioPrepaid,radioPostpaid, selectedRadioButton ;

    private LinearLayout linPlanType,billDueDate;

    private SharedPreferences pref,userInfoPref;
    private SharedPreferences.Editor editor,userInfoEditor;

    private static final String TAG = Mobile.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private static final int REQUEST_CODE_PLAN_TYPE = 4;
    private Uri uriContact;
    private String contactID,parameterType,parameterValue,URL;


    private Uri mImageCaptureUri;
    private static int RESULT_LOAD_IMG = 1;
    private static final int CROP_FROM_CAMERA = 7;
    private static final int PICK_FROM_FILE = 8;
    private static final int PICK_FROM_GALLERY = 9;
    public static final int RESULT_OK= -1;

    ImageView profileImage;
    MyDBHelper dbHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_mobile,
                container, false);
        typeface=Typeface.createFromAsset(getActivity().getAssets(),"helvetica.otf");


        activity = getActivity();
        connectionDetector = new ConnectionDetector(activity);

        init(rootView);


        onClick();


        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)) {
                    if (viewFlipper_mobile.getDisplayedChild() == 2) {
                        viewFlipper_mobile.setDisplayedChild(1);
                    } else if (viewFlipper_mobile.getDisplayedChild() == 1) {
                        viewFlipper_mobile.setDisplayedChild(0);
                    } else {
                        getActivity().finish();
//                        Intent i = new Intent(getActivity(), HomeScreenActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(i);
                    }


                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    private void onClick() {
        // TODO Auto-generated method stub



        radioGroup_mobile.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioPrepaidMobile)
                {
                    billDueDate.setVisibility(View.GONE);
                    edtAmountMobile.setText("");

                    if(operatorName != null && linPlanType.getVisibility() == View.VISIBLE && !txtPlanType.getText().toString().trim().equalsIgnoreCase("")) {
                        txtBrowsePlans.setVisibility(View.VISIBLE);
                        linPlanType.setVisibility(View.VISIBLE);

                        billDueDate.setVisibility(View.GONE);
                        edt_mobile.setText("");
                        edtAmountMobile.setText("");

                    }
                    else if(operatorName != null && !btnOperator.getText().toString().trim().equalsIgnoreCase("TATA DOCOMO") ) {
                        txtBrowsePlans.setVisibility(View.VISIBLE);
                        linPlanType.setVisibility(View.GONE);


                        edt_mobile.setText("");
                        edtAmountMobile.setText("");
                    }
                }
                else if( checkedId == R.id.radioPostpaidMobile)
                {
                    txtBrowsePlans.setVisibility(View.GONE);
                    linPlanType.setVisibility(View.GONE);
                    edtAmountMobile.setText("");
                    billDueDate.setVisibility(View.GONE);
                    if(btnOperator.getText().toString().trim().equalsIgnoreCase("TATA DOCOMO")&&(edt_mobile.getText().toString().length() == 10) )
                    {
                        String outstandingurl = getResources().getString(R.string.outstandingamount) + "?mobileNo=" + pref.getString("mobile_number", "") + "&rechMobile=" + edt_mobile.getText().toString().trim();
                        TataDocomoPostpaidOustandingAmount tataDocomoPostpaidOustandingAmount = new TataDocomoPostpaidOustandingAmount(getActivity());
                        tataDocomoPostpaidOustandingAmount.execute(outstandingurl);
                    }
                }
            }
        });




        btnChangeOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {

                    Intent i = new Intent(getActivity(), ServiceProviderListActivity.class);
                    i.putExtra("url", getResources().getString(R.string.service_provider));
                    i.putExtra("type", "MOBILE");
                    startActivityForResult(i, 2);

                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                }
            }
        });


        txtPlanType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i =new Intent(getActivity(),PlanTypeList.class);
                    startActivityForResult(i, REQUEST_CODE_PLAN_TYPE);
            }
        });


        edt_mobile.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = edt_mobile.getRight()
                            - edt_mobile.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    // when EditBox has padding, adjust leftEdge like
                    // leftEdgeOfRightDrawable -= getResources().getDimension(R.dimen.edittext_padding_left_right);
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        // clicked on clear icon
                        //edt_mobile.setText("123");

                        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);

                        return true;
                    }
                }
                return false;
            }
        });

        payment_mobile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                AlertBuilder alert = new AlertBuilder(getActivity());

                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

                if (edt_mobile.getText().toString().trim().equalsIgnoreCase("") || edt_mobile.getText().toString().trim().length() < 10) {
                    alert.showAlert(getResources().getString(R.string.invalid_mobile));
                } else if (edtAmountMobile.getText().toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_amount));
                } else if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {
                    alert.showAlert(getResources().getString(R.string.invalid_mpin));
                } else if (edt_mobile.getText().toString().trim().equalsIgnoreCase("") || edtAmountMobile.getText().toString().trim().equalsIgnoreCase("") || edtMpin.getText().toString().trim().equalsIgnoreCase("") || btnOperator.getText().toString().trim().equalsIgnoreCase("")) {

                    alert.showAlert(activity.getResources().getString(R.string.validation));

                } else {

                    if (radioPrepaid.isChecked() || radioPostpaid.isChecked()) {



                            String mobilerecharge_url = "";
                            String mobileNo;
                            String category;
                            String rechargeMobile;
                            String amount = "";
                            String billerCode;
                            String mpin;
                            String BU = "";
                            String Region="";


                            if (radioPrepaid.isChecked()) {
                                //// System.out.println("Prepaid Selected");

                                mobileNo = pref.getString("mobile_number", "");
                                category ="PrePaid Recharge";
                                rechargeMobile = edt_mobile.getText().toString();
                                amount = edtAmountMobile.getText().toString();
                                billerCode = Uri.encode(btnOperator.getText().toString().trim(), "utf-8");
                                mpin = edtMpin.getText().toString();
                                BU = "PREPAID";
                                Region=Uri.encode(stateName.get(0), "utf-8");

                                parameterType = "Prepaid Mobile";
                                parameterValue = mobileNo + "|" + amount + "|" + rechargeMobile + "|" + category + "|" + billerCode;

                                String BingoString = mobileNo + "|"+""+"|"+mpin+"|"+category+ "|" + ""+"|"+""+"|"+""+"|"+billerCode+"|"+rechargeMobile+"|"+amount+"|"+Region+"|"+BU;
                                String res = Bingo.Bingo_one(BingoString);
//                                // System.out.println("BingoString"+BingoString);

                                category = Uri.encode("PrePaid Recharge", "utf-8");

                                if (btnOperator.getText().toString().trim().equalsIgnoreCase("TATA DOCOMO"))
                                    URL = activity.getResources().getString(R.string.billPayment) + "?mobileNo=" + mobileNo + "&Region=&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU;
                                else
                                    URL = activity.getResources().getString(R.string.billPayment) + "?mobileNo=" + mobileNo + "&Region=" + Uri.encode(stateName.get(0), "utf-8") + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU;


                                if (btnOperator.getText().toString().trim().equalsIgnoreCase("TATA DOCOMO"))
                                    mobilerecharge_url = activity.getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Region=&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU+"&Checksum="+res;
                                else

                                    mobilerecharge_url = activity.getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Region=" + Uri.encode(stateName.get(0), "utf-8") + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU+"&Checksum="+res;


//                                // System.out.println("MobileRechargeurl ......" + mobilerecharge_url);
                            } else if (radioPostpaid.isChecked()) {
                                //// System.out.println("Postpaid Selected");

                                mobileNo = pref.getString("mobile_number", "");
                                category = "PostPaid BillPayment";
                                rechargeMobile = edt_mobile.getText().toString();
                                amount = edtAmountMobile.getText().toString();
                                billerCode = Uri.encode(btnOperator.getText().toString().trim(), "utf-8");
                                mpin = edtMpin.getText().toString();
                                BU = "POSTPAID";
                                Region=Uri.encode(stateName.get(0), "utf-8");

                                parameterType = "Postpaid Mobile";
                                parameterValue = mobileNo + "|" + amount + "|" + rechargeMobile + "|" + category + "|" + billerCode;

                                String BingoString = mobileNo + "|"+""+"|"+mpin+"|"+category+ "|" + ""+"|"+""+"|"+""+"|"+billerCode+"|"+rechargeMobile+"|"+amount+"|"+Region+"|"+BU;
                                String res = Bingo.Bingo_one(BingoString);
//                                // System.out.println("BingoString"+BingoString);

                                category = Uri.encode("PostPaid BillPayment", "utf-8");

                                if (btnOperator.getText().toString().trim().equalsIgnoreCase("TATA DOCOMO"))
                                    URL = activity.getResources().getString(R.string.billPayment) + "?mobileNo=" + mobileNo + "&Region=&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU;
                                else
                                    URL = activity.getResources().getString(R.string.billPayment) + "?mobileNo=" + mobileNo + "&Region=" + Uri.encode(stateName.get(0), "utf-8") + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU;

                                if (btnOperator.getText().toString().trim().equalsIgnoreCase("TATA DOCOMO"))
                                    mobilerecharge_url = activity.getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Region=&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU+"&Checksum="+res;
                                else

                                    mobilerecharge_url = activity.getResources().getString(R.string.billPayment) + "?MPIN=" + mpin + "&mobileNo=" + mobileNo + "&Region=" + Uri.encode(stateName.get(0), "utf-8") + "&Category=" + category + "&billerCode=" + billerCode + "&rechMobile=" + rechargeMobile + "&Amount=" + amount + "&BU=" + BU+"&Checksum="+res;


//                                // System.out.println("MobileRechargeurl ......" + mobilerecharge_url);

                            }


                            CheckBalance checkBalance = new CheckBalance(getActivity());
                            if(checkBalance.getBalanceCheck(amount))
                            {

                                PopupBuilder popup = new PopupBuilder(getActivity());
                                popup.showPopup(checkBalance.getAmount(amount));

                                userInfoEditor.putString("transaction_url", mobilerecharge_url);
                                userInfoEditor.putString("transaction_method","POST2");
                                userInfoEditor.putString("transaction_flag","1");
                                userInfoEditor.commit();

                            }
                            else {
                                MobileRecharge mobileRecharge = new MobileRecharge(getActivity());
                                mobileRecharge.execute(mobilerecharge_url);

                            }



                    } else {
                        alert.showAlert(activity.getResources().getString(R.string.validation));
                    }



                }

            } else {
                alert.newUser();
            }

            }

        });


        edt_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                //// System.out.println(s + ":::::::::::::::::: " + s.length());
                if (s.length() == 10) {
                    if (connectionDetector.isConnectingToInternet()) {

                        String mobile_number = edt_mobile.getText().toString().trim();

                        mobile_number = edt_mobile.getText().toString().trim().replaceFirst("^0", "");

                        //// System.out.println(mobile_number);

                        if (mobile_number.length() == 10) {

                            String operatorurl = getResources().getString(R.string.getOperatorNew_url)+"?MDN=91" + mobile_number;

                            //// System.out.println(operatorurl);
                            GetOperator getOperator = new GetOperator(getActivity());
                            getOperator.execute(operatorurl);
                        } else {
                            //Toast.makeText(getActivity(), activity.getResources().getString(R.string.valid_mobile), Toast.LENGTH_LONG).show();
                        }

                    } else {

                        Toast.makeText(getActivity(), activity.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }
                } else {
                    btnOperator.setText("");
                    btnOperator.setHint("Operator");
                    btnChangeOperator.setVisibility(View.GONE);
                    txtBrowsePlans.setText("Browse Plans");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        btn_confirm_mobile_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewFlipper_mobile.setDisplayedChild(0);

            }
        });

        txtBrowsePlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    Intent i = new Intent(getActivity(), BrowsePlanTabActivity.class);
                    i.putExtra("operator_name", operatorName.get(0));
                    i.putExtra("state", stateName.get(0));
                    i.putExtra("mdn", edt_mobile.getText().toString().trim());
                    if (btnOperator.getText().toString().trim().equalsIgnoreCase("TATA DOCOMO"))
                        i.putExtra("category", txtPlanType.getText().toString().trim());
                    else
                        i.putExtra("category", "");

                    startActivityForResult(i, 3);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void init(View rootView) {
        // TODO Auto-generated method stub

        viewFlipper_mobile = (ViewFlipper) rootView.findViewById(R.id.viewflipper_mobile);
        fontclass.setFont(viewFlipper_mobile,typeface);

        dbHelper = new MyDBHelper(getActivity());

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        linPlanType=(LinearLayout)rootView.findViewById(R.id.linPlanType);

        txtBrowsePlans = (TextView)rootView.findViewById(R.id.txtBrowsePlans);
        btnOperator = (TextView)rootView.findViewById(R.id.btnOperator);
        btnChangeOperator = (TextView)rootView.findViewById(R.id.btnChangeOperator);
        txtPlanType = (TextView)rootView.findViewById(R.id.txtPlanType);
        btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm_mobile);
        btn_confirm.setTypeface(btn_confirm.getTypeface(),Typeface.BOLD);

        payment_mobile = (Button) rootView.findViewById(R.id.payment_mobile);
        payment_mobile.setTypeface(payment_mobile.getTypeface(),Typeface.BOLD);
        edt_mobile = (EditText)rootView.findViewById(R.id.edt_mobile);
        edtpromocode = (EditText)rootView.findViewById(R.id.edtPromocode);
       // spinnerOperator = (Spinner)rootView.findViewById(R.id.spinnerOperator);
        radioPrepaid = (RadioButton)rootView.findViewById(R.id.radioPrepaidMobile);
        radioPostpaid = (RadioButton)rootView.findViewById(R.id.radioPostpaidMobile);

        edtAmountMobile = (EditText)rootView.findViewById(R.id.edtAmountMobile);
        radioGroup_mobile = (RadioGroup)rootView.findViewById(R.id.radioGroup_mobile);
        chkFavMobile = (CheckBox)rootView.findViewById(R.id.chkFavMobile);


        txt_proceed_mobilenumber = (TextView)rootView.findViewById(R.id.txt_proceed_mobilenumber);
        txt_proceed_amount = (TextView)rootView.findViewById(R.id.txt_proceed_amount);
        edtMpin = (EditText)rootView.findViewById(R.id.edtMpin);

        txtSuccessRecharge=(TextView)rootView.findViewById(R.id.txtSuccessRecharge);
        btn_confirm_mobile_done = (Button)rootView.findViewById(R.id.btn_confirm_mobile_done);

        billDueDate=(LinearLayout)rootView.findViewById(R.id.billDueDate);
        edtbillDueDate= (EditText)rootView.findViewById(R.id.edtbillDueDate);



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_PICK_CONTACTS ) {
           // Log.d(TAG, "Response: " + data.toString());
            try {
                uriContact = data.getData();

                // retrieveContactName();
                retrieveContactNumber1();
                //retrieveContactPhoto();
            }
            catch (Exception e)
            {

            }

        }
        else if(requestCode == 2)
        {
            try {
                btnOperator.setText(data.getStringExtra("result"));

                if(radioPrepaid.isChecked()) {
                    if (data.getStringExtra("result").trim().equalsIgnoreCase("TATA DOCOMO")) {
                        txtBrowsePlans.setVisibility(View.VISIBLE);
                        linPlanType.setVisibility(View.VISIBLE);
                    } else {
                        txtBrowsePlans.setVisibility(View.VISIBLE);
                        linPlanType.setVisibility(View.GONE);
                    }
                }else if(radioPostpaid.isChecked()){
                    if (data.getStringExtra("result").trim().equalsIgnoreCase("TATA DOCOMO")) {
                        String outstangingurl = getResources().getString(R.string.outstandingamount) + "?mobileNo=" + pref.getString("mobile_number", "") + "&rechMobile=" + edt_mobile.getText().toString().trim();
                         TataDocomoPostpaidOustandingAmount tataDocomoPostpaidOustandingAmount = new TataDocomoPostpaidOustandingAmount(getActivity());
                        tataDocomoPostpaidOustandingAmount.execute(outstangingurl);
                    }
                    else {
                        edtAmountMobile.setText("");
                        billDueDate.setVisibility(View.GONE);
                    }

                }
                else
                {
                    txtBrowsePlans.setVisibility(View.GONE);
                    linPlanType.setVisibility(View.GONE);
                }
                operatorName = new ArrayList<String>();
                operatorName.add(data.getStringExtra("result"));
            }
            catch (Exception e)
            {

            }
        }
        else if(requestCode == 3)
        {
            try
            {

                String Stramt = data.getStringExtra("result");
                float amt = Float.parseFloat(Stramt);
                int amt1 = (int)amt;
                edtAmountMobile.setText(String.valueOf(amt1));
            }
            catch (Exception e)
            {

            }
        }
        else if(requestCode == REQUEST_CODE_PLAN_TYPE)
        {
            try
            {
                String strPlan = data.getStringExtra("result");
                txtPlanType.setText(String.valueOf(strPlan));
                txtBrowsePlans.setVisibility(View.VISIBLE);
            }
            catch (Exception e)
            {

            }
        }

    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities( intent, 0 );

        int size = list.size();

        // size = 0;
        if (size == 0) {
            Toast.makeText(getActivity(), "Can not find image crop app", Toast.LENGTH_SHORT).show();
//            intent.setData(mImageCaptureUri);
//
//            intent.putExtra("outputX", 200);
//            intent.putExtra("outputY", 200);
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("scale", true);
//            intent.putExtra("return-data", true);
//            Intent i = new Intent(intent);
//            startActivityForResult(i, PICK_FROM_GALLERY);
            return;
        } else {
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res	= list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title 	= getActivity().getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon		= getActivity().getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent= new Intent(intent);

                    co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getActivity(), cropOptions);


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Crop App");
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) {
                        try {
                            startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getActivity(),"Activity is not found",Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (mImageCaptureUri != null ) {
                            getActivity().getContentResolver().delete(mImageCaptureUri, null, null );
                            mImageCaptureUri = null;
                        }
                    }
                } );

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

    private void retrieveContactNumber1() {

        try
        {
            String id = uriContact.getLastPathSegment();

            Contact contact = new Contact(getActivity(),edt_mobile);
            contact.getContactNumber(id);
           // edt_mobile.setText(contactNumber);

//           // query for phone numbers for the selected contact id
//            Cursor c = getActivity().getContentResolver().query(
//                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
//                    new String[]{id}, null);
//
//            int phoneIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//            int phoneType = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
//
//            if(c.getCount() > 1) { // contact has multiple phone numbers
//                final CharSequence[] numbers = new CharSequence[c.getCount()];
//                final CharSequence[] alteredNumbers = new CharSequence[c.getCount()];
//                int i=0;
//                if(c.moveToFirst()) {
//                    while(!c.isAfterLast()) { // for each phone number, add it to the numbers array
//                        String type = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(this.getResources(), c.getInt(phoneType), ""); // insert a type string in front of the number
//                        String number = type + ": " + c.getString(phoneIdx);
//                        numbers[i++] = number;
//                        c.moveToNext();
//                    }
//
//                    int count = numbers.length;
//                    Set<CharSequence> val = new HashSet<CharSequence>();
//
//                    for(int j=0;j<count;j++)
//                    {
//                        val.add(numbers[j]);
//                    }
//
//                    int k = -1;
//                    Iterator it = val.iterator();
//
//                    while (it.hasNext())
//                    {
//                        k++;
//
//                        alteredNumbers[k] = (CharSequence)it.next();
//                    }
//                    // build and show a simple dialog that allows the user to select a number
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle("Select number");
//                    builder.setItems(alteredNumbers, new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int item) {
//                            String number = (String) alteredNumbers[item];
//                            int index = number.indexOf(":");
//                            number = number.substring(index + 2);
//                            edt_mobile.setText(number);
//                        }
//                    });
//                    AlertDialog alert = builder.create();
//                    alert.setOwnerActivity(getActivity());
//                    alert.show();
//
//                } else Log.w(TAG, "No results");
//            } else if(c.getCount() == 1) {
//                // contact has a single phone number, so there's no need to display a second dialog
//
//                //// System.out.println("Coming inside................");
//                c.moveToFirst();
//                edt_mobile.setText(c.getString(phoneIdx).toString());
//            }
//
//            c.close();

//            if (cursorPhone.moveToFirst()) {
//                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                //// System.out.println("First......... "+contactNumber);
//                try
//                {
//
//
//                    contactNumber = contactNumber.replaceAll("\\s","");
//                    contactNumber = contactNumber.replaceFirst("^0", "");
//                    //// System.out.println("Removing zero......... "+contactNumber);
//                }
//                catch(Exception e)
//                {
//                    //// System.out.println("Error Here in contact");
//                }
//                if(contactNumber.contains("+91"))
//                {
//                    //// System.out.println("Searching +91......... "+contactNumber);
//                    contactNumber = contactNumber.replaceAll("\\+91", "");
//                    //// System.out.println("Removing +91......... "+contactNumber);
//                }
//            }
//
//            cursorPhone.close();
//
//            Log.d(TAG, "Contact Phone Number: " + contactNumber);
//            if(!contactNumber.equalsIgnoreCase("null")&& contactNumber != null)
//            {
//                edt_mobile.setText(contactNumber);
//            }
        }
        catch(NullPointerException ex)
        {


        }
        catch (Exception e) {
            // TODO: handle exception
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    private void openAlert(AlertDialog.Builder alertDialog) {
                // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.tick);
        // Setting OK Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

                dialog.cancel();

                edt_mobile.setText("");
                edtAmountMobile.setText("");
                edtMpin.setText("");
                btnOperator.setText("");
                edtpromocode.setText("");
                txtBrowsePlans.setVisibility(View.GONE);
                radioPrepaid.setChecked(true);
                chkFavMobile.setChecked(false);
                btnChangeOperator.setVisibility(View.GONE);


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                dialog.cancel();
                getActivity().finish();
                Intent i = new Intent(activity, HomeScreenActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(i);

            }
        });
        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }

    private class GetOperator extends AsyncTask<String, Void, String> {

        Context context;


        String firstName, lastName, walletBalance;

        public GetOperator(Context context) {
            this.context = context;
            operatorName = new ArrayList<String>();
            stateName = new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Operator...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        if(jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success"))
                        {
                            operatorName.add(jsonMainObj.getString("operatorName"));
                            stateName.add(jsonMainObj.getString("state"));

                            return "Success";

                        }
                        else
                        {
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

                btnOperator.setText(operatorName.get(0).toUpperCase());

                if(radioPrepaid.isChecked()){
                if(operatorName.get(0).toUpperCase().equalsIgnoreCase("TATA DOCOMO")) {
                    txtBrowsePlans.setVisibility(View.GONE);
                    linPlanType.setVisibility(View.VISIBLE);
                    btnChangeOperator.setVisibility(View.VISIBLE);
                }
                else {
                    txtBrowsePlans.setVisibility(View.VISIBLE);
                    linPlanType.setVisibility(View.GONE);
                    btnChangeOperator.setVisibility(View.VISIBLE);
                }
            }
            else{
                txtBrowsePlans.setVisibility(View.GONE);
                linPlanType.setVisibility(View.GONE);
                btnChangeOperator.setVisibility(View.VISIBLE);

                    if(operatorName.get(0).toUpperCase().equalsIgnoreCase("TATA DOCOMO")) {
                        String outstandingurl = getResources().getString(R.string.outstandingamount) + "?mobileNo=" + pref.getString("mobile_number", "") + "&rechMobile=" + edt_mobile.getText().toString().trim();
                        TataDocomoPostpaidOustandingAmount tataDocomoPostpaidOustandingAmount = new TataDocomoPostpaidOustandingAmount(getActivity());
                        tataDocomoPostpaidOustandingAmount.execute(outstandingurl);
                    }


            }



            } else if (result.equalsIgnoreCase("Failure")) {

                Toast.makeText(getActivity(),getResources().getString(R.string.apidown),Toast.LENGTH_LONG).show();;

            } else {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

        }

    }

    private class updatePromocode extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        public updatePromocode(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(RegisterActivity.this);
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
//                JSONObject jsonObject = new JSONObject();
//
//                jsonObject.accumulate("MDN", pref.getString("mobile_number",""));
//                jsonObject.accumulate("FirstName", Uri.encode(edtFullName.getText().toString(), "utf-8"));
//                jsonObject.accumulate("EMailId", Uri.encode(edtEmailID.getText().toString(),"utf-8"));
//                jsonObject.accumulate("DateOfBirth", Uri.encode(edtDate.getText().toString(),"utf-8"));
//
//
//
//                // 4. convert JSONObject to JSON to String
//                String json = jsonObject.toString();
//
//                // 5. set json to StringEntity
//                StringEntity se = new StringEntity(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {

                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                    return "Failure";
                }


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }

            return "Sucess";
        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);


        }

    }

    private class MobileRecharge extends AsyncTask<String, Void, String> {

        Context context;
        String responseMessage;

        String firstName, lastName, walletBalance;

        public MobileRecharge(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

//                // System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {


                            responseMessage = jsonMainObj.getString("responseMessage");

                            if(jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                            {
                                new LocalNotification(getActivity(),jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationTitle)),jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationMessage))).sendNotification();
                            }

                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            return jsonMainObj.getString("responseMessage");

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

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {

                String url = getResources().getString(R.string.promocode)+"?MDN="+ pref.getString("mobile_number", "")+"&PROMOCODE="+edtpromocode.getText().toString().trim()+"&Transaction=Mobile";
                updatePromocode up = new updatePromocode(getActivity());
                up.execute(url);


                if(chkFavMobile.isChecked()) {
                    String addFavorites_url = getActivity().getResources().getString(R.string.addfavourites);
                    AddFavorites addFavorites = new AddFavorites(getActivity());
                    addFavorites.execute(addFavorites_url);
                }

                AlertBuilder alert = new AlertBuilder(getActivity());
                AlertDialog.Builder alertDialog = alert.showRetryAlert(responseMessage+getResources().getString(R.string.make_payment));
                openAlert(alertDialog);


            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

                AlertBuilder alert = new AlertBuilder(getActivity());
                alert.showAlert(result);
            }

        }

    }

    private class AddFavorites extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        public AddFavorites(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setCancelable(false);
//            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("MDN", pref.getString("mobile_number", ""));
                jsonObject.accumulate("parameterType",parameterType);
                jsonObject.accumulate("prarameterValue", parameterValue);
                jsonObject.accumulate("URL", URL);

                // 4. convert JSONObject to JSON to String
                String json = jsonObject.toString();

                //// System.out.println(json);
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                //// System.out.println(json);

                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity(), se);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            return jsonMainObj.getString("responseMessage");


                        } else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {
                            return jsonMainObj.getString("responseMessage");

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
            //// System.out.println("Result" + result);
        }

    }

    private class TataDocomoPostpaidOustandingAmount extends AsyncTask<String, Void, String> {
        Context context;
        ProgressDialog pDialog;
        String balance;

        public TataDocomoPostpaidOustandingAmount(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        if(jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("Success"))
                        {
                            editor.putString("balance", jsonMainObj.getString("balance"));
                            editor.putString("billDueDate1", jsonMainObj.getString("billDueDate"));
                            editor.commit();

                            return "Success";

                        }else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("FAILURE")) {

                            editor.putString("responseMessage", jsonMainObj.getString("responseMessage"));
                            editor.commit();
                            return "failure";
                        }

                        else
                        {

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
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.d("Result::::", ">" + result);

            if (result.equalsIgnoreCase("Success"))
            {
                String DueDate,AmountMobile;
                DueDate=pref.getString("billDueDate1","");
                AmountMobile=pref.getString("balance","");
                edtAmountMobile.setText(AmountMobile);

                if(!DueDate.trim().equalsIgnoreCase("NULL"))
                {
                    edtbillDueDate.setText(DueDate);
                    edtbillDueDate.setEnabled(false);
                    billDueDate.setVisibility(View.VISIBLE);
                }

            } else if (result.equalsIgnoreCase("Failure"))
            {
                billDueDate.setVisibility(View.GONE);
                edtAmountMobile.setText("");
                String responseMessage1 = pref.getString("responseMessage","");
                //// System.out.println(responseMessage1+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                Toast.makeText(getActivity(),responseMessage1,Toast.LENGTH_LONG).show();

            }

//            edtAmountMobile.setText(jsonMainObj.getString("balance"));
//            String responseMessage1 = pref.getString("responseMessage","");
//            String edtbillDueDate1=(jsonMainObj.getString("billDueDate"));
//            //// System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+edtbillDueDate1);
//
//            billDueDate.setVisibility(View.VISIBLE);
//            edtbillDueDate.setText(edtbillDueDate1);
//            edtbillDueDate.setEnabled(false);
//            //// System.out.println(jsonMainObj.getString("balance")+jsonMainObj.getString("billDueDate")+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");



        }
    }

}
