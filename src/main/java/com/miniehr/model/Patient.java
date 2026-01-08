package com.miniehr.model;


import java.time.LocalDate;

public class Patient {
    private Integer patientId;
    private String healthCardNo;
    private String fullName;
    private LocalDate dateOfBirth;
    private String sex;
    private String phone;
    private String address;

    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }

    public String getHealthCardNo() { return healthCardNo; }
    public void setHealthCardNo(String healthCardNo) { this.healthCardNo = healthCardNo; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
