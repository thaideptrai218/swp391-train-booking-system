package vn.vnrailway.dao;

import vn.vnrailway.dto.SalesByMonthYearDTO;
import vn.vnrailway.dto.SalesByWeekDTO;
import vn.vnrailway.utils.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DashboardDAO {

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) as total FROM dbo.Users";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting users: " + e);
        }
        return 0;
    }

    public double getAverageUsersPerMonth() {
        String sql = "SELECT AVG(CAST(UserCount AS FLOAT)) FROM (SELECT COUNT(UserID) AS UserCount FROM Users GROUP BY YEAR(CreatedAt), MONTH(CreatedAt)) AS MonthlyCounts";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getAverageUsersPerYear() {
        String sql = "SELECT AVG(CAST(UserCount AS FLOAT)) FROM (SELECT COUNT(UserID) AS UserCount FROM Users GROUP BY YEAR(CreatedAt)) AS YearlyCounts";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Integer> getMonthlyUserRegistrations(int year) {
        Map<String, Integer> monthlyData = new LinkedHashMap<>();
        String[] months = new String[]{"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        for (String month : months) {
            monthlyData.put(month, 0);
        }

        String sql = "SELECT MONTH(CreatedAt) AS month, COUNT(UserID) AS count FROM Users WHERE YEAR(CreatedAt) = ? GROUP BY MONTH(CreatedAt)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("month");
                    int count = rs.getInt("count");
                    monthlyData.put(months[month - 1], count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlyData;
    }

    public List<Integer> getRegistrationYears() {
        List<Integer> years = new ArrayList<>();
        String sql = "SELECT DISTINCT YEAR(CreatedAt) AS year FROM Users ORDER BY year DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                years.add(rs.getInt("year"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return years;
    }

    public int getTotalTrains() {
        String sql = "SELECT COUNT(*) as total FROM dbo.Trains";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting trains: " + e);
        }
        return 0;
    }

    public int getTotalBookings() {
        String sql = "SELECT COUNT(*) as total FROM dbo.Bookings";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting bookings: " + e);
        }
        return 0;
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(TotalPrice) as total FROM dbo.Bookings WHERE BookingStatus = 'Confirmed' AND PaymentStatus = 'Paid'";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Error calculating revenue: " + e);
        }
        return 0.0;
    }

    public List<SalesByMonthYearDTO> getSalesByMonthYear() {
        List<SalesByMonthYearDTO> salesData = new ArrayList<>();
        String sql = "SELECT YEAR(BookingDateTime) AS BookingYear, " +
                "       MONTH(BookingDateTime) AS BookingMonth, " +
                "       SUM(TotalPrice) AS MonthlySales " +
                "FROM dbo.Bookings " +
                "WHERE BookingStatus = 'Confirmed' AND PaymentStatus = 'Paid' " +
                "GROUP BY YEAR(BookingDateTime), MONTH(BookingDateTime) " +
                "ORDER BY BookingYear, BookingMonth";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int year = rs.getInt("BookingYear");
                int month = rs.getInt("BookingMonth");
                BigDecimal totalSales = rs.getBigDecimal("MonthlySales");
                if (rs.wasNull()) {
                    totalSales = BigDecimal.ZERO;
                }
                SalesByMonthYearDTO dto = new SalesByMonthYearDTO(year, month, totalSales);
                dto.setMonthNameFromMonth(); // Set the month name
                salesData.add(dto);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching sales by month and year: " + e);
            // Consider logging the error more formally or re-throwing a custom exception
        }
        return salesData;
    }

    public List<SalesByWeekDTO> getSalesByWeek() {
        List<SalesByWeekDTO> salesData = new ArrayList<>();
        // SQL Server uses DATEPART(wk, date) for week number
        String sql = "SELECT YEAR(BookingDateTime) AS BookingYear, " +
                "       DATEPART(wk, BookingDateTime) AS BookingWeek, " +
                "       SUM(TotalPrice) AS WeeklySales " +
                "FROM dbo.Bookings " +
                "WHERE BookingStatus = 'Confirmed' AND PaymentStatus = 'Paid' " +
                "GROUP BY YEAR(BookingDateTime), DATEPART(wk, BookingDateTime) " +
                "ORDER BY BookingYear, BookingWeek";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int year = rs.getInt("BookingYear");
                int week = rs.getInt("BookingWeek");
                BigDecimal totalSales = rs.getBigDecimal("WeeklySales");
                if (rs.wasNull()) {
                    totalSales = BigDecimal.ZERO;
                }
                salesData.add(new SalesByWeekDTO(year, week, totalSales));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching sales by week: " + e);
            // Consider logging the error more formally or re-throwing a custom exception
        }
        return salesData;
    }
}
