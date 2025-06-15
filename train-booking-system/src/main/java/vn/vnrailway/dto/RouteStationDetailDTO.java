package vn.vnrailway.dto;

import java.math.BigDecimal;

public class RouteStationDetailDTO {
    private int routeID;
    private int stationID;
    private String routeName;
    private String stationName;
    private int sequenceNumber;
    private BigDecimal distanceFromStart; // Using BigDecimal for precision
    private int defaultStopTime; // Assuming this is in minutes
    private String description;

    // Constructors
    public RouteStationDetailDTO() {
    }

    public RouteStationDetailDTO(int routeID, int stationID, String routeName, String stationName, int sequenceNumber,
            BigDecimal distanceFromStart, int defaultStopTime, String description) {
        this.routeID = routeID;
        this.stationID = stationID;
        this.routeName = routeName;
        this.stationName = stationName;
        this.sequenceNumber = sequenceNumber;
        this.distanceFromStart = distanceFromStart;
        this.defaultStopTime = defaultStopTime;
        this.description = description;
    }

    // Getters and Setters
    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public BigDecimal getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(BigDecimal distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    public int getDefaultStopTime() {
        return defaultStopTime;
    }

    public void setDefaultStopTime(int defaultStopTime) {
        this.defaultStopTime = defaultStopTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RouteStationDetailDTO{" +
                "routeID=" + routeID +
                ", stationID=" + stationID +
                ", routeName='" + routeName + '\'' +
                ", stationName='" + stationName + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", distanceFromStart=" + distanceFromStart +
                ", defaultStopTime=" + defaultStopTime +
                ", description='" + description + '\'' +
                '}';
    }
}
