package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeaturedRoute {
    private int featuredRouteID; // PK for FeaturedRoutes table
    private int routeID; // FK to Routes table
    private int originStationID; // FK to Stations table
    private int destinationStationID; // FK to Stations table
    private String displayName; // e.g., "Ha Noi to Sai Gon"
    // backgroundImageUrl, tripsPerDay, distance, popularTrainNames are NOT in the
    // DB table.
}
