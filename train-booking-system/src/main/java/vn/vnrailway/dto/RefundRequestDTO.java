package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDTO {
    private int refundID;
    private String ticketCode;
    private int ticketID;
    private int userID;
    private int BookingID;
    private String passengerFullName;
    private String passengerIDCard;
    private String passengerType;
    private String seatType;
    private String seatNumber;
    private String coachName;
    private String trainName;
    private String startStation;
    private String endStation;
    private int policyID;
    private LocalDateTime scheduledDeparture;
    private double originalPrice;
    private double refundFee; // feeamount
    private double refundAmount;
    private String refundPolicy;
    private String ticketStatus;
    private String refundStatus;
    private LocalDateTime requestedAt;
    private String userFullName;
    private String email;
    private String userIDCard;
    private String phoneNumber;
    private String noteSTK; // Note for refund request, e.g., "Refund due to cancellation"
    private String image;
}
