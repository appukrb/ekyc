package com.tcs.mmpl.customer.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.Issue;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PastIssuesDetailsActivity extends Activity {


    private ChatArrayAdapter chatArrayAdapter;
    private ChatImageArrayAdapter chatImageArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;

    private String parentId = "";

    private ArrayList<Issue> issueArrayList;
    private String token;
    private ConnectionDetector connectionDetector;
    private SharedPreferences userInfoPref;
    private SharedPreferences.Editor userInfoEditor;
    private String encodedScreenShot="";
    private  Issue issue;
    private String responseMessage="";
    private ImageView imgAttach;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_issues);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        listView = (ListView) findViewById(R.id.msgview);
        issueArrayList = new ArrayList<>();
        issue = (Issue) getIntent().getSerializableExtra("parent_issue");

        userInfoPref = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        imgAttach = (ImageView)findViewById(R.id.imgAttach);
        if(issue.getIssueStatus().trim().equalsIgnoreCase("RESOLVED"))
        {
            imgAttach.setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.linNotSatisfied)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.linSend)).setVisibility(View.GONE);
        }
        else
        {
            imgAttach.setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.linNotSatisfied)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.linSend)).setVisibility(View.VISIBLE);
        }



        token = issue.getIssueId();

        chatText = (EditText) findViewById(R.id.edtMsg);
//        chatText.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    return sendChatMessage();
//                }
//                return false;
//            }
//        });


        if (connectionDetector.isConnectingToInternet()) {
            String sub_issues_url = getResources().getString(R.string.getSHMbyToken_url) + "?Token=" + issue.getIssueId();
            GetSubIssues getSubIssues = new GetSubIssues(getApplicationContext());
            getSubIssues.execute(sub_issues_url);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
        }

    }

