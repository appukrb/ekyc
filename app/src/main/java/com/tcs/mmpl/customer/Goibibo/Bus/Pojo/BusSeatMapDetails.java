package com.tcs.mmpl.customer.Goibibo.Bus.Pojo;

import java.io.Serializable;

/**
 * Created by hp on 2016-07-25.
 */
public class BusSeatMapDetails implements Serializable {

    private int id;
    private String ServiceTaxPercentage = "";
    private String ActualSeatFare = "";
    private String SeatType = "";
    private String RowNo = "";
    private String Deck = "";
    private String operatorServiceChargeAbsolute = "";
    private String ColumnNo = "";
    private String serviceCharge = "";
    private String SeatFare = "";
    private String Height = "";
    private String Width = "";
    private String LSeat = "";
    private String BaseFare = "";
    private String ServiceTax = "";
    private String SeatName = "";
    private String seat_status = "";
    private String SeatStatus = "";
    private String row_type = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceTaxPercentage() {
        return ServiceTaxPercentage;
    }

    public void setServiceTaxPercentage(String serviceTaxPercentage) {
        ServiceTaxPercentage = serviceTaxPercentage;
    }

    public String getActualSeatFare() {
        return ActualSeatFare;
    }

    public void setActualSeatFare(String actualSeatFare) {
        ActualSeatFare = actualSeatFare;
    }

    public String getSeatType() {
        return SeatType;
    }

    public void setSeatType(String seatType) {
        SeatType = seatType;
    }

    public String getRowNo() {
        return RowNo;
    }

    public void setRowNo(String rowNo) {
        RowNo = rowNo;
    }

    public String getDeck() {
        return Deck;
    }

    public void setDeck(String deck) {
        Deck = deck;
    }

    public String getOperatorServiceChargeAbsolute() {
        return operatorServiceChargeAbsolute;
    }

    public void setOperatorServiceChargeAbsolute(String operatorServiceChargeAbsolute) {
        this.operatorServiceChargeAbsolute = operatorServiceChargeAbsolute;
    }

    public String getColumnNo() {
        return ColumnNo;
    }

    public void setColumnNo(String columnNo) {
        ColumnNo = columnNo;
    }

    public String getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(String serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getSeatFare() {
        return SeatFare;
    }

    public void setSeatFare(String seatFare) {
        SeatFare = seatFare;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getWidth() {
        return Width;
    }

    public void setWidth(String width) {
        Width = width;
    }

    public String getLSeat() {
        return LSeat;
    }

    public void setLSeat(String LSeat) {
        this.LSeat = LSeat;
    }

    public String getBaseFare() {
        return BaseFare;
    }

    public void setBaseFare(String baseFare) {
        BaseFare = baseFare;
    }

    public String getServiceTax() {
        return ServiceTax;
    }

    public void setServiceTax(String serviceTax) {
        ServiceTax = serviceTax;
    }

    public String getSeatName() {
        return SeatName;
    }

    public void setSeatName(String seatName) {
        SeatName = seatName;
    }

    public String getSeat_status() {
        return seat_status;
    }

    public void setSeat_status(String seat_status) {
        this.seat_status = seat_status;
    }

    public String getSeatStatus() {
        return SeatStatus;
    }

    public void setSeatStatus(String seatStatus) {
        SeatStatus = seatStatus;
    }

    public String getRow_type() {
        return row_type;
    }

    public void setRow_type(String row_type) {
        this.row_type = row_type;
    }

//    @Override
//    public int compareTo(BusSeatMapDetails busSeatMapDetails) {
//
//        int temp_id=((BusSeatMapDetails)busSeatMapDetails).getId();
//        /* For Descending order*/
//        return temp_id-this.id;
//
//    }
}
