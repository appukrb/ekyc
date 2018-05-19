package com.tcs.mmpl.customer.Goibibo.Bus.Pojo;

import java.io.Serializable;

/**
 * Created by hp on 2016-08-02.
 */
public class BusBasicInfo implements Serializable {

    private String skey;
    private String way;
    private String origin;
    private String destination;
    private String busoperator;
    private String boardingpoint="";
    private String boardingtime="";
    private String seats="NA";
    private String totalprice="NA";
    private String departuredate="";
    private String arrivaldate="";
    private String duration="";

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDeparturedate() {
        return departuredate;
    }

    public void setDeparturedate(String departuredate) {
        this.departuredate = departuredate;
    }

    public String getArrivaldate() {
        return arrivaldate;
    }

    public void setArrivaldate(String arrivaldate) {
        this.arrivaldate = arrivaldate;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getBusoperator() {
        return busoperator;
    }

    public void setBusoperator(String busoperator) {
        this.busoperator = busoperator;
    }

    public String getBoardingpoint() {
        return boardingpoint;
    }

    public void setBoardingpoint(String boardingpoint) {
        this.boardingpoint = boardingpoint;
    }

    public String getBoardingtime() {
        return boardingtime;
    }

    public void setBoardingtime(String boardingtime) {
        this.boardingtime = boardingtime;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }
}
