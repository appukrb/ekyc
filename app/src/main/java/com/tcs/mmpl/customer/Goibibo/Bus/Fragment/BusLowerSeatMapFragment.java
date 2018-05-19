package com.tcs.mmpl.customer.Goibibo.Bus.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Goibibo.Bus.GoibiboBusBoardingPointActivity;
import com.tcs.mmpl.customer.Goibibo.Bus.GoibiboBusDatabaseHelper;
import com.tcs.mmpl.customer.Goibibo.Bus.Interface.ActivityCommunicator;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusSeatMapChainedComparator;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusSeatMapColumnComparator;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusSeatMapDetails;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusSeatMapRowComparator;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by hp on 2016-08-02.
 */
public class BusLowerSeatMapFragment extends Fragment {
    private static final int MODE_PRIVATE = 0;
    private LinearLayout linMainSeatMap, linMain;
    private GoibiboBusDatabaseHelper goibiboBusDatabaseHelper;
    private int amount = 0;
    private ActivityCommunicator activityCommunicator;
    private Context context;
    private Button btnTotalPrice, btnBookNow;
    private LinearLayout linSelectedSeats;
    private TextView txtSelectedSeats;
    private ArrayList<String> selectedSeats;

    private ArrayList<BusSeatMapDetails> result;
    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //// System.out.println("Coming inside OnCreate");
        try {
            result = new ArrayList<BusSeatMapDetails>();
            result = (ArrayList<BusSeatMapDetails>) getArguments().getSerializable("result");


        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seat_map, container, false);
        setRetainInstance(true);

