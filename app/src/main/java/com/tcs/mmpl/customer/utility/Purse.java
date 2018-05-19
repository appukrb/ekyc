package com.tcs.mmpl.customer.utility;

import java.io.Serializable;

/**
 * Created by hp on 21-06-2016.
 */
public class Purse implements Serializable {

    private String purseImage;
    private String purseName;
    private String purseBalance;
    private String purseTextColor;
    private String purseBackgroundColor;

    public String getPurseImage() {
        return purseImage;
    }

    public void setPurseImage(String purseImage) {
        this.purseImage = purseImage;
    }

    public String getPurseName() {
        return purseName;
    }

    public void setPurseName(String purseName) {
        this.purseName = purseName;
    }

    public String getPurseBalance() {
        return purseBalance;
    }

    public void setPurseBalance(String purseBalance) {
        this.purseBalance = purseBalance;
    }

    public String getPurseTextColor() {
        return purseTextColor;
    }

    public void setPurseTextColor(String purseTextColor) {
        this.purseTextColor = purseTextColor;
    }

    public String getPurseBackgroundColor() {
        return purseBackgroundColor;
    }

    public void setPurseBackgroundColor(String purseBackgroundColor) {
        this.purseBackgroundColor = purseBackgroundColor;
    }
}
