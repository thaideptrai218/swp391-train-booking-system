package vn.vnrailway.dao;

import vn.vnrailway.utils.DBContext;
import java.sql.*;

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
        String sql = "SELECT SUM(Amount) as total FROM dbo.PaymentTransactions WHERE Status = 'COMPLETED'";
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
}