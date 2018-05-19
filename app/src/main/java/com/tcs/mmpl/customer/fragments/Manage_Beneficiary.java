package com.tcs.mmpl.customer.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.Activity.AccountTypeActivity;
import com.tcs.mmpl.customer.Activity.PayNowBeneficiaryActivity;
import com.tcs.mmpl.customer.Activity.SelectIFSCCodeActivity;
import com.tcs.mmpl.customer.Activity.ViewBeneficiaryActivity;
import com.tcs.mmpl.customer.Adapter.BeneficiaryDeleteAdapter;
import com.tcs.mmpl.customer.Adapter.BeneficiaryDetails;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 9/16/2015.
 */
public class Manage_Beneficiary extends ListFragment {
    private static final int MODE_PRIVATE = 0;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    private Button btnChooseBeneficiary, btnNextBeneficiary,btnBackBeneficiary,btnAddBeneficiary,btnAddAnotherBeneficiary,btnMpinNext,btnDeleteSubmit,btnDeleteBeneficiary,btnBackConfirmBeneficiary,btnHomeBeneficiary;
    private ViewFlipper viewFlipper_managebeneficiary;
    Spinner spinnerAccountType;
    String[] accountType;
    EditText edtBeneficiaryName, edtBeneficiaryNickName, edtIFSCCode, edtAccountNumber,edtBeneficiaryMpin;
    EditText edtMpinToBank;
    String benCode;

    RadioButton radioButtonAddBankBeneficary,radioButtonDeleteBankBeneficary,radioButtonViewBankBeneficary;
    TextView txtBeneficiaryName,txtBeneficiaryNickName,txtIFSCCode,txtAccountNumber,txtSelectIFSCCode;
    TextView txtAddStatusDone,txtAddBeneficiaryNameDone,txtAddBeneficiaryNickNameDone,txtAddIFSCCode,txtAddBankAccountNumber,txtAddBankAccountType,txtRemarkDone,txtDateAndTimeDone;
    TextView  txtDeleteBeneficiaryNickName, txtDeleteBankName , txtDeleteIFSCCode, txtDeleteAccountNumber, txtDeleteAccountType;
    EditText edtDeleteBeneficiaryMpin;
    private ConnectionDetector connectionDetector;
    private ProgressDialog pDialog;

    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    MyDBHelper dbHelper;

    RadioButton radioButton1,radioButton2,radioButton3,radioButton4,radioButton5;

    TextView txtDeleteStatusDone,txtDeleteBeneficiaryNickNameDone, txtDeleteBankAccountNumberDone,txtDeleteRemarkDone, txtDeleteDateAndTimeDone,txtAccountType,txtAccountTypeSelect;
    Button btnDeleteAnotherBeneficiary;
    static int flag;


    // private Button btn_confirm;
    //  private RadioButton mini_statement;

    public static Manage_Beneficiary newInstance(int index) {
        Manage_Beneficiary f = new Manage_Beneficiary();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        flag = index;
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_managebeneficiary,
                container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");

        init(rootView);

        onClick();


        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)) {
                      if (viewFlipper_managebeneficiary.getDisplayedChild() == 4) {
                        viewFlipper_managebeneficiary.setDisplayedChild(0);
                    } else if (viewFlipper_managebeneficiary.getDisplayedChild() == 3) {
                        //viewFlipper_managebeneficiary.setDisplayedChild(2);
                    } else if (viewFlipper_managebeneficiary.getDisplayedChild() == 2) {
                        //viewFlipper_managebeneficiary.setDisplayedChild(1);
                    } else if (viewFlipper_managebeneficiary.getDisplayedChild() == 1) {
                       // viewFlipper_managebeneficiary.setDisplayedChild(0);
                    } else {
                        getActivity().finish();
//                          Intent i = new Intent(getActivity(),HomeScreenActivity.class);
//                          i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                          startActivity(i);
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

        btnChooseBeneficiary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                edtBeneficiaryName.setText("");
                edtBeneficiaryNickName.setText("");
                edtIFSCCode.setText("");
                edtAccountNumber.setText("");
                txtAccountTypeSelect.setText("");
                txtSelectIFSCCode.setText("");
                edtBeneficiaryMpin.setText("");

                AlertBuilder alert = new AlertBuilder(getActivity());
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {
                if(radioButtonAddBankBeneficary.isChecked()) {
                    viewFlipper_managebeneficiary.setDisplayedChild(1);
                }
                else if(radioButtonDeleteBankBeneficary.isChecked())
                {

                    if (connectionDetector.isConnectingToInternet()) {

                            String beneficiaryurl = getActivity().getResources().getString(R.string.getBeneficiary) + "?MDN=" + pref.getString("mobile_number", "") ;
                            //  String beneficiaryurl = getActivity().getResources().getString(R.string.getBeneficiary) + "?MDN=918089209020&Mpin=1234";
                            GetBeneficiary getBeneficiary = new GetBeneficiary(getActivity());
                            getBeneficiary.execute(beneficiaryurl);

                    } else {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                    }
                   // viewFlipper_managebeneficiary.setDisplayedChild(4);
                }
                else if(radioButtonViewBankBeneficary.isChecked())
                {
                    Intent intent = new Intent(getActivity(), ViewBeneficiaryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else
                {
                    alert.showAlert(getActivity().getResources().getString(R.string.validation));
                   // Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.validation),Toast.LENGTH_LONG).show();
                }

            }
            else
            {
                alert.newUser();

            }


            }
        });


        btnBackBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper_managebeneficiary.setDisplayedChild(0);
            }
        });

