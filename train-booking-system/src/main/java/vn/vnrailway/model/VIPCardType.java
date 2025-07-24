package vn.vnrailway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Class representing a VIP Card Type entity in the railway system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VIPCardType implements Serializable {
    private static final long serialVersionUID = 1L;

    private int vipCardTypeID;
    private String typeName;
    private BigDecimal price;
    private BigDecimal discountPercentage;
    private int durationMonths;
    private String description;

    /**
     * Constructor for creating new VIP card types (without ID)
     */
    public VIPCardType(String typeName, BigDecimal price, BigDecimal discountPercentage, 
                      int durationMonths, String description) {
        this.typeName = typeName;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.durationMonths = durationMonths;
        this.description = description;
    }

    /**
     * Get formatted price in VND
     */
    public String getFormattedPrice() {
        return String.format("%,.0f VND", price);
    }

    /**
     * Get discount percentage as string with % symbol
     */
    public String getFormattedDiscount() {
        return String.format("%.0f%%", discountPercentage);
    }

    /**
     * Get duration description in Vietnamese
     */
    public String getDurationDescription() {
        if (durationMonths == 1) {
            return "1 tháng";
        } else if (durationMonths < 12) {
            return durationMonths + " tháng";
        } else if (durationMonths == 12) {
            return "1 năm";
        } else {
            return (durationMonths / 12) + " năm";
        }
    }

    /**
     * Check if this is a premium card type (Gold or Diamond)
     */
    public boolean isPremium() {
        return typeName != null && (typeName.contains("Vàng") || typeName.contains("Kim Cương"));
    }

    /**
     * Get CSS class for card styling based on type
     */
    public String getCardStyleClass() {
        if (typeName == null) return "vip-card-default";
        
        if (typeName.contains("Đồng")) return "vip-card-bronze";
        if (typeName.contains("Bạc")) return "vip-card-silver";
        if (typeName.contains("Vàng")) return "vip-card-gold";
        if (typeName.contains("Kim Cương")) return "vip-card-diamond";
        
        return "vip-card-default";
    }

    /**
     * Get the base type name without duration suffix
     */
    public String getBaseTypeName() {
        if (typeName == null) return "";
        return typeName.replace(" 3 Tháng", "").replace(" 1 Năm", "");
    }

    /**
     * Get VIP icon based on card type
     */
    public String getVIPIcon() {
        if (typeName == null) return "🎫";
        
        if (typeName.contains("Đồng")) return "🥉";
        if (typeName.contains("Bạc")) return "🥈";
        if (typeName.contains("Vàng")) return "🥇";
        if (typeName.contains("Kim Cương")) return "💎";
        
        return "🎫";
    }

    /**
     * Check if this is a 3-month variant
     */
    public boolean isThreeMonthVariant() {
        return durationMonths == 3;
    }

    /**
     * Check if this is a yearly variant
     */
    public boolean isYearlyVariant() {
        return durationMonths == 12;
    }

    /**
     * Get duration button text
     */
    public String getDurationButtonText() {
        if (durationMonths == 3) return "3 tháng";
        if (durationMonths == 12) return "1 năm";
        return durationMonths + " tháng";
    }
}