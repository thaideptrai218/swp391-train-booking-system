package vn.vnrailway.dao;

import vn.vnrailway.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface UserRepository {
    Optional<User> findById(int userId) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    Optional<User> findByPhone(String phone) throws SQLException;
    List<User> findAll() throws SQLException;
    User save(User user) throws SQLException; // Returns the saved user, possibly with generated ID
    boolean update(User user) throws SQLException;
    boolean deleteById(int userId) throws SQLException;

    List<User> findByAddress(String address) throws SQLException;
    List<User> findByGender(String gender) throws SQLException;
    Optional<User> findByDateOfBirth(LocalDate dateOfBirth) throws SQLException;
    // Optional: A method to verify credentials, though this might also fit in a service
    // Optional<User> findByEmailAndPassword(String email, String passwordHash) throws SQLException;
}
