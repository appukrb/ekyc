package com.tcs.mmpl.customer.Goibibo.Hotel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tcs.mmpl.customer.Goibibo.GoibiboServiceHandler;
import com.tcs.mmpl.customer.Goibibo.Hotel.Parser.JsonHotelParser;
import com.tcs.mmpl.customer.Goibibo.Hotel.Pojo.HotelSearchDetails;
import com.tcs.mmpl.customer.R;
import com.tcs.mmpl.customer.utility.AlertBuilder;

import java.util.ArrayList;

public class GoibiboHotelSearchActivity extends Activity {


    private ListView listHotel;
    private TextView txtPlace,txtDay;


    private SharedPreferences GoibiboPref;
    private SharedPreferences.Editor GoibiboEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_hotel_search);

        GoibiboPref = getSharedPreferences(getResources().getString(R.string.pref_goibibo), MODE_PRIVATE);
        GoibiboEditor = GoibiboPref.edit();

        listHotel = (ListView)findViewById(R.id.listHotel);
        txtPlace = (TextView)findViewById(R.id.txtPlace);
        txtDay = (TextView)findViewById(R.id.txtDay);

        txtPlace.setText(GoibiboPref.getString("hotel_city",""));
        txtDay.setText(GoibiboPref.getString("hotel_from","").split("\n")[0]+"-"+GoibiboPref.getString("hotel_to","").split("\n")[0]+" "+GoibiboPref.getString("hotel_rooms",""));

        GoibiboEditor.putString("hotel_query",getIntent().getStringExtra("query"));
        GoibiboEditor.commit();

        HotelSearch hotelSearch = new HotelSearch(getApplicationContext());
        hotelSearch.execute(getResources().getString(R.string.hotel_city_hotels_url)+"?query="+getIntent().getStringExtra("query")+"&sort_type=popularity");
    }


    private class HotelSearch extends AsyncTask<String, Void, String> {

        Context context;

        private ProgressDialog pDialog;

        public HotelSearch(Context context) {
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GoibiboHotelSearchActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                GoibiboServiceHandler goibiboServiceHandler = new GoibiboServiceHandler(GoibiboHotelSearchActivity.this);
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

            JsonHotelParser jsonHotelParser = new JsonHotelParser(GoibiboHotelSearchActivity.this, result);
            final ArrayList<HotelSearchDetails> hotelSearchDetailsArrayList = jsonHotelParser.convertHotelSearch();

            if(hotelSearchDetailsArrayList.isEmpty()) {

                AlertBuilder alertBuilder = new AlertBuilder(GoibiboHotelSearchActivity.this);
                AlertDialog.Builder  alertDialog = alertBuilder.showRetryAlert("No Hotels Found");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        dialog.cancel();
                        finish();
                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
            else
            {

                listHotel.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                HotelListAdapter adapter = new HotelListAdapter(GoibiboHotelSearchActivity.this, R.layout.hotel_search_row_layout, listHotel, hotelSearchDetailsArrayList);
                listHotel.setAdapter(adapter);



            }


        }

    }
    public class HotelListAdapter extends ArrayAdapter<String> {

        private static final int MODE_PRIVATE = 0;
        Context context;
        ListView listView;
        int layout_id;

        ArrayList<HotelSearchDetails> hotelSearchDetailsArrayList;

        public HotelListAdapter(Context context, int resource,
                              ListView lstView, ArrayList<HotelSearchDetails> hotelSearchDetailsArrayList) {
            super(context, resource);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.layout_id = resource;
            this.listView = lstView;
            this.hotelSearchDetailsArrayList = hotelSearchDetailsArrayList;


        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return hotelSearchDetailsArrayList.size();
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            try {


                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View rowView = inflater.inflate(layout_id, null, true);


                ImageView imgHotel = (ImageView) rowView.findViewById(R.id.imgHotel);
                TextView txtHotelName = (TextView) rowView.findViewById(R.id.txtHotelName);
                TextView txtHotelPlace = (TextView) rowView.findViewById(R.id.txtHotelPlace);
                TextView txtRating = (TextView) rowView.findViewById(R.id.txtRating);
                TextView txtAmount = (TextView) rowView.findViewById(R.id.txtAmount);


                txtHotelName.setText(hotelSearchDetailsArrayList.get(position).getHn());
                txtHotelPlace.setText(hotelSearchDetailsArrayList.get(position).getC());
                txtRating.setText(hotelSearchDetailsArrayList.get(position).getGr() + "/5");
                txtAmount.setText(getResources().getString(R.string.rupee_symbol) + hotelSearchDetailsArrayList.get(position).getPrc());

                Picasso.with(GoibiboHotelSearchActivity.this).load(hotelSearchDetailsArrayList.get(position).getTmob()).placeholder(R.drawable.backgroud_default_image).into(imgHotel);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        GoibiboEditor.putString("hotel_code",hotelSearchDetailsArrayList.get(position).getHc());
                        GoibiboEditor.commit();

                        Intent intent = new Intent(getApplicationContext(), GoibiboHotelViewActivity.class);
                        intent.putExtra("hotel_image",hotelSearchDetailsArrayList.get(position).getTbig());
                        intent.putExtra("hotel_amount",hotelSearchDetailsArrayList.get(position).getPrc());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);



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
