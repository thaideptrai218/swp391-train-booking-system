package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private int ticketID;
    private String ticketCode;
    private int bookingID; // FK
    // private Booking booking; // Object
    private int tripID; // FK
    // private Trip trip; // Object
    private int seatID; // FK (đến ghế vật lý trong bảng Seats)
    // private Seat physicalSeat; // Object
    private int passengerID; // FK
    // private Passenger passengerProfile; // Object
    private int startStationID; // FK
    private int endStationID; // FK
    // private Station startStation; // Object
    // private Station endStation; // Object
    private BigDecimal price; // Giá cuối cùng
    private String ticketStatus; // "Valid", "Used", etc. (Có thể dùng Enum)

    // --- Snapshot fields ---
    private String coachNameSnapshot;
    private String seatNameSnapshot;
    private String passengerName; // Tên hành khách lúc mua vé
    private String passengerIDCardNumber; // Số CCCD/CMND lúc mua vé (có thể null)
    private String fareComponentDetails; // JSON/XML mô tả cách tính giá (có thể null)
    // --- End Snapshot fields ---

    private Integer parentTicketID; // FK (cho vé đổi/trả, có thể null)
}
