package vn.vnrailway.model;

import java.math.BigDecimal;

public class VIPCardType {
    private int vipCardTypeID;
    private String typeName;
    private BigDecimal price;
    private BigDecimal discountPercentage;
    private int durationMonths;
    private String description;

    public VIPCardType() {
    }

    public VIPCardType(int vipCardTypeID, String typeName, BigDecimal price, BigDecimal discountPercentage, int durationMonths, String description) {
        this.vipCardTypeID = vipCardTypeID;
        this.typeName = typeName;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.durationMonths = durationMonths;
        this.description = description;
    }

    public int getVipCardTypeID() {
        return vipCardTypeID;
    }

    public void setVipCardTypeID(int vipCardTypeID) {
        this.vipCardTypeID = vipCardTypeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormattedDiscount() {
        return String.format("%.0f%%", discountPercentage.floatValue() * 100);
    }

    public String getDurationDescription() {
        if (durationMonths >= 12) {
            int years = durationMonths / 12;
            return years + " năm";
        }
        return durationMonths + " tháng";
    }

    public String getVIPIcon() {
        if (typeName.toLowerCase().contains("gold")) {
            return "fa-solid fa-crown text-warning";
        } else if (typeName.toLowerCase().contains("silver")) {
            return "fa-solid fa-star text-secondary";
        }
        return "fa-solid fa-user";
    }

    public String getBaseTypeName() {
        return typeName.split(" ")[0];
    }
}
