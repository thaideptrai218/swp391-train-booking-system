package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.DashboardDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAOImpl extends DashboardDAO {

    @Override
    public int getTotalUsers() {
        String query = "SELECT COUNT(*) FROM Users";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getUserCountByRole(String role) {
        String query = "SELECT COUNT(*) FROM Users WHERE Role = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Map<String, Integer> getGenderDistribution() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT Gender, COUNT(*) AS count FROM Users GROUP BY Gender";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                data.put(rs.getString("Gender"), rs.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public Map<String, Integer> getAgeGroupDistribution() {
        Map<String, Integer> data = new LinkedHashMap<>();
        data.put("0-20", 0);
        data.put("21-30", 0);
        data.put("31-40", 0);
        data.put("41-50", 0);
        data.put("51+", 0);
        data.put("Khác", 0);

        String query = "SELECT " +
                "CASE " +
                "WHEN DateOfBirth IS NULL THEN 'Khác' " +
                "WHEN DATEDIFF(year, DateOfBirth, GETDATE()) BETWEEN 0 AND 20 THEN '0-20' " +
                "WHEN DATEDIFF(year, DateOfBirth, GETDATE()) BETWEEN 21 AND 30 THEN '21-30' " +
                "WHEN DATEDIFF(year, DateOfBirth, GETDATE()) BETWEEN 31 AND 40 THEN '31-40' " +
                "WHEN DATEDIFF(year, DateOfBirth, GETDATE()) BETWEEN 41 AND 50 THEN '41-50' " +
                "ELSE '51+' END AS AgeGroup, " +
                "COUNT(*) AS count " +
                "FROM Users GROUP BY " +
                "CASE " +
                "WHEN DateOfBirth IS NULL THEN 'Khác' " +
                "WHEN DATEDIFF(year, DateOfBirth, GETDATE()) BETWEEN 0 AND 20 THEN '0-20' " +
                "WHEN DATEDIFF(year, DateOfBirth, GETDATE()) BETWEEN 21 AND 30 THEN '21-30' " +
                "WHEN DATEDIFF(year, DateOfBirth, GETDATE()) BETWEEN 31 AND 40 THEN '31-40' " +
                "WHEN DATEDIFF(year, DateOfBirth, GETDATE()) BETWEEN 41 AND 50 THEN '41-50' " +
                "ELSE '51+' END";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                data.put(rs.getString("AgeGroup"), rs.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public Map<String, Integer> getActiveStatusDistribution() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT IsActive, COUNT(*) AS count FROM Users GROUP BY IsActive";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                data.put(rs.getBoolean("IsActive") ? "Active" : "Inactive", rs.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public Map<String, Integer> getLoginRatio(int days) {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT " +
                "SUM(CASE WHEN LastLogin >= DATEADD(day, ?, GETDATE()) THEN 1 ELSE 0 END) AS LoggedIn, " +
                "SUM(CASE WHEN LastLogin < DATEADD(day, ?, GETDATE()) OR LastLogin IS NULL THEN 1 ELSE 0 END) AS NotLoggedIn " +
                "FROM Users";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, -days);
            ps.setInt(2, -days);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.put("Logged In", rs.getInt("LoggedIn"));
                    data.put("Not Logged In", rs.getInt("NotLoggedIn"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public Map<String, Integer> getDailyUserRegistrations(int days) {
        Map<String, Integer> data = new LinkedHashMap<>();
        java.time.LocalDate endDate = java.time.LocalDate.now();
        java.time.LocalDate startDate = endDate.minusDays(days - 1);

        for (java.time.LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            data.put(date.toString(), 0);
        }

        String query = "SELECT CAST(CreatedAt AS DATE) AS registration_date, COUNT(*) AS registration_count " +
                "FROM Users " +
                "WHERE CreatedAt >= ? " +
                "GROUP BY CAST(CreatedAt AS DATE) " +
                "ORDER BY registration_date;";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("registration_date"), rs.getInt("registration_count"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public Map<String, Integer> getMonthlyUserRegistrations(int year) {
        Map<String, Integer> data = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            data.put(String.valueOf(i), 0);
        }

        String query = "SELECT MONTH(CreatedAt) AS registration_month, COUNT(*) AS registration_count " +
                "FROM Users " +
                "WHERE YEAR(CreatedAt) = ? " +
                "GROUP BY MONTH(CreatedAt) " +
                "ORDER BY registration_month;";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(String.valueOf(rs.getInt("registration_month")), rs.getInt("registration_count"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public java.util.List<Integer> getRegistrationYears() {
        java.util.List<Integer> years = new java.util.ArrayList<>();
        String query = "SELECT DISTINCT YEAR(CreatedAt) AS registration_year FROM Users ORDER BY registration_year DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                years.add(rs.getInt("registration_year"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return years;
    }

    @Override
    public Map<String, Integer> getRefundRequestStatus() throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT " +
                "SUM(CASE WHEN r.Status = 'Accepted' THEN 1 ELSE 0 END) AS Accepted, " +
                "SUM(CASE WHEN r.Status = 'Rejected' THEN 1 ELSE 0 END) AS Rejected, " +
                "(SELECT COUNT(*) FROM TempRefundRequests) AS Pending " +
                "FROM Refunds r";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                data.put("Accepted", rs.getInt("Accepted"));
                data.put("Rejected", rs.getInt("Rejected"));
                data.put("Pending", rs.getInt("Pending"));
            }
        }
        return data;
    }

    @Override
    public Map<String, Integer> getRefundRequestsOverTime(int days) throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT CAST(RequestedAt AS DATE) AS request_date, COUNT(*) AS request_count " +
                "FROM TempRefundRequests " +
                "WHERE RequestedAt >= DATEADD(day, ?, GETDATE()) " +
                "GROUP BY CAST(RequestedAt AS DATE) " +
                "ORDER BY request_date;";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, -days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("request_date"), rs.getInt("request_count"));
                }
            }
        }
        return data;
    }

    @Override
    public Map<String, Integer> getFeedbackStatus() throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT status, COUNT(*) AS count FROM Feedback GROUP BY status";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                data.put(rs.getString("status"), rs.getInt("count"));
            }
        }
        return data;
    }

    @Override
    public Map<String, Integer> getFeedbackByTopic() throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT ft.TypeName, COUNT(f.FeedbackID) AS count " +
                "FROM FeedbackTypes ft " +
                "LEFT JOIN Feedback f ON ft.FeedbackTypeID = f.FeedbackTypeID " +
                "GROUP BY ft.TypeName";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                data.put(rs.getString("TypeName"), rs.getInt("count"));
            }
        }
        return data;
    }

    @Override
    public int getPendingRefundsCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM TempRefundRequests";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getPendingFeedbacksCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Feedback WHERE Status = 'Pending'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getUnansweredUsersCount() throws SQLException {
        String query = "WITH LatestMessages AS ( " +
                       "    SELECT UserID, MAX(Timestamp) AS LastMessageTime " +
                       "    FROM Messages " +
                       "    GROUP BY UserID " +
                       ") " +
                       "SELECT COUNT(DISTINCT m.UserID) " +
                       "FROM Messages m " +
                       "JOIN LatestMessages lm ON m.UserID = lm.UserID AND m.Timestamp = lm.LastMessageTime " +
                       "WHERE m.SenderType = 'Customer'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getTotalTrainsCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Trains";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getTotalRevenue(int months) throws SQLException {
        String query = "SELECT SUM(TotalPrice) FROM Bookings WHERE PaymentStatus = 'Paid' AND BookingDateTime >= DATEADD(month, ?, GETDATE())";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, -months);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getExpectedRevenue() throws SQLException {
        double actualRevenueLast6Months = getActualRevenue(6);

        String futureRevenueQuery = "SELECT SUM(ISNULL(b.TotalPrice, 0)) " +
                                    "FROM Bookings b " +
                                    "JOIN Tickets t ON b.BookingID = t.BookingID " +
                                    "JOIN Trips tr ON t.TripID = tr.TripID " +
                                    "WHERE b.PaymentStatus = 'Paid' " +
                                    "AND tr.DepartureDateTime > GETDATE()";

        double futureRevenue = 0;
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(futureRevenueQuery);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                futureRevenue = rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return actualRevenueLast6Months + futureRevenue;
    }

    @Override
    public double getActualRevenue(int months) throws SQLException {
        // Simplified logic: Revenue from trips that have already departed in the selected time frame.
        String query = "SELECT SUM(ISNULL(b.TotalPrice, 0)) " +
               "FROM Bookings b " +
               "JOIN Tickets t ON b.BookingID = t.BookingID " +
               "JOIN Trips tr ON t.TripID = tr.TripID " +
               "WHERE b.PaymentStatus = 'Paid' " +
               "AND tr.DepartureDateTime < GETDATE() " +
               "AND tr.DepartureDateTime >= DATEADD(month, ?, GETDATE())";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, -months);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getTotalRefunds() throws SQLException {
        String query = "SELECT SUM(ActualRefundAmount) FROM Refunds WHERE Status = 'Accepted'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Map<String, Integer> getPopularDepartureStations(int months) throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT TOP 5 s.StationName, COUNT(b.BookingID) AS BookingCount " +
                       "FROM Bookings b " +
                       "JOIN Tickets t ON b.BookingID = t.BookingID " +
                       "JOIN Stations s ON t.StartStationID = s.StationID " +
                       "WHERE b.PaymentStatus = 'Paid' AND b.BookingDateTime >= DATEADD(month, ?, GETDATE()) " +
                       "GROUP BY s.StationName " +
                       "ORDER BY BookingCount DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, -months);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("StationName"), rs.getInt("BookingCount"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public Map<String, Integer> getPopularArrivalStations(int months) throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT TOP 5 s.StationName, COUNT(b.BookingID) AS BookingCount " +
                       "FROM Bookings b " +
                       "JOIN Tickets t ON b.BookingID = t.BookingID " +
                       "JOIN Stations s ON t.EndStationID = s.StationID " +
                       "WHERE b.PaymentStatus = 'Paid' AND b.BookingDateTime >= DATEADD(month, ?, GETDATE()) " +
                       "GROUP BY s.StationName " +
                       "ORDER BY BookingCount DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, -months);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("StationName"), rs.getInt("BookingCount"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public java.util.List<Map<String, Object>> getPopularTrips() throws SQLException {
        java.util.List<Map<String, Object>> popularTrips = new java.util.ArrayList<>();
        String query = "SELECT TOP 10 " +
                       "dep.StationName AS DepartureStation, " +
                       "arr.StationName AS ArrivalStation, " +
                       "COUNT(b.BookingID) AS BookingCount " +
                       "FROM Bookings b " +
                       "JOIN Tickets t ON b.BookingID = t.BookingID " +
                       "JOIN Stations dep ON t.StartStationID = dep.StationID " +
                       "JOIN Stations arr ON t.EndStationID = arr.StationID " +
                       "WHERE b.PaymentStatus = 'Paid' " +
                       "GROUP BY dep.StationName, arr.StationName " +
                       "ORDER BY BookingCount DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> trip = new LinkedHashMap<>();
                trip.put("departureStation", rs.getString("DepartureStation"));
                trip.put("arrivalStation", rs.getString("ArrivalStation"));
                trip.put("bookingCount", rs.getInt("BookingCount"));
                popularTrips.add(trip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return popularTrips;
    }

    @Override
    public int getTotalTicketsSold() throws SQLException {
        String query = "SELECT COUNT(t.TicketCode) FROM Tickets t JOIN Bookings b ON t.BookingID = b.BookingID WHERE b.PaymentStatus = 'Paid'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getRefundableTicketsCount() throws SQLException {
        String query = "SELECT COUNT(t.TicketCode) " +
               "FROM Tickets t " +
               "JOIN Bookings b ON t.BookingID = b.BookingID " +
               "JOIN Trips tr ON t.TripID = tr.TripID " +
               "WHERE b.PaymentStatus = 'Paid' AND tr.DepartureDateTime > GETDATE() " +
               "AND EXISTS ( " +
               "    SELECT 1 FROM CancellationPolicies cp " +
               "    WHERE cp.IsRefundable = 1 AND cp.IsActive = 1 " +
               "      AND GETDATE() BETWEEN cp.EffectiveFromDate AND ISNULL(cp.EffectiveToDate, '9999-12-31') " +
               "      AND DATEDIFF(hour, GETDATE(), tr.DepartureDateTime) >= cp.HoursBeforeDeparture_Min " +
               "      AND DATEDIFF(hour, GETDATE(), tr.DepartureDateTime) < cp.HoursBeforeDeparture_Max " +
               ")";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
