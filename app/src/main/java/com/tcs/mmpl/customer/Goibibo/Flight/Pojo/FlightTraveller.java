package com.tcs.mmpl.customer.Goibibo.Flight.Pojo;

import java.io.Serializable;

/**
 * Created by hp on 2016-08-19.
 */
public class FlightTraveller implements Serializable {

    private String Title;
    private String FirstName;
    private String LastName="";
    private String eticketnumber = "";
    private String DateOfBirth = "";
    private String Type = "";

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEticketnumber() {
        return eticketnumber;
    }

    public void setEticketnumber(String eticketnumber) {
        this.eticketnumber = eticketnumber;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
