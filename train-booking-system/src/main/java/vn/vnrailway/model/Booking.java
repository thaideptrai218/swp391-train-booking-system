package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; // Hoặc java.sql.Timestamp
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private int bookingID;
    private String bookingCode;
    private int userID; // FK
    // private User user; // Object
    private LocalDateTime bookingDateTime;
    private BigDecimal totalPrice;
    private String bookingStatus; // "Pending", "Confirmed", etc. (Có thể dùng Enum)
    private String paymentStatus; // "Unpaid", "Paid", etc. (Có thể dùng Enum)
    private String source;        // Có thể null
    private LocalDateTime expiredAt;  // Có thể null
}
