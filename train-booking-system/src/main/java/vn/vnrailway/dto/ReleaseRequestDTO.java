package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseRequestDTO {
    private int tripId;
    private int seatId;
    private int legOriginStationId;
    private int legDestinationStationId;
    // SessionID will be retrieved from the server-side HTTP session, not part of request body.
    // CoachID might not be strictly necessary for release if the other 4 IDs uniquely identify the hold.
}
