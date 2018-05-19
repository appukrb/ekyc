package com.tcs.mmpl.customer.Goibibo.Bus.Pojo;

import java.io.Serializable;

/**
 * Created by hp on 2016-08-05.
 */
public class BusSeatForHold implements Serializable {

    private String title;
    private String firstName;
    private String lastName;
    private String age;
    private String eMail;
    private String mobile;
    private String seatName;
    private String baseFare;
    private String sTax;
    private String sTaxp;
    private String sChrg;
    private String actualFare;
    private String seatFare;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public void setsTax(String sTax) {
        this.sTax = sTax;
    }

    public void setsTaxp(String sTaxp) {
        this.sTaxp = sTaxp;
    }

    public void setsChrg(String sChrg) {
        this.sChrg = sChrg;
    }

    public void setActualFare(String actualFare) {
        this.actualFare = actualFare;
    }

    public void setSeatFare(String seatFare) {
        this.seatFare = seatFare;
    }
}
