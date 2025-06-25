package vn.vnrailway.dao.impl;

import vn.vnrailway.dao.PassengerDAO;
import vn.vnrailway.model.Passenger;
// import vn.vnrailway.config.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types; // For setting NULL for Date and INTEGER
import java.sql.Date; // For java.time.LocalDate mapping
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PassengerDAOImpl implements PassengerDAO {

    private Passenger mapResultSetToPassenger(ResultSet rs) throws SQLException {
        Passenger p = new Passenger();
        p.setPassengerID(rs.getInt("PassengerID"));
        p.setFullName(rs.getString("FullName"));
        p.setIdCardNumber(rs.getString("IDCardNumber")); // Corrected getter name
        p.setPassengerTypeID(rs.getInt("PassengerTypeID"));
        
        Date dobSql = rs.getDate("DateOfBirth");
        if (dobSql != null) {
            p.setDateOfBirth(dobSql.toLocalDate());
        } else {
            p.setDateOfBirth(null);
        }
        
        int userId = rs.getInt("UserID");
        if (rs.wasNull()) {
            p.setUserID(null); // Model uses Integer for UserID to allow null
        } else {
            p.setUserID(userId);
        }
        return p;
    }

    @Override
    public long insertPassenger(Connection conn, Passenger passenger) throws SQLException {
        String sql = "INSERT INTO dbo.Passengers (FullName, IDCardNumber, PassengerTypeID, DateOfBirth, UserID) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, passenger.getFullName());
            
            if (passenger.getIdCardNumber() != null) { // Corrected getter name
                ps.setString(2, passenger.getIdCardNumber());
            } else {
                ps.setNull(2, Types.NVARCHAR);
            }
            
            ps.setInt(3, passenger.getPassengerTypeID());
            
            if (passenger.getDateOfBirth() != null) {
                ps.setDate(4, Date.valueOf(passenger.getDateOfBirth()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            
            if (passenger.getUserID() != null) { 
                ps.setInt(5, passenger.getUserID());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1); // PassengerID
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public Passenger findById(Connection conn, int passengerId) throws SQLException {
        String sql = "SELECT * FROM dbo.Passengers WHERE PassengerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPassenger(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean deletePassengerById(Connection conn, int passengerId) throws SQLException {
        String sql = "DELETE FROM dbo.Passengers WHERE PassengerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
}
