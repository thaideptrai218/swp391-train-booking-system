package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private int locationID;
    private String locationCode; // Added field
    private String locationName;
    private String city;
    private String region;
    private String link;
}
