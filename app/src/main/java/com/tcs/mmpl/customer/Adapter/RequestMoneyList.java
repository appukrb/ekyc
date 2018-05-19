package com.tcs.mmpl.customer.Adapter;

import java.io.Serializable;

/**
 * Created by hp on 03-03-2016.
 */
public class RequestMoneyList implements Serializable {

    private String amount;
    private String mobilenumber;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

}
