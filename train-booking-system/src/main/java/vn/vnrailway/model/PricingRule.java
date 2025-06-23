package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingRule {
    private int ruleID;
    private String ruleName;
    private String description;
    private Integer trainTypeID;
    private Integer routeID;
    private BigDecimal basePricePerKm;
    private boolean isForRoundTrip;
    private LocalDate applicableDateStart;
    private LocalDate applicableDateEnd;
    private LocalDate effectiveFromDate;
    private LocalDate effectiveToDate;
    private boolean isActive;
}
