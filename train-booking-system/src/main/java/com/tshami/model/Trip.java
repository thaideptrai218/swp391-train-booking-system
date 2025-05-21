package com.tshami.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; // Hoặc java.sql.Timestamp
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    private int tripID;
    private int trainID; // FK
    // private Train train; // Object
    private int routeID; // FK
    // private Route route; // Object
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private boolean isHolidayTrip;
    private String tripStatus; // "Scheduled", "In Progress", etc. (Có thể dùng Enum)
    private BigDecimal basePriceMultiplier;
}
