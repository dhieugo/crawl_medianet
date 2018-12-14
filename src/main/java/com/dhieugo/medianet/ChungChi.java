package com.dhieugo.medianet;

import java.util.List;

public class ChungChi {

    private String siteId;
    private int internalSiteId; // hidden id
    private String fullname;
    private String nation;
    private String licenseNumber;
    private String noicap;
    private String issueDate;
    private String scope;
    private String status;
    private List<DonviCongTac> donviCongTac;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public int getInternalSiteId() {
        return internalSiteId;
    }

    public void setInternalSiteId(int internalSiteId) {
        this.internalSiteId = internalSiteId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DonviCongTac> getDonviCongTac() {
        return donviCongTac;
    }

    public void setDonviCongTac(List<DonviCongTac> donviCongTac) {
        this.donviCongTac = donviCongTac;
    }

    public String getNoicap() {
        return noicap;
    }

    public void setNoicap(String noicap) {
        this.noicap = noicap;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return "ChungChi{" +
                "siteId='" + siteId + '\'' +
                ", internalSiteId=" + internalSiteId +
                ", fullname='" + fullname + '\'' +
                ", nation='" + nation + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", noicap='" + noicap + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", scope='" + scope + '\'' +
                ", status='" + status + '\'' +
                ", donviCongTac=" + donviCongTac +
                '}';
    }
}
