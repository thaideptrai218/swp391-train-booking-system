package vn.vnrailway.dto.payment;

import java.io.Serializable;

public class CustomerDetailsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String idCardNumber;

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    @Override
    public String toString() {
        return "CustomerDetailsDto{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", idCardNumber='" + idCardNumber + '\'' +
                '}';
    }
}
