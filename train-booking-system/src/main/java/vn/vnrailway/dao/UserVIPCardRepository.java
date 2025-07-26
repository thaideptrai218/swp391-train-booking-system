package vn.vnrailway.dao;

import vn.vnrailway.model.UserVIPCard;

import java.sql.SQLException;
import java.util.Optional;

import java.util.List;

public interface UserVIPCardRepository {
    void save(UserVIPCard userVIPCard) throws SQLException;
    Optional<UserVIPCard> findByUserId(int userId) throws SQLException;
    List<UserVIPCard> findActiveByUserId(int userId) throws SQLException;
    Optional<UserVIPCard> findByTransactionReference(String transactionReference) throws SQLException;
    void deactivateAllForUser(int userId) throws SQLException;
    void create(UserVIPCard userVIPCard) throws SQLException;
    void update(UserVIPCard userVIPCard) throws SQLException;
    float getVIPDiscountPercentage(int userId) throws SQLException;
}
