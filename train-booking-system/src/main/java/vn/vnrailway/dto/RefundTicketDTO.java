
package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundTicketDTO {
     // Thông tin hành khách
    private String passengerFullName;
    private String passengerIDCard;
    private String ticketCode;
    private int ticketID;
    private String passengerType;
    private String seatTypeName;
    private String seatNumber;
    private String coachName;
    private String trainName;
    private String scheduledDepartureTime;
    private String ticketStatus;
    private double price;
    private String startStationName;
    private String endStationName;
    private int policyID;
    private String noteSTK;

    // Thông tin hoàn vé
    private int hoursBeforeDeparture;
    private boolean isRefundable;
    private String refundPolicy;
    private double refundFee;
    private double refundAmount;
    private String refundStatus;
}