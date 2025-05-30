package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachInfoDTO {
    private int coachId;             // From Coach.coachID
    private int positionInTrain;     // From Coach.positionInTrain (for "Toa X" label)
    private String coachTypeName;    // From CoachType.typeName (e.g., "Ghế mềm ĐH")
    private String coachTypeDescription; // From CoachType.description
    private int capacity;            // From Coach.capacity
    // Optional: private String coachSpecificName; // From Coach.coachName, if distinct and needed for description
}
