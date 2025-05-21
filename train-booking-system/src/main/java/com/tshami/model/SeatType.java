package com.tshami.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatType {
    private int seatTypeID;
    private String typeName; // Loại ghế/giường: Ghế mềm, giường tầng 1 K4...
    private BigDecimal priceMultiplier;
    private String description; // Có thể null
}
