package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class KycDocumentList extends Activity { 
 
    ListView listDocument;
    ArrayList<String> documentName;

    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    String url,type;

    private EditText editsearch;

    // private  ArrayAdapter<String> adapter;

    private ListViewAdapter adapter;

    ArrayList<String> arraylist = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_kyc_document_list);

        listDocument = (ListView)findViewById(R.id.listDocument);

        String url = getResources().getString(R.string.kycDocument);

        connectionDetector = new ConnectionDetector(getApplicationContext());
        if(connectionDetector.isConnectingToInternet())
        {
            GetKycDocument getKycDocument = new GetKycDocument(getApplicationContext());
            getKycDocument.execute(url);
        }
        else
        {

            AlertDialog alertDialog = new AlertDialog.Builder(KycDocumentList.this).create();

            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.display_app_name));

            // Setting Dialog Message
            alertDialog.setMessage( getResources().getString(R.string.no_network));

            // Setting Icon to Dialog
            // alertDialog.setIcon(R.drawable.tick);

            // Setting OK Button
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


    private class GetKycDocument extends AsyncTask<String, Void, String> {

        Context context;


        String firstName, lastName, walletBalance;

        public GetKycDocument(Context context) {
            this.context = context;
            documentName = new ArrayList<String>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(KycDocumentList.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                //// System.out.println("Request: >>>>>>>>>>>" + arg0[0].toString());
                WebServiceHandler serviceHandler = new WebServiceHandler(KycDocumentList.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                //// System.out.println("Response: >>>>>>>>>>>" + jsonStr);

                jsonStr = "{ \"value\":"+jsonStr+"}";

                if (jsonStr != null) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);


                        JSONArray jsonArray = jsonMainObj.getJSONArray("value");

                        //// System.out.println("Length of jsonArray..............." + jsonArray.length());


                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = (JSONObject) jsonArray.get(i);
                                documentName.add(obj.getString("document"));
                            }
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

                adapter = new ListViewAdapter(KycDocumentList.this, documentName);
                // Binds the Adapter to the ListView
                listDocument.setAdapter(adapter);


            } else if (result.equalsIgnoreCase("Failure")) {


                AlertDialog alertDialog = new AlertDialog.Builder(KycDocumentList.this).create();

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));

                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));

                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);

                // Setting OK Button
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

                AlertDialog alertDialog = new AlertDialog.Builder(KycDocumentList.this).create();

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.display_app_name));

                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.apidown));

                // Setting Icon to Dialog
                // alertDialog.setIcon(R.drawable.tick);

                // Setting OK Button
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

        private ArrayList<String> documentName;

        public ListViewAdapter(Context context,
                              ArrayList<String> documentName) {
            mContext = context;

            inflater = LayoutInflater.from(mContext);
            this.documentName = documentName;

        }

        public class ViewHolder {
            TextView txtDocument;

        }

        @Override
        public int getCount() {
            return documentName.size();
        }

        @Override
        public String getItem(int position) {
            return documentName.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.listview_merchantitem, null);
                // Locate the TextViews in listview_item.xml
                holder.txtDocument = (TextView) view.findViewById(R.id.merchantName);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // Set the results into TextViews
            holder.txtDocument.setText(documentName.get(position));



            // Listen for ListView Item Click
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // Send single item click data to SingleItemView Class

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", documentName.get(position));
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });

            return view;
        }



    }


}

