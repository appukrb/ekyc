package com.tcs.mmpl.customer.utility;

import java.io.Serializable;

/**
 * Created by hp on 2016-11-22.
 */
public class IssueCategory implements Serializable {

    private String category_name;
    private String category_note;
    private String category_image;
    private String category_html;

    public String getCategory_html() {
        return category_html;
    }

    public void setCategory_html(String category_html) {
        this.category_html = category_html;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_note() {
        return category_note;
    }

    public void setCategory_note(String category_note) {
        this.category_note = category_note;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }
}
