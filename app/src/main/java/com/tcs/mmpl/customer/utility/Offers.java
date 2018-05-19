package com.tcs.mmpl.customer.utility;

/**
 * Created by hp on 12-04-2016.
 */
public class Offers {

    private String couponID;

    public String getCouponID() {
        return couponID;
    }

    public void setCouponID(String couponID) {
        this.couponID = couponID;
    }

    public String getCouponImage() {
        return couponImage;
    }

    public void setCouponImage(String couponImage) {
        this.couponImage = couponImage;
    }

    public String getMerchantUrl() {
        return merchantUrl;
    }

    public void setMerchantUrl(String merchantUrl) {
        this.merchantUrl = merchantUrl;
    }

    public String getMerchantAboutMe() {
        return merchantAboutMe;
    }

    public void setMerchantAboutMe(String merchantAboutMe) {
        this.merchantAboutMe = merchantAboutMe;
    }

    private String couponImage;
    private String merchantUrl;
    private String merchantAboutMe;
}

