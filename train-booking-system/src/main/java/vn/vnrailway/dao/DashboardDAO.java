package vn.vnrailway.dao;

import vn.vnrailway.dto.SalesByMonthYearDTO;
import vn.vnrailway.utils.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

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
}
