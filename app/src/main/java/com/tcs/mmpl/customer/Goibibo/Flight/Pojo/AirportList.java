package com.tcs.mmpl.customer.Goibibo.Flight.Pojo;

import java.io.Serializable;

/**
 * Created by hp on 2016-08-19.
 */
public class AirportList implements Serializable {

    private String city;
    private String code;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
