package vn.vnrailway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Class representing a User's VIP Card in the railway system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVIPCard implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userVIPCardID;
    private int userID;
    private int vipCardTypeID;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiryDate;
    private boolean isActive;
    private String transactionReference;

    // Related objects (for joined queries)
    private VIPCardType vipCardType;
    private User user;

    /**
     * Constructor for creating new VIP card purchases
     */
    public UserVIPCard(int userID, int vipCardTypeID, LocalDateTime expiryDate, String transactionReference) {
        this.userID = userID;
        this.vipCardTypeID = vipCardTypeID;
        this.purchaseDate = LocalDateTime.now();
        this.expiryDate = expiryDate;
        this.isActive = true;
        this.transactionReference = transactionReference;
    }

    /**
     * Check if the VIP card is currently valid (active and not expired)
     */
    public boolean isValid() {
        return isActive && expiryDate != null && LocalDateTime.now().isBefore(expiryDate);
    }

    /**
     * Check if the VIP card is expired
     */
    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    /**
     * Get remaining days until expiry
     */
    public long getRemainingDays() {
        if (expiryDate == null)
            return 0;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiryDate))
            return 0;
        return java.time.Duration.between(now, expiryDate).toDays();
    }

    /**
     * Get VIP card status as Vietnamese string
     */
    public String getStatusText() {
        if (!isActive)
            return "Đã hủy";
        if (isExpired())
            return "Đã hết hạn";
        if (getRemainingDays() <= 7)
            return "Sắp hết hạn";
        return "Đang hoạt động";
    }

    /**
     * Get CSS class for status styling
     */
    public String getStatusClass() {
        if (!isActive)
            return "status-cancelled";
        if (isExpired())
            return "status-expired";
        if (getRemainingDays() <= 7)
            return "status-expiring";
        return "status-active";
    }

    public Date toDatePurchaseDate() {
        return Date.from(purchaseDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date toDateExpiryDate() {
        return Date.from(expiryDate.atZone(ZoneId.systemDefault()).toInstant());
    }
}