package com.tcs.mmpl.customer.utility;

import java.io.Serializable;

/**
 * Created by hp on 2016-12-01.
 */
public class KycList implements Serializable {

    private String kycStatus;
    private String emailID;
    private String kycID;
    private String dob;
    private String firstName;
    private String lastName;
    private String pincode;
    private String motherMaiden;
    private String profileStatus;
    private String poaStatus;
    private String poiStatus;

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getKycID() {
        return kycID;
    }

    public void setKycID(String kycID) {
        this.kycID = kycID;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getMotherMaiden() {
        return motherMaiden;
    }

    public void setMotherMaiden(String motherMaiden) {
        this.motherMaiden = motherMaiden;
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(String profileStatus) {
        this.profileStatus = profileStatus;
    }

    public String getPoaStatus() {
        return poaStatus;
    }

    public void setPoaStatus(String poaStatus) {
        this.poaStatus = poaStatus;
    }

    public String getPoiStatus() {
        return poiStatus;
    }

    public void setPoiStatus(String poiStatus) {
        this.poiStatus = poiStatus;
    }
}
