package com.tcs.mmpl.customer.utility;

import java.io.Serializable;

/**
 * Created by hp on 2016-12-17.
 */
public class RestaurantCouponList implements Serializable {

    private String hotelName;
    private String hotelImage = "";
    private String menuCard = "NA";
    private String phone;
    private String hotelCuisine;
    private String id;
    private Deals deal;
    private Address address;

    private String deal_id;
    private String restaurant_id;
    private String coupon_code;
    private String state;
    private String expires_at;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(String deal_id) {
        this.deal_id = deal_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
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

    public String getMenuCard() {
        return menuCard;
    }

    public void setMenuCard(String menuCard) {
        this.menuCard = menuCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getHotelCuisine() {
        return hotelCuisine;
    }

    public void setHotelCuisine(String hotelCuisine) {
        this.hotelCuisine = hotelCuisine;
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
}
