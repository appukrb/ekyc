package com.tcs.mmpl.customer.Adapter;

import java.io.Serializable;

/**
 * Created by hp on 2016-06-29.
 */
public class MerchantImageAndName implements Serializable {
    private String image;
    private String categoryName;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
