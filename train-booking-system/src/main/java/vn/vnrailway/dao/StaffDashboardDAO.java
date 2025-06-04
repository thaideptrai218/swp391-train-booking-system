package vn.vnrailway.dao;

import vn.vnrailway.utils.DBContext;
import java.sql.*;
import java.time.LocalDate;

public class StaffDashboardDAO {
    
    public int getPendingBookings() {
        String sql = "SELECT COUNT(*) as total FROM dbo.Bookings WHERE Status = 'PENDING'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting pending bookings: " + e);
        }
        return 0;
    }
    
    public int getTodayDepartures() {
        String sql = "SELECT COUNT(DISTINCT t.TripID) as total FROM dbo.Trips t " +
                    "WHERE CAST(t.DepartureTime AS DATE) = CAST(GETDATE() AS DATE)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting today's departures: " + e);
        }
        return 0;
    }
    
    public int getActiveTickets() {
        String sql = "SELECT COUNT(*) as total FROM dbo.Tickets t " +
                    "JOIN dbo.Trips tr ON t.TripID = tr.TripID " +
                    "WHERE tr.DepartureTime > GETDATE() AND t.Status = 'ACTIVE'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting active tickets: " + e);
        }
        return 0;
    }
    
    public int getRecentRequests() {
        String sql = "SELECT COUNT(*) as total FROM dbo.SupportRequests " +
                    "WHERE CreatedAt >= DATEADD(day, -1, GETDATE())";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error counting recent requests: " + e);
        }
        return 0;
    }
}