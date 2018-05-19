package com.tcs.mmpl.customer.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tcs.mmpl.customer.utility.ConnectionDetector;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.WebServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Admin on 9/15/2015.
 */
public class CheckTransactions extends ListFragment {
    private static final int MODE_PRIVATE = 0 ;
    FontClass fontclass=new FontClass();
    Typeface typeface;

    private RadioGroup RadioGroupTwo;
    private RadioButton rbPrepaid, rbPostpaid, rbCheck, rbToday, rbYesterday, rbStatement, rbMini;

    Integer pos1,pos2,total;
    // int pos1,pos2,total;
    private Button button;
    String buttonSelected;
    private ViewFlipper viewFlipper_checktransactions;
    View rootView;
    Activity activity;
     ListView lstView;
    //TransactionAdapter adapter;
    // private Button btn_confirm;
    //  private RadioButton mini_statement;.


    TransactionAdapter adapter;
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;

    SharedPreferences pref, userInfoPref;
    SharedPreferences.Editor editor, userInfoEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        activity = getActivity();

       // lstView = getListView();

        connectionDetector = new ConnectionDetector(getActivity());

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        rootView = inflater.inflate(R.layout.flipperlayout_checktransactions,
                container, false);
        typeface=Typeface.createFromAsset(getActivity().getAssets(),"helvetica.otf");
        viewFlipper_checktransactions = (ViewFlipper)rootView.findViewById(R.id.viewflipper_checktransactions);
        fontclass.setFont(viewFlipper_checktransactions,typeface );
        button = (Button)rootView.findViewById(R.id.idsubmitbutton);

        RadioGroupTwo = (RadioGroup)rootView.findViewById(R.id.group2);



        rbToday = (RadioButton)rootView.findViewById(R.id.idToday);
        rbYesterday = (RadioButton)rootView.findViewById(R.id.idYesterday);
        rbStatement = (RadioButton)rootView.findViewById(R.id.idstatement);
        rbMini = (RadioButton)rootView.findViewById(R.id.idministatement);

        button.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view)
            {


                int selectedId2 = RadioGroupTwo.getCheckedRadioButtonId();
                RadioButton radioButton2 = (RadioButton) rootView.findViewById(selectedId2);
                //  //// System.out.println("selected_2=" + radioButton2.getText());
                pos2 = RadioGroupTwo.indexOfChild(radioButton2);

                //// System.out.println("Position............"+pos2);

                if( pos2.equals(2) )
                {

                    //System.out.print("#######################");
                    //viewFlipper_checktransactions.setDisplayedChild(1);
                }

                else
                {
                    String transurl = "";

                    if(pos2.equals(0))
                    {
                         transurl = getActivity().getResources().getString(R.string.transactionDetails)+"?MDN="+pref.getString("mobile_number","")+"&userType=C&toDate=Today&fromDate=Today";
                    }
                    else if(pos2.equals(1))
                    {
                        transurl = getActivity().getResources().getString(R.string.transactionDetails)+"?MDN="+pref.getString("mobile_number","")+"&userType=C&toDate=Yesterday&fromDate=Yesterday";
                    }
                    else
                    {
                        transurl = getActivity().getResources().getString(R.string.ministatement)+"?MDN="+pref.getString("mobile_number","")+"&userType=C&toDate=MiniStatement";
                    }

                    // String transurl = " https://app.mrupee.in:8443/mRupeeServiceNew/getAllTranscationDetail?MDN=" + pref.getString("mobile_number", "") + "&userType=C&toDate=24/09/2015&fromDate=10/09/2015";
                    if (connectionDetector.isConnectingToInternet()) {

                        GetTransaction trans = new GetTransaction(getActivity());
                        trans.execute(transurl);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }


//                    ArrayList<String> message = new ArrayList<String>();
//                    message.add("AAA");
//                    message.add("BBB");
//                    ArrayList<String> amount = new ArrayList<String>();
//                    amount.add("200");
//                    amount.add("120");
//                    lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//                    adapter = new TransactionAdapter(getActivity(), R.layout.viewtransaction_row_layout, lstView, message, amount);
//                    setListAdapter(adapter);
//
//                    Toast.makeText(getActivity(),"No Data Found", Toast.LENGTH_SHORT).show();
                }
                /*if(pos1 == 0 && pos2==3)
                {
                    viewFlipper_checktransactions.setDisplayedChild(1);
                }
                else
                {
                    Toast.makeText(getActivity(),"No Data Found", Toast.LENGTH_SHORT).show();
                }*/

            }


        });
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)) {
                    if (viewFlipper_checktransactions.getDisplayedChild() == 1)
                    {
                        viewFlipper_checktransactions.setDisplayedChild(0);
                    }
                    else
                    {
                        getActivity().finish();
                    }


                    return true;
                }
                return false;
            }
        });

        return rootView;



    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // GlobalList is a class that holds global variables, arrays etc
        // getMenuCategories returns global arraylist which is initialized in GlobalList class

//        mAdapter = new CustomArrayAdapter(getActivity(), android.R.id.list, menuItems);
//        listView.setAdapter(mAdapter);
    }


    private class GetTransaction extends AsyncTask<String, Void, String> {

        Context context;

        String firstName, lastName, walletBalance;
        ArrayList<String> desc,amount,date,month,day,year,type;

        public GetTransaction(Context context) {
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

                        if (jsonMainObj.getString("responseStatus").equalsIgnoreCase("SUCCESS")) {

                            JSONArray jsonArray = jsonMainObj
                                    .getJSONArray("transctionDetaillist");
                            //// System.out.println("I am here>>>>>>>>"+ jsonArray.length());

                            desc =new ArrayList<String>();
                            amount = new ArrayList<String>();
                            date = new ArrayList<String>();
                            day = new ArrayList<String>();
                            month = new ArrayList<String>();
                            year = new ArrayList<String>();
                            type = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject j1 = jsonArray.getJSONObject(i);
                                //// System.out.println("Category..............."+j1.getString("category"));
                                desc.add(j1.getString("category"));
                                amount.add(j1.getString("amount"));
                                date.add(j1.getString("date"));
                                day.add(j1.getString("day"));
                                month.add(j1.getString("month"));
                                year.add(j1.getString("year"));
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

            if (pDialog.isShowing())
                pDialog.dismiss();

            //// System.out.println("Result>>>>>>>>>>>>>>>>>" + result);

            if (result.equalsIgnoreCase("Success")) {


                viewFlipper_checktransactions.setDisplayedChild(2);

                final ListView lstView = getListView();

                lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                adapter = new TransactionAdapter(getActivity(),
                        R.layout.viewtransaction_row_layout, lstView, amount, desc,date,day,month,year,type);
                setListAdapter(adapter);





            } else if (result.equalsIgnoreCase("Failure")) {


                Toast.makeText(getActivity(), getResources().getString(R.string.apidown), Toast.LENGTH_LONG).show();


            } else {


                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }

        }

    }


}








