package vn.vnrailway.dao;

import vn.vnrailway.model.VIPCardType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface VIPCardTypeRepository {
    Optional<VIPCardType> findById(int id) throws SQLException;
    List<VIPCardType> findAll() throws SQLException;
    void save(VIPCardType vipCardType) throws SQLException;
    void update(VIPCardType vipCardType) throws SQLException;
    void deleteById(int id) throws SQLException;
}
