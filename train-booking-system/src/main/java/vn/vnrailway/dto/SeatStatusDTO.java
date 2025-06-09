package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatStatusDTO {
    private int seatID;
    private String seatName; // e.g., "A1", "12", "Upper 5" - for display
    private int seatNumberInCoach;  // Physical sequential number in coach
    private int seatTypeID;
    private String seatTypeName;
    private String berthLevel; // e.g., "Upper", "Lower", NULL for non-berth seats
    private BigDecimal seatPriceMultiplier;
    private BigDecimal coachPriceMultiplier;
    private BigDecimal tripBasePriceMultiplier;
    private boolean isEnabled; // From S.IsEnabled in DB (Seats table)
    private String availabilityStatus; // "Available", "Occupied", "Disabled" - determined by SP
    private BigDecimal calculatedPrice; // Added for displaying the final price
}
