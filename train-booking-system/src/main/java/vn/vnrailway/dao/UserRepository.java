package vn.vnrailway.dao;

import vn.vnrailway.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface UserRepository {
    Optional<User> findById(int userId) throws SQLException;

    Optional<User> findByEmail(String email) throws SQLException;

    void updateAvatar(int userId, String avatarPath) throws SQLException;

    Optional<User> findByPhone(String phone) throws SQLException;

    Optional<User> findByIdCardNumber(String idCardNumber) throws SQLException;

    List<User> findAll() throws SQLException;

    List<User> findByRole(String role) throws SQLException;

    User save(User user) throws SQLException; // Returns the saved user, possibly with generated ID

    boolean update(User user) throws SQLException;

    boolean deleteById(int userId) throws SQLException;

    List<User> findByAddress(String address) throws SQLException;

    List<User> findByGender(String gender) throws SQLException;

    Optional<User> findByDateOfBirth(LocalDate dateOfBirth) throws SQLException;

    boolean lockById(int userId) throws SQLException;

    boolean unlockById(int userId) throws SQLException;

    boolean hideById(int userId) throws SQLException;

    List<Object[]> getLogsByPage(int page, int pageSize, String searchTerm, String action, String startDate,
            String endDate) throws SQLException;

    int getTotalLogCount(String searchTerm, String action, String startDate, String endDate) throws SQLException;

    Optional<User> getUserByBookingCode(String bookingCode) throws SQLException;
    // Optional: A method to verify credentials, though this might also fit in a
    // service
    // Optional<User> findByEmailAndPassword(String email, String passwordHash)
    // throws SQLException;
}