//    private boolean sendChatMessage() {
////        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
//        chatArrayAdapter.add(new ChatMessage(side, "We understand that you are unable to add money to your wallet.\n\n.We apologize for the inconvenience.\n\nWe value your association and thank you for being mRUPEE customer.\n\nRegards\nmRUPEE Team"));
//
//        chatText.setText("");
//        side = !side;
//        return true;
//    }

    public void openAttachment(View v)
    {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            Uri selectedImageUri = data.getData();
          if (requestCode == 1) {
                String selectedPath3 = getPath(selectedImageUri);
                //// System.out.println("selectedPath3:::" + selectedPath3);

                Bitmap bm = null;
                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

//                imgDocBack.setImageBitmap(bm);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, baos); //bm is the bitmap object
                encodedScreenShot = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);


              try {
                  JSONObject jsonObject = new JSONObject();
                  jsonObject.accumulate("token", issue.getIssueId());
                  jsonObject.accumulate("issue", "");
                  jsonObject.accumulate("image", encodedScreenShot);




                  if (connectionDetector.isConnectingToInternet()) {
                      UpdateScreenShot updateScreenShot = new UpdateScreenShot(getApplicationContext(),bm,jsonObject);
                      updateScreenShot.execute();
                  } else {
                      Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                  }
              }
              catch (Exception e)
              {

              }



          }
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void openTicket(View v) {
        imgAttach.setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.linNotSatisfied)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.linSend)).setVisibility(View.VISIBLE);
    }

    public void submitFeedback(View v) {


        if(chatText.getText().toString().trim().equalsIgnoreCase("") || chatText.getText().toString().trim().length()<10)
        {
            AlertBuilder alertBuilder = new AlertBuilder(PastIssuesDetailsActivity.this);
            alertBuilder.showAlert("Please enter more details");
        }
        else {
            if (connectionDetector.isConnectingToInternet()) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("token", token);
                    jsonObject.accumulate("issue", chatText.getText().toString().trim());
                    jsonObject.accumulate("image", "");




                    if (connectionDetector.isConnectingToInternet()) {
                        UpdateSHM updateSHM = new UpdateSHM(getApplicationContext(),issueArrayList,jsonObject);
                        updateSHM.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e)
                {

                }
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }

        }

    }

    class ChatArrayAdapter extends ArrayAdapter<Issue> {

        private TextView chatText, txtDate,txtUser;
        private ImageView imgScreen;
        private List<Issue> issueList = new ArrayList<Issue>();
        private Context context;

        public ChatArrayAdapter(Context context, int textViewResourceId, List<Issue> issueList) {
            super(context, textViewResourceId);
            this.issueList = issueList;
            this.context = context;
        }

        @Override
        public void add(Issue object) {
            issueList.add(object);
            super.add(object);
        }

        public int getCount() {
            return this.issueList.size();
        }

        public Issue getItem(int index) {
            return this.issueList.get(index);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Issue issue = getItem(position);
            View row = convertView;
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (issue.getIssueSent().trim().equalsIgnoreCase("admin")) {
                row = inflater.inflate(R.layout.left, parent, false);
            } else if (issue.getIssueSent().trim().equalsIgnoreCase("user")) {
                row = inflater.inflate(R.layout.right, parent, false);

            } else {
                row = inflater.inflate(R.layout.right, parent, false);

            }
            chatText = (TextView) row.findViewById(R.id.msgr);
            txtDate = (TextView) row.findViewById(R.id.txtDate);
            txtUser = (TextView)row.findViewById(R.id.txtUser);
            imgScreen = (ImageView)row.findViewById(R.id.imgScreen);
            if(issue.getIssueImage().trim().equalsIgnoreCase("NA")) {

                imgScreen.setVisibility(View.GONE);
                chatText.setVisibility(View.VISIBLE);


                if (issue.getIssueSent().trim().equalsIgnoreCase("admin"))
                    txtUser.setText("customer care");
                else
                    txtUser.setText(userInfoPref.getString("firstname", "user"));
                chatText.setText(issue.getIssueContent());
                txtDate.setText(issue.getIssueDate());
            }
            else
            {
                imgScreen.setVisibility(View.VISIBLE);
                chatText.setVisibility(View.GONE);
                Picasso.with(PastIssuesDetailsActivity.this).load(issue.getIssueImage()).into(imgScreen);
                txtUser.setText(userInfoPref.getString("firstname", "user"));
                txtDate.setText(issue.getIssueDate());
            }
            return row;
        }
    }

    class ChatImageArrayAdapter extends ArrayAdapter<Issue> {

        private TextView chatText, txtDate,txtUser;
        private ImageView imgScreen;
        private List<Issue> issueList = new ArrayList<Issue>();
        private Context context;
        private Bitmap bitmap;

        public ChatImageArrayAdapter(Context context, int textViewResourceId, List<Issue> issueList,Bitmap bitmap) {
            super(context, textViewResourceId);
            this.issueList = issueList;
            this.context = context;
            this.bitmap = bitmap;
        }

        @Override
        public void add(Issue object) {
            issueList.add(object);
            super.add(object);
        }

        public int getCount() {
            return this.issueList.size();
        }

        public Issue getItem(int index) {
            return this.issueList.get(index);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Issue issue = getItem(position);
            View row = convertView;
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (issue.getIssueSent().trim().equalsIgnoreCase("admin")) {
                row = inflater.inflate(R.layout.left, parent, false);
            } else if (issue.getIssueSent().trim().equalsIgnoreCase("user")) {
                row = inflater.inflate(R.layout.right, parent, false);

            } else {
                row = inflater.inflate(R.layout.right, parent, false);

            }

            chatText = (TextView) row.findViewById(R.id.msgr);
            txtDate = (TextView) row.findViewById(R.id.txtDate);
            txtUser = (TextView)row.findViewById(R.id.txtUser);
            imgScreen = (ImageView)row.findViewById(R.id.imgScreen);

            if(issue.getIssueImage().trim().equalsIgnoreCase("NA")) {

                imgScreen.setVisibility(View.GONE);
                chatText.setVisibility(View.VISIBLE);


                if (issue.getIssueSent().trim().equalsIgnoreCase("admin"))
                    txtUser.setText("customer care");
                else
                    txtUser.setText(userInfoPref.getString("firstname", "user"));
                chatText.setText(issue.getIssueContent());
                txtDate.setText(issue.getIssueDate());
            }
            else
            {
                imgScreen.setVisibility(View.VISIBLE);
                chatText.setVisibility(View.GONE);
                imgScreen.setImageBitmap(bitmap);
                txtUser.setText(userInfoPref.getString("firstname", "user"));
                txtDate.setText(issue.getIssueDate());
            }
            return row;
        }
    }

    private class GetSubIssues extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        ProgressDialog pDialog;


        public GetSubIssues(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PastIssuesDetailsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                WebServiceHandler serviceHandler = new WebServiceHandler(PastIssuesDetailsActivity.this);
                String jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(), WebServiceHandler.POST2);


                Log.i("Response: >>>>>>>>>>>", jsonStr);

                if (jsonStr != null || !jsonStr.equalsIgnoreCase("")) {
                    try {
                        JSONObject jsonMainObj = new JSONObject(jsonStr);

                        if (jsonMainObj.getString("responseStatus").trim().equalsIgnoreCase("SUCCESS")) {
                            parentId = jsonMainObj.getString("tokenId").trim();

                            JSONArray jsonArray = jsonMainObj.getJSONArray("issueList");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Issue issue = new Issue();

                                issue.setIssueDate(jsonObject.getString("issueDate"));
                                issue.setIssueContent(jsonObject.getString("issueContent"));
                                issue.setIssueSent(jsonObject.getString("issueSent"));
                                issue.setIssueImage(jsonObject.getString("image"));

                                issueArrayList.add(issue);
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

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {

                chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right, issueArrayList);
                ;
                listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                listView.setAdapter(chatArrayAdapter);

                //to scroll the list view to bottom on data change
                chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        listView.setSelection(chatArrayAdapter.getCount() - 1);
                    }
                });

            } else if (result.equalsIgnoreCase("Failure")) {

                try {
                    AlertBuilder alertBuilder = new AlertBuilder(PastIssuesDetailsActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                } catch (Exception e) {
                }

            } else {
                AlertBuilder alertBuilder = new AlertBuilder(PastIssuesDetailsActivity.this);
                AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(result);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        finish();
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();

            }

        }

    }

    private class UpdateSHM extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        ProgressDialog pDialog;
        private ArrayList<Issue> issueArrayList;
        private JSONObject jsonObject;

        public UpdateSHM(Context context, ArrayList<Issue> issueArrayList,JSONObject jsonObject) {
            this.context = context;
            this.issueArrayList = issueArrayList;
            this.jsonObject = jsonObject;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PastIssuesDetailsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected String doInBackground(String... arg0) {

            try {


                String json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                WebServiceHandler serviceHandler = new WebServiceHandler(PastIssuesDetailsActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(getResources().getString(R.string.updateSHMBNew), WebServiceHandler.POST);

                // System.out.println("json: >>>>>>>>>>>" + json);
                // System.out.println("response: >>>>>>>>>>>" + jsonStr);
                JSONObject jsonObject1 = new JSONObject(jsonStr);
                if (jsonObject1.getString("responseStatus").trim().equalsIgnoreCase("Success")) {
                    responseMessage = jsonObject1.getString("responseMessage");
                    return "Success";
                } else  if (jsonObject1.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {
                    responseMessage = jsonObject1.getString("responseMessage");
                    return "Failure";
                }
                else
                {
                    return "Failed";
                }



            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failed";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {


                Date dNow = new Date();
                SimpleDateFormat ft =
                        new SimpleDateFormat("yyyy-MM-dd");

                Issue issue = new Issue();
                issue.setIssueContent(chatText.getText().toString().trim());
                issue.setIssueDate(ft.format(dNow));
                issueArrayList.add(issue);
                chatText.setText("");
                chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.left, issueArrayList);
                ;
                listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                listView.setAdapter(chatArrayAdapter);
                //to scroll the list view to bottom on data change
                chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        listView.setSelection(chatArrayAdapter.getCount() - 1);
                    }
                });
            } else if (result.equalsIgnoreCase("Failure")) {

                try {
                    AlertBuilder alertBuilder = new AlertBuilder(PastIssuesDetailsActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(responseMessage);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                } catch (Exception e) {
                }

            }
            else
            {
                try {
                    AlertBuilder alertBuilder = new AlertBuilder(PastIssuesDetailsActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                } catch (Exception e) {
                }
            }

        }

    }


    private class UpdateScreenShot extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;

        ProgressDialog pDialog;
        private Bitmap bitmap;
        private JSONObject jsonObject;

        public UpdateScreenShot(Context context, Bitmap bitmap,JSONObject jsonObject) {
            this.context = context;
            this.bitmap = bitmap;
            this.jsonObject = jsonObject;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PastIssuesDetailsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                String json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                WebServiceHandler serviceHandler = new WebServiceHandler(PastIssuesDetailsActivity.this, se);
                String jsonStr = serviceHandler.makeServiceCall(getResources().getString(R.string.updateSHMBNew), WebServiceHandler.POST);

                // System.out.println("json: >>>>>>>>>>>" + json);
                // System.out.println("response: >>>>>>>>>>>" + jsonStr);
                JSONObject jsonObject1 = new JSONObject(jsonStr);
                if (jsonObject1.getString("responseStatus").trim().equalsIgnoreCase("Success")) {
                    responseMessage = jsonObject1.getString("responseMessage");
                    return "Success";
                } else if (jsonObject1.getString("responseStatus").trim().equalsIgnoreCase("Failure"))  {
                    responseMessage = jsonObject1.getString("responseMessage");
                    return "Failure";
                }
                else
                {
                    return "Failed";
                }

            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failed";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {


                Date dNow = new Date();
                SimpleDateFormat ft =
                        new SimpleDateFormat("yyyy-MM-dd");

                Issue issue = new Issue();
                issue.setIssueContent(chatText.getText().toString().trim());
                issue.setIssueDate(ft.format(dNow));
                issue.setIssueImage("DONE");

                issueArrayList.add(issue);
                chatText.setText("");
                 chatImageArrayAdapter = new ChatImageArrayAdapter(getApplicationContext(), R.layout.left, issueArrayList,bitmap);
                ;
                listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                listView.setAdapter(chatImageArrayAdapter);
                //to scroll the list view to bottom on data change
                chatImageArrayAdapter.registerDataSetObserver(new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        listView.setSelection(chatImageArrayAdapter.getCount() - 1);
                    }
                });
            } else if (result.equalsIgnoreCase("Failure")) {

                try {
                    AlertBuilder alertBuilder = new AlertBuilder(PastIssuesDetailsActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(responseMessage);
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                } catch (Exception e) {
                }

            }
            else
            {
                try {
                    AlertBuilder alertBuilder = new AlertBuilder(PastIssuesDetailsActivity.this);
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                } catch (Exception e) {
                }
            }

        }

    }

}
