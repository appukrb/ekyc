package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.Bank;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class SelectIFSCCodeActivity extends Activity {

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    String url;
    private ListView listIFSCCode;
    private EditText edtSearchIFSC;
    private ListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ifsccode);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        url = getResources().getString(R.string.bank_url);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        listIFSCCode = (ListView)findViewById(R.id.listIFSCCode);
        edtSearchIFSC = (EditText)findViewById(R.id.edtSearchIFSC);
        if(connectionDetector.isConnectingToInternet())
        {
            GetIFSC getIFSC = new GetIFSC(getApplicationContext());
            getIFSC.execute(url);
        }
        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();;
        }

    }

    private class GetIFSC extends AsyncTask<String, Void, String> {

        Context context;

        ArrayList<Bank> list;
        public GetIFSC(Context context) {
            this.context = context;

            list = new ArrayList<>();


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SelectIFSCCodeActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(SelectIFSCCodeActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);
                jsonStr = "{\"value\":"+jsonStr+"}";
                //// System.out.println(jsonStr);

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);
                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");

                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                Bank bank = new Bank();
                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                bank.setBankname(obj.getString("bankname"));
                                bank.setIfsccode(obj.getString("ifsccode"));

                                list.add(bank);

                            }

                            Bank bank = new Bank();
                            bank.setBankname("OTHERS");
                            bank.setIfsccode("OTHERS");
                            list.add(bank);
                            return "Success";
                        }

                        else
                            return "Failure";


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
                //Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();

                adapter=new ListViewAdapter(getBaseContext(),list);
                listIFSCCode.setAdapter(adapter);


                edtSearchIFSC.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                        // When user changed the Text

                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        String text = edtSearchIFSC.getText().toString().toLowerCase(Locale.getDefault());
                        adapter.filter(text);
                    }
                });




            } else if (result.equalsIgnoreCase("Failure")) {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        SelectIFSCCodeActivity.this).create();

                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                alertDialog.setMessage(getResources().getString(R.string.apidown));
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        finish();

                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        SelectIFSCCodeActivity.this).create();

                alertDialog.setTitle(getResources().getString(R.string.display_app_name));
                alertDialog.setMessage(result);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        finish();

                    }
                });

                alertDialog.setCancelable(false);
                // Showing Alert Message
                alertDialog.show();

            }
        }

    }

    public class ListViewAdapter extends BaseAdapter {

        // Declare Variables
        Context mContext;
        LayoutInflater inflater;
        private ArrayList<Bank> list = null;
        private ArrayList<Bank> arraylist;


        public ListViewAdapter(Context context,
                               ArrayList<Bank> list) {
            mContext = context;
            this.list = list;
            inflater = LayoutInflater.from(mContext);
            this.arraylist = new ArrayList<Bank>();
            this.arraylist.addAll(list);

        }

        public class ViewHolder {
            TextView txtCity;
//            TextView txtCode;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Bank getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.view_airport_layout, null);
                // Locate the TextViews in listview_item.xml
                holder.txtCity = (TextView) view.findViewById(R.id.txtCity);
//                holder.txtCode = (TextView) view.findViewById(R.id.txtCode);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // Set the results into TextViews
            holder.txtCity.setText(list.get(position).getBankname());
//            holder.txtCode.setText(list.get(position).getIfsccode());

            // Listen for ListView Item Click
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {


                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",list.get(position).getIfsccode());
                    setResult(RESULT_OK, returnIntent);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);

                    finish();
                }
            });

            return view;
        }

        // Filter Class
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            list.clear();
            if (charText.length() == 0) {
                list.addAll(arraylist);
            } else {
                for (Bank wp : arraylist) {
                    if (wp.getBankname().toLowerCase(Locale.getDefault())
                            .contains(charText)) {
                        list.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

}
