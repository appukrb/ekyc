package com.tcs.mmpl.customer.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.Adapter.BeneficiaryAdapter;
import com.tcs.mmpl.customer.Adapter.BeneficiaryDetails;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.CheckBalance;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.LocalNotification;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.PopupBuilder;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 9/15/2015.
 */
public class TransferToBank extends ListFragment {

    private static final int MODE_PRIVATE = 0;
    FontClass fontclass = new FontClass();
    Typeface typeface;

    private Button button, btnBankNext, btnMpinNext,btn_Add_Beneficiary;
    private ViewFlipper viewFlipper_transferTo_Bank;
    private EditText edtAmountToBank, edtMpinToBank, edtMpinToBankConfirm;
    private ProgressDialog pDialog;

    TextView txtBeneficiaryNickName, txtBankName, txtBankIFSCCode, txtBranchName, txtAmount;
    TextView txtBeneficiaryNickNameDone, txtBankNameDone, txtBankIFSCCodeDone, txtAmountDone, txtStatusDone, txtProductDone, txtBankAccountNumberDone, txtTransactionFeeDone, txtTotalAmountDone, txtRemarkDone, txtDateAndTimeDone;

    private ConnectionDetector connectionDetector;
    private Button confirm, btnTransferToBank, btnAnotherTransaction,btnBack,btnHome;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    String beneficiaryCode, amount, bankAccountNumber;

    MyDBHelper dbHelper;

