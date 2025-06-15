package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManageTripViewDTO {
    private int tripID;
    private String trainName; // Or trainCode, depending on what's more suitable from Train table
    private String routeName; // From Route table
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private boolean isHolidayTrip;
    private String tripStatus;
    // private BigDecimal basePriceMultiplier; // Removed

    // Add trainID and routeID if they are needed for edit/delete links/actions
    // directly on this view
    private int trainID;
    private int routeID;
}
