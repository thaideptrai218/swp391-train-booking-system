package vn.vnrailway.dto;

import java.math.BigDecimal;

public class SalesByMonthDTO {
    private int month;
    private String monthName;
    private BigDecimal totalSales;

    public SalesByMonthDTO() {
    }

    public SalesByMonthDTO(int month, BigDecimal totalSales) {
        this.month = month;
        this.totalSales = totalSales;
        setMonthNameFromMonth();
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        // Automatically update monthName when month is set
        setMonthNameFromMonth();
    }

    public String getMonthName() {
        return monthName;
    }

    // Setter for monthName is not strictly necessary if always derived,
    // but can be included for flexibility or direct setting.
    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    private void setMonthNameFromMonth() {
        String[] monthNames = {
                "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        };
        if (this.month >= 1 && this.month <= 12) {
            this.monthName = monthNames[this.month - 1];
        } else {
            this.monthName = "Không xác định";
        }
    }
}
