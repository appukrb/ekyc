package com.tcs.mmpl.customer.Goibibo.Hotel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.tcs.mmpl.customer.Goibibo.Hotel.Pojo.RoomDetails;
import com.tcs.mmpl.customer.R;

import java.util.ArrayList;

public class GoibiboHotelSelectRoom extends Activity {

    ArrayList<RoomDetails> roomDetailsArrayList;
    private ListView listSelectRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_goibibo_hotel_select_room);

        roomDetailsArrayList = (ArrayList<RoomDetails>) getIntent().getSerializableExtra("room_data");
        listSelectRoom = (ListView) findViewById(R.id.listSelectRoom);


        listSelectRoom.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        SelectRoomListAdapter adapter = new SelectRoomListAdapter(GoibiboHotelSelectRoom.this, R.layout.hotel_select_room_layout, listSelectRoom, roomDetailsArrayList);
        listSelectRoom.setAdapter(adapter);
    }

    public class SelectRoomListAdapter extends ArrayAdapter<String> {

        private static final int MODE_PRIVATE = 0;
        Context context;
        ListView listView;
        int layout_id;

        ArrayList<RoomDetails> roomDetailsArrayList;

        public SelectRoomListAdapter(Context context, int resource,
                                     ListView lstView, ArrayList<RoomDetails> roomDetailsArrayList) {
            super(context, resource);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.layout_id = resource;
            this.listView = lstView;
            this.roomDetailsArrayList = roomDetailsArrayList;

        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return roomDetailsArrayList.size();
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            try {


                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View rowView = inflater.inflate(layout_id, null, true);


                ImageView imgHotel = (ImageView) rowView.findViewById(R.id.imgHotel);
                TextView txtTypeName = (TextView) rowView.findViewById(R.id.txtTypeName);
                TextView txtAmount = (TextView) rowView.findViewById(R.id.txtAmount);
                TextView txtCancellation = (TextView) rowView.findViewById(R.id.txtCancellation);
                TextView txtRoomDetails = (TextView) rowView.findViewById(R.id.txtRoomDetails);
                TextView txtCancelPolicy = (TextView) rowView.findViewById(R.id.txtCancelPolicy);
                TextView txtBookNow = (TextView) rowView.findViewById(R.id.txtBookNow);


                txtTypeName.setText(roomDetailsArrayList.get(position).getRoom_info().getType_name());
                txtAmount.setText(getResources().getString(R.string.rupee_symbol)+roomDetailsArrayList.get(position).getMp());
                txtTypeName.setText(roomDetailsArrayList.get(position).getRoom_info().getType_name());
                txtCancellation.setText(roomDetailsArrayList.get(position).getFctxt());

                Picasso.with(GoibiboHotelSelectRoom.this).load(roomDetailsArrayList.get(position).getRoom_info().getImage()).placeholder(R.drawable.backgroud_default_image).into(imgHotel);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        Intent intent = new Intent(getApplicationContext(), GoibiboHotelBookReviewActivity.class);
                        intent.putExtra("room_details", roomDetailsArrayList.get(position));

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                    }
                });

                txtBookNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), GoibiboHotelBookReviewActivity.class);
                        intent.putExtra("room_details", roomDetailsArrayList.get(position));

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                txtCancelPolicy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),GoibiboHotelCancelPolicyActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("room_details", roomDetailsArrayList.get(position));
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
