package com.tcs.mmpl.customer.Adapter;

/**
 * Created by hp on 25-02-2016.
 */
public class MerchantNames {

    private String merchantName;
    private String merchantNumber;
    public MerchantNames(String merchantName,String merchantNumber)
    {
        this.merchantName = merchantName;
        this.merchantNumber = merchantNumber;
    }
    public String getMerchantName() {
        return this.merchantName;
    }

    public String getMerchantNumber() {
        return this.merchantNumber;
    }


}
