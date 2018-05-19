package com.tcs.mmpl.customer.Goibibo.Bus.Pojo;

import java.io.Serializable;


/**
 * Created by hp on 2016-07-19.
 */
public class BusDetails implements Serializable {

    private String origin;
    private String rating;
    private String DepartureTime;
    private String seat;
    private String duration;
    private String qtype;
    private String busCondition;
    private String destination;
    private String BusType;
    private String SeatsAvailable;
    private String availabilityStatus;
    private String TravelsName;
    private String ArrivalTime;
    private String arrdate;
    private String totalfare;
    private String skey;
    private String depdate;


    public String getDepdate() {
        return depdate;
    }

    public void setDepdate(String depdate) {
        this.depdate = depdate;
    }



    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }



    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDepartureTime() {
        return DepartureTime;
    }

    public void setDepartureTime(String departureTime) {
        DepartureTime = departureTime;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getQtype() {
        return qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

    public String getBusCondition() {
        return busCondition;
    }

    public void setBusCondition(String busCondition) {
        this.busCondition = busCondition;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getBusType() {
        return BusType;
    }

    public void setBusType(String busType) {
        BusType = busType;
    }

    public String getSeatsAvailable() {
        return SeatsAvailable;
    }

    public void setSeatsAvailable(String seatsAvailable) {
        SeatsAvailable = seatsAvailable;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getTravelsName() {
        return TravelsName;
    }

    public void setTravelsName(String travelsName) {
        TravelsName = travelsName;
    }

    public String getArrivalTime() {
        return ArrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        ArrivalTime = arrivalTime;
    }

    public String getArrdate() {
        return arrdate;
    }

    public void setArrdate(String arrdate) {
        this.arrdate = arrdate;
    }

    public String getTotalfare() {
        return totalfare;
    }

    public void setTotalfare(String totalfare) {
        this.totalfare = totalfare;
    }
}
