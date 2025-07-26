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
        user.setPasswordHash(rs.getString("PasswordHash"));
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
        user.setGender(rs.getString("Gender"));
        user.setAddress(rs.getString("Address"));
        user.setAvatarPath(rs.getString("AvatarPath"));
        return user;
    }

    @Override
    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address, AvatarPath FROM Users WHERE UserID = ?";
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
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address, AvatarPath FROM Users WHERE Email = ?";
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
    public void updateAvatar(int userId, String avatarPath) throws SQLException {
        String sql = "UPDATE Users SET AvatarPath = ? WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, avatarPath);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<User> findByPhone(String phone) throws SQLException {
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address, AvatarPath FROM Users WHERE PhoneNumber = ?";
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
    public Optional<User> findByIdCardNumber(String idCardNumber) throws SQLException {
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address, AvatarPath  FROM Users WHERE IDCardNumber = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idCardNumber);
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
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address, AvatarPath FROM Users";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    public List<User> findWithPagination(int offset, int limit) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY UserID ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<User> searchAndFilterUsers(String searchTerm, String role, String status, int offset, int limit)
            throws SQLException {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Users WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql.append(" AND (");

            // FullName search logic
            String[] searchWords = searchTerm.split("\\s+");
            sql.append("(");
            for (int i = 0; i < searchWords.length; i++) {
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append("FullName LIKE ?");
                params.add("%" + searchWords[i] + "%");
            }
            sql.append(")");

            // OR Email OR PhoneNumber
            sql.append(" OR Email LIKE ? OR PhoneNumber LIKE ?)");
            params.add("%" + searchTerm + "%");
            params.add("%" + searchTerm + "%");
        }

        if (role != null && !role.isEmpty() && !role.equals("all")) {
            sql.append(" AND Role = ?");
            params.add(role);
        }

        if (status != null && !status.isEmpty() && !status.equals("all")) {
            sql.append(" AND IsActive = ?");
            params.add("active".equals(status));
        }

        sql.append(" ORDER BY UserID ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(limit);

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }

    public int countFilteredUsers(String searchTerm, String role, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Users WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql.append(" AND (");

            // FullName search logic
            String[] searchWords = searchTerm.split("\\s+");
            sql.append("(");
            for (int i = 0; i < searchWords.length; i++) {
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append("FullName LIKE ?");
                params.add("%" + searchWords[i] + "%");
            }
            sql.append(")");

            // OR Email OR PhoneNumber
            sql.append(" OR Email LIKE ? OR PhoneNumber LIKE ?)");
            params.add("%" + searchTerm + "%");
            params.add("%" + searchTerm + "%");
        }

        if (role != null && !role.isEmpty() && !role.equals("all")) {
            sql.append(" AND Role = ?");
            params.add(role);
        }

        if (status != null && !status.isEmpty() && !status.equals("all")) {
            sql.append(" AND IsActive = ?");
            params.add("active".equals(status));
        }

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public List<User> findByRole(String role) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, Gender, Address, AvatarPath FROM Users WHERE Role = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
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
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getIdCardNumber());
            ps.setString(6, user.getRole());
            ps.setBoolean(7, user.isActive());

            if (user.getCreatedAt() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(user.getCreatedAt()));
            } else {
                ps.setTimestamp(8, Timestamp.valueOf(java.time.LocalDateTime.now()));
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
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        return user;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE Users SET FullName = ?, PhoneNumber = ?, IDCardNumber = ?, Role = ?, IsActive = ?, LastLogin = ?, DateOfBirth = ?, Gender = ?, Address = ?, Email = ?, PasswordHash = ? WHERE UserID = ?";
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
            ps.setNString(11, user.getPasswordHash());
            ps.setInt(12, user.getUserID());

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
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address, AvatarPath FROM Users WHERE Address = ?";
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
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address, AvatarPath FROM Users WHERE Gender = ?";
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
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin, DateOfBirth, Gender, Address, AvatarPath FROM Users WHERE DateOfBirth = ?";
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
                    () -> System.out.println("User with ID " + testUserId + " not found."));

            // Test findByEmail
            String testEmail = "test@example.com"; // Ensure this email exists or use a real one from your DB
            System.out.println("\nTesting findByEmail for email: " + testEmail);
            Optional<User> userByEmailOpt = userRepository.findByEmail(testEmail);
            userByEmailOpt.ifPresentOrElse(
                    u -> System.out.println("Found user by email: " + u),
                    () -> System.out.println("User with email " + testEmail + " not found."));

            // Test findByPhone
            String testPhone = "0123456789"; // Ensure this phone number exists or use a real one from your DB
            System.out.println("\nTesting findByPhone for phone: " + testPhone);
            Optional<User> userByPhoneOpt = userRepository.findByPhone(testPhone);
            userByPhoneOpt.ifPresentOrElse(
                    u -> System.out.println("Found user by phone: " + u),
                    () -> System.out.println("User with phone " + testPhone + " not found."));
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

        } catch (SQLException e) {
            System.err.println("Error testing UserRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean lockById(int userId) throws SQLException {
        String sql = "UPDATE Users SET IsActive = 0 WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean unlockById(int userId) throws SQLException {
        String sql = "UPDATE Users SET IsActive = 1 WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean hideById(int userId) throws SQLException {
        String sql = "UPDATE Users SET IsActive = 0 WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public List<Object[]> getLogsByPage(int page, int pageSize, String searchTerm, String action, String startDate,
            String endDate) throws SQLException {
        List<Object[]> auditLogs = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT LogId, EditorEmail, Action, TargetEmail, OldValue, NewValue, LogTime FROM dbo.AuditLogs WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql.append(" AND TargetEmail LIKE ?");
            params.add("%" + searchTerm + "%");
        }

        if (action != null && !action.isEmpty() && !action.equals("all")) {
            sql.append(" AND Action = ?");
            params.add(action);
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND LogTime >= ?");
            params.add(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND LogTime <= ?");
            params.add(endDate);
        }

        sql.append(" ORDER BY LogId ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] log = new Object[7];
                    log[0] = rs.getInt("LogId");
                    log[1] = rs.getString("EditorEmail");
                    log[2] = rs.getString("Action");
                    log[3] = rs.getString("TargetEmail");
                    log[4] = rs.getString("OldValue");
                    log[5] = rs.getString("NewValue");
                    log[6] = rs.getTimestamp("LogTime");
                    auditLogs.add(log);
                }
            }
        }
        return auditLogs;
    }

    @Override
    public int getTotalLogCount(String searchTerm, String action, String startDate, String endDate)
            throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM dbo.AuditLogs WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql.append(" AND TargetEmail LIKE ?");
            params.add("%" + searchTerm + "%");
        }

        if (action != null && !action.isEmpty() && !action.equals("all")) {
            sql.append(" AND Action = ?");
            params.add(action);
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND LogTime >= ?");
            params.add(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND LogTime <= ?");
            params.add(endDate);
        }

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public Optional<User> getUserByBookingCode(String bookingCode) throws SQLException {
        String sql = "SELECT u.* FROM Users u JOIN Bookings b ON u.UserID = b.UserID WHERE b.BookingCode = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }
}
