package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date; // Or java.sql.Timestamp

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporarySeatHold {

    private int holdId;
    private int tripId;
    private int seatId;
    private int coachId;
    private int legOriginStationId;    // Was LegStartStationID in your DDL, using Java naming convention
    private int legDestinationStationId; // Was LegEndStationID in your DDL
    private String sessionId;
    private Integer userId; // Integer for nullable
    private Date expiresAt; // Use java.util.Date or java.sql.Timestamp
    private Date createdAt; // Use java.util.Date or java.sql.Timestamp
}
