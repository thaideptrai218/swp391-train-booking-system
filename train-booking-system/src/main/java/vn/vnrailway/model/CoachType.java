package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachType {
    private int coachTypeID;
    private String typeName; // Loại toa: giường nằm K4 ĐH, ghế mềm ĐH...
    private BigDecimal priceMultiplier;
    private String description; // Có thể null
    private boolean isCompartmented;
    private Integer defaultCompartmentCapacity; // e.g., 4 or 6 for sleepers, null if not compartmented
}
