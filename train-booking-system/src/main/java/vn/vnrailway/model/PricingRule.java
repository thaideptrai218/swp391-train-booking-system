package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate; // Hoặc java.sql.Date

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingRule {
    private int ruleID;
    private String ruleName;
    private Integer trainTypeID; // FK, có thể null
    private Integer coachTypeID; // FK, có thể null
    private Integer seatTypeID;  // FK, có thể null
    private Integer passengerTypeID; // FK, có thể null
    private Integer routeID;     // FK, có thể null
    private Integer tripID;      // FK, có thể null
    private BigDecimal basePricePerKm; // Có thể null
    private BigDecimal fixedPrice;     // Có thể null
    private BigDecimal holidaySurchargePercentage;
    private Integer bookingTimeWindowHoursBeforeDepartureMin; // Có thể null
    private Integer bookingTimeWindowHoursBeforeDepartureMax; // Có thể null
    private boolean isForRoundTrip;
    private int priority;
    private LocalDate effectiveFromDate;
    private LocalDate effectiveToDate; // Có thể null
    private boolean isActive;
}
