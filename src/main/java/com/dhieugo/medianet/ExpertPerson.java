package com.dhieugo.medianet;

import java.util.List;

public class ExpertPerson {
    private String fullname;
    private String dob;
    private String gender;
    private String nation;
    private String licenseNumber;
    private String scope;
    private String degree;
    private List<String> workHistories;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public List<String> getWorkHistories() {
        return workHistories;
    }

    public void setWorkHistories(List<String> workHistories) {
        this.workHistories = workHistories;
    }

    @Override
    public String toString() {
        return "ExpertPerson{" +
                "fullname='" + fullname + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", nation='" + nation + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", scope='" + scope + '\'' +
                ", degree='" + degree + '\'' +
                ", workHistories=" + workHistories +
                '}';
    }
}
