package com.tcs.mmpl.customer.Goibibo.Bus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusBoardingPoint;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.FontClass;

import java.util.ArrayList;

public class GoibiboBusBoardingPointActivity extends Activity {
    private GoibiboBusDatabaseHelper goibiboBusDatabaseHelper;
    private ArrayList<BusBoardingPoint> busBoardingPointArrayList;
    private ListView listBoardingPoint;
    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;
    private String res = "NA";
    private RelativeLayout mainlinear;
    private FontClass fontclass = new FontClass();
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_bus_boarding_point);

        mainlinear = (RelativeLayout) findViewById(R.id.mainlinear);
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "helvetica.otf");
        fontclass.setFont(mainlinear, typeface);

        goibiboBusDatabaseHelper = new GoibiboBusDatabaseHelper(getApplicationContext());
        GoibiboPref = getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

        listBoardingPoint = (ListView) findViewById(R.id.listBoardingPoint);

        busBoardingPointArrayList = goibiboBusDatabaseHelper.getBoardingPointAll();


        listBoardingPoint.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        BoardingPointAdapter adapter = new BoardingPointAdapter(GoibiboBusBoardingPointActivity.this,
                R.layout.boardingpoint_row_layout, listBoardingPoint, busBoardingPointArrayList);
        listBoardingPoint.setAdapter(adapter);

    }


    public class BoardingPointAdapter extends ArrayAdapter<String> {

        private static final int MODE_PRIVATE = 0;
        Context context;
        ListView listView;
        int layout_id;


        ArrayList<BusBoardingPoint> busBoardingPointArrayList;


        public BoardingPointAdapter(Context context, int resource,
                                    ListView lstView, ArrayList<BusBoardingPoint> busBoardingPointArrayList) {
            super(context, resource);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.layout_id = resource;
            this.listView = lstView;

            this.busBoardingPointArrayList = busBoardingPointArrayList;


        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return busBoardingPointArrayList.size();
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            try {


                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View rowView = inflater.inflate(layout_id, null, true);


                TextView txtBoardingPoint = (TextView) rowView.findViewById(R.id.txtBoardingPoint);
                TextView txtBoardingTime = (TextView) rowView.findViewById(R.id.txtBoardingTime);

                txtBoardingPoint.setText(busBoardingPointArrayList.get(position).getBPLocation());
                txtBoardingTime.setText(busBoardingPointArrayList.get(position).getBPTime().trim().split(" ")[1]);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        goibiboBusDatabaseHelper.updateBusInfo(busBoardingPointArrayList.get(position).getBPLocation(), busBoardingPointArrayList.get(position).getBPTime(), busBoardingPointArrayList.get(position).getBPId());
                        goibiboBusDatabaseHelper.updateSeatStatus();
                        res = busBoardingPointArrayList.get(position).getBPLocation();

                        if (GoibiboPref.getString(getResources().getString(R.string.bus_travel), getResources().getString(R.string.up)).equalsIgnoreCase(getResources().getString(R.string.up_down)) && GoibiboPref.getString(getString(R.string.way), "").equalsIgnoreCase(getResources().getString(R.string.onw))) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", res);
                            ((Activity) context).setResult(6, returnIntent);
                            ((Activity) context).finish();
                        } else {
                            ((Activity) context).finish();
                            Intent intent = new Intent(context, GoibiboBusBookingReviewActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ((Activity) context).startActivity(intent);
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
