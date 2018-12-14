package com.dhieugo.medianet;

import java.util.List;

public class MediaOffice {

    private int internalSiteId; // hidden id
    private String hiddenField2;
    private String siteId; // this is the id in the website
    private String name;
    private String licenseNumber; // Số giấy phép
    private String address;
    private String issueDate;
    private String status; // Tình trạng
    private String type; // Hình thức tổ chức
    /**
     * Expand 	Nội tổng hợp
     * Expand 	Chuyên khoa Ngoại
     * Expand 	Chuyên khoa Sản phụ khoa
     * Expand Chuyên khoa thuộc hệ nội	Chuyên khoa thuộc hệ nội
     * Expand Chẩn đoán hình ảnh	Chẩn đoán hình ảnh
     * Expand Xét nghiệm	Xét nghiệm
     */
    private List<String> scopes; // Danh sách phạm vi hoạt động đã đăng ký

    private ExpertPerson expertPerson;

    public int getInternalSiteId() {
        return internalSiteId;
    }

    public String getHiddenField2() {
        return hiddenField2;
    }

    public void setHiddenField2(String hiddenField2) {
        this.hiddenField2 = hiddenField2;
    }

    public void setInternalSiteId(int internalSiteId) {
        this.internalSiteId = internalSiteId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public ExpertPerson getExpertPerson() {
        return expertPerson;
    }

    public void setExpertPerson(ExpertPerson expertPerson) {
        this.expertPerson = expertPerson;
    }

    @Override
    public String toString() {
        return "MediaOffice{" +
                "internalSiteId='" + internalSiteId + '\'' +
                ", siteId='" + siteId + '\'' +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", address='" + address + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", scopes=" + scopes +
                ", expertPerson=" + expertPerson +
                '}';
    }
}
