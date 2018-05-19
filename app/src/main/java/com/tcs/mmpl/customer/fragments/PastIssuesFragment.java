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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Activity.PastIssuesDetailsActivity;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.Issue;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hp on 2016-11-15.
 */
public class PastIssuesFragment extends Fragment {

    private static final int MODE_PRIVATE = 0;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private ListView listView;
    private TextView txtTransactionError;
    private ProgressBar progressBar;
    // create boolean for fetching data
    private boolean isViewShown = false;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            try {
                String issue_url = getActivity().getResources().getString(R.string.getSHMbyMDN_url) + "?MDN=" + pref.getString("mobile_number", "");
                Log.i("url", issue_url);
                GetIssues getAccountStatement = new GetIssues(getActivity());
                getAccountStatement.execute(issue_url);
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

        View rootView = inflater.inflate(R.layout.fragment_past_issues, container, false);
        setRetainInstance(true);


        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
        RelativeLayout linParent = (RelativeLayout) rootView.findViewById(R.id.linParent);
        fontclass.setFont(linParent, typeface);
        // Get ListView object from xml
        listView = (ListView) rootView.findViewById(R.id.list);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        txtTransactionError = (TextView) rootView.findViewById(R.id.txtTransactionError);

        return rootView;
    }

    private class GetIssues extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        ProgressDialog pDialog;
        private ArrayList<Issue> issueArrayList = new ArrayList<>();

        public GetIssues(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressBar.setVisibility(View.VISIBLE);
            progressBar.getIndeterminateDrawable().setColorFilter(getActivity().getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);


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

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {
                            JSONArray jsonArray = jsonMainObj.getJSONArray("issueList");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Issue issue = new Issue();

                                issue.setIssueId(jsonObject.getString("tokenId"));
                                issue.setIssueStatus(jsonObject.getString("issueStatus"));
                                issue.setIssueDate(jsonObject.getString("issueDate"));
                                issue.setIssueContent(jsonObject.getString("issueContent"));
                                issue.setIssueCategory(jsonObject.getString("issueCategory"));

                                issueArrayList.add(issue);
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

            progressBar.setVisibility(View.GONE);

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {

                try {
                    txtTransactionError.setVisibility(View.GONE);

                    ViewIssuesAdapter adapter = new ViewIssuesAdapter(getActivity(),
                            R.layout.layout_past_issues, listView, issueArrayList);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {


                            // ListView Clicked item value
                            Issue issue = (Issue) listView.getItemAtPosition(position);
                            Intent intent = new Intent(getActivity(), PastIssuesDetailsActivity.class);
                            intent.putExtra("parent_issue", issue);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }

                    });

                } catch (Exception e) {

                }
            } else if (result.equalsIgnoreCase("Failure")) {

                try {
                    AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                    alertBuilder.showAlert(getResources().getString(R.string.apidown));
                } catch (Exception e) {
                }

            } else {
                txtTransactionError.setVisibility(View.VISIBLE);
                txtTransactionError.setText(result);
            }

        }

    }

    class ViewIssuesAdapter extends BaseAdapter {
        private Context context;
        private String[] values;
        private ListView list;
        private int resourceid;
        private ArrayList<Issue> issueArrayList;

        public ViewIssuesAdapter(Context context, int resourceid, ListView list, ArrayList<Issue> issueArrayList) {
            this.context = context;
            this.issueArrayList = issueArrayList;
            this.list = list;
            this.resourceid = resourceid;


        }

        @Override
        public int getCount() {
            return issueArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return issueArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            View row = inflater.inflate(resourceid, null, true);
            final TextView txtCategory = (TextView) row.findViewById(R.id.txtIssueCategory);
            final TextView txtIssueDate = (TextView) row.findViewById(R.id.txtIssueDate);
            final TextView txtIssueContent = (TextView) row.findViewById(R.id.txtIssueContent);
            final TextView txtIssueStatus = (TextView) row.findViewById(R.id.txtIssueStatus);
            final TextView txtTicketID = (TextView) row.findViewById(R.id.txtTicketID);


            txtCategory.setText(issueArrayList.get(position).getIssueCategory());
            txtIssueDate.setText(issueArrayList.get(position).getIssueDate());
            if (issueArrayList.get(position).getIssueContent().trim().length() > 25)
                txtIssueContent.setText(issueArrayList.get(position).getIssueContent().trim().substring(0,25)+"...");
            else
                txtIssueContent.setText(issueArrayList.get(position).getIssueContent().trim());
            txtIssueStatus.setText(issueArrayList.get(position).getIssueStatus());
            txtTicketID.setText(issueArrayList.get(position).getIssueId());


            return row;
        }


    }
}
