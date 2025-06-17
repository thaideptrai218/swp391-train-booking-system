package vn.vnrailway.dto;

import java.math.BigDecimal;

public class SalesByYearDTO {
    private int year;
    private BigDecimal totalSales;

    public SalesByYearDTO() {
    }

    public SalesByYearDTO(int year, BigDecimal totalSales) {
        this.year = year;
        this.totalSales = totalSales;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }
}
