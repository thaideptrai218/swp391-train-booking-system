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
    // private String trainName; // No longer populated by findAllForManagerView
    private String routeName; // From Route table
    // private LocalDateTime departureDateTime; // No longer populated by
    // findAllForManagerView
    // private LocalDateTime arrivalDateTime; // No longer populated by
    // findAllForManagerView
    private boolean isHolidayTrip;
    private String tripStatus;
    private BigDecimal basePriceMultiplier;

    // Add trainID and routeID if they are needed for edit/delete links/actions
    // directly on this view
    // private int trainID; // No longer populated by findAllForManagerView
    private int routeID;
}
