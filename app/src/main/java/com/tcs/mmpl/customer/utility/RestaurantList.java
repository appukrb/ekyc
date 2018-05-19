package com.tcs.mmpl.customer.utility;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hp on 2016-12-09.
 */
public class RestaurantList implements Serializable {

    private String hotelName;
    private String hotelImage="";
    private String menuCard = "NA";
    private String phone;
    private String hotelAddress;
    private String hotelCuisine;
    private String locality;
    private String city;
    private String title;
    private String id;
    private Deals  deal;

    private ArrayList<String> menuCardList;

    public ArrayList<String> getMenuCardList() {
        return menuCardList;
    }

    public void setMenuCardList(ArrayList<String> menuCardList) {
        this.menuCardList = menuCardList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Deals getDeal() {
        return deal;
    }

    public void setDeal(Deals deal) {
        this.deal = deal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHotelAddress() {
        return hotelAddress;
    }

    public void setHotelAddress(String hotelAddress) {
        this.hotelAddress = hotelAddress;
    }

    public String getHotelCuisine() {
        return hotelCuisine;
    }

    public void setHotelCuisine(String hotelCuisine) {
        this.hotelCuisine = hotelCuisine;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMenuCard() {
        return menuCard;
    }

    public void setMenuCard(String menuCard) {
        this.menuCard = menuCard;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelImage() {
        return hotelImage;
    }

    public void setHotelImage(String hotelImage) {
        this.hotelImage = hotelImage;
    }
}
