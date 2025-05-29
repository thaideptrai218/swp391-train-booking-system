package vn.vnrailway.model;

public class Location {
    private int locationID;
    private String locationName;
    private String link;

    public Location() {
    }

    public Location(int locationID, String locationName, String link) {
        this.locationID = locationID;
        this.locationName = locationName;
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
                ", link='" + link + '\'' +
                '}';
    }
}
