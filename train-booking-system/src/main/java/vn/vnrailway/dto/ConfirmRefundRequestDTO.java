package vn.vnrailway.dto;

import java.time.LocalDateTime;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmRefundRequestDTO {
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
    private String scheduledDeparture;
    private double originalPrice;
    private double refundFee; // feeamount
    private double refundAmount;
    private String refundPolicy;
    private String ticketStatus;
    private String refundStatus;
    private String requestedAt;
    private String processedAt;
    private String notes;
    private String images;
    private String userFullName;
    private String userEmail;
    private String userIDCard;
    private String userPhoneNumber;
    private String staffFullName;
    private String staffEmail;
    private String staffIDCard;
    private String staffPhoneNumber;
}
