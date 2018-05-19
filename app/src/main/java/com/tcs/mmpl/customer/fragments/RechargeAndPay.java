package com.tcs.mmpl.customer.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.billdesk.sdk.PaymentOptions;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.WebServiceHandler;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 9/15/2015.
 */
public class RechargeAndPay extends Fragment
{


    private static final int MODE_PRIVATE = 0;
    FontClass fontclass=new FontClass();
    Typeface typeface;
    private Button button;
    private ViewFlipper viewFlipper_recharge;
    private Button btn_confirm;
    Spinner spnr_pay_oprator;
    String[] oprator_pay;
    ProgressDialog pDialog;
    SharedPreferences pref,userInfoPref;
    SharedPreferences.Editor editor,userInfoEditor;
    EditText edtMpin;
    Spinner spinnerAmount;
    Button btnLoadMoney;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.flipperlayout_rechargeandpay,
                container, false);
        typeface=Typeface.createFromAsset(getActivity().getAssets(),"helvetica.otf");

        init(rootView);

       // onClick();


       /* rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK ))
                {
                    if(viewFlipper_recharge.getDisplayedChild() == 2)
                    {
                        viewFlipper_recharge.setDisplayedChild(1);
                    }
                    else if(viewFlipper_recharge.getDisplayedChild() == 1)
                    {
                        viewFlipper_recharge.setDisplayedChild(0);
                    }
                    else
                    {
                        getActivity().finish();
                    }

                    return true;
                }
                return false;
            }
        } );*/

        return rootView;
    }

   /* private void onClick()
    {
        // TODO Auto-generated method stub

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                viewFlipper_recharge.setDisplayedChild(1);



            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                viewFlipper_landline.setDisplayedChild(2);



            }
        });

    }
*/
    private void init(View rootView) {
        // TODO Auto-generated method stub

         //button = (Button) rootView.findViewById(R.id.payment_landline);
        viewFlipper_recharge = (ViewFlipper) rootView.findViewById(R.id.viewflipper_rechargeAndPay);
        fontclass.setFont(viewFlipper_recharge,typeface );
      //  btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm_landline);

        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

        userInfoPref = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPref.edit();

        edtMpin = (EditText)rootView.findViewById(R.id.edtMpin);
        btnLoadMoney = (Button)rootView.findViewById(R.id.btnLoadMoney);

        spinnerAmount = (Spinner) rootView.findViewById(R.id.spinnerAmount);
        oprator_pay = getResources().getStringArray(R.array.amount_arrays);

        spinnerAmount.setAdapter(new MyCustomAdapter(rootView.getContext(),
                android.R.layout.simple_spinner_item,oprator_pay));

        spinnerAmount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {

               /* Toast.makeText(parentView.getContext(),
                        "You have selected " + oprator[+position],
                        Toast.LENGTH_LONG).show();*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        btnLoadMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String billDeskConnect_url = getResources().getString(R.string.billDeskConnect);//+"?Recharge="+recharge;
                //// System.out.println(billDeskConnect_url);
                GetBillDeskConnect dc=new GetBillDeskConnect(getActivity());
                dc.execute(billDeskConnect_url);
            }
        });



    }

    public class MyCustomAdapter extends ArrayAdapter<String> {


        private Context context;
//		LayoutInflater inflater;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub

        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent)
        {


            View v = super.getDropDownView( position,  convertView, parent);
            Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
            ((TextView)v).setTypeface(custom_font);
            ((TextView)v).setPadding(10,10,10,10);

            return v;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {


            View v = super.getView( position,  convertView, parent);
            Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
            ((TextView)v).setTypeface(custom_font);
            return v;
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    private class GetBillDeskConnect extends
            AsyncTask<String, Void, String> {

        Context context;

        String concatResult,token;

        public GetBillDeskConnect (Context context ) {
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
            String jsonStr;
            try
            {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("MDN", pref.getString("mobile_number", "")));
                //nameValuePairs.add(new BasicNameValuePair("Amount", spinnerAmount.getSelectedItem().toString()));
                nameValuePairs.add(new BasicNameValuePair("Amount", "2"));


                // Creating service handler class instance
                WebServiceHandler serviceHandler = new WebServiceHandler(getActivity(),nameValuePairs);

                // Making a request to url and getting response
                jsonStr = serviceHandler.makeServiceCall(arg0[0].toString(),
                        WebServiceHandler.POST1);

                //// System.out.println("Result.........."+jsonStr);

            }
            catch(Exception e)
            {
                return "Failure";
            }
            //jsonStr = "{ \"value\":" + jsonStr + "}";
            if (jsonStr != null && jsonStr.contains("|")) {

                String[] resArray = jsonStr.split("#");
                concatResult=resArray[0];
                token = resArray[1];
                String[] res = jsonStr.split("\\|");


                //// System.out.println("msg to billdesk..............." + concatResult);
                //// System.out.println("Token........................."+token);

                //// System.out.println("Transid........." + res[1]);

                return "Success";
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            if (result == null) {
                Toast.makeText(context, getResources().getString(R.string.apidown),
                        Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("Failure")) {
                Toast.makeText(context, getResources().getString(R.string.apidown),
                        Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("Success")) {


                Float floatValue = Float.parseFloat("2");

               // Float floatValue = Float.parseFloat(spinnerAmount.getSelectedItem().toString());
                String amount = String.format("%.2f", floatValue);

                //// System.out.println("Amount::::::::::"+amount);

                String mobile =pref.getString("mobile_number", "").replaceAll("91","");

                Intent intent = new Intent(getActivity(), PaymentOptions.class);

                intent.putExtra("msg",concatResult);
                intent.putExtra("token",token);
                intent.putExtra("user-email", "tthtestmail@gmail.com");
                intent.putExtra("user-mobile",mobile);
                intent.putExtra("amount",amount);
                intent.putExtra("orientation", Configuration.ORIENTATION_PORTRAIT);
                intent.putExtra("callback", new MyObject());
                //// System.out.println("1...");
                startActivity(intent);
                //finish();
            } else {
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }

        }

    }

}







