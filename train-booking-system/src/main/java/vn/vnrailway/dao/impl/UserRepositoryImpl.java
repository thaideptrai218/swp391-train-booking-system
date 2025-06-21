package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public class UserRepositoryImpl implements UserRepository {

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setFullName(rs.getString("FullName"));
        user.setEmail(rs.getString("Email"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));
        user.setPasswordHash(rs.getString("PasswordHash")); // Use setPasswordHash for plain-text password
        user.setIdCardNumber(rs.getString("IDCardNumber"));
        user.setRole(rs.getString("Role"));
        user.setActive(rs.getBoolean("IsActive"));

        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            user.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }

        Timestamp lastLoginTimestamp = rs.getTimestamp("LastLogin");
        if (lastLoginTimestamp != null) {
            user.setLastLogin(lastLoginTimestamp.toLocalDateTime());
        }
        user.setDateOfBirth(rs.getObject("DateOfBirth", LocalDate.class));
        user.setGender(rs.getString("Gender"));
        user.setAddress(rs.getString("Address"));
        return user;
    }

    @Override
    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address FROM Users WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address FROM Users WHERE Email = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByPhone(String phone) throws SQLException {
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address FROM Users WHERE PhoneNumber = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address FROM Users";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    @Override
    public User save(User user) throws SQLException {
        String sql = "INSERT INTO Users (FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getPasswordHash()); // Use getPasswordHash for plain-text password
            ps.setString(5, user.getIdCardNumber());
            ps.setString(6, user.getRole());
            ps.setBoolean(7, user.isActive());

            if (user.getCreatedAt() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(user.getCreatedAt()));
            } else {
                ps.setTimestamp(8, Timestamp.valueOf(java.time.LocalDateTime.now())); // Default to now if not set
            }

            if (user.getLastLogin() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(user.getLastLogin()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }

            if (user.getDateOfBirth() != null) {
                ps.setDate(10, Date.valueOf(user.getDateOfBirth()));
            } else {
                ps.setNull(10, Types.DATE);
            }

            ps.setString(11, user.getGender());
            ps.setString(12, user.getAddress());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserID(generatedKeys.getInt(1));
                } else {
                    // This might happen if UserID is not an identity column or not configured to return.
                    // Or if the DB doesn't support RETURN_GENERATED_KEYS in this way for this table.
                    // For now, we assume it should return an ID.
                    System.err.println("Creating user succeeded, but no ID was obtained. UserID might not be auto-generated or not configured to be returned.");
                }
            }
        }
        return user;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE Users SET FullName = ?, PhoneNumber = ?, IDCardNumber = ?, Role = ?, IsActive = ?, LastLogin = ?, DateOfBirth = ?, Gender = ?, Address = ?, Email = ? WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhoneNumber());
            ps.setString(3, user.getIdCardNumber());
            ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isActive());

            if (user.getLastLogin() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(user.getLastLogin()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            if (user.getDateOfBirth() != null) {
                ps.setDate(7, Date.valueOf(user.getDateOfBirth()));
            } else {
                ps.setNull(7, Types.DATE);
            }

            ps.setString(8, user.getGender());
            ps.setString(9, user.getAddress());
            ps.setString(10, user.getEmail());
            ps.setInt(11, user.getUserID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public List<User> findByAddress(String address) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address FROM Users WHERE Address = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, address);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }

    @Override
    public List<User> findByGender(String gender) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address FROM Users WHERE Gender = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gender);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }

    @Override
    public Optional<User> findByDateOfBirth(LocalDate dateOfBirth) throws SQLException {
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address FROM Users WHERE DateOfBirth = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (dateOfBirth != null) {
                ps.setDate(1, Date.valueOf(dateOfBirth));
            } else {
                ps.setNull(1, Types.DATE);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    // Main method for testing
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepositoryImpl();
        try {
            // Test findAll
            System.out.println("Testing findAll users:");
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                users.forEach(u -> System.out.println(u));
            }

            // Test findById (assuming user with ID 1 exists, or change ID for testing)
            int testUserId = 1; // Ensure this ID exists
            System.out.println("\nTesting findById for user ID: " + testUserId);
            Optional<User> userOpt = userRepository.findById(testUserId);
            userOpt.ifPresentOrElse(
                u -> System.out.println("Found user: " + u),
                () -> System.out.println("User with ID " + testUserId + " not found.")
            );

            // Test findByEmail
            String testEmail = "test@example.com"; // Ensure this email exists or use a real one from your DB
            System.out.println("\nTesting findByEmail for email: " + testEmail);
            Optional<User> userByEmailOpt = userRepository.findByEmail(testEmail);
            userByEmailOpt.ifPresentOrElse(
                u -> System.out.println("Found user by email: " + u),
                () -> System.out.println("User with email " + testEmail + " not found.")
            );

            // Test findByPhone
            String testPhone = "0123456789"; // Ensure this phone number exists or use a real one from your DB
            System.out.println("\nTesting findByPhone for phone: " + testPhone);
            Optional<User> userByPhoneOpt = userRepository.findByPhone(testPhone);
            userByPhoneOpt.ifPresentOrElse(
                u -> System.out.println("Found user by phone: " + u),
                () -> System.out.println("User with phone " + testPhone + " not found.")
            );

            // Example of saving a new user (uncomment and modify to test)
            /*
            System.out.println("\nTesting save new user:");
            User newUser = new User();
            newUser.setFullName("Test User Main");
            newUser.setEmail("newusermain@example.com");
            newUser.setPhoneNumber("1234567890");
            newUser.setPasswordHash("hashedpasswordmain"); // In a real app, hash this properly
            newUser.setRole("Customer");
            newUser.setActive(true);
            newUser.setCreatedAt(java.time.LocalDateTime.now());

            User savedUser = userRepository.save(newUser);
            System.out.println("Saved user: " + savedUser);

            if (savedUser.getUserID() > 0) {
                // Example of updating the user
                System.out.println("\nTesting update user ID: " + savedUser.getUserID());
                savedUser.setPhoneNumber("0987654321");
                boolean updated = userRepository.update(savedUser);
                System.out.println("Update successful: " + updated);

                Optional<User> updatedUserOpt = userRepository.findById(savedUser.getUserID());
                updatedUserOpt.ifPresent(u -> System.out.println("Updated user details: " + u));

                // Example of deleting the user
                System.out.println("\nTesting delete user ID: " + savedUser.getUserID());
                boolean deleted = userRepository.deleteById(savedUser.getUserID());
                System.out.println("Delete successful: " + deleted);
            }
            */

        } catch (SQLException e) {
            System.err.println("Error testing UserRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
