package com.tcs.mmpl.customer.Goibibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GoibiboMyBookingsActivity extends Fragment {

    private static final int MODE_PRIVATE = 0;
    private ListView listBookings;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String fromInfo="",toInfo="";
    private RelativeLayout mainlinear;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    Activity activity;

    TextView txtNotify;
    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {

            if(getView() != null)
            {
                MyBookings myBookings = new MyBookings(getActivity());
                myBookings.execute(getActivity().getResources().getString(R.string.booking_detail_url) + "?MDN=" + pref.getString("mobile_number", ""));
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i("Goibibo", "Bookings");

        View rootView = inflater.inflate(R.layout.activity_goibibo_my_bookings, container, false);
        setRetainInstance(true);
        mainlinear = (RelativeLayout) rootView.findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        GoibiboPref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();


        pref = getActivity().getSharedPreferences("userstatus", MODE_PRIVATE);
        editor = pref.edit();

txtNotify = (TextView)rootView.findViewById(R.id.txtNotify);
        listBookings = (ListView) rootView.findViewById(R.id.listBookings);

        MyBookings myBookings = new MyBookings(getActivity());
        myBookings.execute(getActivity().getResources().getString(R.string.booking_detail_url) + "?MDN=" + pref.getString("mobile_number", ""));

        return rootView;
    }



    private class MyBookings extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private  ArrayList<GoibiboBookingInfo> goibiboBookingInfoList;

        public MyBookings(Context context) {
            this.context = context;
            goibiboBookingInfoList = new ArrayList<>();

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

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(getActivity());
                String jsonStr = goibiboServiceHandler.makeServiceCall(arg0[0].toString(), GoibiboServiceHandler.POST);
                return jsonStr;


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("responseStatus").trim().equalsIgnoreCase("Success")) {


                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        GoibiboBookingInfo goibiboBookingInfo = new GoibiboBookingInfo();
                        goibiboBookingInfo.setFromPlace(jsonObject1.getString("fromPlace"));
                        goibiboBookingInfo.setToPlace(jsonObject1.getString("toPlace"));
                        goibiboBookingInfo.setSkey(jsonObject1.getString("skey"));
                        goibiboBookingInfo.setRetskey(jsonObject1.getString("retskey"));
                        goibiboBookingInfo.setType(jsonObject1.getString("type"));
                        goibiboBookingInfo.setDateofjourney(jsonObject1.getString("dateofjourney"));
                        goibiboBookingInfo.setDateofboarding(jsonObject1.getString("dateofboarding"));
                        goibiboBookingInfo.setTravelstatus(jsonObject1.getString("travelstatus"));
                        goibiboBookingInfo.setBookingId(jsonObject1.getString("bookingId"));


                        goibiboBookingInfoList.add(goibiboBookingInfo);

                    }

                    if(!goibiboBookingInfoList.isEmpty()) {
                        listBookings.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        MyBookingAdapter adapter = new MyBookingAdapter(getActivity(), R.layout.view_my_booking_row_layout, listBookings, goibiboBookingInfoList);
                        listBookings.setAdapter(adapter);
                    }
                    else
                    {
                        AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                        AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                                dialog.cancel();
                                //getActivity().finish();
                            }
                        });
                        // Showing Alert Message
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }


                } else if (jsonObject.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {
//                    AlertBuilder alertBuilder = new AlertBuilder(getActivity());
//                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(jsonObject.getString("responsemessage"));
//                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Write your code here to execute after dialog closed
//                            dialog.cancel();
//                            //getActivity().finish();
//                        }
//                    });
//                    // Showing Alert Message
//                    alertDialog.setCancelable(false);
//                    alertDialog.show();
                    txtNotify.setVisibility(View.VISIBLE);
                    txtNotify.setText(jsonObject.getString("responsemessage"));

                }
                else
                {
                    AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            //getActivity().finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            } catch (Exception e) {

            }


        }

    }

    public class MyBookingAdapter extends ArrayAdapter<String> {

        private static final int MODE_PRIVATE = 0;
        Context context;
        ListView listView;
        int layout_id;

        ArrayList<GoibiboBookingInfo> goibiboBookingInfoList;

        public MyBookingAdapter(Context context, int resource,
                                ListView lstView, ArrayList<GoibiboBookingInfo> goibiboBookingInfoList) {
            super(context, resource);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.layout_id = resource;
            this.listView = lstView;
            this.goibiboBookingInfoList = goibiboBookingInfoList;


        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return goibiboBookingInfoList.size();
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            try {


                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View rowView = inflater.inflate(layout_id, null, true);


                TextView txtStartPlaceInfo = (TextView) rowView.findViewById(R.id.txtStartPlaceInfo);
                TextView txtEndPlaceInfo = (TextView) rowView.findViewById(R.id.txtEndPlaceInfo);
                TextView txtStartPlaceInfoTime = (TextView) rowView.findViewById(R.id.txtStartPlaceInfoTime);
                TextView txtEndPlaceInfoTime = (TextView) rowView.findViewById(R.id.txtEndPlaceInfoTime);
                TextView txtBookingId = (TextView) rowView.findViewById(R.id.txtBookingId);


                if(!goibiboBookingInfoList.get(position).getFromPlace().equalsIgnoreCase("null")) {
                    txtStartPlaceInfo.setText(goibiboBookingInfoList.get(position).getFromPlace());
                    txtStartPlaceInfoTime.setText( goibiboBookingInfoList.get(position).getDateofjourney().split(" ")[0]+ "\n" + goibiboBookingInfoList.get(position).getDateofjourney().split(" ")[1]);

                    txtEndPlaceInfo.setText(goibiboBookingInfoList.get(position).getToPlace());
                    txtEndPlaceInfoTime.setText(goibiboBookingInfoList.get(position).getDateofboarding().split(" ")[0]+ "\n" + goibiboBookingInfoList.get(position).getDateofboarding().split(" ")[1]);
                    txtBookingId.setText(goibiboBookingInfoList.get(position).getBookingId());
                }



                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        fromInfo = goibiboBookingInfoList.get(position).getFromPlace()+ "\n" +goibiboBookingInfoList.get(position).getDateofjourney().split(" ")[0]+ "\n" + goibiboBookingInfoList.get(position).getDateofjourney().split(" ")[1];
                        toInfo = goibiboBookingInfoList.get(position).getToPlace() + "\n" + goibiboBookingInfoList.get(position).getDateofboarding().split(" ")[0]+ "\n" + goibiboBookingInfoList.get(position).getDateofboarding().split(" ")[1];

                        BusStatusCheck busStatusCheck = new BusStatusCheck(getActivity(),goibiboBookingInfoList.get(position).getBookingId(),goibiboBookingInfoList.get(position).getSkey(),goibiboBookingInfoList.get(position).getRetskey());
                        busStatusCheck.execute(getResources().getString(R.string.bus_status_url)+"?bookingID="+goibiboBookingInfoList.get(position).getBookingId());



                    }
                });

                return rowView;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                return null;
            }
        }

    }

    private class BusStatusCheck extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private String bookingID,skey,retskey;


        public BusStatusCheck(Context context,String bookingID,String skey,String retskey) {
            this.context = context;
            this.bookingID = bookingID;
            this.skey = skey;
            this.retskey = retskey;


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

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(getActivity());
                String jsonStr = goibiboServiceHandler.makeServiceCall(arg0[0].toString(), GoibiboServiceHandler.POST);
                return jsonStr;


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            try {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.popup_booking_layout);
                dialog.setTitle("Information");

                // set the custom dialog components - text and button
                Button dialogBtnCancel = (Button) dialog.findViewById(R.id.btnCancelTicket);
                Button dialogBtnOk = (Button) dialog.findViewById(R.id.btnClose);

                final LinearLayout linSuccess = (LinearLayout) dialog.findViewById(R.id.linSuccess);
                final LinearLayout linError = (LinearLayout) dialog.findViewById(R.id.linError);

                final TextView txtFromInfo = (TextView) dialog.findViewById(R.id.txtFromInfo);
                final TextView txtToInfo = (TextView) dialog.findViewById(R.id.txtToInfo);
                final TextView txtBookingID = (TextView) dialog.findViewById(R.id.txtBookingID);
                final TextView txtStatus = (TextView) dialog.findViewById(R.id.txtStatus);
                final TextView txtError = (TextView)dialog.findViewById(R.id.txtError);


                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("responseStatus").trim().equalsIgnoreCase("Success")) {

                    linSuccess.setVisibility(View.VISIBLE);
                    txtFromInfo.setText(fromInfo);
                    txtToInfo.setText(toInfo);
                    txtBookingID.setText("Booking ID : "+bookingID);
                    txtStatus.setText("Status : " + jsonObject.getString("status"));

                    if(jsonObject.getString("status").equalsIgnoreCase("CONFIRMED"))
                        dialogBtnCancel.setVisibility(View.VISIBLE);
                    else
                        dialogBtnCancel.setVisibility(View.GONE);




                    dialogBtnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CancelTicket cancelTicket = new CancelTicket(getActivity());
                            cancelTicket.execute(getResources().getString(R.string.cancelbusticket_url) + "?MDN=" + pref.getString("mobile_number", "") + "&skey=" + skey + "&retskey=" + retskey + "&bookingID=" + bookingID);

                            dialog.dismiss();
                        }
                    });
                    dialogBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });


                    dialog.show();




                } else if (jsonObject.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {

                    linError.setVisibility(View.VISIBLE);
                    txtError.setText(jsonObject.getString("responseMessage"));
                    dialogBtnCancel.setVisibility(View.GONE);

                    dialogBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });


                    dialog.show();

                }
                else {
                    linError.setVisibility(View.VISIBLE);
                    txtError.setText(getResources().getString(R.string.apidown));
                    dialogBtnCancel.setVisibility(View.GONE);

                    dialogBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });


                    dialog.show();
                }
            } catch (Exception e) {

            }




        }

    }

    private class CancelTicket extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;
        private  ArrayList<GoibiboBookingInfo> goibiboBookingInfoList;

        public CancelTicket(Context context) {
            this.context = context;
            goibiboBookingInfoList = new ArrayList<>();

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

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(getActivity());
                String jsonStr = goibiboServiceHandler.makeServiceCall(arg0[0].toString(), GoibiboServiceHandler.POST);
                return jsonStr;


            } catch (Exception e) {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return "Failure";
            }


        }

        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.i("JSON 123",result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("responseStatus").trim().equalsIgnoreCase("Success")) {

                    AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(jsonObject.getString("responseMessage"));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            //getActivity().finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();


                } else if (jsonObject.getString("responseStatus").trim().equalsIgnoreCase("Failure")) {

                    AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(jsonObject.getString("responseMessage"));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                            //getActivity().finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                }
                else {
                    AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                    AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.apidown));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            dialog.cancel();
                           getActivity(). finish();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            } catch (Exception e) {

            }




        }

    }


}
