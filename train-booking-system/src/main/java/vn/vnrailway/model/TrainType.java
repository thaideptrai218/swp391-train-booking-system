package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainType {
    private int trainTypeID;
    private String typeName;
    private String description; // Có thể null
    private BigDecimal averageVelocity;
}
