package com.tcs.mmpl.customer.Goibibo.Bus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;
import com.tcs.mmpl.customer.utility.MyConnectionHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GoibiboBusActivity extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private static final int MODE_PRIVATE = 0;
    Context context;
    LinearLayout mainlinear;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    MyConnectionHelper db;
    private String search_url = "";
    private CalendarPickerView dialogView;
    private AlertDialog theDialog;
    private TextView txtDate, txtReturnDate;
    private SimpleDateFormat df;
    private TextView txtFrom, txtTo;
    private RadioButton radio_one_way, radio_round_trip;
    private GoibiboBusDatabaseHelper goibiboBusDatabaseHelper;

    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;
    private TextView txtAC, txtNonAC, txtSleeper, txtSeater;

    private String selectedType = "";
    private int f1 = 0, f2 = 0, f3 = 0, f4 = 0;

    private LinearLayout linReturn, linOnward;
    private Button btnSearch;
    private String toDate="",returnDate = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

        Log.i("Goibibo", "Bus");

        View rootView = inflater.inflate(R.layout.activity_goibibo_bus, container, false);
        setRetainInstance(true);
        mainlinear = (LinearLayout) rootView.findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);
        db = new MyConnectionHelper(getActivity());

        GoibiboPref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

        goibiboBusDatabaseHelper = new GoibiboBusDatabaseHelper(getActivity());

        ((RadioGroup) rootView.findViewById(R.id.radio_group)).setOnCheckedChangeListener(this);
        radio_one_way = (RadioButton) rootView.findViewById(R.id.radio_one_way);
        radio_round_trip = (RadioButton) rootView.findViewById(R.id.radio_round_trip);
        txtDate = (TextView) rootView.findViewById(R.id.txtDate);
        txtReturnDate = (TextView) rootView.findViewById(R.id.txtReturnDate);
        txtFrom = (TextView) rootView.findViewById(R.id.txtFrom);
        txtTo = (TextView) rootView.findViewById(R.id.txtTo);

        txtAC = (TextView) rootView.findViewById(R.id.txtAC);
        txtNonAC = (TextView) rootView.findViewById(R.id.txtNonAC);
        txtSleeper = (TextView) rootView.findViewById(R.id.txtSleeper);
        txtSeater = (TextView) rootView.findViewById(R.id.txtSeater);

        linReturn = (LinearLayout) rootView.findViewById(R.id.linReturn);
        linOnward = (LinearLayout) rootView.findViewById(R.id.linOnward);

        btnSearch = (Button) rootView.findViewById(R.id.btnSearch);

        df = new SimpleDateFormat("d MMM''yy\nEEEE");
        Date currentDate = new Date();
        String strCurrentDate = df.format(currentDate);

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, 1);
        Date nextDate = c.getTime();
        String strNextDate = df.format(nextDate);

        txtDate.setText(strCurrentDate);
        txtReturnDate.setText(strNextDate);


        onClick();

        return rootView;
    }

    private void onClick() {
        // TODO Auto-generated method stub

        txtFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCity(v);
            }
        });

        txtTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCity(v);

            }
        });

        linOnward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOnwardCalendar();
            }
        });

        linReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReturnCalendar();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBus();
            }
        });

        txtAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f1 == 0) {
                    f1 = 1;
                    txtAC.setTextColor(Color.BLUE);

                    f2 = 0;
                    txtNonAC.setTextColor(Color.BLACK);

                } else {
                    f1 = 0;
                    txtAC.setTextColor(Color.BLACK);

                }
            }
        });

        txtNonAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f2 == 0) {
                    f2 = 1;
                    txtNonAC.setTextColor(Color.BLUE);

                    f1 = 0;
                    txtAC.setTextColor(Color.BLACK);

                } else {
                    f2 = 0;
                    txtNonAC.setTextColor(Color.BLACK);

                }
            }
        });

        txtSleeper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f3 == 0) {
                    f3 = 1;
                    txtSleeper.setTextColor(Color.BLUE);
                } else {
                    f3 = 0;
                    txtSleeper.setTextColor(Color.BLACK);
                }
            }
        });

        txtSeater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f4 == 0) {
                    f4 = 1;
                    txtSeater.setTextColor(Color.BLUE);

                } else {
                    f4 = 0;
                    txtSeater.setTextColor(Color.BLACK);

                }
            }
        });
    }


    public void searchBus() {

        String departure = "", arrival = "";

        goibiboBusDatabaseHelper.deletbusInfoAll();

        AlertBuilder alertBuilder = new AlertBuilder(getActivity());

        if (txtFrom.getText().toString().equalsIgnoreCase("")) {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_from));
        } else if (txtTo.getText().toString().trim().equalsIgnoreCase("")) {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_to));
        } else if (txtFrom.getText().toString().trim().equalsIgnoreCase(txtTo.getText().toString().trim())) {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_source_destination));
        } else if (radio_one_way.isChecked()) {
            String depDate = "";

            try {
                SimpleDateFormat newFormat = new SimpleDateFormat("yyyyMMdd");
                Date newDate = df.parse(txtDate.getText().toString().trim());
                depDate = newFormat.format(newDate);
            } catch (Exception e) {

            }
            GoibiboEditor.putString(getResources().getString(R.string.bus_travel), getResources().getString(R.string.up));
            GoibiboEditor.commit();
            search_url = getResources().getString(R.string.bus_search_url) + "?source=" + Uri.encode(txtFrom.getText().toString().trim(), "utf-8") + "&destination=" + Uri.encode(txtTo.getText().toString().trim(), "utf-8") + "&dateofdeparture=" + depDate;
            //// System.out.println(search_url);
            Intent i = new Intent(getActivity(), GoibiboBusSearchOneWayActivity.class);
            i.putExtra("place",txtFrom.getText().toString()+" - "+txtTo.getText().toString());
            i.putExtra("day",txtDate.getText().toString().split("\n")[0]);
            i.putExtra("url", search_url);
            i.putExtra("ac", f1);
            i.putExtra("nonac", f2);
            i.putExtra("sleeper", f3);
            i.putExtra("seater", f4);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else if (radio_round_trip.isChecked()) {
            String depDate = "", arrDate = "";

            try {
                SimpleDateFormat newFormat = new SimpleDateFormat("yyyyMMdd");
                Date newDate = df.parse(txtDate.getText().toString().trim());
                depDate = newFormat.format(newDate);

                Date newArrDate = df.parse(txtReturnDate.getText().toString().trim());
                arrDate = newFormat.format(newArrDate);
            } catch (Exception e) {

            }

            GoibiboEditor.putString(getResources().getString(R.string.bus_travel), getResources().getString(R.string.up_down));
            GoibiboEditor.commit();
            search_url = getResources().getString(R.string.bus_search_url) + "?source=" + Uri.encode(txtFrom.getText().toString().trim(), "utf-8") + "&destination=" + Uri.encode(txtTo.getText().toString().trim(), "utf-8") + "&dateofdeparture=" + depDate + "&dateofarrival=" + arrDate;
            //// System.out.println(search_url);
            Intent i = new Intent(getActivity(), GoibiboBusSearchRoundTripActivity.class);
            i.putExtra("place",txtFrom.getText().toString()+" - "+txtTo.getText().toString());
            i.putExtra("day", txtDate.getText().toString().split("\n")[0] + " - " + txtReturnDate.getText().toString().split("\n")[0]);
            i.putExtra("url", search_url);
            i.putExtra("url", search_url);
            i.putExtra("ac", f1);
            i.putExtra("nonac", f2);
            i.putExtra("sleeper", f3);
            i.putExtra("seater", f4);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

    }


    public void openOnwardCalendar() {

        showCalendarInDialog("onward", R.layout.control_calendar);
        Calendar next = Calendar.getInstance();
        next.add(Calendar.MONTH, 3);
        dialogView.init(new Date(), next.getTime()) //
                .withSelectedDate(new Date());
    }

    public void openReturnCalendar() {

        showCalendarInDialog("return", R.layout.control_calendar);
        Calendar next = Calendar.getInstance();
        next.add(Calendar.MONTH, 3);
        dialogView.init(new Date(), next.getTime()) //
                .withSelectedDate(new Date());
    }

    private void showCalendarInDialog(final String title, int layoutResId) {
        dialogView = (CalendarPickerView) getActivity().getLayoutInflater().inflate(layoutResId, null, false);
        theDialog = new AlertDialog.Builder(getActivity()) //
                .setTitle(title)
                .setView(dialogView)
                .setNeutralButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        String selectedDate = df.format(dialogView.getSelectedDate());
                        if (title.equalsIgnoreCase("onward")) {
                            txtDate.setText(selectedDate);

                            try {
                                if (df.parse(txtDate.getText().toString()).after(df.parse(txtReturnDate.getText().toString()))) {
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(dialogView.getSelectedDate());
                                    c.add(Calendar.DATE, 1);
                                    Date nextDate = c.getTime();
                                    String strNextDate = df.format(nextDate);
                                    txtReturnDate.setText(strNextDate);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                if (df.parse(txtDate.getText().toString()).after(df.parse(selectedDate)))
                                    Toast.makeText(getActivity(), "Return date should be after departure date", Toast.LENGTH_LONG).show();
                                else
                                    txtReturnDate.setText(selectedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .create();
        theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                dialogView.fixDialogDimens();
            }
        });
        theDialog.show();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_one_way:
                linReturn.setVisibility(View.GONE);
                break;
            case R.id.radio_round_trip:
                linReturn.setVisibility(View.VISIBLE);
                break;

        }
    }


    public void selectCity(View v) {
        Intent intent = new Intent(getActivity(), GoibiboBusCityListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch (v.getId()) {
            case R.id.txtFrom:
                startActivityForResult(intent, 1);
                break;
            case R.id.txtTo:
                startActivityForResult(intent, 2);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //// System.out.println("requestCode" + requestCode);
        if (requestCode == 1) {
            try {
                String city = data.getStringExtra("result");

                txtFrom.setText(city);

            } catch (Exception e) {

            }
        } else if (requestCode == 2) {
            try {
                String city = data.getStringExtra("result");
                txtTo.setText(city);


            } catch (Exception e) {

            }
        }


    }

}
