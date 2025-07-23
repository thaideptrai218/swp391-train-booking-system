package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.VIPCardTypeRepository;
import vn.vnrailway.model.VIPCardType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of VIPCardTypeRepository for database operations.
 */
public class VIPCardTypeRepositoryImpl implements VIPCardTypeRepository {

    /**
     * Maps ResultSet to VIPCardType object
     */
    private VIPCardType mapResultSetToVIPCardType(ResultSet rs) throws SQLException {
        VIPCardType vipCardType = new VIPCardType();
        vipCardType.setVipCardTypeID(rs.getInt("VIPCardTypeID"));
        vipCardType.setTypeName(rs.getString("TypeName"));
        vipCardType.setPrice(rs.getBigDecimal("Price"));
        vipCardType.setDiscountPercentage(rs.getBigDecimal("DiscountPercentage"));
        vipCardType.setDurationMonths(rs.getInt("DurationMonths"));
        vipCardType.setDescription(rs.getString("Description"));
        return vipCardType;
    }

    @Override
    public List<VIPCardType> findAll() throws SQLException {
        List<VIPCardType> vipCardTypes = new ArrayList<>();
        String sql = "SELECT VIPCardTypeID, TypeName, Price, DiscountPercentage, DurationMonths, Description " +
                    "FROM VIPCardTypes ORDER BY Price ASC";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            
            while (rs.next()) {
                vipCardTypes.add(mapResultSetToVIPCardType(rs));
            }
        }
        return vipCardTypes;
    }

    @Override
    public Optional<VIPCardType> findById(int vipCardTypeID) throws SQLException {
        String sql = "SELECT VIPCardTypeID, TypeName, Price, DiscountPercentage, DurationMonths, Description " +
                    "FROM VIPCardTypes WHERE VIPCardTypeID = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, vipCardTypeID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVIPCardType(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<VIPCardType> findByTypeName(String typeName) throws SQLException {
        String sql = "SELECT VIPCardTypeID, TypeName, Price, DiscountPercentage, DurationMonths, Description " +
                    "FROM VIPCardTypes WHERE TypeName = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, typeName);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVIPCardType(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public VIPCardType create(VIPCardType vipCardType) throws SQLException {
        String sql = "INSERT INTO VIPCardTypes (TypeName, Price, DiscountPercentage, DurationMonths, Description) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, vipCardType.getTypeName());
            statement.setBigDecimal(2, vipCardType.getPrice());
            statement.setBigDecimal(3, vipCardType.getDiscountPercentage());
            statement.setInt(4, vipCardType.getDurationMonths());
            statement.setString(5, vipCardType.getDescription());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating VIP card type failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vipCardType.setVipCardTypeID(generatedKeys.getInt(1));
                    return vipCardType;
                } else {
                    throw new SQLException("Creating VIP card type failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public boolean update(VIPCardType vipCardType) throws SQLException {
        String sql = "UPDATE VIPCardTypes SET TypeName = ?, Price = ?, DiscountPercentage = ?, " +
                    "DurationMonths = ?, Description = ? WHERE VIPCardTypeID = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, vipCardType.getTypeName());
            statement.setBigDecimal(2, vipCardType.getPrice());
            statement.setBigDecimal(3, vipCardType.getDiscountPercentage());
            statement.setInt(4, vipCardType.getDurationMonths());
            statement.setString(5, vipCardType.getDescription());
            statement.setInt(6, vipCardType.getVipCardTypeID());
            
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int vipCardTypeID) throws SQLException {
        String sql = "DELETE FROM VIPCardTypes WHERE VIPCardTypeID = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, vipCardTypeID);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsById(int vipCardTypeID) throws SQLException {
        String sql = "SELECT 1 FROM VIPCardTypes WHERE VIPCardTypeID = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, vipCardTypeID);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<VIPCardType> findByPriceRange(double minPrice, double maxPrice) throws SQLException {
        List<VIPCardType> vipCardTypes = new ArrayList<>();
        String sql = "SELECT VIPCardTypeID, TypeName, Price, DiscountPercentage, DurationMonths, Description " +
                    "FROM VIPCardTypes WHERE Price BETWEEN ? AND ? ORDER BY Price ASC";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setBigDecimal(1, BigDecimal.valueOf(minPrice));
            statement.setBigDecimal(2, BigDecimal.valueOf(maxPrice));
            
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    vipCardTypes.add(mapResultSetToVIPCardType(rs));
                }
            }
        }
        return vipCardTypes;
    }

    @Override
    public List<VIPCardType> findByDuration(int durationMonths) throws SQLException {
        List<VIPCardType> vipCardTypes = new ArrayList<>();
        String sql = "SELECT VIPCardTypeID, TypeName, Price, DiscountPercentage, DurationMonths, Description " +
                    "FROM VIPCardTypes WHERE DurationMonths = ? ORDER BY Price ASC";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, durationMonths);
            
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    vipCardTypes.add(mapResultSetToVIPCardType(rs));
                }
            }
        }
        return vipCardTypes;
    }
}