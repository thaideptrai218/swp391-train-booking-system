package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSearchResultDTO {
    // Fields directly from the SearchTrips Stored Procedure's SELECT statement
    private String legType;                     // E.g., "Outbound", "Return" - from SP: LegType
    private int tripId;                         // TripID from Trips table - from SP: TripID
    private String trainName;                   // TrainName from Trains table - from SP: TrainName
    private String routeName;                   // RouteName from Routes table - from SP: RouteName
    
    private String originStationName;           // Name of the origin station for this leg - from SP: OriginStation
    private LocalDateTime scheduledDeparture;   // Scheduled departure time from the origin station for this leg - from SP: DepartureTime
    
    private String destinationStationName;      // Name of the destination station for this leg - from SP: DestinationStation
    private LocalDateTime scheduledArrival;     // Scheduled arrival time at the destination station for this leg - from SP: ArrivalTime
    
    private int durationMinutes;                // Calculated duration of this leg in minutes - from SP: DurationMinutes

    private LocalDateTime tripOverallDepartureTime; // Overall departure time of the trip from its very first station - from SP: TripOverallDeparture
    private LocalDateTime tripOverallArrivalTime;   // Overall arrival time of the trip at its very last station - from SP: TripOverallArrival

    // Additional IDs for potential further operations or linking (can be populated if available from joins in SP)
    // These might not be directly in the final SELECT of the SP but are part of the joined tables.
    // If they are needed, the SP would have to be modified to select them, or they are populated by the service layer.
    // For now, assuming they might be populated by the service if the IDs are fetched separately or available.
    private int trainId;                        // TrainID from Trains table (TR.TrainID in SP)
    private int routeId;                        // RouteID from Routes table (R.RouteID in SP)
    private int originStationId;                // StationID of the origin station for this leg (TS_Origin.StationID in SP)
    private int destinationStationId;           // StationID of the destination station for this leg (TS_Dest.StationID in SP)
    
    // Other relevant information
    private String tripStatus;                  // Status of the trip (e.g., "Scheduled"), SP filters on T.TripStatus
}
