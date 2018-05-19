package com.tcs.mmpl.customer.Goibibo.Bus.Pojo;

import java.io.Serializable;

/**
 * Created by hp on 2016-08-02.
 */
public class BusBoardingPoint implements Serializable {
    private String BPId;
    private String BPLocation;
    private String BPTime;
    private String BPName;

    public String getBPId() {
        return BPId;
    }

    public void setBPId(String BPId) {
        this.BPId = BPId;
    }

    public String getBPLocation() {
        return BPLocation;
    }

    public void setBPLocation(String BPLocation) {
        this.BPLocation = BPLocation;
    }

    public String getBPTime() {
        return BPTime;
    }

    public void setBPTime(String BPTime) {
        this.BPTime = BPTime;
    }

    public String getBPName() {
        return BPName;
    }

    public void setBPName(String BPName) {
        this.BPName = BPName;
    }
}
