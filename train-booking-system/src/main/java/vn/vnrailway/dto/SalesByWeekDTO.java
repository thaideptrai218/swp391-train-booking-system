package vn.vnrailway.dto;

import java.math.BigDecimal;

public class SalesByWeekDTO {
    private String weekOfYear; // e.g., "2023-W25" or "Week 25, 2023"
    private int year;
    private int week;
    private BigDecimal totalSales;

    public SalesByWeekDTO() {
    }

    public SalesByWeekDTO(int year, int week, BigDecimal totalSales) {
        this.year = year;
        this.week = week;
        this.weekOfYear = String.format("%d-W%02d", year, week);
        this.totalSales = totalSales;
    }

    public String getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(String weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    @Override
    public String toString() {
        return "SalesByWeekDTO{" +
                "weekOfYear='" + weekOfYear + '\'' +
                ", year=" + year +
                ", week=" + week +
                ", totalSales=" + totalSales +
                '}';
    }
}
