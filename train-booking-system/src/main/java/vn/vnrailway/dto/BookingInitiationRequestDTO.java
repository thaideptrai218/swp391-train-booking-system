package vn.vnrailway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingInitiationRequestDTO {
    private List<SeatToBookDTO> seatsToBook;
    // You could add other top-level information here if needed for the booking initiation,
    // e.g., promo codes, overall booking notes, etc. For now, just the seats.
}
