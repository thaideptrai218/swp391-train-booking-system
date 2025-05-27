package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Station {
    private int stationID;
    private String stationCode;
    private String stationName;
    private String address; // Có thể null
    private String city; // Có thể null
    private String region; // Có thể null
    private String phoneNumber;
}
