package com.tcs.mmpl.customer.utility;

import java.io.Serializable;

/**
 * Created by hp on 2016-11-17.
 */
public class Issue implements Serializable {


    private String issueId;
    private String issueCategory;
    private String issueContent;
    private String issueDate;
    private String issueStatus;
    private String issueSent="NA";
    private String issueImage = "NA";

    public String getIssueImage() {
        return issueImage;
    }

    public void setIssueImage(String issueImage) {
        this.issueImage = issueImage;
    }

    public String getIssueSent() {
        return issueSent;
    }

    public void setIssueSent(String issueSent) {
        this.issueSent = issueSent;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getIssueCategory() {
        return issueCategory;
    }

    public void setIssueCategory(String issueCategory) {
        this.issueCategory = issueCategory;
    }

    public String getIssueContent() {
        return issueContent;
    }

    public void setIssueContent(String issueContent) {
        this.issueContent = issueContent;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }
}
