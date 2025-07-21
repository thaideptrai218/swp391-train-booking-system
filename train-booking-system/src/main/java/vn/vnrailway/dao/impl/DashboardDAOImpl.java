package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.DashboardDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAOImpl extends DashboardDAO {

    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    @Override
    public int getTotalUsers() {
        String query = "SELECT COUNT(*) FROM Users";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, role);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, -days);
            ps.setInt(2, -days);
            rs = ps.executeQuery();
            if (rs.next()) {
                data.put("Logged In", rs.getInt("LoggedIn"));
                data.put("Not Logged In", rs.getInt("NotLoggedIn"));
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("registration_date"), rs.getInt("registration_count"));
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, year);
            rs = ps.executeQuery();
            while (rs.next()) {
                data.put(String.valueOf(rs.getInt("registration_month")), rs.getInt("registration_count"));
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                data.put("Accepted", rs.getInt("Accepted"));
                data.put("Rejected", rs.getInt("Rejected"));
                data.put("Pending", rs.getInt("Pending"));
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, -days);
            rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("request_date"), rs.getInt("request_count"));
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
        return data;
    }

    @Override
    public Map<String, Integer> getFeedbackStatus() throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT status, COUNT(*) AS count FROM Feedback GROUP BY status";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("status"), rs.getInt("count"));
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
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
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("TypeName"), rs.getInt("count"));
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
        return data;
    }
}
