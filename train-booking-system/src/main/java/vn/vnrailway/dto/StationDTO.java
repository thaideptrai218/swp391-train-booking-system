package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationDTO {
    private int stationID;
    private String stationCode;
    private String stationName;

    public static void main(String[] args) {
        // Create some sample StationDTO objects
        StationDTO station1 = new StationDTO(1, "SGO", "Sài Gòn");
        StationDTO station2 = new StationDTO(2, "HNO", "Hà Nội");
        StationDTO station3 = new StationDTO(3, "DNA", "Đà Nẵng");

        // Print the station DTOs
        System.out.println("Station 1: " + station1);
        System.out.println("Station 2: " + station2);
        System.out.println("Station 3: " + station3);

        // Example of accessing individual fields
        System.out.println("\nDetails of Station 1:");
        System.out.println("ID: " + station1.getStationID());
        System.out.println("Code: " + station1.getStationCode());
        System.out.println("Name: " + station1.getStationName());
    }
}
