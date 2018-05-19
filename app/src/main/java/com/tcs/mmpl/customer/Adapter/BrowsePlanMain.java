package com.tcs.mmpl.customer.Adapter;

import java.util.ArrayList;

/**
 * Created by hp on 11-11-2015.
 */
public class BrowsePlanMain {


    String seg_name;
    ArrayList<BrowsePlanChild> arrayList;


    public ArrayList<BrowsePlanChild> getPackDetails() {
        return arrayList;
    }
    public void setPackDetails(ArrayList<BrowsePlanChild> arrayList) {
        this.arrayList = arrayList;
    }
    public String getSegName() {
        return seg_name;
    }
    public void setSegName(String seg_name) {
        this.seg_name = seg_name;
    }

}
