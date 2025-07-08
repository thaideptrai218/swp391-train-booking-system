package vn.vnrailway.dto;

import java.math.BigDecimal;

public class SalesByMonthYearDTO {
    private int year;
    private int month;
    private BigDecimal totalSales;
    private String monthName; // To store month name like "January", "February"

    public SalesByMonthYearDTO() {
    }

    public SalesByMonthYearDTO(int year, int month, BigDecimal totalSales) {
        this.year = year;
        this.month = month;
        this.totalSales = totalSales;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    // Helper to set month name based on month number
    public void setMonthNameFromMonth() {
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
