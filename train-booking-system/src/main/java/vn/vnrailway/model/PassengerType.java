package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerType {
    private int passengerTypeID;
    private String typeName;
    private BigDecimal discountPercentage;
    private String description; // Có thể null
    private boolean requiresDocument;
}
