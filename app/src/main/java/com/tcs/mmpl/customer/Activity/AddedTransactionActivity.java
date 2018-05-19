package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddedTransactionActivity  extends ListFragment {

    private static final int MODE_PRIVATE = 0;
    ArrayList<String> amount, desc,date,month,day,year,type,status,transId;
    TransactionAdapter adapter;
    private ImageView imgBanner;
    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;
    String banner;
    private ProgressBar progressBar;
    private TextView txtTransactionError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getView()!=null) {

        }
    }
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            try {
                String transurl = getActivity().getResources().getString(R.string.added) + "?MDN=" + pref.getString("mobile_number", "");
                GetAccountStatement getAccountStatement = new GetAccountStatement(getActivity());
                getAccountStatement.execute(transurl);
            }
            catch (Exception e)
            {

            }
        }
    }



    // create boolean for fetching data
    private boolean isViewShown = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;




        } else {
            isViewShown = false;
        }
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isViewShown = false;
    }
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_added_transaction, container, false);
        setRetainInstance(true);

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();
        imgBanner = (ImageView)rootView.findViewById(R.id.imgBanner);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        txtTransactionError = (TextView)rootView.findViewById(R.id.txtTransactionError);
        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();
        banner = pref.getString("banner","");
        if(!banner.trim().isEmpty()) {
            byte[] byteArray = Base64.decode(banner, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                    byteArray.length);
            imgBanner.setImageBitmap(bitmap);
            imgBanner.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent re = new Intent(getActivity(), Electricity_Payment.class);
                re.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(re);
            }
        });

        String transurl = getActivity().getResources().getString(R.string.added) + "?MDN=" + pref.getString("mobile_number", "");
        // System.out.println(transurl);
        GetAccountStatement getAccountStatement = new GetAccountStatement(getActivity());
        getAccountStatement.execute(transurl);


        return rootView;
    }

    private class GetAccountStatement extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        ProgressDialog pDialog;

        public GetAccountStatement(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

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

                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("SUCCESS")) {

                            JSONArray jsonArray = jsonMainObj.getJSONArray("data");
                            //// System.out.println("I am here>>>>>>>>"+ jsonArray.length());

                            desc =new ArrayList<String>();
                            amount = new ArrayList<String>();
                            date = new ArrayList<String>();
                            day = new ArrayList<String>();
                            month = new ArrayList<String>();
                            year = new ArrayList<String>();
                            type = new ArrayList<String>();
                            status = new ArrayList<String>();
                            transId = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject j1 = jsonArray.getJSONObject(i);
                                //// System.out.println("Category..............."+j1.getString("category"));
                                desc.add(j1.getString("category"));
                                amount.add(j1.getString("amount"));
                                date.add(j1.getString("date"));
                                day.add(j1.getString("day"));
                                month.add(j1.getString("month"));
                                year.add(j1.getString("year"));
                                type.add(j1.getString("type"));
                                status.add(j1.getString("status"));
                                transId.add(j1.getString("transactionId"));

                            }

                            return "Success";

                        } else if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("FAILURE")) {
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


            progressBar.setVisibility(View.GONE);

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {
                txtTransactionError.setVisibility(View.GONE);

                try {

                    final ListView lstView = getListView();

                    lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    adapter = new TransactionAdapter(getActivity(),
                            R.layout.layout_paid, lstView, amount, desc, date, day, month, year, type,status,transId);
                    setListAdapter(adapter);
                }
                catch (Exception e)
                {

                }



            } else if (result.equalsIgnoreCase("Failure")) {

                try {
                    AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                    alertBuilder.showAlert(getResources().getString(R.string.apidown));
                }
                catch (Exception e)
                {

                }



            } else {

                txtTransactionError.setVisibility(View.VISIBLE);
                txtTransactionError.setText(result);

            }

        }

    }

    public class TransactionAdapter extends ArrayAdapter<String> {

        private static final int MODE_PRIVATE = 0;
        Context context;
        ListView listView;
        int layout_id;

        ArrayList<String> amount;
        ArrayList<String> desc;
        ArrayList<String> day;
        ArrayList<String> date;
        ArrayList<String> month;
        ArrayList<String> year;
        ArrayList<String> type;
        ArrayList<String> status;
        ArrayList<String> transid;


        public TransactionAdapter(Context context, int resource,
                                  ListView lstView, ArrayList<String> amount, ArrayList<String> desc, ArrayList<String> date, ArrayList<String> day, ArrayList<String> month, ArrayList<String> year, ArrayList<String> type, ArrayList<String> status, ArrayList<String> transid) {
            super(context, resource);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.layout_id = resource;
            this.listView = lstView;
            // this.message=message;
            this.amount = amount;
            this.desc = desc;
            this.day = day;
            this.date = date;
            this.month = month;
            this.year = year;
            this.type = type;
            this.status = status;
            this.transid = transid;


        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return amount.size();
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            try {


                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View rowView = inflater.inflate(layout_id, null, true);


                TextView txtDesc = (TextView) rowView.findViewById(R.id.txtDesc);
                TextView txtAmount = (TextView) rowView.findViewById(R.id.txtAmount);
                TextView txtDate = (TextView) rowView.findViewById(R.id.txtDate);
                ImageView imgMode = (ImageView) rowView.findViewById(R.id.imgMode);
                TextView txtTransId=(TextView)rowView.findViewById(R.id.txtTransId);
                TextView txtStatus=(TextView)rowView.findViewById(R.id.txtStatus);



                txtAmount.setText(context.getResources().getString(R.string.rupee_symbol)+amount.get(position));
                txtDesc.setText(desc.get(position));
                txtDate.setText(date.get(position) + " " + month.get(position) + " " + year.get(position));
                txtTransId.setText(Html.fromHtml("Transaction Id : " + transid.get(position)));
                txtStatus.setText(Html.fromHtml("Status : "+status.get(position)));
                if(type.isEmpty())
                    imgMode.setImageResource(R.drawable.minus_mode);
                else if (type.get(position).equalsIgnoreCase("credit"))
                    imgMode.setImageResource(R.drawable.plus_mode);
                else if (type.get(position).equalsIgnoreCase("debit"))
                    imgMode.setImageResource(R.drawable.minus_mode);


//            txtDay.setText(day.get(position));
//            txtMonth.setText(month.get(position));


                return rowView;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                return null;
            }
        }

    }
}
