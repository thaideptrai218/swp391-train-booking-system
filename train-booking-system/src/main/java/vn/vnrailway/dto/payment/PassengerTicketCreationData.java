package vn.vnrailway.dto.payment;

import java.io.Serializable;

public class PassengerTicketCreationData implements Serializable {
    private static final long serialVersionUID = 1L;

    private int passengerId; // The ID from dbo.Passengers table
    private OriginalCartItemDataDto originalCartItem;

    // Constructor
    public PassengerTicketCreationData(int passengerId, OriginalCartItemDataDto originalCartItem) {
        this.passengerId = passengerId;
        this.originalCartItem = originalCartItem;
    }

    // Getters
    public int getPassengerId() {
        return passengerId;
    }

    public OriginalCartItemDataDto getOriginalCartItem() {
        return originalCartItem;
    }

    // Setters - generally not needed for immutable session data, but can be added
    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public void setOriginalCartItem(OriginalCartItemDataDto originalCartItem) {
        this.originalCartItem = originalCartItem;
    }

    @Override
    public String toString() {
        return "PassengerTicketCreationData{" +
                "passengerId=" + passengerId +
                ", originalCartItemSeatName=" + (originalCartItem != null ? originalCartItem.getSeatName() : "null") +
                '}';
    }
}
