package com.tshami.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; // Hoặc java.sql.Timestamp

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripStation {
    private int tripStationID;
    private int tripID; // FK
    private int stationID; // FK
    // private Trip trip; // Object
    // private Station station; // Object
    private int sequenceNumber;
    private LocalDateTime scheduledArrival; // Có thể null
    private LocalDateTime scheduledDeparture; // Có thể null
    private LocalDateTime actualArrival; // Có thể null
    private LocalDateTime actualDeparture; // Có thể null
}
