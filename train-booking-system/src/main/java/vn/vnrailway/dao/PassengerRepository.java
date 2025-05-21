package vn.vnrailway.dao;

import vn.vnrailway.model.Passenger;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PassengerRepository {
    Optional<Passenger> findById(int passengerId) throws SQLException;
    List<Passenger> findAll() throws SQLException;
    List<Passenger> findByUserId(int userId) throws SQLException; // Find passengers associated with a user account
    List<Passenger> findByIdCardNumber(String idCardNumber) throws SQLException;
    
    Passenger save(Passenger passenger) throws SQLException;
    boolean update(Passenger passenger) throws SQLException;
    boolean deleteById(int passengerId) throws SQLException;
}
