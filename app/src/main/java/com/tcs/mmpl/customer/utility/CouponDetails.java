package com.tcs.mmpl.customer.utility;

import java.io.Serializable;

/**
 * Created by hp on 2016-12-22.
 */
public class CouponDetails implements Serializable {

    private String couponId;
    private String couponstatus;
    private String consumedDate;
    private String expiryDate;
    private String restName;

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponstatus() {
        return couponstatus;
    }

    public void setCouponstatus(String couponstatus) {
        this.couponstatus = couponstatus;
    }

    public String getConsumedDate() {
        return consumedDate;
    }

    public void setConsumedDate(String consumedDate) {
        this.consumedDate = consumedDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }
}
