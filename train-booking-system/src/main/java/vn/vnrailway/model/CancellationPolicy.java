package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; // Hoặc java.sql.Timestamp
import java.math.BigDecimal;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancellationPolicy {
    private int policyID;
    private String policyName;
    private int hoursBeforeDepartureMin;
    private Integer hoursBeforeDepartureMax;
    private BigDecimal feePercentage;
    private BigDecimal fixedFeeAmount;
    private boolean isRefundable;
    private String description;
    private boolean isActive;
    private Date effectiveFromDate;
}
