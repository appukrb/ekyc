package com.tcs.mmpl.customer.Goibibo.Bus.Pojo;

import java.io.Serializable;

/**
 * Created by hp on 2016-08-05.
 */
public class BusTraveller implements Serializable {

    private String title;
    private String firstname;
    private String middlename;
    private String lastname="";
    private String age;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
