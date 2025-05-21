package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSearchResultDTO {
    // Trip Information
    private int tripID;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private String tripStatus;
    private boolean isHolidayTrip;

    // Train Information
    private int trainID;
    private String trainName;
    // private String trainTypeName; // If needed from TrainType table

    // Route Information
    private int routeID;
    private String routeName;

    // Origin Station Information (example, specific to the search query)
    private int originStationID;
    private String originStationName;
    private String originStationCode;
    private LocalDateTime scheduledDepartureFromOrigin; // From TripStations

    // Destination Station Information (example, specific to the search query)
    private int destinationStationID;
    private String destinationStationName;
    private String destinationStationCode;
    private LocalDateTime scheduledArrivalAtDestination; // From TripStations
    
    // Pricing Information (example, could be base price or calculated)
    private BigDecimal estimatedPrice; // This might be calculated later or be a base price indicator

    // Duration (calculated, can be added in service layer or here if fetched directly)
    // private long durationInMinutes; 
    
    // Add any other fields that are commonly displayed in search results.
    // For example, number of available seats of different types, etc.
    // This DTO can evolve as search requirements become clearer.
}
