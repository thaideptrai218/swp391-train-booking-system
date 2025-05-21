package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStation {
    private int routeStationID;
    private int routeID; // FK
    private int stationID; // FK
    // private Route route; // Object
    // private Station station; // Object
    private int sequenceNumber;
    private BigDecimal distanceFromStart;
    private int defaultStopTime; // (minutes)
}
