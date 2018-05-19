package com.tcs.mmpl.customer.utility;

import java.io.Serializable;

/**
 * Created by hp on 2016-11-23.
 */

public class HotelBhavan implements Serializable {


    private String shopName;
    private String shopCode;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }
}
