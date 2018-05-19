package com.tcs.mmpl.customer.Goibibo.Hotel.Pojo;

import java.io.Serializable;

/**
 * Created by hp on 2016-09-21.
 */
public class HotelCity implements Serializable {

    private String city_name;
    private String country_name;
    private String country_code;

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }
}
