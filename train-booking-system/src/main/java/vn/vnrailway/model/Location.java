package vn.vnrailway.model;

public class Location {
    private int locationID;
    private String locationName;
    private String city;
    private String region;
    private String link;

    public Location() {
    }

    public Location(int locationID, String locationName, String city, String region, String link) {
        this.locationID = locationID;
        this.locationName = locationName;
        this.city = city;
        this.region = region;
        this.link = link;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationID=" + locationID +
                ", locationName='" + locationName + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
