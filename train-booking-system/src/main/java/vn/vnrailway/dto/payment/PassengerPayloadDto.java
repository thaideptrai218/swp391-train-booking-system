package vn.vnrailway.dto.payment;

import java.io.Serializable;

public class PassengerPayloadDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private OriginalCartItemDataDto originalCartItem;
    private String fullName;
    private String passengerTypeID; // Assuming String from payload, can be int
    private String idCardNumber;
    private String dateOfBirth; // Store as String, parse to Date if needed

    // Getters and Setters
    public OriginalCartItemDataDto getOriginalCartItem() {
        return originalCartItem;
    }

    public void setOriginalCartItem(OriginalCartItemDataDto originalCartItem) {
        this.originalCartItem = originalCartItem;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassengerTypeID() {
        return passengerTypeID;
    }

    public void setPassengerTypeID(String passengerTypeID) {
        this.passengerTypeID = passengerTypeID;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "PassengerPayloadDto{" +
                "originalCartItem=" + (originalCartItem != null ? originalCartItem.getSeatName() : "null") + // Example
                ", fullName='" + fullName + '\'' +
                ", passengerTypeID='" + passengerTypeID + '\'' +
                ", idCardNumber='" + idCardNumber + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                '}';
    }
}
