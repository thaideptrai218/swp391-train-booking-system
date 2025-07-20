package vn.vnrailway.model;

import java.time.LocalDate;

public class HolidayPrice {
    private int id;
    private String holidayName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private float discountPercentage;
    private boolean isActive;

    public HolidayPrice() {
    }

    public HolidayPrice(int id, String holidayName, String description, LocalDate startDate, LocalDate endDate,
            float discountPercentage, boolean isActive) {
        this.id = id;
        this.holidayName = holidayName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountPercentage = discountPercentage;
        this.isActive = isActive;
    }

    public HolidayPrice(int id, String holidayName, LocalDate startDate, LocalDate endDate, float coefficient,
            boolean isActive) {
        this.id = id;
        this.holidayName = holidayName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountPercentage = coefficient;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public float getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(float discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
