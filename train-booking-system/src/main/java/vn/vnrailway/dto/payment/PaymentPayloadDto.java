package vn.vnrailway.dto.payment;

import java.io.Serializable;
import java.util.List;

public class PaymentPayloadDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private CustomerDetailsDto customerDetails;
    private List<PassengerPayloadDto> passengers;
    private String paymentMethod;
    private double totalAmount;

    // Getters and Setters
    public CustomerDetailsDto getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(CustomerDetailsDto customerDetails) {
        this.customerDetails = customerDetails;
    }

    public List<PassengerPayloadDto> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerPayloadDto> passengers) {
        this.passengers = passengers;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "PaymentPayloadDto{" +
                "customerDetails=" + customerDetails +
                ", passengersCount=" + (passengers != null ? passengers.size() : 0) +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
