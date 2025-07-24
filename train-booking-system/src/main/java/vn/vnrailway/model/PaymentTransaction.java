package vn.vnrailway.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date; // Using java.util.Date for DATETIME2 mapping

public class PaymentTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private int transactionID;
    private int bookingID; // Corresponds to Booking.BookingID
    private String paymentGatewayTransactionID; // ID from the payment gateway
    private String paymentGateway; // e.g., "VNPay", "PayPal", "Stripe"
    private BigDecimal amount;
    private Date transactionDateTime; // SQL DATETIME2 maps well to java.util.Date or Timestamp
    private String status; // e.g., "Pending", "Success", "Failed", "Cancelled"
    private String notes;

    // Constructors
    public PaymentTransaction() {
    }

    // Getters and Setters
    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public String getPaymentGatewayTransactionID() {
        return paymentGatewayTransactionID;
    }

    public void setPaymentGatewayTransactionID(String paymentGatewayTransactionID) {
        this.paymentGatewayTransactionID = paymentGatewayTransactionID;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(Date transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "transactionID=" + transactionID +
                ", bookingID=" + bookingID +
                ", paymentGatewayTransactionID='" + paymentGatewayTransactionID + '\'' +
                ", paymentGateway='" + paymentGateway + '\'' +
                ", amount=" + amount +
                ", transactionDateTime=" + transactionDateTime +
                ", status='" + status + '\'' +
                ", notes='" + (notes != null ? notes.substring(0, Math.min(notes.length(), 50)) + "..." : "null") + '\''
                +
                '}';
    }
}
