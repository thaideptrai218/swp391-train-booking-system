package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatTypePricingDTO {
    private int seatTypeID;
    private String seatTypeName;
    private String coachTypeName;
    private String description;
    private BigDecimal pricePerSeat;
    private int availableSeats;
    private int totalSeats;
    private boolean hasAvailableSeats;
}