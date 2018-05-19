package com.tcs.mmpl.customer.Goibibo.Bus.Parser;

import android.content.Context;

import com.tcs.mmpl.customer.Goibibo.Bus.GoibiboBusDatabaseHelper;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusDetails;
import com.tcs.mmpl.customer.Goibibo.Bus.Pojo.BusSeatMapDetails;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hp on 2016-07-26.
 */
public class JsonParser {

    private Context context;
    private String json;
    private GoibiboBusDatabaseHelper goibiboBusDatabaseHelper;

    public JsonParser(Context context, String json) {

        this.context = context;
        this.json = json;

        goibiboBusDatabaseHelper = new GoibiboBusDatabaseHelper(context);
    }

    public ArrayList<BusDetails> convertOnwardBusSearch(String seat, String busCondition) {


        ArrayList<BusDetails> busDetailsList = new ArrayList<BusDetails>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String data = jsonObject.get("data").toString();

            JSONObject jsonObject1 = new JSONObject(data);
            JSONArray arrayOnwardFlights = jsonObject1.getJSONArray("onwardflights");

            for (int i = 0; i < arrayOnwardFlights.length(); i++) {
                JSONObject jsonObject2 = arrayOnwardFlights.getJSONObject(i);


                JSONObject seatDetailJsonObj = new JSONObject(jsonObject2.getString("RouteSeatTypeDetail"));
                JSONArray listArray = seatDetailJsonObj.getJSONArray("list");
                JSONObject availableJsonObj = listArray.getJSONObject(0);

                if (!availableJsonObj.getString("SeatsAvailable").equalsIgnoreCase("0"))

                {
                    if(jsonObject2.get("seat").toString().contains(seat) || seat.contains("NA")) {
                        if (jsonObject2.get("busCondition").toString().equalsIgnoreCase(busCondition) || busCondition.equalsIgnoreCase("NA")) {

                            BusDetails busDetails = new BusDetails();
                            busDetails.setOrigin(jsonObject2.get("origin").toString());
                            busDetails.setRating(jsonObject2.get("rating").toString());
                            busDetails.setDepartureTime(jsonObject2.get("DepartureTime").toString());
                            busDetails.setSeat(jsonObject2.get("seat").toString());
                            busDetails.setDuration(jsonObject2.get("duration").toString());
                            busDetails.setQtype(jsonObject2.get("qtype").toString());
                            busDetails.setBusCondition(jsonObject2.get("busCondition").toString());
                            busDetails.setDestination(jsonObject2.get("destination").toString());
                            busDetails.setBusType(jsonObject2.get("BusType").toString());
                            busDetails.setSkey(jsonObject2.get("skey").toString());

                            busDetails.setTotalfare((new JSONObject(jsonObject2.get("fare").toString())).get("totalfare").toString());

                            JSONArray list = (new JSONObject(jsonObject2.get("RouteSeatTypeDetail").toString())).getJSONArray("list");
                            JSONObject jsonList = list.getJSONObject(0);
                            busDetails.setSeatsAvailable(jsonList.get("SeatsAvailable").toString());
                            busDetails.setAvailabilityStatus(jsonList.get("availabilityStatus").toString());

                            busDetails.setTravelsName(jsonObject2.get("TravelsName").toString());
                            busDetails.setArrivalTime(jsonObject2.get("ArrivalTime").toString());
                            busDetails.setArrdate(jsonObject2.get("arrdate").toString());
                            busDetails.setDepdate(jsonObject2.get("depdate").toString());


                            busDetailsList.add(busDetails);
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return busDetailsList;
    }

    public ArrayList<BusDetails> convertReturnBusSearch(String seat, String busCondition) {
        ArrayList<BusDetails> busDetailsList = new ArrayList<BusDetails>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String data = jsonObject.get("data").toString();

            JSONObject jsonObject1 = new JSONObject(data);

            JSONArray arrayOnwardFlights = jsonObject1.getJSONArray("returnflights");

            for (int i = 0; i < arrayOnwardFlights.length(); i++) {

                JSONObject jsonObject2 = arrayOnwardFlights.getJSONObject(i);


                JSONObject seatDetailJsonObj = new JSONObject(jsonObject2.getString("RouteSeatTypeDetail"));
                JSONArray listArray = seatDetailJsonObj.getJSONArray("list");
                JSONObject availableJsonObj = listArray.getJSONObject(0);

                if (!availableJsonObj.getString("SeatsAvailable").equalsIgnoreCase("0")) {

                    if (jsonObject2.get("seat").toString().contains(seat) || seat.contains("NA")) {
                        if (jsonObject2.get("busCondition").toString().equalsIgnoreCase(busCondition) || busCondition.equalsIgnoreCase("NA")) {

                            BusDetails busDetails = new BusDetails();
                            busDetails.setOrigin(jsonObject2.get("origin").toString());
                            busDetails.setRating(jsonObject2.get("rating").toString());
                            busDetails.setDepartureTime(jsonObject2.get("DepartureTime").toString());
                            busDetails.setSeat(jsonObject2.get("seat").toString());
                            busDetails.setDuration(jsonObject2.get("duration").toString());
                            busDetails.setQtype(jsonObject2.get("qtype").toString());
                            busDetails.setBusCondition(jsonObject2.get("busCondition").toString());
                            busDetails.setDestination(jsonObject2.get("destination").toString());
                            busDetails.setBusType(jsonObject2.get("BusType").toString());
                            busDetails.setSkey(jsonObject2.get("skey").toString());

                            busDetails.setTotalfare((new JSONObject(jsonObject2.get("fare").toString())).get("totalfare").toString());

                            JSONArray list = (new JSONObject(jsonObject2.get("RouteSeatTypeDetail").toString())).getJSONArray("list");
                            JSONObject jsonList = list.getJSONObject(0);
                            busDetails.setSeatsAvailable(jsonList.get("SeatsAvailable").toString());
                            busDetails.setAvailabilityStatus(jsonList.get("availabilityStatus").toString());

                            busDetails.setTravelsName(jsonObject2.get("TravelsName").toString());
                            busDetails.setArrivalTime(jsonObject2.get("ArrivalTime").toString());
                            busDetails.setArrdate(jsonObject2.get("arrdate").toString());
                            busDetails.setDepdate(jsonObject2.get("depdate").toString());


                            busDetailsList.add(busDetails);

                        }
                    }
                }
            }
        } catch (Exception e) {

        }

        //// System.out.println("Size inside the function::" + busDetailsList.size());
        return busDetailsList;
    }

    public ArrayList<BusSeatMapDetails> convertSeatMap() {

        ArrayList<BusSeatMapDetails> busSeatMapDetailsArrayList = new ArrayList<BusSeatMapDetails>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String data = jsonObject.get("data").toString();

            JSONObject jsonObject1 = new JSONObject(data);

            JSONObject jsonBoarding = new JSONObject(new JSONObject(jsonObject1.getString("onwardBPs")).getString("GetBoardingPointsResult"));
            JSONArray jsonArrayBoarding = jsonBoarding.getJSONArray("list");
            for (int j = 0; j < jsonArrayBoarding.length(); j++) {
                JSONObject j1 = jsonArrayBoarding.getJSONObject(j);
                goibiboBusDatabaseHelper.insertBoardingPoint(j1.getString("BPLocation"), j1.getString("BPTime"), j1.getString("BPName"));
            }

            JSONArray arrayOnwardFlights = jsonObject1.getJSONArray("onwardSeats");

            int prev_row = 0;
            int column_count = 1;

            ArrayList<String> first_seat_check = new ArrayList<String>();
            boolean first_row_col_flag = false;

            for (int i = 0; i < arrayOnwardFlights.length(); i++) {
                JSONObject jsonObject2 = arrayOnwardFlights.getJSONObject(i);

                BusSeatMapDetails busSeatMapDetails = new BusSeatMapDetails();
                busSeatMapDetails.setId(i);
                busSeatMapDetails.setServiceTaxPercentage(jsonObject2.get("ServiceTaxPercentage").toString());
                busSeatMapDetails.setActualSeatFare(jsonObject2.get("ActualSeatFare").toString());
                busSeatMapDetails.setSeatType(jsonObject2.get("SeatType").toString());
                busSeatMapDetails.setRowNo(jsonObject2.get("RowNo").toString());
                busSeatMapDetails.setDeck(jsonObject2.get("Deck").toString());
                busSeatMapDetails.setOperatorServiceChargeAbsolute(jsonObject2.get("operatorServiceChargeAbsolute").toString());
                busSeatMapDetails.setColumnNo(jsonObject2.get("ColumnNo").toString());
                busSeatMapDetails.setServiceCharge(jsonObject2.get("serviceCharge").toString());
                busSeatMapDetails.setSeatFare(jsonObject2.get("SeatFare").toString());
                busSeatMapDetails.setHeight(jsonObject2.get("Height").toString());
                busSeatMapDetails.setWidth(jsonObject2.get("Width").toString());
                busSeatMapDetails.setLSeat(jsonObject2.get("LSeat").toString());
                busSeatMapDetails.setBaseFare(jsonObject2.get("BaseFare").toString());
                busSeatMapDetails.setServiceTax(jsonObject2.get("ServiceTax").toString());
                busSeatMapDetails.setSeatName(jsonObject2.get("SeatName").toString());
                busSeatMapDetails.setSeat_status(jsonObject2.get("seat_status").toString());
                busSeatMapDetails.setSeatStatus(jsonObject2.get("SeatStatus").toString());
                busSeatMapDetails.setRow_type(jsonObject2.get("row_type").toString());


                String seatType = jsonObject2.get("SeatType").toString();
                int current_column = Integer.parseInt(jsonObject2.get("ColumnNo").toString());
                int current_row = Integer.parseInt(jsonObject2.get("RowNo").toString());
                if (jsonObject2.get("Deck").toString().equalsIgnoreCase("1")) {
                    //Deck 1

                    //First ROW and First Column Check Starts
                    if (current_row == 0 && current_column == 0) {
                        first_seat_check.add("R" + current_row + "C0");
                        first_row_col_flag = true;
                    }

                    if (current_row == 0 && !first_seat_check.contains("R" + current_row + "C0") && first_row_col_flag == false) {
                        BusSeatMapDetails busSeatMapDetailsNew = new BusSeatMapDetails();
                        busSeatMapDetailsNew.setId(1);
                        busSeatMapDetailsNew.setRowNo("0");
                        busSeatMapDetailsNew.setDeck("1");
                        busSeatMapDetailsNew.setColumnNo("0");
                        if (seatType.trim().equalsIgnoreCase("Sleeper"))
                            busSeatMapDetailsNew.setHeight("4");
                        else
                            busSeatMapDetailsNew.setHeight("3");
                        busSeatMapDetailsNew.setWidth("1");


                        busSeatMapDetailsArrayList.add(busSeatMapDetailsNew);
                        first_row_col_flag = true;
                    }
                    //First ROW and First Column Check Ends

                    if (prev_row != current_row) {

                        //// System.out.println("Deck 1");
                        //// System.out.println("Prev Row::::::" + prev_row);
                        //// System.out.println("Current Row:::::::" + current_row);
                        //// System.out.println("Column Count:::::::" + column_count);

                        //First ROW and First Column Check Starts
                        if (current_column == 0) {
                            first_seat_check.add("R" + current_row + "C0");
                        } else if (!first_seat_check.contains("R" + current_row + "C0")) {
                            BusSeatMapDetails busSeatMapDetailsNew = new BusSeatMapDetails();
                            busSeatMapDetailsNew.setId(1);
                            busSeatMapDetailsNew.setRowNo(String.valueOf(current_row));
                            busSeatMapDetailsNew.setDeck("1");
                            busSeatMapDetailsNew.setColumnNo("0");
                            if (seatType.trim().equalsIgnoreCase("Sleeper"))
                                busSeatMapDetailsNew.setHeight("4");
                            else
                                busSeatMapDetailsNew.setHeight("3");
                            busSeatMapDetailsNew.setWidth("1");


                            busSeatMapDetailsArrayList.add(busSeatMapDetailsNew);
                        }
                        //First ROW and First Column Check Ends

                        int maxCol = column_count;
                        column_count = 0;

                        int diff = current_row - prev_row;
                        prev_row = current_row;
                        if (diff == 2) {
                            int row_no = current_row - 1;
                            for (int m = 0; m <= maxCol; m++) {

                                int col_no = m * 2;

                                BusSeatMapDetails busSeatMapDetailsNew = new BusSeatMapDetails();
                                busSeatMapDetailsNew.setId(100 + i);
                                busSeatMapDetailsNew.setServiceTaxPercentage("");
                                busSeatMapDetailsNew.setActualSeatFare("");
                                busSeatMapDetailsNew.setSeatType("");
                                busSeatMapDetailsNew.setRowNo(String.valueOf(row_no));
                                busSeatMapDetailsNew.setDeck("1");
                                busSeatMapDetailsNew.setOperatorServiceChargeAbsolute("");
                                busSeatMapDetailsNew.setColumnNo(String.valueOf(col_no));
                                busSeatMapDetailsNew.setServiceCharge("");
                                busSeatMapDetailsNew.setSeatFare("");
                                busSeatMapDetailsNew.setHeight("3");
                                busSeatMapDetailsNew.setWidth("1");
                                busSeatMapDetailsNew.setLSeat("");
                                busSeatMapDetailsNew.setBaseFare("");
                                busSeatMapDetailsNew.setServiceTax("");
                                busSeatMapDetailsNew.setSeatName("");
                                busSeatMapDetailsNew.setSeat_status("");
                                busSeatMapDetailsNew.setSeatStatus("");
                                busSeatMapDetailsNew.setRow_type("");

                                busSeatMapDetailsArrayList.add(busSeatMapDetailsNew);
                            }
                        } else if (diff == 1) {
                            //// System.out.println("COLUMN DIFFERENCE::::::::::" + current_column);
                            int col_diff = current_column;
                            if (col_diff > 2) {
                                int row_no = current_row;
                                if (seatType.trim().equalsIgnoreCase("Seater")) {
                                    for (int n = 1; n < col_diff; n++) {
                                        int col_no = n * 1;

                                        BusSeatMapDetails busSeatMapDetailsNew = new BusSeatMapDetails();
                                        busSeatMapDetailsNew.setId(200 + n);
                                        busSeatMapDetailsNew.setRowNo(String.valueOf(row_no));
                                        busSeatMapDetailsNew.setDeck("1");
                                        busSeatMapDetailsNew.setColumnNo(String.valueOf(col_no));
                                        busSeatMapDetailsNew.setHeight("3");
                                        busSeatMapDetailsNew.setWidth("1");

                                        busSeatMapDetailsArrayList.add(busSeatMapDetailsNew);
                                    }
                                } else if (seatType.trim().equalsIgnoreCase("Sleeper")) {
                                    for (int n = 2; n < col_diff; n = n + 2) {
                                        int col_no = n;

                                        BusSeatMapDetails busSeatMapDetailsNew = new BusSeatMapDetails();
                                        busSeatMapDetailsNew.setId(300 + n);
                                        busSeatMapDetailsNew.setRowNo(String.valueOf(row_no));
                                        busSeatMapDetailsNew.setDeck("1");
                                        busSeatMapDetailsNew.setColumnNo(String.valueOf(col_no));
                                        busSeatMapDetailsNew.setHeight("4");
                                        busSeatMapDetailsNew.setWidth("1");

                                        busSeatMapDetailsArrayList.add(busSeatMapDetailsNew);
                                    }
                                }
                            }

                        }
                    } else {
                        column_count = column_count + 1;
                    }
                } else if (jsonObject2.get("Deck").toString().equalsIgnoreCase("2")) {
                    //Deck 2

                    if (prev_row <= current_row) {

                        if (prev_row != current_row) {
                            //// System.out.println("Deck 2");
                            //// System.out.println("Prev Row::::::" + prev_row);
                            //// System.out.println("Current Row:::::::" + current_row);
                            //// System.out.println("Column Count:::::::" + column_count);

                            //First ROW and First Column Check Starts
                            if (current_column == 0) {
                                first_seat_check.add("R" + current_row + "C0");
                            } else if (!first_seat_check.contains("R" + current_row + "C0")) {
                                BusSeatMapDetails busSeatMapDetailsNew = new BusSeatMapDetails();
                                busSeatMapDetailsNew.setId(1);
                                busSeatMapDetailsNew.setRowNo(String.valueOf(current_row));
                                busSeatMapDetailsNew.setDeck("2");
                                busSeatMapDetailsNew.setColumnNo("0");
                                if (seatType.trim().equalsIgnoreCase("Sleeper"))
                                    busSeatMapDetailsNew.setHeight("4");
                                else
                                    busSeatMapDetailsNew.setHeight("3");
                                busSeatMapDetailsNew.setWidth("1");


                                busSeatMapDetailsArrayList.add(busSeatMapDetailsNew);
                            }
                            //First ROW and First Column Check Ends

                            int maxCol = column_count;
                            column_count = 0;

                            int diff = current_row - prev_row;
                            prev_row = current_row;
                            if (diff == 2) {
                                int row_no = current_row - 1;
                                for (int m = 0; m <= maxCol; m++) {

                                    int col_no = m * 2;

                                    BusSeatMapDetails busSeatMapDetailsNew = new BusSeatMapDetails();
                                    busSeatMapDetailsNew.setId(400 + m);
                                    busSeatMapDetailsNew.setServiceTaxPercentage("");
                                    busSeatMapDetailsNew.setActualSeatFare("");
                                    busSeatMapDetailsNew.setSeatType("");
                                    busSeatMapDetailsNew.setRowNo(String.valueOf(row_no));
                                    busSeatMapDetailsNew.setDeck("2");
                                    busSeatMapDetailsNew.setOperatorServiceChargeAbsolute("");
                                    busSeatMapDetailsNew.setColumnNo(String.valueOf(col_no));
                                    busSeatMapDetailsNew.setServiceCharge("");
                                    busSeatMapDetailsNew.setSeatFare("");
                                    busSeatMapDetailsNew.setHeight("3");
                                    busSeatMapDetailsNew.setWidth("1");
                                    busSeatMapDetailsNew.setLSeat("");
                                    busSeatMapDetailsNew.setBaseFare("");
                                    busSeatMapDetailsNew.setServiceTax("");
                                    busSeatMapDetailsNew.setSeatName("");
                                    busSeatMapDetailsNew.setSeat_status("");
                                    busSeatMapDetailsNew.setSeatStatus("");
                                    busSeatMapDetailsNew.setRow_type("");

                                    busSeatMapDetailsArrayList.add(busSeatMapDetailsNew);
                                }
                            } else if (diff == 1) {
                                //// System.out.println("COLUMN DIFFERENCE::::::::::" + current_column);
                                int col_diff = current_column;
                                if (col_diff > 2) {
                                    int row_no = current_row;
                                    if (seatType.trim().equalsIgnoreCase("Seater")) {
                                        for (int n = 1; n < col_diff; n++) {
                                            int col_no = n * 1;

                                            BusSeatMapDetails busSeatMapDetailsNew = new BusSeatMapDetails();
                                            busSeatMapDetailsNew.setId(500 + n);
                                            busSeatMapDetailsNew.setRowNo(String.valueOf(row_no));
                                            busSeatMapDetailsNew.setDeck("2");
                                            busSeatMapDetailsNew.setColumnNo(String.valueOf(col_no));
                                            busSeatMapDetailsNew.setHeight("3");
                                            busSeatMapDetailsNew.setWidth("1");

                                            busSeatMapDetailsArrayList.add(busSeatMapDetailsNew);
                                        }
                                    } else if (seatType.trim().equalsIgnoreCase("Sleeper")) {
                                        for (int n = 2; n < col_diff; n = n + 2) {
                                            int col_no = n;

                                            BusSeatMapDetails busSeatMapDetailsNew = new BusSeatMapDetails();
                                            busSeatMapDetailsNew.setId(600 + n);
                                            busSeatMapDetailsNew.setRowNo(String.valueOf(row_no));
                                            busSeatMapDetailsNew.setDeck("2");
                                            busSeatMapDetailsNew.setColumnNo(String.valueOf(col_no));
                                            busSeatMapDetailsNew.setHeight("4");
                                            busSeatMapDetailsNew.setWidth("1");


                                            busSeatMapDetailsArrayList.add(busSeatMapDetailsNew);
                                        }
                                    }
                                }
                            }
                        } else {

                            column_count = column_count + 1;
                        }
                    }


                }
                busSeatMapDetailsArrayList.add(busSeatMapDetails);

            }

        } catch (Exception e) {

        }

        return busSeatMapDetailsArrayList;
    }



}