//        btnNextBeneficiary.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//
//                AlertBuilder alert = new AlertBuilder(getActivity());
//
//                String bName = edtBeneficiaryName.getText().toString().trim();
//                String bNickName = edtBeneficiaryNickName.getText().toString().trim();
//                String ifsc = "";
//
//                if(txtSelectIFSCCode.getText().toString().trim().equalsIgnoreCase("Others"))
//                ifsc = edtIFSCCode.getText().toString().trim();
//                else
//                ifsc = txtSelectIFSCCode.getText().toString().trim();
//                String accountNumber = edtAccountNumber.getText().toString().trim();
//
//                if (bName.equalsIgnoreCase("") || bNickName.equalsIgnoreCase("") || ifsc.equalsIgnoreCase("") || accountNumber.equalsIgnoreCase("") || txtAccountTypeSelect.getText().toString().trim().equalsIgnoreCase(""))
//                {
//                    //Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.validation),Toast.LENGTH_LONG).show();
//                    alert.showAlert(getActivity().getResources().getString(R.string.validation));
//                }
//                else {
//
//
//                    txtBeneficiaryName.setText(bNickName);
//                    txtBeneficiaryNickName.setText(bName);
//                    txtIFSCCode.setText(ifsc);
//                    txtAccountNumber.setText(accountNumber);
//                    txtAccountType.setText(spinnerAccountType.getSelectedItem().toString());
//                    viewFlipper_managebeneficiary.setDisplayedChild(2);
//                }
//
//
//            }
//        });


        btnBackConfirmBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper_managebeneficiary.setDisplayedChild(1);
            }
        });


        btnAddBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertBuilder alert = new AlertBuilder(getActivity());

                String ifsc = "";

                if(txtSelectIFSCCode.getText().toString().trim().equalsIgnoreCase("OTHERS"))
                ifsc = edtIFSCCode.getText().toString().trim();
                else
                ifsc = txtSelectIFSCCode.getText().toString().trim();

                if (edtBeneficiaryName.getText().toString().trim().equalsIgnoreCase("") || edtBeneficiaryNickName.getText().toString().trim().equalsIgnoreCase("") || ifsc.equalsIgnoreCase("") || edtAccountNumber.getText().toString().trim().equalsIgnoreCase("") || txtAccountTypeSelect.getText().toString().trim().equalsIgnoreCase(""))
                {
                    //Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.validation),Toast.LENGTH_LONG).show();
                    alert.showAlert(getActivity().getResources().getString(R.string.validation));
                }
                else if(edtBeneficiaryMpin.getText().toString().trim().equalsIgnoreCase(""))
                {
                    alert.showAlert(getActivity().getResources().getString(R.string.invalid_mpin));
                   // Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.mpin),Toast.LENGTH_LONG).show();
                }
                else {

                    if (connectionDetector.isConnectingToInternet()) {


                        String bName = Uri.encode(edtBeneficiaryName.getText().toString(), "utf-8");
                        String bNickName = Uri.encode(edtBeneficiaryNickName.getText().toString(), "utf-8");
                        String accountNumber = edtAccountNumber.getText().toString();
                        String accountType= Uri.encode(txtAccountTypeSelect.getText().toString(), "utf-8");
                        String mpin = edtBeneficiaryMpin.getText().toString().trim();

                        String addBeneficiaryurl = getActivity().getResources().getString(R.string.addNewBankBeneficiary)+"?mobileNo1="+pref.getString("mobile_number","")+
                                                    "&Account2="+userInfoPref.getString("account","")+"&benAccountNo="+accountNumber+"&beneficiaryName="+bName+"&beneficiaryNickName="+bNickName+"&IFSC="+ifsc+"&accountType="+accountType+"&MPIN="+mpin;
                        AddNewBeneficiary addBeneficiary = new AddNewBeneficiary(getActivity(),ifsc,accountType);
                        addBeneficiary.execute(addBeneficiaryurl);
                    } else {



                        Toast.makeText(getActivity(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                    }


                }
            }
        });

        btnAddAnotherBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edtBeneficiaryName.setText("");
                edtBeneficiaryNickName.setText("");
                edtIFSCCode.setText("");
                edtAccountNumber.setText("");
                txtAccountTypeSelect.setText("");
                txtSelectIFSCCode.setText("");
                edtBeneficiaryMpin.setText("");
                viewFlipper_managebeneficiary.setDisplayedChild(1);
            }
        });


        btnHomeBeneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().finish();
                Intent i = new Intent(getActivity(),PayNowBeneficiaryActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            }
        });





        btnDeleteSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = -1;
                if(radioButton1.isChecked())
                    selectedId = radioButton1.getId();

                if(radioButton2.isChecked())
                    selectedId = radioButton2.getId();

                if(radioButton3.isChecked())
                    selectedId = radioButton3.getId();

                if(radioButton4.isChecked())
                    selectedId = radioButton4.getId();

                if(radioButton5.isChecked())
                    selectedId = radioButton5.getId();

                if(selectedId != -1) {


                    //// System.out.println("Selected ID......................" + selectedId);

                    Cursor cursor = dbHelper.fun_select_beneficiary(selectedId);

                    int len = cursor.getCount();
                    if (len > 0) {
                        if (cursor.moveToFirst())
                            do {

                                benCode = cursor.getString(1);

                                txtDeleteBeneficiaryNickName.setText(cursor.getString(3));
                                txtDeleteAccountNumber.setText(cursor.getString(4));
                                txtDeleteAccountType.setText(cursor.getString(9));
                                txtDeleteBankName.setText(cursor.getString(6));
                                txtDeleteIFSCCode.setText(cursor.getString(10));

                            } while (cursor.moveToNext());
                    }



                }
                viewFlipper_managebeneficiary.setDisplayedChild(6);
            }
        });


