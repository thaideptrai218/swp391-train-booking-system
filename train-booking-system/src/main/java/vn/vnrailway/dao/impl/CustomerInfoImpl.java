package vn.vnrailway.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.CustomerInfoDAO;
import vn.vnrailway.model.User;

public class CustomerInfoImpl implements CustomerInfoDAO {

    private static final int PAGE_SIZE = 10;

    @Override
    public List<User> getAllCustomers(int page) {
        List<User> customers = new ArrayList<>();
        int offset = (page - 1) * PAGE_SIZE;
        String sql = "SELECT * FROM Users WHERE Role = 'Customer' ORDER BY UserID ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, PAGE_SIZE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("UserID"),
                            rs.getString("FullName"),
                            rs.getString("Email"),
                            rs.getString("PhoneNumber"),
                            rs.getString("PasswordHash"),
                            rs.getString("IDCardNumber"),
                            rs.getString("Role"),
                            rs.getBoolean("IsActive"),
                            rs.getObject("CreatedAt", LocalDateTime.class),
                            rs.getObject("LastLogin", LocalDateTime.class),
                            rs.getBoolean("IsGuestAccount"),
                            rs.getObject("DateOfBirth", LocalDate.class),
                            rs.getString("Gender"),
                            rs.getString("Address"));
                    customers.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public int getTotalCustomers() {
        String sql = "SELECT COUNT(*) FROM Users WHERE Role = 'Customer'";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}