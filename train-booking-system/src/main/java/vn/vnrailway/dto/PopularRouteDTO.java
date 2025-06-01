package vn.vnrailway.dto;

public class PopularRouteDTO {
    private String originName;
    private String destinationName;
    private String backgroundImageUrl;
    private String tripsPerDay;
    private String distance;
    private String popularTrainNames;
    private String originLocationId; // To construct image URL or for other purposes

    public PopularRouteDTO() {
    }

    public PopularRouteDTO(String originName, String destinationName, String backgroundImageUrl, String tripsPerDay,
            String distance, String popularTrainNames, String originLocationId) {
        this.originName = originName;
        this.destinationName = destinationName;
        this.backgroundImageUrl = backgroundImageUrl;
        this.tripsPerDay = tripsPerDay;
        this.distance = distance;
        this.popularTrainNames = popularTrainNames;
        this.originLocationId = originLocationId;
    }

    // Getters and Setters
    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getTripsPerDay() {
        return tripsPerDay;
    }

    public void setTripsPerDay(String tripsPerDay) {
        this.tripsPerDay = tripsPerDay;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPopularTrainNames() {
        return popularTrainNames;
    }

    public void setPopularTrainNames(String popularTrainNames) {
        this.popularTrainNames = popularTrainNames;
    }

    public String getOriginLocationId() {
        return originLocationId;
    }

    public void setOriginLocationId(String originLocationId) {
        this.originLocationId = originLocationId;
    }

    @Override
    public String toString() {
        return "PopularRouteDTO{" +
                "originName='" + originName + '\'' +
                ", destinationName='" + destinationName + '\'' +
                ", backgroundImageUrl='" + backgroundImageUrl + '\'' +
                ", tripsPerDay='" + tripsPerDay + '\'' +
                ", distance='" + distance + '\'' +
                ", popularTrainNames='" + popularTrainNames + '\'' +
                ", originLocationId='" + originLocationId + '\'' +
                '}';
    }
}
