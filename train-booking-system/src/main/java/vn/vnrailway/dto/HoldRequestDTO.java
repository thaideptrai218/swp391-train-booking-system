package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoldRequestDTO {
    private int tripId;
    private int seatId;
    private int coachId; // Important for context, e.g., if SeatIDs are not globally unique
    private int legOriginStationId;
    private int legDestinationStationId;
}
