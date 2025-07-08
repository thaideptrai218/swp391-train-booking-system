package vn.vnrailway.dao;

import vn.vnrailway.model.PassengerType;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PassengerTypeRepository {
    Optional<PassengerType> findById(int passengerTypeId) throws SQLException;

    Optional<PassengerType> findByTypeName(String typeName) throws SQLException;

    List<PassengerType> findAll() throws SQLException;

    List<PassengerType> findAllOrderByDiscountPercentage() throws SQLException;

    PassengerType save(PassengerType passengerType) throws SQLException;

    boolean update(PassengerType passengerType) throws SQLException;

    boolean deleteById(int passengerTypeId) throws SQLException;

}
