package com.tcs.mmpl.customer.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.Activity.SelfHelpFAQActivity;
import com.tcs.mmpl.customer.Activity.SelfHelpFeedback;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.IssueCategory;
import com.tcs.mmpl.customer.utility.LastTransactions;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hp on 2016-11-15.
 */
public class NewIssueFragment extends Fragment {

    private FontClass fontclass = new FontClass();
    private Typeface typeface;
    private static final int MODE_PRIVATE = 0;
    private ListView listView;
    private ArrayList<IssueCategory> issueCategories;
    private ArrayList<LastTransactions> lastTransactionsArrayList;
    private ProgressBar progressBar1;
    private TextView txtTransactionError1;
    private LinearLayout linOtherIssues;
    private LinearLayout linLastTransactionIssues, linOtherIssuesList;
    // create boolean for fetching data
    private boolean isViewShown = false;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            try {
//                String category_url = getActivity().getResources().getString(R.string.getSHMCategory_url);
//                Log.i("url", category_url);
//                GetIssueCategory getIssueCategory = new GetIssueCategory(getActivity());
//                getIssueCategory.execute(category_url);
            } catch (Exception e) {

            }
        }
    }

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_new_issues, container, false);
        setRetainInstance(true);




        // Get ListView object from xml
        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
        RelativeLayout linParent = (RelativeLayout) rootView.findViewById(R.id.linParent);
        fontclass.setFont(linParent, typeface);


        progressBar1 = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        txtTransactionError1 = (TextView) rootView.findViewById(R.id.txtTransactionError1);
        linOtherIssues = (LinearLayout) rootView.findViewById(R.id.linOtherIssues);

        linOtherIssuesList = (LinearLayout) rootView.findViewById(R.id.linOtherIssuesList);
        linLastTransactionIssues = (LinearLayout) rootView.findViewById(R.id.linLastTransactionIssues);

        showCategory();

        String last_transaction_url = getActivity().getResources().getString(R.string.getSHMTranscation_url)+"?MDN="+pref.getString("mobile_number","");
        GetLastTransactions getLastTransactions = new GetLastTransactions(getActivity());
        getLastTransactions.execute(last_transaction_url);

        return rootView;
    }

    private void showCategory()
    {

        issueCategories = new ArrayList<IssueCategory>();

        IssueCategory issueCategory = new IssueCategory();
        issueCategory.setCategory_name(getResources().getString(R.string.recharge_bill_payment_help));
        issueCategory.setCategory_html(getResources().getString(R.string.recharge_html_url));
        issueCategory.setCategory_note(getResources().getString(R.string.recharge_bill_payment_help_note));
        issueCategories.add(issueCategory);

        issueCategory = new IssueCategory();
        issueCategory.setCategory_name(getResources().getString(R.string.add_money_help));
        issueCategory.setCategory_html(getResources().getString(R.string.addmoney_html_url));
        issueCategory.setCategory_note(getResources().getString(R.string.add_money_help_note));
        issueCategories.add(issueCategory);

        issueCategory = new IssueCategory();
        issueCategory.setCategory_name(getResources().getString(R.string.mpin_otp_help));
        issueCategory.setCategory_html(getResources().getString(R.string.mpin_html_url));
        issueCategory.setCategory_note(getResources().getString(R.string.mpin_otp_help_note));
        issueCategories.add(issueCategory);

        issueCategory = new IssueCategory();
        issueCategory.setCategory_name(getResources().getString(R.string.money_transfer_help));
        issueCategory.setCategory_html(getResources().getString(R.string.moneytransfer_html_url));
        issueCategory.setCategory_note(getResources().getString(R.string.money_transfer_help_note));
        issueCategories.add(issueCategory);


        issueCategory = new IssueCategory();
        issueCategory.setCategory_name(getResources().getString(R.string.account_help));
        issueCategory.setCategory_html(getResources().getString(R.string.account_html_url));
        issueCategory.setCategory_note(getResources().getString(R.string.account_help_note));
        issueCategories.add(issueCategory);


        issueCategory = new IssueCategory();
        issueCategory.setCategory_name(getResources().getString(R.string.others_help));
        issueCategory.setCategory_html(getResources().getString(R.string.recharge_html_url));
        issueCategory.setCategory_note(getResources().getString(R.string.others_help_note));
        issueCategories.add(issueCategory);

        linOtherIssues.setVisibility(View.VISIBLE);



        for (int i = 0; i < issueCategories.size(); i++) {

            final IssueCategory issueCategory1 = (IssueCategory) issueCategories.get(i);
            View row = LayoutInflater.from(getActivity()).inflate(R.layout.layout_new_issues, null, true);
            final TextView txtCategoryName = (TextView) row.findViewById(R.id.txtCategoryName);
            TextView txtCategoryNote = (TextView) row.findViewById(R.id.txtCategoryNote);
            ImageView imgCategory = (ImageView) row.findViewById(R.id.imgCategory);
            View v = (View) row.findViewById(R.id.view);

            final int finalI = i;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (issueCategory1.getCategory_name().trim().equalsIgnoreCase(getResources().getString(R.string.others_help))) {
                        Intent intent = new Intent(getActivity(), SelfHelpFeedback.class);
                        intent.putExtra("mode", issueCategory1.getCategory_name());
                        intent.putExtra("desc", "NA");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(getActivity(), SelfHelpFAQActivity.class);
                        intent.putExtra("mode", issueCategory1);
                        intent.putExtra("index", finalI);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });

            txtCategoryName.setText(issueCategories.get(i).getCategory_name());
            txtCategoryNote.setText(issueCategories.get(i).getCategory_note());
            Picasso.with(getActivity()).load(issueCategories.get(i).getCategory_image()).error(R.drawable.ic_info).into(imgCategory);

            if (i != issueCategories.size()-1)
                v.setVisibility(View.VISIBLE);

            linOtherIssuesList.addView(row);
        }
    }


    private class GetLastTransactions extends AsyncTask<String, Void, String> {

        Context context;
        ProgressDialog pDialog;


        public GetLastTransactions(Context context) {
            this.context = context;


            lastTransactionsArrayList = new ArrayList<LastTransactions>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressBar1.setVisibility(View.VISIBLE);
            progressBar1.getIndeterminateDrawable().setColorFilter(getActivity().getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);


        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity());
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);

                Log.i("Response: >>>>>>>>>>>", jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("SUCCESS")) {
                            JSONArray jsonArray = jsonMainObj.getJSONArray("transctionDetaillist");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                LastTransactions lastTransactions = new LastTransactions();

                                lastTransactions.setCategory(jsonObject.getString("category"));
                                lastTransactions.setAmount(jsonObject.getString("amount"));
                                lastTransactions.setDate(jsonObject.getString("date") + " " + jsonObject.getString("month") + " " + jsonObject.getString("year"));
                                lastTransactions.setTransactionId(jsonObject.getString("transactionId"));
                                lastTransactions.setStatus(jsonObject.getString("status"));

                                lastTransactionsArrayList.add(lastTransactions);
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

            progressBar1.setVisibility(View.GONE);


            if (result.equalsIgnoreCase("Success")) {

                try {


                    for (int i = 0; i < lastTransactionsArrayList.size(); i++) {

                        final LastTransactions lastTransactions = (LastTransactions) lastTransactionsArrayList.get(i);
                        View row = LayoutInflater.from(getActivity()).inflate(R.layout.layout_last_transactions, null, true);
                        final TextView txtCategory = (TextView) row.findViewById(R.id.txtCategory);
                        TextView txtDate = (TextView) row.findViewById(R.id.txtDate);
                        TextView txtAmount = (TextView) row.findViewById(R.id.txtAmount);
                        TextView txtTransId = (TextView)row.findViewById(R.id.txtTransId);
                        TextView txtStatus = (TextView)row.findViewById(R.id.txtStatus);

                        View v = (View) row.findViewById(R.id.view);

                        row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent intent = new Intent(getActivity(), SelfHelpFeedback.class);
                                intent.putExtra("mode", lastTransactions.getCategory());
                                intent.putExtra("desc", lastTransactions.getCategory() + "&" + lastTransactions.getAmount() + "&" + lastTransactions.getStatus() + "&" + lastTransactions.getTransactionId() + "&" + lastTransactions.getReferenceId());
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);


                            }
                        });

                        txtCategory.setText(lastTransactionsArrayList.get(i).getCategory());
                        txtAmount.setText(getActivity().getResources().getString(R.string.rupee_symbol) + lastTransactionsArrayList.get(i).getAmount());
                        txtDate.setText(lastTransactionsArrayList.get(i).getDate());
                        txtTransId.setText(Html.fromHtml("Transaction Id : " + lastTransactionsArrayList.get(i).getTransactionId()));
                        txtStatus.setText(Html.fromHtml("Status : " + lastTransactionsArrayList.get(i).getStatus()));

                        if (i == lastTransactionsArrayList.size()-1)
                            v.setVisibility(View.GONE);

                        linLastTransactionIssues.addView(row);
                    }




                } catch (Exception e) {

                }
            } else if (result.equalsIgnoreCase("Failure")) {

                try {

                    linLastTransactionIssues.setVisibility(View.VISIBLE);
                    txtTransactionError1.setVisibility(View.VISIBLE);
                    txtTransactionError1.setText(getResources().getString(R.string.apidown));


                } catch (Exception e) {
                }

            } else {

                try {

                linLastTransactionIssues.setVisibility(View.VISIBLE);
                txtTransactionError1.setVisibility(View.VISIBLE);
                txtTransactionError1.setText(result);
                } catch (Exception e) {
                }
            }

        }

    }

}
