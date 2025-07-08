package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckBookingDTO {
    private String bookingCode;
    private String userFullName;
    private String userEmail;
    private String userIDCardNumber;
    private String userPhoneNumber;

    private List<InfoPassengerDTO> infoPassengers; // List of passengers' info
}
