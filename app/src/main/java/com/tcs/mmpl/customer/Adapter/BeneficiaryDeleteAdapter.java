package com.tcs.mmpl.customer.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.Activity.MoneyTransferActivity;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Bingo;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tcs.mmpl.customer.R.layout.popup_mpin_layout;

/**
 * Created by hp on 22-10-2015.
 */
public class BeneficiaryDeleteAdapter extends ArrayAdapter<String> {

    private static final int MODE_PRIVATE = 0;
    Context context;
    ListView listView;
    int layout_id;

    FontClass fontclass = new FontClass();
    Typeface typeface;
    ArrayList<String> beneficiaryID,beneficiaryNickName,mobileNumber,accountNumber;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<String> beneficiaryCode;

    public BeneficiaryDeleteAdapter(Context context, int resource,
                              ListView lstView,ArrayList<String> beneficiaryID,ArrayList<String> beneficiaryNickName,ArrayList<String> mobileNumber,ArrayList<String> accountNumber,ArrayList<String> beneficiaryCode) {
        super(context, resource);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.layout_id=resource;
        this.listView= lstView;
        // this.message=message;
        this.beneficiaryID=beneficiaryID;
        this.beneficiaryNickName=beneficiaryNickName;
        this.mobileNumber=mobileNumber;
        this.accountNumber = accountNumber;
        this.beneficiaryCode = beneficiaryCode;

        pref = context.getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();


    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return beneficiaryID.size();
    }



    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        try {


            typeface = Typeface.createFromAsset(context.getAssets(), "helvetica.otf");


            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View rowView = inflater.inflate(layout_id, null, true);


            TextView txtBeneficiaryNickName = (TextView) rowView.findViewById(R.id.txtBeneficiaryNickName);
            TextView txtmobileNumber = (TextView) rowView.findViewById(R.id.txtMobileNumber);
            TextView txtDelete = (TextView)rowView.findViewById(R.id.txtDelete);

            txtBeneficiaryNickName.setText(beneficiaryNickName.get(position));
            txtmobileNumber.setText(accountNumber.get(position));

            txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   // Toast.makeText(context,"clicked:"+accountNumber.get(position),Toast.LENGTH_LONG).show();
                    LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
                    View popupView = layoutInflater.inflate(popup_mpin_layout, null);
                    final PopupWindow popupWindow = new PopupWindow(popupView,
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    final EditText edtMpin = (EditText) popupView
                            .findViewById(R.id.edittext_edit_popup);



                    Button btnCancel = (Button)popupView.findViewById(R.id.button_pop_no);

                    Button btnSubmit = (Button) popupView
                            .findViewById(R.id.button_pop_yes);

                    edtMpin.setTypeface(typeface);
                    btnCancel.setTypeface(typeface);
                    btnSubmit.setTypeface(typeface);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });

                    btnSubmit.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edtMpin.getText().toString().trim().equalsIgnoreCase("")) {
                                Toast.makeText(context,context.getString(R.string.mpin),Toast.LENGTH_LONG).show();
                            } else {
                                String BingoString = pref.getString("mobile_number", "")+"|"+beneficiaryCode.get(position)+"|"+edtMpin.getText().toString().trim();
                                String res = Bingo.Bingo_one(BingoString);
                                // System.out.println("BingoString"+BingoString);

                                String deletebeneficiaryurl = context.getResources().getString(R.string.deleteBankBeneficiary) + "?MDN=" + pref.getString("mobile_number", "") +"&benCode="+beneficiaryCode.get(position)+ "&Mpin=" + edtMpin.getText().toString().trim()
                                        +"&Checksum="+res;
                                //// System.out.println(deletebeneficiaryurl);
                                DeleteBeneficiary ex = new DeleteBeneficiary(context,popupWindow);
                                ex.execute(deletebeneficiaryurl);
                            }

                        }
                    });

                    popupWindow.setOutsideTouchable(false);
                    popupWindow.setFocusable(true);
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                }
            });


            return rowView;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }


    private class DeleteBeneficiary extends AsyncTask<String, Void, String> {

        Context context;
        ProgressDialog pDialog;

        PopupWindow popupWindow;


        String ifsccode,accountType,accountNumber,dateTime,beneficiaryName,beneficiaryNickName,remark,status;
        public DeleteBeneficiary(Context context, PopupWindow popupWindow) {
            this.context = context;
            this.popupWindow = popupWindow;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(context.getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                WebServiceHandler serviceHandler = new WebServiceHandler(context);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("status").equalsIgnoreCase("SUCCESS")) {

                            status = jsonMainObj.getString("status");
//                            beneficiaryName = txtBeneficiaryName.getText().toString();
//                            beneficiaryNickName = txtBeneficiaryNickName.getText().toString();
//                            ifsccode = txtIFSCCode.getText().toString();
//                            accountNumber = txtAccountNumber.getText().toString();
//                            accountType = txtAccountType.getText().toString();
                            remark = jsonMainObj.getString("remark");
                            dateTime = jsonMainObj.getString("dateTime");

                            return "Success";

                        } else if (jsonMainObj.getString("status").equalsIgnoreCase("ERROR")) {

                            status = jsonMainObj.getString("status");
//                            beneficiaryName = txtBeneficiaryName.getText().toString();
//                            beneficiaryNickName = txtBeneficiaryNickName.getText().toString();
//                            ifsccode = txtIFSCCode.getText().toString();
//                            accountNumber = txtAccountNumber.getText().toString();
//                            accountType = txtAccountType.getText().toString();
                            remark = jsonMainObj.getString("remark");
                            dateTime = jsonMainObj.getString("dateTime");

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

            //// System.out.println("Result>>>>>>>>>>>>>>>>>"+result);

            if (result.equalsIgnoreCase("Success")) {

                this.popupWindow.dismiss();;
                Toast.makeText(context,remark, Toast.LENGTH_LONG).show();
                Intent i = new Intent(context,MoneyTransferActivity.class);
                i.putExtra("index", "2");
                i.putExtra("flag","2");
                context.startActivity(i);
                ((Activity)context).finish();


//                viewFlipper_managebeneficiary.setDisplayedChild(7);

            }

            else if (result.equalsIgnoreCase("ERROR")) {

                Toast.makeText(context, remark, Toast.LENGTH_LONG).show();



            }
            else if (result.equalsIgnoreCase("Failure")) {

                Toast.makeText(context, context.getString(R.string.apidown), Toast.LENGTH_LONG).show();



            } else {

                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }

        }

    }


}
