package com.tcs.mmpl.customer.Adapter;

import com.tcs.mmpl.customer.R;

/**
 * Created by Admin on 10/1/2015.
 */
public class FavoritePojo {


    String personname;
    String operatorname;
    Integer img;
    String amount;
    String type;


    private boolean selected= false;

    public FavoritePojo() {
        selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public String getFavID() {
        return favID;
    }

    public void setFavID(String favID) {
        this.favID = favID;
    }

    String favID;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;

    public FavoritePojo(String personname, String operatorname,String url,String amount,String type,String favID) {
        this.personname = personname;
        this.operatorname = operatorname;
        this.img= R.drawable.ic_info;
        this.url = url;
        this.amount = amount;
        this.type = type;

        this.favID = favID;
    }

    public String getPersonname() {
        return personname;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }

    public String getOperatorname() {
        return operatorname;
    }

    public void setOperatorname(String operatorname) {
        this.operatorname = operatorname;
    }

    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }
}
