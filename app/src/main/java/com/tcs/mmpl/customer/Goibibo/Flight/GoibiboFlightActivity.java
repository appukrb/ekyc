package com.tcs.mmpl.customer.Goibibo.Flight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;
import com.tcs.mmpl.customer.utility.FontClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GoibiboFlightActivity extends Fragment implements RadioGroup.OnCheckedChangeListener {


    private static final int MODE_PRIVATE = 0;
    LinearLayout mainlinear;
    FontClass fontclass = new FontClass();
    Typeface typeface;
    private CalendarPickerView dialogView;
    private AlertDialog.Builder builder;
    private AlertDialog theDialog;
    private TextView txtDate, txtReturnDate, txtClass, txtPassengers;
    private SimpleDateFormat df;
    private TextView txtFrom, txtTo;
    private RadioButton radio_one_way, radio_round_trip;
    private String search_url = "",seatingclass="E",adults="1",children="0",infants="0";

    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;

    private GoibiboFlightDatabaseHelper goibiboFlightDatabaseHelper;
    private LinearLayout linReturn, linOnward,linchoosePassengers,linchooseClass;
    private CheckBox chkFlights;

    private Button btnSearch;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i("Goibibo", "Flight");

        View rootView = inflater.inflate(R.layout.activity_goibibo_flight, container, false);
        setRetainInstance(true);
        mainlinear = (LinearLayout) rootView.findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        GoibiboPref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();


        goibiboFlightDatabaseHelper = new GoibiboFlightDatabaseHelper(getActivity());
        chkFlights = (CheckBox)rootView.findViewById(R.id.chkFlights);
        mainlinear = (LinearLayout) rootView.findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        GoibiboPref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

        ((RadioGroup) rootView.findViewById(R.id.radio_group)).setOnCheckedChangeListener(this);
        radio_one_way = (RadioButton) rootView.findViewById(R.id.radio_one_way);
        radio_round_trip = (RadioButton) rootView.findViewById(R.id.radio_round_trip);
        txtDate = (TextView) rootView.findViewById(R.id.txtDate);
        txtReturnDate = (TextView) rootView.findViewById(R.id.txtReturnDate);
        txtClass = (TextView) rootView.findViewById(R.id.txtClass);
        txtPassengers = (TextView) rootView.findViewById(R.id.txtPassengers);
        txtFrom = (TextView) rootView.findViewById(R.id.txtFrom);
        txtTo = (TextView) rootView.findViewById(R.id.txtTo);

        linReturn = (LinearLayout) rootView.findViewById(R.id.linReturn);
        linOnward = (LinearLayout) rootView.findViewById(R.id.linOnward);
        linchoosePassengers = (LinearLayout)rootView.findViewById(R.id.linchoosePassengers);
        linchooseClass = (LinearLayout)rootView.findViewById(R.id.linchooseClass);

        btnSearch = (Button)rootView.findViewById(R.id.btnSearch);

        df = new SimpleDateFormat("d MMM''yy\nEEEE");
        Date currentDate = new Date();
        String strCurrentDate = df.format(currentDate);
        txtDate.setText(strCurrentDate);
        txtReturnDate.setText(strCurrentDate);

        onClick();

        return rootView;
    }

    private void onClick() {

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

        linchoosePassengers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePassengers();
            }
        });

        linchooseClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseClass();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFlight();
            }
        });

    }

    public void choosePassengers() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.number_picker_row_layout);
        dialog.setTitle("Add Passengers");

        // set the custom dialog components - text and button
        Button dialogBtnAdultMinus = (Button) dialog.findViewById(R.id.btnAdultMinus);
        Button dialogBtnAdultPlus = (Button) dialog.findViewById(R.id.btnAdultPlus);
        Button dialogBtnChildMinus = (Button) dialog.findViewById(R.id.btnChildMinus);
        Button dialogBtnChildPlus = (Button) dialog.findViewById(R.id.btnChildPlus);
        Button dialogBtnInfantMinus = (Button) dialog.findViewById(R.id.btnInfantMinus);
        Button dialogBtnInfantPlus = (Button) dialog.findViewById(R.id.btnInfantPlus);
        Button dialogBtnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button dialogBtnOk = (Button) dialog.findViewById(R.id.btnOk);


        final TextView txtAdult = (TextView) dialog.findViewById(R.id.txtAdult);
        final TextView txtChild = (TextView) dialog.findViewById(R.id.txtChild);
        final TextView txtInfants = (TextView) dialog.findViewById(R.id.txtInfant);

        final TextView txtAdultValue = (TextView) dialog.findViewById(R.id.txtAdultValue);
        final TextView txtChildValue = (TextView) dialog.findViewById(R.id.txtChildValue);
        final TextView txtInfantValue = (TextView) dialog.findViewById(R.id.txtInfantValue);

        String[] passengerArray = txtPassengers.getText().toString().trim().split(",");
        txtAdult.setText(passengerArray[0].trim());
        txtChild.setText(passengerArray[1].trim());
        txtInfants.setText(passengerArray[2].trim());

        txtAdultValue.setText(passengerArray[0].trim().split(" ")[0]);
        txtChildValue.setText(passengerArray[1].trim().split(" ")[0]);
        txtInfantValue.setText(passengerArray[2].trim().split(" ")[0]);

        // if button is clicked, close the custom dialog
        dialogBtnAdultMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int adultValue = Integer.parseInt(txtAdultValue.getText().toString().trim());
                int infantValue = Integer.parseInt(txtInfantValue.getText().toString().trim());
                if (adultValue != 1) {
                    if (adultValue == 2)
                        txtAdult.setText(String.valueOf(Integer.parseInt(txtAdultValue.getText().toString().trim()) - 1) + " Adult");
                    else
                        txtAdult.setText(String.valueOf(Integer.parseInt(txtAdultValue.getText().toString().trim()) - 1) + " Adults");
                    txtAdultValue.setText(String.valueOf(Integer.parseInt(txtAdultValue.getText().toString().trim()) - 1));


                    if (adultValue <= infantValue) {
                        if (adultValue == 2)
                            txtInfants.setText(String.valueOf(Integer.parseInt(txtAdultValue.getText().toString().trim())) + " Infant");
                        else
                            txtInfants.setText(String.valueOf(Integer.parseInt(txtAdultValue.getText().toString().trim())) + " Infants");
                        txtInfantValue.setText(String.valueOf(Integer.parseInt(txtAdultValue.getText().toString().trim())));
                    }
                }

            }
        });
        dialogBtnAdultPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adultValue = Integer.parseInt(txtAdultValue.getText().toString().trim());

                if (adultValue < 9) {
                    txtAdult.setText(String.valueOf(Integer.parseInt(txtAdultValue.getText().toString().trim()) + 1) + " Adults");
                    txtAdultValue.setText(String.valueOf(Integer.parseInt(txtAdultValue.getText().toString().trim()) + 1));
                }

            }
        });
        dialogBtnChildMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int childValue = Integer.parseInt(txtChildValue.getText().toString().trim());
                if (childValue > 0) {
                    if (childValue == 1)
                        txtChild.setText(String.valueOf(Integer.parseInt(txtChildValue.getText().toString().trim()) - 1) + " Children");
                    else if (childValue == 2)
                        txtChild.setText(String.valueOf(Integer.parseInt(txtChildValue.getText().toString().trim()) - 1) + " Child");
                    else
                        txtChild.setText(String.valueOf(Integer.parseInt(txtChildValue.getText().toString().trim()) - 1) + " Children");
                    txtChildValue.setText(String.valueOf(Integer.parseInt(txtChildValue.getText().toString().trim()) - 1));
                }

            }
        });
        dialogBtnChildPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int childValue = Integer.parseInt(txtChildValue.getText().toString().trim());
                if (childValue < 9) {
                    if (childValue == 0)
                        txtChild.setText(String.valueOf(Integer.parseInt(txtChildValue.getText().toString().trim()) + 1) + " Child");
                    else
                        txtChild.setText(String.valueOf(Integer.parseInt(txtChildValue.getText().toString().trim()) + 1) + " Children");
                    txtChildValue.setText(String.valueOf(Integer.parseInt(txtChildValue.getText().toString().trim()) + 1));
                }
            }
        });
        dialogBtnInfantMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int infantValue = Integer.parseInt(txtInfantValue.getText().toString().trim());
                if (infantValue > 0) {
                    if (infantValue == 1)
                        txtInfants.setText(String.valueOf(Integer.parseInt(txtInfantValue.getText().toString().trim()) - 1) + " Infants");
                    else if (infantValue == 2)
                        txtInfants.setText(String.valueOf(Integer.parseInt(txtInfantValue.getText().toString().trim()) - 1) + " Infant");
                    else
                        txtInfants.setText(String.valueOf(Integer.parseInt(txtInfantValue.getText().toString().trim()) - 1) + " Infants");
                    txtInfantValue.setText(String.valueOf(Integer.parseInt(txtInfantValue.getText().toString().trim()) - 1));
                }

            }
        });
        dialogBtnInfantPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int infantValue = Integer.parseInt(txtInfantValue.getText().toString().trim());
                int adultValue = Integer.parseInt(txtAdultValue.getText().toString().trim());
                if (adultValue > infantValue) {

                    if (infantValue == 0)
                        txtInfants.setText(String.valueOf(Integer.parseInt(txtInfantValue.getText().toString().trim()) + 1) + " Infant");
                    else
                        txtInfants.setText(String.valueOf(Integer.parseInt(txtInfantValue.getText().toString().trim()) + 1) + " Infants");

                    txtInfantValue.setText(String.valueOf(Integer.parseInt(txtInfantValue.getText().toString().trim()) + 1));
                } else {
                    Toast.makeText(getActivity(), R.string.invalid_infant_count, Toast.LENGTH_LONG).show();
                }

            }
        });
        dialogBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                txtPassengers.setText(txtAdult.getText().toString().trim() + ", " + txtChild.getText().toString().trim() + ", " + txtInfants.getText().toString().trim());

                adults=txtAdult.getText().toString().trim().split(" ")[0];
                children=txtChild.getText().toString().trim().split(" ")[0];
                infants=txtInfants.getText().toString().trim().split(" ")[0];
            }
        });


        dialog.show();

    }

    public void chooseClass() {
        String selectedClass = txtClass.getText().toString().trim();
        int selectedItem = 0;
        if (selectedClass.equalsIgnoreCase("Economy")) {
            selectedItem = 0;
            seatingclass = "E";
        }
        else if (selectedClass.equalsIgnoreCase("Business")) {
            selectedItem = 1;
            seatingclass = "B";
        }
//        else if (selectedClass.equalsIgnoreCase("First Class"))
//            selectedItem = 2;
//        else
//            selectedItem = 3;

//        final CharSequence[] items = {
//                "Economy", "Business", "First Class", "Premium Economy"
//        };

        final CharSequence[] items = {
                "Economy", "Business"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select a Class");
        builder.setSingleChoiceItems(items, selectedItem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection

                switch (item) {
                    case 0:
                        txtClass.setText(items[0]);
                        break;

                    case 1:
                        txtClass.setText(items[1]);
                        break;

                    case 2:
                        txtClass.setText(items[2]);
                        break;

                    case 3:
                        txtClass.setText(items[3]);
                        break;
                }
                dialog.cancel();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
                                    txtReturnDate.setText(selectedDate);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else
                            txtReturnDate.setText(selectedDate);
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

    public void searchFlight()
    {
        GoibiboEditor.putString("adult",adults);
        GoibiboEditor.putString("child",children);
        GoibiboEditor.putString("infant",infants);
        GoibiboEditor.putString("passengers", txtPassengers.getText().toString().trim());
        GoibiboEditor.commit();

        goibiboFlightDatabaseHelper.deleteFlightInfo();
        goibiboFlightDatabaseHelper.deleteFlightTraveller();

        int passengerCount = Integer.parseInt(adults) + Integer.parseInt(children) + Integer.parseInt(infants);
        AlertBuilder alertBuilder = new AlertBuilder(getActivity());

        int f = 0;
        if(chkFlights.isChecked())
            f=1;
        else
        f=0;

        if(passengerCount > 9)
        {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_flight_pass_count));
        }
        else if(txtFrom.getText().toString().equalsIgnoreCase(""))
        {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_from));
        }
        else if(txtTo.getText().toString().trim().equalsIgnoreCase(""))
        {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_to));
        }
        else if(txtFrom.getText().toString().trim().equalsIgnoreCase(txtTo.getText().toString().trim()))
        {
            alertBuilder.showAlert(getResources().getString(R.string.invalid_source_destination));
        }
        else if(radio_one_way.isChecked()) {
            String depDate = "";

            try {
                SimpleDateFormat newFormat = new SimpleDateFormat("yyyyMMdd");
                Date newDate = df.parse(txtDate.getText().toString().trim());
                depDate = newFormat.format(newDate);
            }
            catch (Exception e)
            {

            }
            GoibiboEditor.putString(getResources().getString(R.string.flight_travel), getResources().getString(R.string.up));
            GoibiboEditor.commit();
            search_url = getResources().getString(R.string.flight_search_url)+"?source="+txtFrom.getText().toString().trim()+"&destination="+txtTo.getText().toString().trim()+"&dateofdeparture="+depDate+"&seatingclass="+seatingclass+"&adults="+adults+"&children="+children+"&infants="+infants+"&counter=100"+"&flightstop="+f;

            //// System.out.println(search_url);
            Intent i = new Intent(getActivity(), GoibiboFlightSearchOneWayActivity.class);
            i.putExtra("url", search_url);
            i.putExtra("place",txtFrom.getText().toString()+" - "+txtTo.getText().toString());
            i.putExtra("day",txtDate.getText().toString().split("\n")[0]);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else if(radio_round_trip.isChecked()) {
            String depDate = "",arrDate = "";

            try {
                SimpleDateFormat newFormat = new SimpleDateFormat("yyyyMMdd");
                Date newDate = df.parse(txtDate.getText().toString().trim());
                depDate = newFormat.format(newDate);

                Date newArrDate = df.parse(txtReturnDate.getText().toString().trim());
                arrDate = newFormat.format(newArrDate);
            }
            catch (Exception e)
            {

            }

            GoibiboEditor.putString(getResources().getString(R.string.flight_travel), getResources().getString(R.string.up_down));
            GoibiboEditor.commit();
            search_url = getResources().getString(R.string.flight_search_url)+"?source="+txtFrom.getText().toString().trim()+"&destination="+txtTo.getText().toString().trim()+"&dateofdeparture="+depDate+"&dateofarrival=" +arrDate+"&seatingclass="+seatingclass+"&adults="+adults+"&children="+children+"&infants="+infants+"&counter=100"+"&flightstop="+f;

            //// System.out.println(search_url);
            Intent i = new Intent(getActivity(), GoibiboFlightSearchRoundTripActivity.class);
            i.putExtra("place",txtFrom.getText().toString()+" - "+txtTo.getText().toString());
            i.putExtra("day", txtDate.getText().toString().split("\n")[0] + " - " + txtReturnDate.getText().toString().split("\n")[0]);

            i.putExtra("url", search_url);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

    }

    public void selectCity(View v) {
        Intent intent = new Intent(getActivity(), GoibiboFlightCityListActivity.class);
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
                String code = data.getStringExtra("result");

                GoibiboEditor.putString("flight_from",data.getStringExtra("city"));
                GoibiboEditor.commit();
                txtFrom.setText(code);

            } catch (Exception e) {

            }
        } else if (requestCode == 2) {
            try {
                String code = data.getStringExtra("result");


                GoibiboEditor.putString("flight_to",data.getStringExtra("city"));
                GoibiboEditor.commit();
                txtTo.setText(code);



            } catch (Exception e) {

            }
        }


    }

}
