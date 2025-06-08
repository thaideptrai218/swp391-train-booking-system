package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatToBookDTO {
    private int tripId;
    private int seatId;
    private int coachId; // Good to have for context or if server needs to re-verify coach details
    private int legOriginStationId;
    private int legDestinationStationId;
    // You might also include other details if the server needs to verify them,
    // e.g., the calculatedPrice at the time of selection, though the server should re-calculate authoritatively.
    // private double priceAtSelection; 
}
