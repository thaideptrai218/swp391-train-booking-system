package vn.vnrailway.dao;

import vn.vnrailway.utils.DBContext;
import java.sql.*;
import java.util.*;

public class BookingTrendDAO {
    
    public Map<String, Integer> getBookingTrends() {
        Map<String, Integer> trends = new LinkedHashMap<>();
        
        String sql = "SELECT FORMAT(BookingDateTime, 'yyyy-MM') as Month, " +
                    "COUNT(*) as BookingCount " +
                    "FROM dbo.Bookings " +
                    "WHERE BookingDateTime >= DATEADD(MONTH, -6, GETDATE()) " +
                    "GROUP BY FORMAT(BookingDateTime, 'yyyy-MM') " +
                    "ORDER BY Month";
                    
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String month = rs.getString("Month");
                int count = rs.getInt("BookingCount");
                trends.put(month, count);
            }
        } catch (SQLException e) {
            System.out.println("Error getting booking trends: " + e);
        }
        return trends;
    }
}