//        btnDeleteBeneficiary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String mpin = edtDeleteBeneficiaryMpin.getText().toString().trim();
//
//                if(mpin.equalsIgnoreCase(""))
//                {
//                    Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.invalidmpin),Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    if (connectionDetector.isConnectingToInternet()) {
//
//
//
//
//                            String deletebeneficiaryurl = getActivity().getResources().getString(R.string.deleteBankBeneficiary) + "?MDN=" + pref.getString("mobile_number", "") +"&benCode="+benCode+ "&Mpin=" + edtMpinToBank.getText().toString().trim();
//                            //  String beneficiaryurl = getActivity().getResources().getString(R.string.getBeneficiary) + "?MDN=918089209020&Mpin=1234";
//                            DeleteBeneficiary deleteBeneficiary = new DeleteBeneficiary(getActivity());
//                          deleteBeneficiary.execute(deletebeneficiaryurl);
//
//                    } else {
//                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
//
//                    }
//                }
//
//            }
//        });
//        btnDeleteAnotherBeneficiary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewFlipper_managebeneficiary.setDisplayedChild(0);
//            }
//        });




        txtAccountTypeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i =new Intent(getActivity(),AccountTypeActivity.class);
                startActivityForResult(i, 1);
            }
        });

        txtSelectIFSCCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getActivity(),SelectIFSCCodeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(i, 2);
            }
        });
    }

    private void init(View rootView) {
        // TODO Auto-generated method stub


        viewFlipper_managebeneficiary = (ViewFlipper) rootView.findViewById(R.id.viewflipper_managebeneficiary);
        fontclass.setFont(viewFlipper_managebeneficiary, typeface);

        if(flag == 1)
        viewFlipper_managebeneficiary.setDisplayedChild(1);

        connectionDetector = new ConnectionDetector(getActivity());
        dbHelper = new MyDBHelper(getActivity());

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        //Manage Beneficiary Scenario

        radioButtonAddBankBeneficary = (RadioButton)rootView.findViewById(R.id.radioButtonAddBankBeneficary);
        radioButtonDeleteBankBeneficary = (RadioButton)rootView.findViewById(R.id.radioButtonDeleteBankBeneficary);
        radioButtonViewBankBeneficary = (RadioButton)rootView.findViewById(R.id.radioButtonViewBankBeneficary);
        btnChooseBeneficiary = (Button) rootView.findViewById(R.id.btnChooseBeneficiary);

        btnChooseBeneficiary.setTypeface(btnChooseBeneficiary.getTypeface(), Typeface.BOLD);

        //Add Beneficiary Scenario

        edtBeneficiaryName = (EditText) rootView.findViewById(R.id.edtBeneficiaryName);
        edtBeneficiaryNickName = (EditText) rootView.findViewById(R.id.edtBeneficiaryNickName);
        edtIFSCCode = (EditText) rootView.findViewById(R.id.edtIFSCCode);
        txtSelectIFSCCode = (TextView)rootView.findViewById(R.id.txtSelectIFSCCode);
        edtAccountNumber = (EditText) rootView.findViewById(R.id.edtAccountNumber);
//        btnNextBeneficiary = (Button) rootView.findViewById(R.id.btnNextBeneficiary);
        btnBackBeneficiary = (Button)rootView.findViewById(R.id.btnBackBeneficiary);
        spinnerAccountType = (Spinner) rootView.findViewById(R.id.spinnerAccountType);
        accountType = getResources().getStringArray(R.array.Account);
        txtAccountTypeSelect = (TextView)rootView.findViewById(R.id.txtAccountTypeSelect);

//        btnNextBeneficiary.setTypeface(btnNextBeneficiary.getTypeface(), Typeface.BOLD);
        btnBackBeneficiary.setTypeface(btnBackBeneficiary.getTypeface(), Typeface.BOLD);


        txtBeneficiaryName = (TextView)rootView.findViewById(R.id.txtBeneficiaryName);
        txtBeneficiaryNickName = (TextView)rootView.findViewById(R.id.txtBeneficiaryNickName);
        txtAccountNumber = (TextView)rootView.findViewById(R.id.txtAccountNumber);
        txtAccountType = (TextView)rootView.findViewById(R.id.txtAccountType);
        txtIFSCCode = (TextView)rootView.findViewById(R.id.txtIFSCCode);
        edtBeneficiaryMpin = (EditText)rootView.findViewById(R.id.edtBeneficiaryMpin);
        btnAddBeneficiary = (Button)rootView.findViewById(R.id.btnAddBeneficiary);
        btnBackConfirmBeneficiary = (Button)rootView.findViewById(R.id.btnBackConfirmBeneficiary);

        btnAddBeneficiary.setTypeface(btnAddBeneficiary.getTypeface(), Typeface.BOLD);
        btnBackConfirmBeneficiary.setTypeface(btnBackConfirmBeneficiary.getTypeface(), Typeface.BOLD);


        txtAddStatusDone  = (TextView)rootView.findViewById(R.id.txtAddStatusDone);
        txtAddBeneficiaryNameDone  = (TextView)rootView.findViewById(R.id.txtAddBeneficiaryNameDone);
        txtAddBeneficiaryNickNameDone  = (TextView)rootView.findViewById(R.id.txtAddBeneficiaryNickNameDone);
        txtAddIFSCCode  = (TextView)rootView.findViewById(R.id.txtAddIFSCCode);
        txtAddBankAccountNumber  = (TextView)rootView.findViewById(R.id.txtAddBankAccountNumber);
        txtAddBankAccountType  = (TextView)rootView.findViewById(R.id.txtAddBankAccountType);
        txtRemarkDone  = (TextView)rootView.findViewById(R.id.txtRemarkDone);
        txtDateAndTimeDone  = (TextView)rootView.findViewById(R.id.txtDateAndTimeDone);
        btnAddAnotherBeneficiary = (Button)rootView.findViewById(R.id.btnAddAnotherBeneficiary);
        btnHomeBeneficiary = (Button)rootView.findViewById(R.id.btnHomeBeneficiary);

        btnAddAnotherBeneficiary.setTypeface(btnAddAnotherBeneficiary.getTypeface(), Typeface.BOLD);
        btnHomeBeneficiary.setTypeface(btnHomeBeneficiary.getTypeface(), Typeface.BOLD);



        spinnerAccountType.setAdapter(new MyCustomAdapter(rootView.getContext(),
                R.layout.row_layout, accountType));

        spinnerAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {

               /* Toast.makeText(parentView.getContext(),
                        "You have selected " + oprator[+position],
                        Toast.LENGTH_LONG).show();*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });




        //Delete Beneficiary Scenario

        edtMpinToBank = (EditText)rootView.findViewById(R.id.edtMpinToBank);
        btnMpinNext = (Button)rootView.findViewById(R.id.btnMpinNext);
        radioButton1 = (RadioButton)rootView.findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton)rootView.findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton)rootView.findViewById(R.id.radioButton3);
        radioButton4 =  (RadioButton)rootView.findViewById(R.id.radioButton4);
        radioButton5 =  (RadioButton)rootView.findViewById(R.id.radioButton5);

        btnDeleteSubmit = (Button)rootView.findViewById(R.id.btnDeleteSubmit);

        txtDeleteBeneficiaryNickName = (TextView)rootView.findViewById(R.id.txtDeleteBeneficiaryNickName);
        txtDeleteBankName = (TextView)rootView.findViewById(R.id.txtDeleteBankName);
        txtDeleteIFSCCode = (TextView)rootView.findViewById(R.id.txtDeleteIFSCCode);
        txtDeleteAccountNumber = (TextView)rootView.findViewById(R.id.txtDeleteAccountNumber);
        txtDeleteAccountType = (TextView)rootView.findViewById(R.id.txtDeleteAccountType);
        edtDeleteBeneficiaryMpin = (EditText)rootView.findViewById(R.id.edtDeleteBeneficiaryMpin);
        btnDeleteBeneficiary = (Button)rootView.findViewById(R.id.btnDeleteBeneficiary);


        txtDeleteStatusDone = (TextView)rootView.findViewById(R.id.txtDeleteStatusDone);
        txtDeleteBeneficiaryNickNameDone = (TextView)rootView.findViewById(R.id.txtDeleteBeneficiaryNickNameDone);
        txtDeleteBankAccountNumberDone = (TextView)rootView.findViewById(R.id.txtDeleteBankAccountNumberDone);
        txtDeleteRemarkDone = (TextView)rootView.findViewById(R.id.txtDeleteRemarkDone);
        txtDeleteDateAndTimeDone =(TextView)rootView.findViewById(R.id.txtDeleteDateAndTimeDone);
        btnDeleteAnotherBeneficiary = (Button)rootView.findViewById(R.id.btnDeleteAnotherBeneficiary);



    }

    public class MyCustomAdapter extends ArrayAdapter<String> {


        private Context context;
//		LayoutInflater inflater;
          Typeface custom_font;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {

            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub

            custom_font = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");

        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            LayoutInflater inflater=getActivity().getLayoutInflater();
            View row=inflater.inflate(R.layout.row_layout, parent, false);
            TextView v=(TextView)row.findViewById(R.id.txtAccount);


            // TextView v = super.getDropDownView(position, convertView, parent);

            v.setTypeface(custom_font);
           // v.setTextSize(getResources().getDimensionPixelSize(R.dimen.normal_text_size));
           // ((TextView) v).setPadding(10, 10, 10, 10);

            return row;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View row=inflater.inflate(R.layout.row_layout, parent, false);
            TextView v=(TextView)row.findViewById(R.id.txtAccount);


           // View v = super.getView(position, convertView, parent);

          v.setTypeface(custom_font);
          //  v.setTextSize(getResources().getDimensionPixelSize(R.dimen.normal_text_size));
            return row;
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
  if(requestCode == 1)
        {
            try {
                txtAccountTypeSelect.setText(data.getStringExtra("result"));


            }
            catch (Exception e)
            {

            }
        }
        else if(requestCode == 2)
  {
      try
      {
          if(data.getStringExtra("result").equalsIgnoreCase("Others"))
          {
              txtSelectIFSCCode.setText(data.getStringExtra("result"));
              edtIFSCCode.setVisibility(View.VISIBLE);
          }
          else
          {
              txtSelectIFSCCode.setText(data.getStringExtra("result"));
              edtIFSCCode.setVisibility(View.GONE);

          }
      }
      catch (Exception e)
      {
          e.printStackTrace();
      }
  }

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }


    private class AddNewBeneficiary extends AsyncTask<String, Void, String> {

        Context context;


        String ifsc,accountType,dateTime,remark,status;
        public AddNewBeneficiary(Context context,String ifsc,String accountType) {
            this.context = context;
            this.ifsc = ifsc;
            this.accountType = accountType;


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

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("status").equalsIgnoreCase("SUCCESS")) {

                            status = jsonMainObj.getString("status");
                            remark = jsonMainObj.getString("remark");
                            dateTime = jsonMainObj.getString("dateTime");

                            return "Success";

                        } else if (jsonMainObj.getString("status").equalsIgnoreCase("ERROR")) {

                            status = jsonMainObj.getString("status");
                            remark = jsonMainObj.getString("remark");
                            dateTime = jsonMainObj.getString("dateTime");

                            return remark;

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

                txtAddStatusDone.setText(status);
                txtAddBeneficiaryNameDone.setText(edtBeneficiaryName.getText().toString());
                txtAddBeneficiaryNickNameDone.setText(edtBeneficiaryNickName.getText().toString());
                txtAddIFSCCode.setText(ifsc);
                txtAddBankAccountNumber.setText(edtAccountNumber.getText().toString());
                txtAddBankAccountType.setText(accountType);
                txtRemarkDone.setText(remark);
                txtDateAndTimeDone.setText(dateTime);

                viewFlipper_managebeneficiary.setDisplayedChild(3);

            } else if (result.equalsIgnoreCase("Failure")) {

                 Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

        }

    }

    private class GetBeneficiary extends AsyncTask<String, Void, String> {

        Context context;
        int flag = 0;

        private ArrayList<String> beneficiaryID,beneficiaryNickName,mobileNumber,accountNumber,beneficiaryCode;

        public GetBeneficiary(Context context) {
            this.context = context;

            beneficiaryID = new ArrayList<String>();
            beneficiaryNickName = new ArrayList<String>();
            mobileNumber =new ArrayList<String>();
            accountNumber = new ArrayList<String>();
            beneficiaryCode = new ArrayList<String>();


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



                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {

                        dbHelper.fun_deleteAll_tbl_beneficiary_details();
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            flag =1;
                            JSONArray jsonArray = jsonMainObj.getJSONArray("beneficiaryDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject j1 = (JSONObject) jsonArray.get(i);
//
                                dbHelper.fun_insert_tbl_beneficiary_details(j1.getString("beneficiaryCode"), j1.getString("beneficiaryNickname"), j1.getString("accountNumber"),
                                        j1.getString("accountType"), j1.getString("mobileNumber"),j1.getString("bankType"),j1.getString("bankName"),
                                        j1.getString("branchName"),j1.getString("beneficiaryAadhaarNo"),j1.getString("aadharStatus"),j1.getString("routingBankType"),j1.getString("validateStatus"),j1.getString("beneficiaryName"),j1.getString("ifsccode"));


                            }

                            return "Success";

                        }  else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("No Beneficiary")) {

                            flag = 0;
                            editor.putInt("flag", 0);
                            editor.commit();
                            return "Success";

                        } else if(jsonMainObj.getString("responseStatus").equalsIgnoreCase("Failure")) {
                            return jsonMainObj.getString("responseMessage");
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
                    return "Success";
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

            //// System.out.println("Flag::::::::"+flag);

            if (result.equalsIgnoreCase("Success")) {

                if (flag == 1) {

                    BeneficiaryDetails bd = new BeneficiaryDetails();
                    Cursor cursor = dbHelper.fun_select_tbl_beneficiary_details();
                    int len = cursor.getCount();


                    if (len > 0) {
                        if (cursor.moveToFirst()) {

                            do {

                                beneficiaryID.add(cursor.getString(0));
                                beneficiaryCode.add(cursor.getString(1));
                                beneficiaryNickName.add(cursor.getString(2));
                                accountNumber.add(cursor.getString(4));
                                mobileNumber.add(cursor.getString(8));

                                bd.setBeneficiaryID(cursor.getString(0));
                                bd.setBeneficiaryName(cursor.getString(2));
                                bd.setMobileNumber(cursor.getString(8));
                                bd.setAccountNumber(cursor.getString(4));



                            }while (cursor.moveToNext());


                        }
                    }

                    final ListView lstView = getListView();

                    lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    BeneficiaryDeleteAdapter adapter = new BeneficiaryDeleteAdapter(getActivity(),
                            R.layout.beneficiary_delete_layout,lstView, beneficiaryID, beneficiaryNickName,mobileNumber,accountNumber,beneficiaryCode);
                    setListAdapter(adapter);

                    lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                            Toast.makeText(getActivity(), beneficiaryNickName.get(position) + " Clicked!"
//                                    , Toast.LENGTH_SHORT).show();

//                            Cursor cursor = dbHelper.fun_select_beneficiary(Integer.parseInt(beneficiaryID.get(position)));
//
//                            int len = cursor.getCount();
//                            if (len > 0) {
//                                if (cursor.moveToFirst())
//                                    do {
//
//
//                                        beneficiaryCode = cursor.getString(1);
//                                        txtBeneficiaryNickName.setText(cursor.getString(3));
//                                        bankAccountNumber = cursor.getString(4);
//                                        txtBankName.setText(cursor.getString(6));
//                                        txtBankIFSCCode.setText(cursor.getString(10));
//                                        txtBranchName.setText(cursor.getString(9));
//                                        txtAmount.setText(amount);
//
//                                    } while (cursor.moveToNext());
//                            }
//
//
//                            viewFlipper_transferTo_Bank.setDisplayedChild(2);

                        }
                    });

                    viewFlipper_managebeneficiary.setDisplayedChild(4);

//                    linErrorBeneficiary.setVisibility(View.GONE);
//
//                    tl = (TableLayout) getActivity().findViewById(R.id.maintable);
//                    // addHeaders();
//                    addData();
                } else if (flag == 0) {

                    AlertDialog alertDialog = new AlertDialog.Builder(
                            getActivity()).create();

                    // Setting Dialog Title
                    alertDialog.setTitle(getResources().getString(R.string.display_app_name));

                    // Setting Dialog Message
                    alertDialog.setMessage("No Beneficiary Found");

                    // Setting Icon to Dialog
                    // alertDialog.setIcon(R.drawable.tick);

                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                                dialog.dismiss();
//                            viewFlipper_managebeneficiary.setDisplayedChild(1);
                        }
                    });

                    alertDialog.setCancelable(false);
                    // Showing Alert Message
                    alertDialog.show();
                    //  linErrorBeneficiary.setVisibility(View.VISIBLE);
                }


                // addHeaders();
                //addData();

            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {


                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

        }

    }


    public void addData() {


        Cursor cursor = dbHelper.fun_select_tbl_beneficiary_details();
        int len = cursor.getCount();
        int i = 0;

        if(len>0)
        {
            if(cursor.moveToFirst())
                do{

                    if(i == 0)
                    {
                        radioButton1.setVisibility(View.VISIBLE);
                        radioButton1.setText(cursor.getString(2));
                        radioButton1.setId(cursor.getInt(0));
                    }
                    else if(i==1)
                    {
                        radioButton2.setVisibility(View.VISIBLE);
                        radioButton2.setText(cursor.getString(2));
                        radioButton2.setId(cursor.getInt(0));
                    }
                    else if(i==2)
                    {
                        radioButton3.setVisibility(View.VISIBLE);
                        radioButton3.setText(cursor.getString(2));
                        radioButton3.setId(cursor.getInt(0));
                    }
                    else if(i==3)
                    {
                        radioButton4.setVisibility(View.VISIBLE);
                        radioButton4.setText(cursor.getString(2));
                        radioButton4.setId(cursor.getInt(0));
                    }
                    else if(i==4)
                    {
                        radioButton5.setVisibility(View.VISIBLE);
                        radioButton5.setText(cursor.getString(2));
                        radioButton5.setId(cursor.getInt(0));
                    }

                    i++;

                }while (cursor.moveToNext());
        }


    }

}


