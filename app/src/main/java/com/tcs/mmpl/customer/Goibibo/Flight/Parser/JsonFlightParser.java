package com.tcs.mmpl.customer.Goibibo.Flight.Parser;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.tcs.mmpl.customer.Goibibo.Flight.GoibiboFlightDatabaseHelper;
import com.tcs.mmpl.customer.Goibibo.Flight.Pojo.FlightDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hp on 2016-07-26.
 */
public class JsonFlightParser {

    private Context context;
    private String json;
    private GoibiboFlightDatabaseHelper goibiboFlightDatabaseHelper;

    public JsonFlightParser(Context context, String json) {

        this.context = context;
        this.json = json;

        goibiboFlightDatabaseHelper = new GoibiboFlightDatabaseHelper(context);
    }

    public ArrayList<FlightDetails> convertOnwardFlightSearch() {

        ArrayList<FlightDetails> flightDetailsList = new ArrayList<FlightDetails>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String data = jsonObject.get("data").toString();

            JSONObject jsonObject1 = new JSONObject(data);
            JSONArray arrayOnwardFlights = jsonObject1.getJSONArray("onwardflights");

            for (int i = 0; i < arrayOnwardFlights.length(); i++) {
                JSONObject jsonObject2 = arrayOnwardFlights.getJSONObject(i);


                if (!jsonObject2.getString("seatsavailable").equalsIgnoreCase("0")) {

                    FlightDetails flightDetails = new FlightDetails();

                    flightDetails.setBookingdata(jsonObject2.toString());

                    flightDetails.setOrigin(jsonObject2.get("origin").toString());
                    flightDetails.setRating(jsonObject2.get("rating").toString());
                    flightDetails.setDepartureTime(jsonObject2.get("DepartureTime").toString());
                    flightDetails.setFlightcode(jsonObject2.get("flightcode").toString());
                    flightDetails.setGroup(jsonObject2.get("Group").toString());
                    flightDetails.setFarebasis(jsonObject2.get("farebasis").toString());
                    flightDetails.setSeatingclass(jsonObject2.get("seatingclass").toString());
                    flightDetails.setHoldflag(jsonObject2.get("holdflag").toString());
                    flightDetails.setCINFO(jsonObject2.get("CINFO").toString());
                    flightDetails.setDeptime(jsonObject2.get("deptime").toString());
                    flightDetails.setDuration(jsonObject2.get("duration").toString());
                    flightDetails.setFlightno(jsonObject2.get("flightno").toString());
                    flightDetails.setDestination(jsonObject2.get("destination").toString());
                    flightDetails.setStops(jsonObject2.get("stops").toString());
                    flightDetails.setSeatsavailable(jsonObject2.get("seatsavailable").toString());
                    flightDetails.setWarnings(jsonObject2.get("warnings").toString());
                    flightDetails.setCarrierid(jsonObject2.get("carrierid").toString());
                    flightDetails.setSearchKey(jsonObject2.get("searchKey").toString());
                    flightDetails.setAirline(jsonObject2.get("airline").toString());
                    flightDetails.setDepdate(jsonObject2.get("depdate").toString());
                    flightDetails.setArrtime(jsonObject2.get("arrtime").toString());
                    flightDetails.setArrdate(jsonObject2.get("arrdate").toString());

                    JSONObject jsonFare = new JSONObject(jsonObject2.get("fare").toString());
                    flightDetails.setTotalfare(jsonFare.get("totalfare").toString());


                    flightDetailsList.add(flightDetails);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flightDetailsList;
    }

    public ArrayList<FlightDetails> convertReturnFlightSearch() {
        ArrayList<FlightDetails> flightDetailsList = new ArrayList<FlightDetails>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String data = jsonObject.get("data").toString();

            JSONObject jsonObject1 = new JSONObject(data);

            JSONArray arrayOnwardFlights = jsonObject1.getJSONArray("returnflights");

            for (int i = 0; i < arrayOnwardFlights.length(); i++) {

                JSONObject jsonObject2 = arrayOnwardFlights.getJSONObject(i);

                if (!jsonObject2.getString("seatsavailable").equalsIgnoreCase("0")) {


                    FlightDetails flightDetails = new FlightDetails();

                    flightDetails.setBookingdata(jsonObject2.toString());

                    flightDetails.setOrigin(jsonObject2.get("origin").toString());
                    flightDetails.setRating(jsonObject2.get("rating").toString());
                    flightDetails.setDepartureTime(jsonObject2.get("DepartureTime").toString());
                    flightDetails.setFlightcode(jsonObject2.get("flightcode").toString());
                    flightDetails.setGroup(jsonObject2.get("Group").toString());
                    flightDetails.setFarebasis(jsonObject2.get("farebasis").toString());
                    flightDetails.setSeatingclass(jsonObject2.get("seatingclass").toString());
                    flightDetails.setHoldflag(jsonObject2.get("holdflag").toString());
                    flightDetails.setCINFO(jsonObject2.get("CINFO").toString());
                    flightDetails.setDeptime(jsonObject2.get("deptime").toString());
                    flightDetails.setDuration(jsonObject2.get("duration").toString());
                    flightDetails.setFlightno(jsonObject2.get("flightno").toString());
                    flightDetails.setDestination(jsonObject2.get("destination").toString());
                    flightDetails.setStops(jsonObject2.get("stops").toString());
                    flightDetails.setSeatsavailable(jsonObject2.get("seatsavailable").toString());
                    flightDetails.setWarnings(jsonObject2.get("warnings").toString());
                    flightDetails.setCarrierid(jsonObject2.get("carrierid").toString());
                    flightDetails.setSearchKey(jsonObject2.get("searchKey").toString());
                    flightDetails.setAirline(jsonObject2.get("airline").toString());
                    flightDetails.setDepdate(jsonObject2.get("depdate").toString());
                    flightDetails.setArrtime(jsonObject2.get("arrtime").toString());
                    flightDetails.setArrdate(jsonObject2.get("arrdate").toString());

                    JSONObject jsonFare = new JSONObject(jsonObject2.get("fare").toString());
                    flightDetails.setTotalfare(jsonFare.get("totalfare").toString());


                    flightDetailsList.add(flightDetails);

                }
            }
        } catch (Exception e) {

        }

        //// System.out.println("Size inside the function::" + flightDetailsList.size());
        return flightDetailsList;
    }


    public ArrayList<FlightDetails> convertOnwardFlightSearchNew() {


        ArrayList<FlightDetails> flightDetailsList = new ArrayList<FlightDetails>();
        try {
            Gson gson = new Gson();

        } catch (Exception e) {

        }
        return flightDetailsList;
    }


    public JSONArray convertRepriceResponse()
    {
        JSONArray jsonBookingDataArray = new JSONArray();
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            String data = jsonObject.getString("data");

            JSONObject jsonObject1 = new JSONObject(data);


            JSONArray jsonArray1 = jsonObject1.getJSONArray("onwardflights");
            if(jsonArray1.length()>0)
            {
                JSONObject j2 = jsonArray1.getJSONObject(0);
                jsonBookingDataArray.put(j2);
            }


            JSONArray jsonArray = jsonObject1.getJSONArray("returnflights");
            if(jsonArray.length()>0)
            {
                JSONObject j1 = jsonArray.getJSONObject(0);
                jsonBookingDataArray.put(j1);
            }


        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.i("Reprice BookingData",jsonBookingDataArray.toString());

        return  jsonBookingDataArray;
    }
}