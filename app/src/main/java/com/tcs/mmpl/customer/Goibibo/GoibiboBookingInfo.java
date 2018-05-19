package com.tcs.mmpl.customer.Goibibo;

import java.io.Serializable;

/**
 * Created by hp on 2016-08-12.
 */
public class GoibiboBookingInfo implements Serializable {

    private String fromPlace;
    private String toPlace;
    private String skey;
    private String retskey;
    private String bookingId;
    private String type;
    private String dateofjourney;
    private String dateofboarding;
    private String travelstatus;

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getRetskey() {
        return retskey;
    }

    public void setRetskey(String retskey) {
        this.retskey = retskey;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateofjourney() {
        return dateofjourney;
    }

    public void setDateofjourney(String dateofjourney) {
        this.dateofjourney = dateofjourney;
    }

    public String getDateofboarding() {
        return dateofboarding;
    }

    public void setDateofboarding(String dateofboarding) {
        this.dateofboarding = dateofboarding;
    }

    public String getTravelstatus() {
        return travelstatus;
    }

    public void setTravelstatus(String travelstatus) {
        this.travelstatus = travelstatus;
    }
}
