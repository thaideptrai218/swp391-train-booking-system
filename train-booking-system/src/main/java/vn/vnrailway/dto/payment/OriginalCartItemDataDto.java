package vn.vnrailway.dto.payment;

import java.io.Serializable;

public class OriginalCartItemDataDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int seatID;
    private String seatName;
    private int seatNumberInCoach;
    private int seatTypeID;
    private String seatTypeName;
    private String berthLevel;
    private double seatPriceMultiplier;
    private double coachPriceMultiplier;
    private double tripBasePriceMultiplier;
    private String availabilityStatus;
    private double calculatedPrice;
    private boolean enabled;
    private String tripId; // Assuming tripId is String as per payload, can be int if needed
    private String coachId; // Assuming coachId is String, can be int
    private String legOriginStationId; // Assuming String, can be int
    private String legDestinationStationId; // Assuming String, can be int
    private String tripLeg;
    private String coachPosition;
    private String trainName;
    private String originStationName;
    private String destinationStationName;
    private String scheduledDepartureDisplay;
    private String holdExpiresAt; // Store as String, parse to Date if needed

    // Getters and Setters

    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public int getSeatNumberInCoach() {
        return seatNumberInCoach;
    }

    public void setSeatNumberInCoach(int seatNumberInCoach) {
        this.seatNumberInCoach = seatNumberInCoach;
    }

    public int getSeatTypeID() {
        return seatTypeID;
    }

    public void setSeatTypeID(int seatTypeID) {
        this.seatTypeID = seatTypeID;
    }

    public String getSeatTypeName() {
        return seatTypeName;
    }

    public void setSeatTypeName(String seatTypeName) {
        this.seatTypeName = seatTypeName;
    }

    public String getBerthLevel() {
        return berthLevel;
    }

    public void setBerthLevel(String berthLevel) {
        this.berthLevel = berthLevel;
    }

    public double getSeatPriceMultiplier() {
        return seatPriceMultiplier;
    }

    public void setSeatPriceMultiplier(double seatPriceMultiplier) {
        this.seatPriceMultiplier = seatPriceMultiplier;
    }

    public double getCoachPriceMultiplier() {
        return coachPriceMultiplier;
    }

    public void setCoachPriceMultiplier(double coachPriceMultiplier) {
        this.coachPriceMultiplier = coachPriceMultiplier;
    }

    public double getTripBasePriceMultiplier() {
        return tripBasePriceMultiplier;
    }

    public void setTripBasePriceMultiplier(double tripBasePriceMultiplier) {
        this.tripBasePriceMultiplier = tripBasePriceMultiplier;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public double getCalculatedPrice() {
        return calculatedPrice;
    }

    public void setCalculatedPrice(double calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getCoachId() {
        return coachId;
    }

    public void setCoachId(String coachId) {
        this.coachId = coachId;
    }

    public String getLegOriginStationId() {
        return legOriginStationId;
    }

    public void setLegOriginStationId(String legOriginStationId) {
        this.legOriginStationId = legOriginStationId;
    }

    public String getLegDestinationStationId() {
        return legDestinationStationId;
    }

    public void setLegDestinationStationId(String legDestinationStationId) {
        this.legDestinationStationId = legDestinationStationId;
    }

    public String getTripLeg() {
        return tripLeg;
    }

    public void setTripLeg(String tripLeg) {
        this.tripLeg = tripLeg;
    }

    public String getCoachPosition() {
        return coachPosition;
    }

    public void setCoachPosition(String coachPosition) {
        this.coachPosition = coachPosition;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getOriginStationName() {
        return originStationName;
    }

    public void setOriginStationName(String originStationName) {
        this.originStationName = originStationName;
    }

    public String getDestinationStationName() {
        return destinationStationName;
    }

    public void setDestinationStationName(String destinationStationName) {
        this.destinationStationName = destinationStationName;
    }

    public String getScheduledDepartureDisplay() {
        return scheduledDepartureDisplay;
    }

    public void setScheduledDepartureDisplay(String scheduledDepartureDisplay) {
        this.scheduledDepartureDisplay = scheduledDepartureDisplay;
    }

    public String getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public void setHoldExpiresAt(String holdExpiresAt) {
        this.holdExpiresAt = holdExpiresAt;
    }

    @Override
    public String toString() {
        return "OriginalCartItemDataDto{" +
                "seatID=" + seatID +
                ", seatName='" + seatName + '\'' +
                // ... include other fields for completeness if desired
                '}';
    }
}
