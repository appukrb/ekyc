package com.tcs.mmpl.customer.Goibibo.Flight.Fragment;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.Goibibo.Flight.GoibiboFlightBookReviewActivity;
import com.tcs.mmpl.customer.Goibibo.Flight.GoibiboFlightDatabaseHelper;
import com.tcs.mmpl.customer.Goibibo.Flight.GoibiboFlightSearchRoundTripActivity;
import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.FlightDetails;
import com.tcs.mmpl.customer.R;

import java.util.ArrayList;

/**
 * Created by hp on 2016-07-23.
 */
public class FlightOneWayFragment extends Fragment {

    private static final int MODE_PRIVATE = 0;
    private ListView listFlight;
    private ArrayList<FlightDetails> result;

    private GoibiboFlightDatabaseHelper goibiboFlightDatabaseHelper;
    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;

    public static FlightOneWayFragment newInstance(ArrayList<FlightDetails> result) {
        FlightOneWayFragment flightOneWayFragment = new FlightOneWayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("result", result);
        flightOneWayFragment.setArguments(bundle);
        return flightOneWayFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //// System.out.println("Coming inside OnCreate");
        try {
            result = new ArrayList<FlightDetails>();
            result = (ArrayList<FlightDetails>) getArguments().getSerializable("result");


        } catch (NullPointerException e) {

        }
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_flight_way, container, false);
        setRetainInstance(true);
        //// System.out.println("Coming inside OnCreateview");

        GoibiboPref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

        goibiboFlightDatabaseHelper = new GoibiboFlightDatabaseHelper(getActivity());
        listFlight = (ListView) rootView.findViewById(R.id.listFlight);
        showFlightList(result);

        return rootView;
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


    public void showFlightList(final ArrayList<FlightDetails> flightDetailsList) {


        try {


            //// System.out.println("Coming inside List");


            listFlight.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            final FlightListAdapter adapter = new FlightListAdapter(getActivity(), R.layout.flight_search_row_layout, listFlight, flightDetailsList);
            listFlight.setAdapter(adapter);
            listFlight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    adapter.setSelectedIndex(position);

                    GoibiboEditor.putString(getResources().getString(R.string.way), getActivity().getResources().getString(R.string.onw));
                    GoibiboEditor.commit();


                    goibiboFlightDatabaseHelper.insertFlightInfo(flightDetailsList.get(position),getActivity().getResources().getString(R.string.onw) );


                    if (GoibiboPref.getString(getActivity().getResources().getString(R.string.flight_travel), getActivity().getResources().getString(R.string.up)).equalsIgnoreCase(getActivity().getResources().getString(R.string.up_down)) && GoibiboPref.getString(getActivity().getString(R.string.way), "").equalsIgnoreCase(getActivity().getResources().getString(R.string.onw))) {

                        ((ViewPager) ((GoibiboFlightSearchRoundTripActivity) getActivity()).findViewById(R.id.pager)).setCurrentItem(2);
                    }
                    else
                    {
                        Intent intent = new Intent(getActivity(),GoibiboFlightBookReviewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class FlightListAdapter extends ArrayAdapter<String> {

        private static final int MODE_PRIVATE = 0;
        Context context;
        ListView listView;
        int layout_id;
        ArrayList<FlightDetails> flightDetailsList;
        private int selectedIndex;

        public FlightListAdapter(Context context, int resource,
                                 ListView lstView, ArrayList<FlightDetails> flightDetailsList) {
            super(context, resource);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.layout_id = resource;
            this.listView = lstView;
            this.flightDetailsList = flightDetailsList;
            selectedIndex = -1;


        }

        public void setSelectedIndex(int ind) {
            selectedIndex = ind;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return flightDetailsList.size();
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            try {

                if (view == null) {
                    holder = new ViewHolder();
                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                    view = inflater.inflate(layout_id, null, true);


                    holder.txtTiming = (TextView) view.findViewById(R.id.txtTiming);
                    holder.txtAmount = (TextView) view.findViewById(R.id.txtAmount);
                    holder.txtWarning = (TextView) view.findViewById(R.id.txtWarning);
                    holder.txtDuration = (TextView) view.findViewById(R.id.txtDuration);
                    holder.txtAirlines = (TextView) view.findViewById(R.id.txtAirlines);
                    holder.imgAirlines = (ImageView) view.findViewById(R.id.imgAirlines);
                    holder.linFlight = (LinearLayout) view.findViewById(R.id.linFlight);

                    view.setTag(holder);


                } else {
                    holder = (ViewHolder) view.getTag();
                }

                holder.txtTiming.setText(flightDetailsList.get(position).getDeptime() + getResources().getString(R.string.right_arrow_symbol) + flightDetailsList.get(position).getArrtime());
                holder.txtAmount.setText(context.getResources().getString(R.string.rupee_symbol) + flightDetailsList.get(position).getTotalfare());
                holder.txtDuration.setText(flightDetailsList.get(position).getDuration());
                holder.txtWarning.setText(flightDetailsList.get(position).getWarnings());
                holder.txtAirlines.setText(flightDetailsList.get(position).getAirline());

                if (selectedIndex != -1 && position == selectedIndex) {
                    holder.linFlight.setBackgroundResource(R.drawable.layout_shadow_effect_pressed);
                } else {
                    holder.linFlight.setBackgroundResource(R.drawable.layout_shadow_effect);

                }

//                int id = getActivity().getResources().getIdentifier(flightDetailsList.get(position).getCarrierid(), "drawable", getActivity().getPackageName());
//                Drawable drawable = getResources().getDrawable(id);
//                holder.imgAirlines.setImageDrawable(drawable);

                String imagename="";
                if(flightDetailsList.get(position).getAirline().contains(" "))
                    imagename = flightDetailsList.get(position).getAirline().replaceAll(" ","_");
                else
                imagename = flightDetailsList.get(position).getAirline();
                Picasso.with(getActivity()).load("https://app.mrupee.in:8443/carrierImages/"+imagename+".gif").error(R.drawable.backgroud_default_image).placeholder(R.drawable.backgroud_default_image).into(holder.imgAirlines);



                return view;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                return null;
            }
        }

        public class ViewHolder {
            TextView txtTiming, txtAirlines;
            TextView txtAmount;
            TextView txtWarning;
            TextView txtDuration;
            ImageView imgAirlines;
            LinearLayout linFlight;

        }

    }
}
