package com.tcs.mmpl.customer.Goibibo.Bus.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tcs.mmpl.customer.Goibibo.Bus.GoibiboBusDatabaseHelper;
import com.tcs.mmpl.customer.Goibibo.Bus.GoibiboBusSearchRoundTripActivity;
import com.tcs.mmpl.customer.Goibibo.Bus.GoibiboBusSeatMapActivity;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusBasicInfo;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusDetails;
import com.tcs.mmpl.customer.R;

import java.util.ArrayList;

/**
 * Created by hp on 2016-07-23.
 */
public class BusOneWayFragment extends Fragment {

    private static final int MODE_PRIVATE = 0;
    private ListView listBus;
    private  ArrayList<BusDetails> result;


    private GoibiboBusDatabaseHelper goibiboBusDatabaseHelper;
    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;
    private static final int REQUEST_RETURN_BUS = 5;


    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //// System.out.println("Coming inside OnCreate");
        try {
            result = new ArrayList<BusDetails>();
            result = (ArrayList<BusDetails>) getArguments().getSerializable("result");


        } catch (NullPointerException e) {

        }
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bus_one_way, container, false);
        setRetainInstance(true);
        //// System.out.println("Coming inside OnCreateview");

        GoibiboPref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

        goibiboBusDatabaseHelper = new GoibiboBusDatabaseHelper(getActivity());
        listBus = (ListView) rootView.findViewById(R.id.listBus);
        showBusList(result);

        return rootView;
    }


    public static BusOneWayFragment newInstance(ArrayList<BusDetails> result) {
        BusOneWayFragment busOneWayFragment = new BusOneWayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("result", result);
        busOneWayFragment.setArguments(bundle);
        return busOneWayFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RETURN_BUS) {
            try {
                //// System.out.println("Coming result to first activity");
                String res = data.getStringExtra("result");
                if (!res.equalsIgnoreCase("NA"))
                    ((ViewPager) ((GoibiboBusSearchRoundTripActivity) getActivity()).findViewById(R.id.pager)).setCurrentItem(2);
            } catch (Exception e) {

            }
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Sync UI state to current fragment and task state
//        if(isTaskRunning(busSearch)) {
//            showProgressBar();
//        }else {
//            hideProgressBar();
//        }

        super.onActivityCreated(savedInstanceState);
    }


    public void showBusList(final ArrayList<BusDetails> busDetailsList) {


//
//        // System.out.println("Coming inside Bus");
//        final ArrayList<BusDetails> busDetailsList = new ArrayList<BusDetails>();
//        try
//        {
//            JSONObject jsonObject = new JSONObject(json);
//            String data = jsonObject.get("data").toString();
//
//            JSONObject jsonObject1 = new JSONObject(data);
//            JSONArray arrayOnwardFlights = jsonObject1.getJSONArray("onwardflights");
//
//
//            for(int i=0 ; i<arrayOnwardFlights.length();i++)
//            {
//
//                // System.out.println("Coming inside for");
//                JSONObject jsonObject2 = arrayOnwardFlights.getJSONObject(i);
//                BusDetails busDetails = new BusDetails();
//
//                busDetails.setOrigin(jsonObject2.get("origin").toString());
//                busDetails.setRating(jsonObject2.get("rating").toString());
//                busDetails.setDepartureTime(jsonObject2.get("DepartureTime").toString());
//                busDetails.setSeat(jsonObject2.get("seat").toString());
//                busDetails.setDuration(jsonObject2.get("duration").toString());
//                busDetails.setQtype(jsonObject2.get("qtype").toString());
//                busDetails.setBusCondition(jsonObject2.get("busCondition").toString());
//                busDetails.setDestination(jsonObject2.get("destination").toString());
//                busDetails.setBusType(jsonObject2.get("BusType").toString());
//                busDetails.setSkey(jsonObject2.get("skey").toString());
//                busDetails.setTotalfare((new JSONObject(jsonObject2.get("fare").toString())).get("totalfare").toString());
//
//                JSONArray list = (new JSONObject(jsonObject2.get("RouteSeatTypeDetail").toString())).getJSONArray("list");
//                JSONObject jsonList = list.getJSONObject(0);
//                busDetails.setTravelsName(jsonList.get("SeatsAvailable").toString());
//                busDetails.setTravelsName(jsonList.get("availabilityStatus").toString());
//                busDetails.setTravelsName(jsonObject2.get("TravelsName").toString());
//                busDetails.setArrivalTime(jsonObject2.get("ArrivalTime").toString());
//                busDetails.setArrdate(jsonObject2.get("arrdate").toString());
//
//
//                busDetailsList.add(busDetails);
//
//            }

        try {
//            JsonParser jsonParser = new JsonParser(getActivity(), json);
//            final ArrayList<BusDetails> busDetailsList = jsonParser.convertOnwardBusSearch();
            if (busDetailsList.isEmpty()) {

//                AlertBuilder alertBuilder = new AlertBuilder(getActivity());
//                AlertDialog.Builder alertDialog = alertBuilder.showRetryAlert(getResources().getString(R.string.bus_not_found));
//                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Write your code here to execute after dialog closed
//                        dialog.cancel();
//                        getActivity().finish();
//                    }
//                });
//                alertDialog.show();

            } else {
                //// System.out.println("Coming inside List");


                listBus.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                BusListAdapter adapter = new BusListAdapter(getActivity(), R.layout.bus_search_row_layout, listBus, busDetailsList);
                listBus.setAdapter(adapter);

                listBus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        goibiboBusDatabaseHelper.deleteSeatMapAll();
                        goibiboBusDatabaseHelper.deletbusInfoAll();
                        goibiboBusDatabaseHelper.deletebusTraveller();
                        goibiboBusDatabaseHelper.deletebusBoardingPoint();


                        BusBasicInfo busBasicInfo = new BusBasicInfo();
                        busBasicInfo.setSkey(busDetailsList.get(position).getSkey());
                        busBasicInfo.setWay(getActivity().getResources().getString(R.string.onw));
                        busBasicInfo.setOrigin(busDetailsList.get(position).getOrigin());
                        busBasicInfo.setDestination(busDetailsList.get(position).getDestination());
                        busBasicInfo.setBusoperator(busDetailsList.get(position).getTravelsName());
                        busBasicInfo.setDeparturedate(busDetailsList.get(position).getDepdate());
                        busBasicInfo.setArrivaldate(busDetailsList.get(position).getArrdate());
                        busBasicInfo.setDuration(busDetailsList.get(position).getDuration());

                        goibiboBusDatabaseHelper.insertBusInfo(busBasicInfo);

                        GoibiboEditor.putString(getResources().getString(R.string.way), getActivity().getResources().getString(R.string.onw));
                        GoibiboEditor.commit();

                        if (GoibiboPref.getString(getActivity().getResources().getString(R.string.bus_travel), getActivity().getResources().getString(R.string.up)).equalsIgnoreCase(getActivity().getResources().getString(R.string.up_down)) && GoibiboPref.getString(getActivity().getString(R.string.way),"").equalsIgnoreCase(getActivity().getResources().getString(R.string.onw))) {

                            Intent intent = new Intent(getActivity(), GoibiboBusSeatMapActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("BusDetails", busDetailsList.get(position));
                            startActivityForResult(intent, REQUEST_RETURN_BUS);
                        } else {

                            Intent intent = new Intent(getActivity(), GoibiboBusSeatMapActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("BusDetails", busDetailsList.get(position));
                            startActivity(intent);
                        }
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class BusListAdapter extends ArrayAdapter<String> {

        private static final int MODE_PRIVATE = 0;
        Context context;
        ListView listView;
        int layout_id;

        ArrayList<BusDetails> busDetailsList;

        public BusListAdapter(Context context, int resource,
                              ListView lstView, ArrayList<BusDetails> busDetailsList) {
            super(context, resource);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.layout_id = resource;
            this.listView = lstView;
            this.busDetailsList = busDetailsList;


        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return busDetailsList.size();
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            try {


                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View rowView = inflater.inflate(layout_id, null, true);


                TextView txtTiming = (TextView) rowView.findViewById(R.id.txtTiming);
                TextView txtAmount = (TextView) rowView.findViewById(R.id.txtAmount);
                TextView txtTravels = (TextView) rowView.findViewById(R.id.txtTravels);
                TextView txtDuration = (TextView) rowView.findViewById(R.id.txtDuration);
                TextView txtBusCondition = (TextView) rowView.findViewById(R.id.txtBusCondition);
                TextView txtSeatLeft = (TextView) rowView.findViewById(R.id.txtSeatLeft);
                TextView txtRating = (TextView) rowView.findViewById(R.id.txtRating);

                txtTiming.setText(busDetailsList.get(position).getDepartureTime() + getResources().getString(R.string.right_arrow_symbol) + busDetailsList.get(position).getArrivalTime());
                txtAmount.setText(context.getResources().getString(R.string.rupee_symbol) + busDetailsList.get(position).getTotalfare());
                txtTravels.setText(busDetailsList.get(position).getTravelsName());
                txtDuration.setText(busDetailsList.get(position).getDuration());
                txtBusCondition.setText(busDetailsList.get(position).getBusType());
                if(busDetailsList.get(position).getSeatsAvailable().trim().equalsIgnoreCase("1"))
                    txtSeatLeft.setText(busDetailsList.get(position).getSeatsAvailable()+" Seat Left");
                else
                txtSeatLeft.setText(busDetailsList.get(position).getSeatsAvailable()+" Seats Left");
                txtRating.setText(busDetailsList.get(position).getRating() + "/5");


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        goibiboBusDatabaseHelper.deleteSeatMapAll();
                        goibiboBusDatabaseHelper.deletbusInfoAll();
                        goibiboBusDatabaseHelper.deletebusTraveller();
                        goibiboBusDatabaseHelper.deletebusBoardingPoint();

                        BusBasicInfo busBasicInfo = new BusBasicInfo();
                        busBasicInfo.setSkey(busDetailsList.get(position).getSkey());
                        busBasicInfo.setWay(getActivity().getResources().getString(R.string.onw));
                        busBasicInfo.setOrigin(busDetailsList.get(position).getOrigin());
                        busBasicInfo.setDestination(busDetailsList.get(position).getDestination());
                        busBasicInfo.setBusoperator(busDetailsList.get(position).getTravelsName());
                        busBasicInfo.setDeparturedate(busDetailsList.get(position).getDepdate());
                        busBasicInfo.setArrivaldate(busDetailsList.get(position).getArrdate());
                        busBasicInfo.setDuration(busDetailsList.get(position).getDuration());

                        goibiboBusDatabaseHelper.insertBusInfo(busBasicInfo);

                        GoibiboEditor.putString(getResources().getString(R.string.way), getActivity().getResources().getString(R.string.onw));
                        GoibiboEditor.commit();


                        if (GoibiboPref.getString(getActivity().getResources().getString(R.string.bus_travel), getActivity().getResources().getString(R.string.up)).equalsIgnoreCase(getActivity().getResources().getString(R.string.up_down)) && GoibiboPref.getString(getActivity().getString(R.string.way),"").equalsIgnoreCase(getActivity().getResources().getString(R.string.onw))) {

                            Intent intent = new Intent(getActivity(), GoibiboBusSeatMapActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("BusDetails", busDetailsList.get(position));
                            startActivityForResult(intent, REQUEST_RETURN_BUS);
                        } else {
                            Intent intent = new Intent(getActivity(), GoibiboBusSeatMapActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("BusDetails", busDetailsList.get(position));
                            startActivity(intent);
                        }

                    }
                });


                return rowView;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                return null;
            }
        }

    }
}
