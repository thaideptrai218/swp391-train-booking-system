package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripStationInfoDTO {
    private int tripID;
    private int stationId; // Added for identifying the station to update
    private String stationName;
    private BigDecimal distanceFromStart; // Assuming DistanceFromStart can have decimal values
    private java.time.LocalDate scheduledDepartureDate;
    private java.time.LocalTime scheduledDepartureTime;
    // private LocalDateTime scheduledArrival; // Removed as per new query

    private BigDecimal estimateTime; // Estimated travel time from the start of the route to this station
    private Integer defaultStopTime; // Default stop time at this station in minutes
}
