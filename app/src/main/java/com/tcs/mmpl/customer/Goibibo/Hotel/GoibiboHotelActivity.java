package com.tcs.mmpl.customer.Goibibo.Hotel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GoibiboHotelActivity extends Fragment {

    private static final int MODE_PRIVATE = 0;
    private CalendarPickerView dialogView;
    private AlertDialog theDialog;

    private SimpleDateFormat df;
    private TextView txtDate,txtReturnDate,txtFrom,txtRooms;
    private LinearLayout linReturn, linOnward,linRooms;
    private Button btnHotelSearch;

    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;


    private int room_count=1;

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            //// System.out.println("MenuVisibility");

        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //// System.out.println("OnCreate Hotel");
    }

    // create boolean for fetching data
    private boolean isViewShown = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        //// System.out.println("setUserVisible");

        if (getView() != null) {
            isViewShown = true;
            //// System.out.println("setUserVisible inside if");

           clearRooms();

        } else {
            isViewShown = false;
            //// System.out.println("setUserVisible inside else");
        }
    }
    @Override
     public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        //// System.out.println("OnHiddenChanged");

        isViewShown = false;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("Goibibo", "Hotel");

        //// System.out.println("OnCreateView");

        View rootView = inflater.inflate(R.layout.activity_goibibo_hotel, container, false);
        setRetainInstance(true);

        GoibiboPref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

       clearRooms();

        btnHotelSearch = (Button)rootView.findViewById(R.id.btnHotelSearch);
        txtDate = (TextView) rootView.findViewById(R.id.txtDate);
        txtReturnDate = (TextView) rootView.findViewById(R.id.txtReturnDate);
        txtFrom = (TextView)rootView.findViewById(R.id.txtFrom);
        txtRooms = (TextView)rootView.findViewById(R.id.txtRooms);

        linRooms = (LinearLayout)rootView.findViewById(R.id.linRooms);
        linReturn = (LinearLayout) rootView.findViewById(R.id.linReturn);
        linOnward = (LinearLayout) rootView.findViewById(R.id.linOnward);

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

        return  rootView;
    }


    private void onClick() {
        // TODO Auto-generated method stub

        txtFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCity(v);
            }
        });


        linRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             chooseRooms();
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

        btnHotelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertBuilder alertBuilder = new AlertBuilder(getActivity());
                if(txtFrom.getText().toString().equalsIgnoreCase(""))
                {
                    alertBuilder.showAlert(getActivity().getString(R.string.invalid_from));

                }
                else {

                    GoibiboEditor.putString("hotel_from", txtDate.getText().toString());
                    GoibiboEditor.putString("hotel_to", txtReturnDate.getText().toString());
                    GoibiboEditor.putString("hotel_rooms", txtRooms.getText().toString());
                    GoibiboEditor.putString("hotel_city", txtFrom.getText().toString());
                    GoibiboEditor.commit();

                    String fromDate = "", toDate = "";

                    try {
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyyMMdd");
                        Date newDate = df.parse(txtDate.getText().toString().trim());
                        fromDate = newFormat.format(newDate);

                        Date newArrDate = df.parse(txtReturnDate.getText().toString().trim());
                        toDate = newFormat.format(newArrDate);
                    } catch (Exception e) {

                    }
                    String city_code = GoibiboPref.getString("hotel_city_code","");

                    String query = "hotels-"+city_code+"-" + fromDate + "-" + toDate + "-1-1_0";

                    Intent intent = new Intent(getActivity(), GoibiboHotelSearchActivity.class);
                    intent.putExtra("query", query);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }       }
        });


    }

    public void selectCity(View v) {
        Intent intent = new Intent(getActivity(), GoibiboHotelCityListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch (v.getId()) {
            case R.id.txtFrom:
                startActivityForResult(intent, 1);
                break;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //// System.out.println("requestCode" + requestCode);
        if (requestCode == 1) {
            try {
                String code = data.getStringExtra("result");
                GoibiboEditor.putString("hotel_city_code",code);
                GoibiboEditor.commit();
                String city = data.getStringExtra("city");
                txtFrom.setText(city);

            } catch (Exception e) {

            }
        }


    }


    public void chooseRooms()
    {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_room);
        dialog.setTitle("Select Rooms");



        final LinearLayout linRoom1 = (LinearLayout)dialog.findViewById(R.id.linRoom1);
        final LinearLayout linRoom2 = (LinearLayout)dialog.findViewById(R.id.linRoom2);
        final LinearLayout linRoom3 = (LinearLayout)dialog.findViewById(R.id.linRoom3);
        final LinearLayout linRoom4 = (LinearLayout)dialog.findViewById(R.id.linRoom4);


        final TextView txtAddRoom = (TextView)dialog.findViewById(R.id.txtAddRoom);
        final TextView txtRemoveRoom = (TextView)dialog.findViewById(R.id.txtRemoveRoom);
        final TextView txtDone = (TextView)dialog.findViewById(R.id.txtDone);

        // Room 1 Starts
        final Button btnAdultMinus1 = (Button)dialog.findViewById(R.id.btnAdultMinus1);
        final Button btnAdultPlus1 = (Button)dialog.findViewById(R.id.btnAdultPlus1);
        final TextView txtAdultValue1 = (TextView)dialog.findViewById(R.id.txtAdultValue1);

        final Button btnChildPlus1 = (Button)dialog.findViewById(R.id.btnChildPlus1);
        final Button btnChildMinus1 = (Button)dialog.findViewById(R.id.btnChildMinus1);
        final TextView txtChildValue1 = (TextView)dialog.findViewById(R.id.txtChildValue1);


        btnAdultMinus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(GoibiboPref.getInt("adult1",1) > 1)
                {
                    GoibiboEditor.putInt("adult1",GoibiboPref.getInt("adult1",1)-1);
                    GoibiboEditor.commit();

                    txtAdultValue1.setText(String.valueOf(GoibiboPref.getInt("adult1", 1)));
                }

            }
        });

        btnAdultPlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(GoibiboPref.getInt("adult1",1) < 4)
                {
                    GoibiboEditor.putInt("adult1",GoibiboPref.getInt("adult1",1)+1);
                    GoibiboEditor.commit();

                    txtAdultValue1.setText(String.valueOf(GoibiboPref.getInt("adult1",1)));
                }
            }
        });


        //Room 1 Ends
        //Room 2 Starts

        //Room 2 Ends


        //Room 3 Starts


        //Room 3 Ends
        room_count = GoibiboPref.getInt("room_count",1);
        if(room_count > 1)
            txtRemoveRoom.setVisibility(View.VISIBLE);

        if(room_count == 4)
            txtAddRoom.setVisibility(View.INVISIBLE);

        switch (room_count)
        {
            case 1:
                linRoom1.setVisibility(View.VISIBLE);
                linRoom2.setVisibility(View.GONE);
                linRoom3.setVisibility(View.GONE);
                linRoom4.setVisibility(View.GONE);
                break;
            case 2:
                linRoom1.setVisibility(View.VISIBLE);
                linRoom2.setVisibility(View.VISIBLE);
                linRoom3.setVisibility(View.GONE);
                linRoom4.setVisibility(View.GONE);

                break;
            case 3:
                linRoom1.setVisibility(View.VISIBLE);
                linRoom2.setVisibility(View.VISIBLE);
                linRoom3.setVisibility(View.VISIBLE);
                linRoom4.setVisibility(View.GONE);

                break;
            case 4:

                linRoom1.setVisibility(View.VISIBLE);
                linRoom2.setVisibility(View.VISIBLE);
                linRoom3.setVisibility(View.VISIBLE);
                linRoom4.setVisibility(View.VISIBLE);

                break;
        }




        txtAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    room_count = room_count + 1;


                if(room_count == 1)
                    txtRemoveRoom.setVisibility(View.GONE);
                else if(room_count == 4) {
                    txtAddRoom.setVisibility(View.INVISIBLE);
                    txtRemoveRoom.setVisibility(View.VISIBLE);
                }
                else
                txtRemoveRoom.setVisibility(View.VISIBLE);
                switch (room_count)
                {
                    case 1:
                        linRoom1.setVisibility(View.VISIBLE);
                        linRoom2.setVisibility(View.GONE);
                        linRoom3.setVisibility(View.GONE);
                        linRoom4.setVisibility(View.GONE);
                        break;
                    case 2:
                        linRoom1.setVisibility(View.VISIBLE);
                        linRoom2.setVisibility(View.VISIBLE);
                        linRoom3.setVisibility(View.GONE);
                        linRoom4.setVisibility(View.GONE);

                        break;
                    case 3:
                        linRoom1.setVisibility(View.VISIBLE);
                        linRoom2.setVisibility(View.VISIBLE);
                        linRoom3.setVisibility(View.VISIBLE);
                        linRoom4.setVisibility(View.GONE);

                        break;
                    case 4:

                        linRoom1.setVisibility(View.VISIBLE);
                        linRoom2.setVisibility(View.VISIBLE);
                        linRoom3.setVisibility(View.VISIBLE);
                        linRoom4.setVisibility(View.VISIBLE);

                        break;
                }

            }
        });


        txtRemoveRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                room_count = room_count - 1;

                if(room_count == 1)
                    txtRemoveRoom.setVisibility(View.INVISIBLE);
                else if(room_count > 1)
                    txtAddRoom.setVisibility(View.VISIBLE);

                switch (room_count)
                {
                    case 1:
                        linRoom1.setVisibility(View.VISIBLE);
                        linRoom2.setVisibility(View.GONE);
                        linRoom3.setVisibility(View.GONE);
                        linRoom4.setVisibility(View.GONE);
                        break;
                    case 2:
                        linRoom1.setVisibility(View.VISIBLE);
                        linRoom2.setVisibility(View.VISIBLE);
                        linRoom3.setVisibility(View.GONE);
                        linRoom4.setVisibility(View.GONE);

                        break;
                    case 3:
                        linRoom1.setVisibility(View.VISIBLE);
                        linRoom2.setVisibility(View.VISIBLE);
                        linRoom3.setVisibility(View.VISIBLE);
                        linRoom4.setVisibility(View.GONE);

                        break;
                    case 4:

                        linRoom1.setVisibility(View.VISIBLE);
                        linRoom2.setVisibility(View.VISIBLE);
                        linRoom3.setVisibility(View.VISIBLE);
                        linRoom4.setVisibility(View.VISIBLE);

                        break;
                }

            }
        });


        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoibiboEditor.putInt("room_count",room_count);
                GoibiboEditor.commit();




                dialog.cancel();

            }
        });


        dialog.show();
    }

    private void clearRooms()
    {
        GoibiboEditor.putInt("room_count",1);
        GoibiboEditor.commit();
    }
}
