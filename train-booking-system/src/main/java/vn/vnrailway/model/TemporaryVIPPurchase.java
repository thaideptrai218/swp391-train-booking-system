package vn.vnrailway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Class representing a temporary VIP card purchase in the railway system.
 * Similar to temporary seat holds in the booking system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryVIPPurchase implements Serializable {
    private static final long serialVersionUID = 1L;

    private int tempVIPPurchaseID;
    private String sessionID;
    private Integer userID; // Can be null for guest sessions
    private int vipCardTypeID;
    private int durationMonths;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    // Related objects (for joined queries)
    private VIPCardType vipCardType;
    private User user;

    /**
     * Constructor for creating new temporary VIP purchases
     */
    public TemporaryVIPPurchase(String sessionID, Integer userID, int vipCardTypeID, 
                               int durationMonths, BigDecimal price, int expiryMinutes) {
        this.sessionID = sessionID;
        this.userID = userID;
        this.vipCardTypeID = vipCardTypeID;
        this.durationMonths = durationMonths;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(expiryMinutes);
    }

    /**
     * Check if the temporary purchase is still valid (not expired)
     */
    public boolean isValid() {
        return expiresAt != null && LocalDateTime.now().isBefore(expiresAt);
    }

    /**
     * Check if the temporary purchase is expired
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Get remaining minutes until expiry
     */
    public long getRemainingMinutes() {
        if (expiresAt == null) return 0;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) return 0;
        return java.time.Duration.between(now, expiresAt).toMinutes();
    }

    /**
     * Calculate expiry date for the actual VIP card if purchased
     */
    public LocalDateTime calculateVIPCardExpiryDate() {
        return LocalDateTime.now().plusMonths(durationMonths);
    }

    /**
     * Get formatted price in VND
     */
    public String getFormattedPrice() {
        return String.format("%,.0f VND", price);
    }
}