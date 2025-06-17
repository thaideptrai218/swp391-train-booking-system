package vn.vnrailway.dto;

public class StationPopularityDTO {
    private String stationName;
    private long count; // Could be number of departures, arrivals, or total tickets involving this
                        // station

    public StationPopularityDTO() {
    }

    public StationPopularityDTO(String stationName, long count) {
        this.stationName = stationName;
        this.count = count;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "StationPopularityDTO{" +
                "stationName='" + stationName + '\'' +
                ", count=" + count +
                '}';
    }
}