    RadioGroup rg;
    LinearLayout linBenName;
    RadioButton radioButton1, radioButton2, radioButton3, radioButton4, radioButton5;
    TableLayout tl;
    TableRow tr;
    TextView companyTV, valueTV;
    LinearLayout linErrorBeneficiary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_transfer_to_bank,
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
                    if (viewFlipper_transferTo_Bank.getDisplayedChild() == 2) {
                        viewFlipper_transferTo_Bank.setDisplayedChild(1);
                    } else if (viewFlipper_transferTo_Bank.getDisplayedChild() == 1) {
                        viewFlipper_transferTo_Bank.setDisplayedChild(0);
                    } else {
                        getActivity().finish();
//                        Intent i = new Intent(getActivity(),HomeScreenActivity.class);
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



    private void init(View rootView) {
        // TODO Auto-generated method stub

        viewFlipper_transferTo_Bank = (ViewFlipper) rootView.findViewById(R.id.viewflipper_transfertToBank);
        fontclass.setFont(viewFlipper_transferTo_Bank, typeface);

        connectionDetector = new ConnectionDetector(getActivity());
        dbHelper = new MyDBHelper(getActivity());

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        edtMpinToBank = (EditText) rootView.findViewById(R.id.edtMpinToBank);
        btnMpinNext = (Button) rootView.findViewById(R.id.btnMpinNext);
      //  btnMpinNext.setTypeface(btnMpinNext.getTypeface(),Typeface.BOLD);

        edtAmountToBank = (EditText) rootView.findViewById(R.id.edtAmountToBank);
        btnBankNext = (Button) rootView.findViewById(R.id.btnBankNext);
        rg = (RadioGroup) rootView.findViewById(R.id.radioGroupBeneficiary);
        linBenName = (LinearLayout) rootView.findViewById(R.id.linBenName);
        radioButton1 = (RadioButton) rootView.findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) rootView.findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) rootView.findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) rootView.findViewById(R.id.radioButton4);
        radioButton5 = (RadioButton) rootView.findViewById(R.id.radioButton5);
        linErrorBeneficiary = (LinearLayout) rootView.findViewById(R.id.linErrorBeneficiary);

        txtBeneficiaryNickName = (TextView) rootView.findViewById(R.id.txtBeneficiaryNickName);
        txtBankName = (TextView) rootView.findViewById(R.id.txtBankName);
        txtBankIFSCCode = (TextView) rootView.findViewById(R.id.txtBankIFSCCode);
        txtBranchName = (TextView) rootView.findViewById(R.id.txtBranchName);
        txtAmount = (TextView) rootView.findViewById(R.id.txtAmount);
        edtMpinToBankConfirm = (EditText) rootView.findViewById(R.id.edtMpinToBankConfirm);
        btnTransferToBank = (Button) rootView.findViewById(R.id.btnTransferToBank);
        btnBack = (Button)rootView.findViewById(R.id.btnBack);
        btnHome = (Button)rootView.findViewById(R.id.btnHome);
        btn_Add_Beneficiary=(Button)rootView.findViewById(R.id.btn_Add_Beneficiary);

        txtBeneficiaryNickNameDone = (TextView) rootView.findViewById(R.id.txtBeneficiaryNickNameDone);
        txtBankNameDone = (TextView) rootView.findViewById(R.id.txtBankNameDone);
        txtBankIFSCCodeDone = (TextView) rootView.findViewById(R.id.txtBankIFSCCodeDone);
        txtAmountDone = (TextView) rootView.findViewById(R.id.txtAmountDone);
        txtStatusDone = (TextView) rootView.findViewById(R.id.txtStatusDone);
        txtProductDone = (TextView) rootView.findViewById(R.id.txtProductDone);
        txtBankAccountNumberDone = (TextView) rootView.findViewById(R.id.txtBankAccountNumberDone);
        txtTransactionFeeDone = (TextView) rootView.findViewById(R.id.txtTransactionFeeDone);
        txtTotalAmountDone = (TextView) rootView.findViewById(R.id.txtTotalAmountDone);
        txtRemarkDone = (TextView) rootView.findViewById(R.id.txtRemarkDone);
        txtDateAndTimeDone = (TextView) rootView.findViewById(R.id.txtDateAndTimeDone);
        btnAnotherTransaction = (Button) rootView.findViewById(R.id.btnAnotherTransaction);


    }


    private void onClick() {
        // TODO Auto-generated method stub

        btn_Add_Beneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent i = new Intent(getActivity(), MoneyTransferActivity.class);
                i.putExtra("index","2");
                i.putExtra("flag","1");
                startActivity(i);

            }
        });
        btnBankNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                AlertBuilder alert = new AlertBuilder(getActivity());
                amount = edtAmountToBank.getText().toString();
                if (userInfoPref.getBoolean("new_user_registered_newapp", false)) {

                if (amount.trim().equalsIgnoreCase("")) {
                    alert.showAlert(getActivity().getResources().getString(R.string.invalidamount));


                } else {


                        if (connectionDetector.isConnectingToInternet()) {

                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edtAmountToBank.getWindowToken(), 0);

                            String beneficiaryurl = getActivity().getResources().getString(R.string.getBeneficiary) + "?MDN=" + pref.getString("mobile_number", "");
                            //  String beneficiaryurl = getActivity().getResources().getString(R.string.getBeneficiary) + "?MDN=918089209020&Mpin=1234";
                            GetBeneficiary getBeneficiary = new GetBeneficiary(getActivity());
                            getBeneficiary.execute(beneficiaryurl);

                        } else {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

                        }



                }

                } else {
                    alert.newUser();
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent i = new Intent(getActivity(),HomeScreenActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper_transferTo_Bank.setDisplayedChild(1);
            }
        });

        btnTransferToBank.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                AlertBuilder alert = new AlertBuilder(getActivity());

                String mpin = edtMpinToBankConfirm.getText().toString().trim();
                if (mpin.equalsIgnoreCase("")) {

                    alert.showAlert(getActivity().getResources().getString(R.string.invalid_mpin));
                    //Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.invalidmpin), Toast.LENGTH_LONG).show();
                } else {
                    if (connectionDetector.isConnectingToInternet()) {

                        String BingoString = pref.getString("mobile_number", "").trim() + "|" + mpin.trim()+"|"+beneficiaryCode.trim() ;
                        String res = Bingo.Bingo_one(BingoString);

                        String transfertobankurl = getActivity().getResources().getString(R.string.transfertobank) + "?mobileNo1="
                                + pref.getString("mobile_number", "") + "&benefiaryCode=" + beneficiaryCode + "&Amount="
                                + txtAmount.getText().toString().trim() + "&Mpin=" + mpin+"&Checksum="+res;

                        CheckBalance checkBalance = new CheckBalance(getActivity());
                        if(checkBalance.getBalanceCheck(txtAmount.getText().toString().trim()))
                        {
                            PopupBuilder popup = new PopupBuilder(getActivity());
                            popup.showPopup(checkBalance.getAmount(txtAmount.getText().toString().trim()));
                            userInfoEditor.putString("transaction_url", transfertobankurl);
                            userInfoEditor.putString("transaction_method","POST2");
                            userInfoEditor.putString("transaction_flag","2");
                            userInfoEditor.commit();
                        }
                        else {
                            TransferToBankAsync transfertobank = new TransferToBankAsync(getActivity());
                            transfertobank.execute(transfertobankurl);
                        }
                    } else {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

        btnAnotherTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edtAmountToBank.setText("");
                edtMpinToBankConfirm.setText("");
                viewFlipper_transferTo_Bank.setDisplayedChild(0);
            }
        });

    }


    private class TransferToBankAsync extends AsyncTask<String, Void, String> {

        String transactionfee, totalAmount, dateTime, product, amount, remark, status;
        Context context;

        public TransferToBankAsync(Context context) {
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


                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {


                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("status").equalsIgnoreCase("SUCCESS")) {

                            if(jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationFlag)).trim().equalsIgnoreCase("true"))
                            {
                                new LocalNotification(getActivity(),jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationTitle)),jsonMainObj.getString(getActivity().getResources().getString(R.string.notificationMessage))).sendNotification();
                            }

                            status = jsonMainObj.getString("status");
                            transactionfee = jsonMainObj.getString("transcationFee");
                            totalAmount = jsonMainObj.getString("totalAmount");
                            dateTime = jsonMainObj.getString("dateTime");
                            product = jsonMainObj.getString("product");
                            amount = jsonMainObj.getString("amount");
                            remark = jsonMainObj.getString("remark");

                            return "Success";

                        } else if (jsonMainObj.getString("status").equalsIgnoreCase("ERROR")) {

                            status = jsonMainObj.getString("status");
                            transactionfee = jsonMainObj.getString("transcationFee");
                            totalAmount = jsonMainObj.getString("totalAmount");
                            dateTime = jsonMainObj.getString("dateTime");
                            product = jsonMainObj.getString("product");
                            amount = jsonMainObj.getString("amount");
                            remark = jsonMainObj.getString("remark");

                            return "Success";

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

                txtBeneficiaryNickNameDone.setText(txtBeneficiaryNickName.getText().toString());
                txtBankNameDone.setText(txtBankName.getText().toString());
                txtBankIFSCCodeDone.setText(txtBankIFSCCode.getText().toString());
                txtAmountDone.setText(amount);
                txtStatusDone.setText(status);
                txtProductDone.setText(product);
                txtBankAccountNumberDone.setText(bankAccountNumber);
                txtTransactionFeeDone.setText(transactionfee);
                txtTotalAmountDone.setText(totalAmount);
                txtRemarkDone.setText(remark);
                txtDateAndTimeDone.setText(dateTime);

                viewFlipper_transferTo_Bank.setDisplayedChild(3);

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

      private ArrayList<String> beneficiaryID,beneficiaryNickName,mobileNumber,accountNumber;


        public GetBeneficiary(Context context) {
            this.context = context;

            beneficiaryID = new ArrayList<String>();
            beneficiaryNickName = new ArrayList<String>();
            mobileNumber =new ArrayList<String>();
            accountNumber = new ArrayList<String>();

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

                        //// System.out.println("Response Status:::::::::::"+jsonMainObj.getString("responseStatus"));

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            JSONArray jsonArray = jsonMainObj.getJSONArray("beneficiaryDetail");

                            if (jsonArray.length() > 0) {
                                flag = 1;
                                editor.putInt("flag", flag);
                                editor.commit();
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject j1 = (JSONObject) jsonArray.get(i);
//
                                    dbHelper.fun_insert_tbl_beneficiary_details(j1.getString("beneficiaryCode"), j1.getString("beneficiaryNickname"), j1.getString("accountNumber"),
                                            j1.getString("accountType"), j1.getString("mobileNumber"), j1.getString("bankType"), j1.getString("bankName"),
                                            j1.getString("branchName"), j1.getString("beneficiaryAadhaarNo"), j1.getString("aadharStatus"), j1.getString("routingBankType"), j1.getString("validateStatus"), j1.getString("beneficiaryName"), j1.getString("ifsccode"));


                                }
                            } else {
                                flag = 0;
                                editor.putInt("flag", flag);
                                editor.commit();

                            }

                            return "Success";

                        }
                        else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("Failure"))
                        {
                         return jsonMainObj.getString("responseMessage");
                        }
                        else if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("No Beneficiary")) {

                            flag = 0;
                            editor.putInt("flag", 0);
                            editor.commit();
                            return "Success";

                        } else {
                            flag = 0;
                            editor.putInt("flag", 0);
                            editor.commit();
                            return "Failure";
                        }

                    } catch (JSONException e) {
                        editor.putInt("flag", 0);
                        editor.commit();
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

                if (flag == 1) {

                    BeneficiaryDetails bd = new BeneficiaryDetails();
                    Cursor cursor = dbHelper.fun_select_tbl_beneficiary_details();
                    int len = cursor.getCount();


                    if (len > 0) {
                        if (cursor.moveToFirst()) {

                            do {
                                beneficiaryID.add(cursor.getString(0));
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
                    BeneficiaryAdapter adapter = new BeneficiaryAdapter(getActivity(),
                            R.layout.beneficiary_layout,lstView, beneficiaryID, beneficiaryNickName,mobileNumber,accountNumber);
                    setListAdapter(adapter);

                    lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                            Toast.makeText(getActivity(), beneficiaryNickName.get(position) + " Clicked!"
//                                    , Toast.LENGTH_SHORT).show();

                            Cursor cursor = dbHelper.fun_select_beneficiary(Integer.parseInt(beneficiaryID.get(position)));

                                int len = cursor.getCount();
                                if (len > 0) {
                                    if (cursor.moveToFirst())
                                        do {


                                            beneficiaryCode = cursor.getString(1);
                                            txtBeneficiaryNickName.setText(cursor.getString(3));
                                            bankAccountNumber = cursor.getString(4);
                                            txtBankName.setText(cursor.getString(6));
                                            txtBankIFSCCode.setText(cursor.getString(10));
                                            txtBranchName.setText(cursor.getString(9));
                                            txtAmount.setText(amount);

                                        } while (cursor.moveToNext());
                                }


                                viewFlipper_transferTo_Bank.setDisplayedChild(2);

                        }
                    });

                    viewFlipper_transferTo_Bank.setDisplayedChild(1);

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
                    alertDialog.setMessage("Please add Beneficiary to continue");

                    // Setting Icon to Dialog
                    // alertDialog.setIcon(R.drawable.tick);

                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            getActivity().finish();
                            Intent i = new Intent(getActivity(), MoneyTransferActivity.class);
                            i.putExtra("index","2");
                            i.putExtra("flag","1");
                            startActivity(i);
                        }
                    });

                    alertDialog.setCancelable(false);
                    // Showing Alert Message
                    alertDialog.show();
                    //  linErrorBeneficiary.setVisibility(View.VISIBLE);
                }

            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {

                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

        }

    }

    /**
     * This function add the headers to the table *
     */
    public void addHeaders() {

        /** Create a TableRow dynamically **/
        tr = new TableRow(getActivity());
        tr.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        /** Creating a TextView to add to the row **/
        TextView selectCheckbox = new TextView(getActivity());
        selectCheckbox.setText("Select");
        selectCheckbox.setTextColor(Color.BLACK);
        selectCheckbox.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        selectCheckbox.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        selectCheckbox.setPadding(5, 5, 5, 0);
        tr.addView(selectCheckbox);  // Adding textView to tablerow.

        /** Creating another textview **/
        TextView beneficiary = new TextView(getActivity());
        beneficiary.setText("Beneficiary Name");
        beneficiary.setTextColor(Color.BLACK);
        beneficiary.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        beneficiary.setPadding(5, 5, 5, 0);
        beneficiary.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tr.addView(beneficiary); // Adding textView to tablerow.

        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        // we are adding two textviews for the divider because we have two columns
        tr = new TableRow(getActivity());
        tr.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

//        /** Creating another textview **/
//        CheckBox chkSelect = new CheckBox(getActivity());
//        chkSelect.setSelected(false);
//        chkSelect.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//        chkSelect.setPadding(5, 0, 0, 0);
//
//        tr.addView(chkSelect); // Adding textView to tablerow.
//
//        TextView beneficiaryName = new TextView(getActivity());
//        beneficiaryName.setText("-------------------------");
//        beneficiaryName.setTextColor(Color.GREEN);
//        beneficiaryName.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
//        beneficiaryName.setPadding(5, 0, 0, 0);
//        beneficiaryName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//        tr.addView(beneficiaryName); // Adding textView to tablerow.
//
//        // Add the TableRow to the TableLayout
//        tl.addView(tr, new TableLayout.LayoutParams(
//                LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT));
    }

    /**
     * This function add the data to the table *
     */
    public void addData() {


        Cursor cursor = dbHelper.fun_select_tbl_beneficiary_details();
        int len = cursor.getCount();
        int i = 0;

        if (len > 0) {
            if (cursor.moveToFirst())
                do {

                    if (i == 0) {
                        radioButton1.setVisibility(View.VISIBLE);
                        radioButton1.setText(cursor.getString(2));
                        radioButton1.setId(cursor.getInt(0));
                    } else if (i == 1) {
                        radioButton2.setVisibility(View.VISIBLE);
                        radioButton2.setText(cursor.getString(2));
                        radioButton2.setId(cursor.getInt(0));
                    } else if (i == 2) {
                        radioButton3.setVisibility(View.VISIBLE);
                        radioButton3.setText(cursor.getString(2));
                        radioButton3.setId(cursor.getInt(0));
                    } else if (i == 3) {
                        radioButton4.setVisibility(View.VISIBLE);
                        radioButton4.setText(cursor.getString(2));
                        radioButton4.setId(cursor.getInt(0));
                    } else if (i == 4) {
                        radioButton5.setVisibility(View.VISIBLE);
                        radioButton5.setText(cursor.getString(2));
                        radioButton5.setId(cursor.getInt(0));
                    }

                    i++;

                } while (cursor.moveToNext());
        }


    }

}
