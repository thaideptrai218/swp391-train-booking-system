package vn.vnrailway.dto;

public class BestSellerLocationDTO {
    private String locationName;
    private long ticketsSold;

    public BestSellerLocationDTO() {
    }

    public BestSellerLocationDTO(String locationName, long ticketsSold) {
        this.locationName = locationName;
        this.ticketsSold = ticketsSold;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public long getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(long ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    @Override
    public String toString() {
        return "BestSellerLocationDTO{" +
                "locationName='" + locationName + '\'' +
                ", ticketsSold=" + ticketsSold +
                '}';
    }
}
