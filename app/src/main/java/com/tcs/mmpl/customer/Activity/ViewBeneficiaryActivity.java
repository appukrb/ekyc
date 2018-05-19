package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Beneficiary;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.MyDBHelper;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewBeneficiaryActivity extends Activity {

    private MyDBHelper dbHelper;
    private ConnectionDetector connectionDetector;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_beneficiary);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        dbHelper = new MyDBHelper(getApplicationContext());
        pref = getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();


        if(connectionDetector.isConnectingToInternet())
        {
            String beneficiaryurl = getResources().getString(R.string.getBeneficiary) + "?MDN=" + pref.getString("mobile_number", "");
            GetBeneficiary getBeneficiary = new GetBeneficiary(getApplicationContext());
            getBeneficiary.execute(beneficiaryurl);
        }
    }


    private class GetBeneficiary extends AsyncTask<String, Void, String> {

        Context context;
        int flag = 0;

        private ArrayList<Beneficiary> beneficiaryList;
        private ProgressDialog pDialog;


        public GetBeneficiary(Context context) {
            this.context = context;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ViewBeneficiaryActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                beneficiaryList = new ArrayList<Beneficiary>();
                WebServiceHandler serviceHandler = new WebServiceHandler(ViewBeneficiaryActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                Log.i("",jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {

                        dbHelper.fun_deleteAll_tbl_beneficiary_details();
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            flag =1;
                            JSONArray jsonArray = jsonMainObj.getJSONArray("beneficiaryDetail");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                Beneficiary  beneficiary = new Beneficiary();

//                                JSONObject j1 = (JSONObject) jsonArray.get(i);

                                JSONObject j1 =  jsonArray.getJSONObject(i);
//
                                dbHelper.fun_insert_tbl_beneficiary_details(j1.getString("beneficiaryCode"), j1.getString("beneficiaryNickname"), j1.getString("accountNumber"),
                                        j1.getString("accountType"), j1.getString("mobileNumber"), j1.getString("bankType"), j1.getString("bankName"),
                                        j1.getString("branchName"), j1.getString("beneficiaryAadhaarNo"), j1.getString("aadharStatus"), j1.getString("routingBankType"), j1.getString("validateStatus"), j1.getString("beneficiaryName"), j1.getString("ifsccode"));



                                //// System.out.println(" beneficiaryList " + j1.getString("beneficiaryNickname"));
                                //// System.out.println(" beneficiaryList " + j1.getString("mobileNumber"));

                                beneficiary.setBeneficiaryCode(j1.getString("beneficiaryCode"));
                                beneficiary.setBeneficiaryNickname(j1.getString("beneficiaryNickname"));
                                beneficiary.setAccountNumber(j1.getString("accountNumber"));
                                beneficiary.setAccountType(j1.getString("accountType"));
                                beneficiary.setMobileNumber(j1.getString("mobileNumber"));
                                beneficiary.setBankType(j1.getString("bankType"));
                                beneficiary.setBankName(j1.getString("bankName"));
                                beneficiary.setBranchName(j1.getString("branchName"));
                                beneficiary.setBeneficiaryAadhaarNo(j1.getString("beneficiaryAadhaarNo"));
                                beneficiary.setAadharStatus(j1.getString("aadharStatus"));
                                beneficiary.setRoutingBankType(j1.getString("routingBankType"));
                                beneficiary.setValidateStatus(j1.getString("validateStatus"));
                                beneficiary.setBeneficiaryName(j1.getString("beneficiaryName"));
                                beneficiary.setIfsccode(j1.getString("ifsccode"));

                                beneficiaryList.add(beneficiary);


                                //// System.out.println("beneficiaryList.get(position).getBeneficiaryNickname()"+beneficiaryList.get(0).getBeneficiaryNickname());


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



            if (result.equalsIgnoreCase("Success")) {

                if (flag == 1) {

                    final ListView lstView = (ListView)findViewById(R.id.listBeneficiary);

                    lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    ViewBeneficiaryAdapter adapter = new ViewBeneficiaryAdapter(ViewBeneficiaryActivity.this,
                            R.layout.layout_view_beneficiary,lstView, beneficiaryList);
                    lstView.setAdapter(adapter);

                    lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                        }
                    });


                } else if (flag == 0) {

                    AlertDialog alertDialog = new AlertDialog.Builder(
                            ViewBeneficiaryActivity.this).create();
                    // Setting Dialog Title
                    alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                    // Setting Dialog Message
                    alertDialog.setMessage("No Beneficiary Found");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.dismiss();
                            finish();

                        }
                    });

                    alertDialog.setCancelable(false);
                    // Showing Alert Message
                    alertDialog.show();

                }


            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(
                        ViewBeneficiaryActivity.this).create();
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.dismiss();
                        finish();

                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();


            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        ViewBeneficiaryActivity.this).create();
                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                // Setting Dialog Message
                alertDialog.setMessage(result);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed

                        dialog.dismiss();
                        finish();

                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            }

        }

    }

    class ViewBeneficiaryAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Beneficiary> beneficiaryList;
        private ListView list;
        private int resourceid;

        public ViewBeneficiaryAdapter(Context context,int resourceid,ListView list,ArrayList<Beneficiary> beneficiaryList) {
            this.context = context;
            this.beneficiaryList = beneficiaryList;
            this.list = list;
            this.resourceid =resourceid;


        }

        @Override
        public int getCount() {
            return beneficiaryList.size();
        }

        @Override
        public Object getItem(int i) {
            return beneficiaryList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            View row = inflater.inflate(resourceid, null, true);
            final TextView txtBeneficiaryNickName = (TextView) row.findViewById(R.id.txtBeneficiaryNickName);
            TextView txtAccountNumber = (TextView) row.findViewById(R.id.txtAccountNumber);


            txtBeneficiaryNickName.setText(beneficiaryList.get(position).getBeneficiaryName());
            txtAccountNumber.setText(beneficiaryList.get(position).getAccountNumber());

            return row;
        }


    }

}
