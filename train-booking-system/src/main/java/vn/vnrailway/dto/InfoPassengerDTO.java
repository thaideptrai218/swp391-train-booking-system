package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoPassengerDTO {
    private String PassengerFullName;
    private String PassengerIDCard;
    private String PassengerType;
    private String SeatTypeName;
    private String SeatNumber;
    private String CoachName;
    private String TrainName;
    private String TicketStatus;
    private double Price;
    private String StartStationName;
    private String EndStationName;
}
