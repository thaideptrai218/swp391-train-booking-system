package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.PassengerRepository;
import vn.vnrailway.model.Passenger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PassengerRepositoryImpl implements PassengerRepository {

    private Passenger mapResultSetToPassenger(ResultSet rs) throws SQLException {
        Passenger passenger = new Passenger();
        passenger.setPassengerID(rs.getInt("PassengerID"));
        passenger.setFullName(rs.getString("FullName"));
        passenger.setIdCardNumber(rs.getString("IDCardNumber"));
        passenger.setPassengerTypeID(rs.getInt("PassengerTypeID"));

        Date dob = rs.getDate("DateOfBirth");
        if (dob != null) {
            passenger.setDateOfBirth(dob.toLocalDate());
        }

        int userIdVal = rs.getInt("UserID");
        if (rs.wasNull()) {
            passenger.setUserID(null);
        } else {
            passenger.setUserID(userIdVal);
        }
        return passenger;
    }

    @Override
    public Optional<Passenger> findById(int passengerId) throws SQLException {
        String sql = "SELECT PassengerID, FullName, IDCardNumber, PassengerTypeID, DateOfBirth, UserID FROM Passengers WHERE PassengerID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPassenger(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Passenger> findAll() throws SQLException {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT PassengerID, FullName, IDCardNumber, PassengerTypeID, DateOfBirth, UserID FROM Passengers";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                passengers.add(mapResultSetToPassenger(rs));
            }
        }
        return passengers;
    }

    @Override
    public List<Passenger> findByUserId(int userId) throws SQLException {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT PassengerID, FullName, IDCardNumber, PassengerTypeID, DateOfBirth, UserID FROM Passengers WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    passengers.add(mapResultSetToPassenger(rs));
                }
            }
        }
        return passengers;
    }

    @Override
    public List<Passenger> findByIdCardNumber(String idCardNumber) throws SQLException {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT PassengerID, FullName, IDCardNumber, PassengerTypeID, DateOfBirth, UserID FROM Passengers WHERE IDCardNumber = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idCardNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    passengers.add(mapResultSetToPassenger(rs));
                }
            }
        }
        return passengers;
    }

    @Override
    public Passenger save(Passenger passenger) throws SQLException {
        String sql = "INSERT INTO Passengers (FullName, IDCardNumber, PassengerTypeID, DateOfBirth, UserID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, passenger.getFullName());
            ps.setString(2, passenger.getIdCardNumber());
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
            if (affectedRows == 0) {
                throw new SQLException("Creating passenger failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    passenger.setPassengerID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating passenger failed, no ID obtained.");
                }
            }
        }
        return passenger;
    }

    @Override
    public boolean update(Passenger passenger) throws SQLException {
        String sql = "UPDATE Passengers SET FullName = ?, IDCardNumber = ?, PassengerTypeID = ?, DateOfBirth = ?, UserID = ? WHERE PassengerID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passenger.getFullName());
            ps.setString(2, passenger.getIdCardNumber());
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
            ps.setInt(6, passenger.getPassengerID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int passengerId) throws SQLException {
        String sql = "DELETE FROM Passengers WHERE PassengerID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Main method for testing (optional)
    public static void main(String[] args) {
        PassengerRepository passengerRepository = new PassengerRepositoryImpl();
        try {
            System.out.println("Testing PassengerRepository...");
            // Example: Find all passengers for UserID 1
            List<Passenger> passengersOfUser1 = passengerRepository.findByUserId(1);
            passengersOfUser1.forEach(System.out::println);

            // Add more specific tests as needed
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
