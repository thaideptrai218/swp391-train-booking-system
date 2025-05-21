package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.PassengerTypeRepository;
import vn.vnrailway.model.PassengerType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal; // Import BigDecimal

public class PassengerTypeRepositoryImpl implements PassengerTypeRepository {

    private PassengerType mapResultSetToPassengerType(ResultSet rs) throws SQLException {
        PassengerType pt = new PassengerType();
        pt.setPassengerTypeID(rs.getInt("PassengerTypeID"));
        pt.setTypeName(rs.getString("TypeName"));
        pt.setDiscountPercentage(rs.getBigDecimal("DiscountPercentage"));
        pt.setDescription(rs.getString("Description"));
        pt.setRequiresDocument(rs.getBoolean("RequiresDocument"));
        return pt;
    }

    @Override
    public Optional<PassengerType> findById(int passengerTypeId) throws SQLException {
        String sql = "SELECT PassengerTypeID, TypeName, DiscountPercentage, Description, RequiresDocument FROM PassengerTypes WHERE PassengerTypeID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPassengerType(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<PassengerType> findByTypeName(String typeName) throws SQLException {
        String sql = "SELECT PassengerTypeID, TypeName, DiscountPercentage, Description, RequiresDocument FROM PassengerTypes WHERE TypeName = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, typeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPassengerType(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<PassengerType> findAll() throws SQLException {
        List<PassengerType> passengerTypes = new ArrayList<>();
        String sql = "SELECT PassengerTypeID, TypeName, DiscountPercentage, Description, RequiresDocument FROM PassengerTypes";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                passengerTypes.add(mapResultSetToPassengerType(rs));
            }
        }
        return passengerTypes;
    }

    @Override
    public PassengerType save(PassengerType passengerType) throws SQLException {
        String sql = "INSERT INTO PassengerTypes (TypeName, DiscountPercentage, Description, RequiresDocument) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, passengerType.getTypeName());
            ps.setBigDecimal(2, passengerType.getDiscountPercentage());
            ps.setString(3, passengerType.getDescription());
            ps.setBoolean(4, passengerType.isRequiresDocument());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating passenger type failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    passengerType.setPassengerTypeID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating passenger type failed, no ID obtained.");
                }
            }
        }
        return passengerType;
    }

    @Override
    public boolean update(PassengerType passengerType) throws SQLException {
        String sql = "UPDATE PassengerTypes SET TypeName = ?, DiscountPercentage = ?, Description = ?, RequiresDocument = ? WHERE PassengerTypeID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passengerType.getTypeName());
            ps.setBigDecimal(2, passengerType.getDiscountPercentage());
            ps.setString(3, passengerType.getDescription());
            ps.setBoolean(4, passengerType.isRequiresDocument());
            ps.setInt(5, passengerType.getPassengerTypeID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int passengerTypeId) throws SQLException {
        String sql = "DELETE FROM PassengerTypes WHERE PassengerTypeID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerTypeId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Main method for testing (optional)
    public static void main(String[] args) {
        PassengerTypeRepository ptRepository = new PassengerTypeRepositoryImpl();
        try {
            System.out.println("Testing PassengerTypeRepository...");
            List<PassengerType> types = ptRepository.findAll();
            types.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
