package vn.vnrailway.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TripPopularityDTO {
    private int tripId;
    private String routeName; // e.g., "Hanoi - Da Nang"
    private String trainName; // e.g., "SE1"
    private LocalDateTime departureDateTime;
    private long bookingCount;

    public TripPopularityDTO() {
    }

    public TripPopularityDTO(int tripId, String routeName, String trainName, LocalDateTime departureDateTime,
            long bookingCount) {
        this.tripId = tripId;
        this.routeName = routeName;
        this.trainName = trainName;
        this.departureDateTime = departureDateTime;
        this.bookingCount = bookingCount;
    }

    // Getters and Setters
    public Date getDepartureDateTimeAsDate() {
        if (this.departureDateTime == null) {
            return null;
        }
        return Date.from(this.departureDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public long getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(long bookingCount) {
        this.bookingCount = bookingCount;
    }

    @Override
    public String toString() {
        return "TripPopularityDTO{" +
                "tripId=" + tripId +
                ", routeName='" + routeName + '\'' +
                ", trainName='" + trainName + '\'' +
                ", departureDateTime=" + departureDateTime +
                ", bookingCount=" + bookingCount +
                '}';
    }
}
