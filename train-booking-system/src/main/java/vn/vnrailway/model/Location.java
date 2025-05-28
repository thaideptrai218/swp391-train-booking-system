package vn.vnrailway.model;

public class Location {
    private int locationID;
    private String locationName;
    private String imageName; // To store the name of the image file, e.g., "location1.jpg"

    public Location() {
    }

    public Location(int locationID, String locationName, String imageName) {
        this.locationID = locationID;
        this.locationName = locationName;
        this.imageName = imageName;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationID=" + locationID +
                ", locationName='" + locationName + '\'' +
                ", imageName='" + imageName + '\'' +
                '}';
    }
}
