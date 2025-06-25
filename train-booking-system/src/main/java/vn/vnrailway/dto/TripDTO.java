package vn.vnrailway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.vnrailway.model.Trip;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    private Trip trip;
    private String tripName;
}
