package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatUIDetailDTO {
    private int seatID; // Matches SP output SeatID
    private String seatName;
    private int seatNumberInCoach; // Matches SP output SeatNumberInCoach
    private int seatTypeID; // Matches SP output SeatTypeID
    private String seatTypeName;
    private String berthLevel; // Assuming SP returns it as String, adjust if int
    private double seatPriceMultiplier;
    private double coachPriceMultiplier;
    private double tripBasePriceMultiplier;
    private boolean isEnabled; // Matches SP output IsEnabled (bit becomes boolean)
    private String availabilityStatus; // "Available", "Occupied", "Disabled"
    private Double calculatedPrice; // Matches SP output CalculatedPrice (use Double for nullable numeric)
    
    // Lombok will generate getters, setters, constructors, etc.
}