        //// System.out.println("Coming inside OnCreateView");
        GoibiboPref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();
        goibiboBusDatabaseHelper = new GoibiboBusDatabaseHelper(getActivity());
        txtSelectedSeats = (TextView) rootView.findViewById(R.id.txtSelectedSeats);
        linMainSeatMap = (LinearLayout) rootView.findViewById(R.id.linMainSeatMap);
        linSelectedSeats = (LinearLayout) rootView.findViewById(R.id.linSelectedSeats);
        linMain = (LinearLayout) rootView.findViewById(R.id.linMain);
        btnBookNow = (Button) rootView.findViewById(R.id.btnBookNow);
        btnTotalPrice = (Button) rootView.findViewById(R.id.btnTotalPrice);
        selectedSeats = new ArrayList<>();


        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int seatCount = goibiboBusDatabaseHelper.getSeatCount();
                if (seatCount > 0) {


                    goibiboBusDatabaseHelper.updateBusInfoSeatAndTotal(txtSelectedSeats.getText().toString(), String.valueOf(amount));

                    Intent intent = new Intent(getActivity(), GoibiboBusBoardingPointActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (GoibiboPref.getString(getActivity().getResources().getString(R.string.bus_travel), getActivity().getResources().getString(R.string.up)).equalsIgnoreCase(getActivity().getResources().getString(R.string.up_down)) && GoibiboPref.getString(getActivity().getString(R.string.way), "").equalsIgnoreCase(getActivity().getResources().getString(R.string.onw))) {
                        startActivityForResult(intent, 6);
                    } else {
                        startActivity(intent);
                        getActivity().finish();
                    }


                } else {
                    AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                    alertBuilder.showAlert(getResources().getString(R.string.seat_not_selected));
                }

            }
        });
        //// System.out.println("Result Length:" + result.size());
        showSeat(result);

        return rootView;
    }


    public static BusLowerSeatMapFragment newInstance(ArrayList<BusSeatMapDetails> result) {
        BusLowerSeatMapFragment busLowerSeatMapFragment = new BusLowerSeatMapFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("result", result);
        busLowerSeatMapFragment.setArguments(bundle);
        return busLowerSeatMapFragment;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 6) {
            try {

                //// System.out.println("Coming result to second activity");
                String res = data.getStringExtra("result");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", res);
                getActivity().setResult(5, returnIntent);
                getActivity().finish();
            } catch (Exception e) {

            }
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public void showSeat(ArrayList<BusSeatMapDetails> busSeatMapDetailsArrayList) {

        //// System.out.println("Before Sorting::::::::::::");
        for (BusSeatMapDetails obj : busSeatMapDetailsArrayList) {
            //// System.out.println("ROW : " + obj.getRowNo() + " COL : " + obj.getColumnNo());
        }

        //Sort the list
        Collections.sort(busSeatMapDetailsArrayList, new BusSeatMapChainedComparator(
                        new BusSeatMapRowComparator(), new BusSeatMapColumnComparator()
                )
        );


        //// System.out.println("\n\nAfter Sorting::::::::::::");
        for (BusSeatMapDetails obj : busSeatMapDetailsArrayList) {
            //// System.out.println("ID : " + obj.getId() + "ROW : " + obj.getRowNo() + " COL : " + obj.getColumnNo());
        }

        int row = -1;
        int column = 0;
        int count = 0;
        LinearLayout mainLayout = null;

        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout main = new LinearLayout(getActivity());
        main.setLayoutParams(param1);
        main.setOrientation(LinearLayout.HORIZONTAL);
        //main.setWeightSum(4);


        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        param.weight = 1;


        for (int i = 0; i < busSeatMapDetailsArrayList.size(); i++) {


            BusSeatMapDetails busSeatMapDetails = busSeatMapDetailsArrayList.get(i);
            if (Integer.parseInt(busSeatMapDetails.getDeck()) == 1) {

                int tempRow = Integer.parseInt(busSeatMapDetails.getRowNo());
                if (row != tempRow) {
                    row = tempRow;
                    if (count != 0) {
                        main.addView(mainLayout);
                    }

                    mainLayout = new LinearLayout(getActivity());
                    mainLayout.setId(busSeatMapDetails.getId());
                    mainLayout.setLayoutParams(param);
                    mainLayout.setOrientation(LinearLayout.VERTICAL);
                    count = count + 1;

                }

                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(5, 5, 5, 5);

                //Create ImageView
                ImageView imageView = new ImageView(getActivity());
                imageView.setId(busSeatMapDetails.getId());

                if (Integer.parseInt(busSeatMapDetails.getHeight()) == 1 && Integer.parseInt(busSeatMapDetails.getWidth()) == 1) {

                    if (busSeatMapDetails.getSeatStatus().equalsIgnoreCase("1")) {
                        if (busSeatMapDetails.getSeat_status().equalsIgnoreCase("availableLadiesSeater"))
                            imageView.setBackgroundResource(R.drawable.seater_ladies);
                        else
                            imageView.setBackgroundResource(R.drawable.seater);
                    } else {
                        imageView.setBackgroundResource(R.drawable.seater_booked);
                        imageView.setClickable(false);
                        imageView.setEnabled(false);
                    }
                } else if (Integer.parseInt(busSeatMapDetails.getHeight()) == 1 && Integer.parseInt(busSeatMapDetails.getWidth()) == 2) {
                    if (busSeatMapDetails.getSeatStatus().equalsIgnoreCase("1")) {
                        if (busSeatMapDetails.getSeat_status().equalsIgnoreCase("availableLadiesSleeper"))
                            imageView.setBackgroundResource(R.drawable.sleeper_ladies_horizontal);
                        else
                            imageView.setBackgroundResource(R.drawable.sleeper_horizontal);
                    } else {
                        imageView.setBackgroundResource(R.drawable.sleeper_booked_horizontal);
                        imageView.setClickable(false);
                        imageView.setEnabled(false);
                    }
                } else if (Integer.parseInt(busSeatMapDetails.getHeight()) == 2 && Integer.parseInt(busSeatMapDetails.getWidth()) == 1) {
                    if (busSeatMapDetails.getSeatStatus().equalsIgnoreCase("1")) {
                        if (busSeatMapDetails.getSeat_status().equalsIgnoreCase("availableLadiesSleeper"))
                            imageView.setBackgroundResource(R.drawable.sleeper_ladies);
                        else
                            imageView.setBackgroundResource(R.drawable.sleeper);
                    } else {
                        imageView.setBackgroundResource(R.drawable.sleeper_booked);
                        imageView.setClickable(false);
                        imageView.setEnabled(false);
                    }
                } else if (Integer.parseInt(busSeatMapDetails.getHeight()) == 3 && Integer.parseInt(busSeatMapDetails.getWidth()) == 1) {
                    imageView.setBackgroundResource(R.drawable.seater_dummy);
                    imageView.setClickable(false);
                    imageView.setEnabled(false);
                } else if (Integer.parseInt(busSeatMapDetails.getHeight()) == 4 && Integer.parseInt(busSeatMapDetails.getWidth()) == 1) {
                    imageView.setBackgroundResource(R.drawable.sleeper_dummy);
                    imageView.setClickable(false);
                    imageView.setEnabled(false);
                }

                imageView.setOnClickListener(onImageClick(imageView, busSeatMapDetails));
                linearLayout.addView(imageView);
                mainLayout.addView(linearLayout);

                if (busSeatMapDetails.getId() == busSeatMapDetailsArrayList.get(busSeatMapDetailsArrayList.size() - 1).getId()) {
                    main.addView(mainLayout);
                }

            }

        }

        if (count != 0) {


            //// System.out.println("Count::::" + count);
            linMain.setVisibility(View.VISIBLE);
            linMainSeatMap.addView(main);
        }


    }

    View.OnClickListener onImageClick(final ImageView imageView, final BusSeatMapDetails busSeatMapDetails) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                Toast.makeText(getActivity(), busSeatMapDetails.getId() + " " + busSeatMapDetails.getSeatName() + " ROW" + busSeatMapDetails.getRowNo() + " COL" + busSeatMapDetails.getColumnNo(), Toast.LENGTH_LONG).show();

                //If it up and down ticket first we have to select onward seat
                if (GoibiboPref.getString(getActivity().getResources().getString(R.string.bus_travel), getActivity().getString(R.string.up)).equalsIgnoreCase(getActivity().getString(R.string.up_down)) && GoibiboPref.getString(getString(R.string.way), "").equalsIgnoreCase(getResources().getString(R.string.ret))) {

                    int OnwardSeatCount = goibiboBusDatabaseHelper.getOnwardSeatCount();
                    if (OnwardSeatCount == 0) {
                        AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                        alertBuilder.showAlert(getResources().getString(R.string.select_onward));
                        return;
                    }
                    int ReturnSeatCount = goibiboBusDatabaseHelper.getReturnSeatCount();
                    if (ReturnSeatCount == OnwardSeatCount) {
                        AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                        if (OnwardSeatCount == 1)
                            alertBuilder.showAlert(getResources().getString(R.string.select_max1) + OnwardSeatCount + " seat");
                        else
                            alertBuilder.showAlert(getResources().getString(R.string.select_max1) + OnwardSeatCount + " seats");
                        return;
                    }
                }

                if (goibiboBusDatabaseHelper.isSeatSelected(busSeatMapDetails.getId())) {

                    selectedSeats.remove(busSeatMapDetails.getSeatName());
                    if (btnTotalPrice.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.total_price))) {
                        amount = amount - Integer.parseInt(busSeatMapDetails.getSeatFare());
                        btnTotalPrice.setText(getResources().getString(R.string.total_price) + " " + getResources().getString(R.string.rupee_symbol) + amount);

                    } else {
                        amount = amount - Integer.parseInt(busSeatMapDetails.getSeatFare());
                        if (amount == 0)
                            btnTotalPrice.setText(getResources().getString(R.string.total_price));
                        else
                            btnTotalPrice.setText(getResources().getString(R.string.total_price) + " " + getResources().getString(R.string.rupee_symbol) + amount);
                    }

                    if (Integer.parseInt(busSeatMapDetails.getHeight()) == 1 && Integer.parseInt(busSeatMapDetails.getWidth()) == 1)
                        imageView.setBackgroundResource(R.drawable.seater);
                    else if (Integer.parseInt(busSeatMapDetails.getHeight()) == 1 && Integer.parseInt(busSeatMapDetails.getWidth()) == 2)
                        imageView.setBackgroundResource(R.drawable.sleeper_horizontal);
                    else if (Integer.parseInt(busSeatMapDetails.getHeight()) == 2 && Integer.parseInt(busSeatMapDetails.getWidth()) == 1)
                        imageView.setBackgroundResource(R.drawable.sleeper);

                    goibiboBusDatabaseHelper.deleteSeatMap(busSeatMapDetails.getId());
                } else {


                    int seatCount = goibiboBusDatabaseHelper.getSeatCount();
                    //// System.out.println("Seat Count:::::::" + seatCount);
                    if (seatCount < 6) {
                        selectedSeats.add(busSeatMapDetails.getSeatName());

                        if (btnTotalPrice.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.total_price))) {
                            //// System.out.println("Coming inside if:::::::" + busSeatMapDetails.getSeatFare());
                            amount = amount + Integer.parseInt(busSeatMapDetails.getSeatFare());
                            btnTotalPrice.setText(getResources().getString(R.string.total_price) + " " + getResources().getString(R.string.rupee_symbol) + amount);
                        } else {
                            //// System.out.println("Coming inside else:::::::" + busSeatMapDetails.getSeatFare());
                            amount = amount + Integer.parseInt(busSeatMapDetails.getSeatFare());
                            btnTotalPrice.setText(getResources().getString(R.string.total_price) + " " + getResources().getString(R.string.rupee_symbol) + amount);
                        }


                        if (Integer.parseInt(busSeatMapDetails.getHeight()) == 1 && Integer.parseInt(busSeatMapDetails.getWidth()) == 1)
                            imageView.setBackgroundResource(R.drawable.seater_selected);
                        else if (Integer.parseInt(busSeatMapDetails.getHeight()) == 1 && Integer.parseInt(busSeatMapDetails.getWidth()) == 2)
                            imageView.setBackgroundResource(R.drawable.sleeper_selected_horizontal);
                        else if (Integer.parseInt(busSeatMapDetails.getHeight()) == 2 && Integer.parseInt(busSeatMapDetails.getWidth()) == 1)
                            imageView.setBackgroundResource(R.drawable.sleeper_selected);

                        goibiboBusDatabaseHelper.insertSeatMap(busSeatMapDetails);
                    } else {
                        AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                        alertBuilder.showAlert(getResources().getString(R.string.max_six_seats));
                    }

                }


                if (selectedSeats.isEmpty()) {
                    linSelectedSeats.setVisibility(View.GONE);
                } else {
                    linSelectedSeats.setVisibility(View.VISIBLE);
                    StringBuffer sb = new StringBuffer();
                    for (String s : selectedSeats) {
                        sb.append(s);
                        sb.append(",");
                    }

                    sb.deleteCharAt(sb.length() - 1);
                    if (selectedSeats.size() > 1)
                        txtSelectedSeats.setText("Seats " + sb.toString());
                    else
                        txtSelectedSeats.setText("Seat " + sb.toString());
                }
            }
        };
    }
}

