package com.tcs.mmpl.customer.Goibibo.Hotel.Parser;

import android.content.Context;
import android.util.Log;

import com.tcs.mmpl.customer.Goibibo.Hotel.Pojo.HotelSearchDetails;
import com.tcs.mmpl.customer.Goibibo.Hotel.Pojo.HotelSelectedDetails;
import com.tcs.mmpl.customer.Goibibo.Hotel.Pojo.RoomDetails;
import com.tcs.mmpl.customer.Goibibo.Hotel.Pojo.Room_info;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hp on 2016-09-26.
 */
public class JsonHotelParser {

    private Context context;
    private String json;


    public JsonHotelParser(Context context, String json) {

        this.context = context;
        this.json = json;


    }

    public ArrayList<HotelSearchDetails> convertHotelSearch() {

        ArrayList<HotelSearchDetails> hotelSearchDetailsArrayList = new ArrayList<HotelSearchDetails>();
        try {

            Log.i("Hotel json",json);
            JSONObject jsonObject = new JSONObject(json);
            String data = jsonObject.getString("data");

            Log.i("Hotel data",data);

            JSONObject jsonObject1 = new JSONObject(data);

            JSONArray jsonArray = jsonObject1.getJSONArray("city_hotel_info");
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject j1 = jsonArray.getJSONObject(j);

                HotelSearchDetails hotelSearchDetails = new HotelSearchDetails();
                hotelSearchDetails.setGr(j1.getString("gr"));
                hotelSearchDetails.setPrc(j1.getString("prc"));
                hotelSearchDetails.setRoom_count(j1.getString("room_count"));
                hotelSearchDetails.setLa(j1.getString("la"));
                hotelSearchDetails.setTmob(j1.getString("tmob"));
                hotelSearchDetails.setHn(j1.getString("hn"));
                hotelSearchDetails.setHc(j1.getString("hc"));
                hotelSearchDetails.setC(j1.getString("c"));
                hotelSearchDetails.setL(j1.getString("l"));
                hotelSearchDetails.setLo(j1.getString("lo"));
                hotelSearchDetails.setGr(j1.getString("gr"));
                hotelSearchDetails.setTbig(j1.getString("tbig"));

                hotelSearchDetailsArrayList.add(hotelSearchDetails);

            }


        } catch (Exception e) {
                e.printStackTrace();
        }

        return hotelSearchDetailsArrayList;
    }


    public ArrayList<HotelSelectedDetails> convertHotelSelected() {

        ArrayList<HotelSelectedDetails> hotelSelectedDetailsArrayList = new ArrayList<HotelSelectedDetails>();
        try {

            Log.i("Hotel json",json);
            JSONObject jsonObject = new JSONObject(json);
            String data = jsonObject.getString("data");

            Log.i("Hotel data",data);

            JSONObject j1 = new JSONObject(data);


            HotelSelectedDetails hotelSelectedDetails = new HotelSelectedDetails();

            hotelSelectedDetails.setGr(j1.getString("gr"));
            hotelSelectedDetails.setPrc(j1.getString("prc"));
            hotelSelectedDetails.setRoom_count(j1.getString("room_count"));
            hotelSelectedDetails.setLa(j1.getString("la"));
            hotelSelectedDetails.setPincode(j1.getString("pincode"));
            hotelSelectedDetails.setHn(j1.getString("hn"));
            hotelSelectedDetails.setHc(j1.getString("hc"));
            hotelSelectedDetails.setC(j1.getString("c"));
            hotelSelectedDetails.setL(j1.getString("l"));
            hotelSelectedDetails.setLo(j1.getString("lo"));
            hotelSelectedDetails.setGr(j1.getString("gr"));
            hotelSelectedDetails.setState(j1.getString("state"));
            hotelSelectedDetails.setFree_cancel(j1.getString("free_cancel"));
            hotelSelectedDetails.setCountry(j1.getString("country"));
            hotelSelectedDetails.setAddress(j1.getString("address"));

            JSONArray jsonroomdata = j1.getJSONArray("rooms_data");

            ArrayList<RoomDetails> roomDetailsArrayList = new ArrayList<>();
            for(int i=0;i<jsonroomdata.length();i++)
            {
                RoomDetails roomDetails = new RoomDetails();

                JSONObject j2 = jsonroomdata.getJSONObject(i);

                roomDetails.setOffercode(j2.getString("offercode"));
                roomDetails.setVendor_discount(j2.getString("vendor_discount"));
                roomDetails.setPaynowflag(j2.getString("paynowflag"));
                roomDetails.setRtc(j2.getString("rtc"));
                roomDetails.setRtn(j2.getString("rtn"));
                roomDetails.setRpc(j2.getString("rpc"));
                roomDetails.setTtc(j2.getString("ttc"));
                roomDetails.setTtc_u(j2.getString("ttc_u"));
                roomDetails.setOrrate(j2.getString("orrate"));
                roomDetails.setTp_alltax(j2.getString("tp_alltax"));
                roomDetails.setCur(j2.getString("cur"));
                roomDetails.setOriginal_rate(j2.getString("original_rate"));
                roomDetails.setOp_wt(j2.getString("op_wt"));
                roomDetails.setTotal_price(j2.getString("total_price"));
                roomDetails.setMp(j2.getString("mp"));
                roomDetails.setFctxt(j2.getString("fctxt"));
                roomDetails.setVendor_discount(j2.getString("vendor_discount_u"));
                roomDetails.setHo(j2.getString("ho"));
                roomDetails.setOp(j2.getString("op"));
                roomDetails.setFwdp(j2.getString("fwdp"));
                roomDetails.setIbp(j2.getString("ibp"));
                roomDetails.setTp(j2.getString("tp"));
                roomDetails.setTtc(j2.getString("ttc"));
                roomDetails.setTp_alltax(j2.getString("tp_alltax"));

                JSONObject jsonroominfo = new JSONObject(j2.getString("room_info"));
                Room_info room_info = new Room_info();
                room_info.setType_name(jsonroominfo.getString("type_name"));
                room_info.setVendor(jsonroominfo.getString("vendor"));
                room_info.setDescription(jsonroominfo.getString("description"));
                room_info.setMax_occ(jsonroominfo.getString("max_occ"));
                room_info.setImage(jsonroominfo.getString("image"));
                room_info.setBase_occ(jsonroominfo.getString("base_occ"));
                room_info.setRoom_count(jsonroominfo.getString("room_count"));
                room_info.setSt(jsonroominfo.getString("st"));
                room_info.setExtra_bedtype(jsonroominfo.getString("extra_bedtype"));
                room_info.setType_code(jsonroominfo.getString("type_code"));
                room_info.setSlot_booking(jsonroominfo.getString("slot_booking"));
                room_info.setRoomsize(jsonroominfo.getString("roomsize"));


                roomDetails.setRoom_info(room_info);

                roomDetailsArrayList.add(roomDetails);
            }

            hotelSelectedDetails.setRoomDetails(roomDetailsArrayList);



            hotelSelectedDetailsArrayList.add(hotelSelectedDetails);




        } catch (Exception e) {
            e.printStackTrace();
        }

        return hotelSelectedDetailsArrayList;
    }



}
