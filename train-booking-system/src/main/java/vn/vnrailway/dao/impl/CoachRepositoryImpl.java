package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.CoachRepository;
import vn.vnrailway.model.Coach;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoachRepositoryImpl implements CoachRepository {

    private Coach mapResultSetToCoach(ResultSet rs) throws SQLException {
        Coach coach = new Coach();
        coach.setCoachID(rs.getInt("CoachID"));
        coach.setTrainID(rs.getInt("TrainID"));
        coach.setCoachNumber(rs.getInt("CoachNumber"));
        coach.setCoachName(rs.getString("CoachName"));
        coach.setCoachTypeID(rs.getInt("CoachTypeID"));
        coach.setCapacity(rs.getInt("Capacity"));
        coach.setPositionInTrain(rs.getInt("PositionInTrain"));
        return coach;
    }

    @Override
    public Optional<Coach> findById(int coachId) throws SQLException {
        String sql = "SELECT CoachID, TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain FROM Coaches WHERE CoachID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCoach(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Coach> findAll() throws SQLException {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT CoachID, TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain FROM Coaches";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                coaches.add(mapResultSetToCoach(rs));
            }
        }
        return coaches;
    }

    @Override
    public List<Coach> findByTrainId(int trainId) throws SQLException {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT CoachID, TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain FROM Coaches WHERE TrainID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    coaches.add(mapResultSetToCoach(rs));
                }
            }
        }
        return coaches;
    }

    @Override
    public List<Coach> findByTrainIdOrderByPositionInTrainDesc(int trainId) throws SQLException {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT CoachID, TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain FROM Coaches WHERE TrainID = ? ORDER BY PositionInTrain DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    coaches.add(mapResultSetToCoach(rs));
                }
            }
        }
        return coaches;
    }

    @Override
    public List<Coach> findByCoachTypeId(int coachTypeId) throws SQLException {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT CoachID, TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain FROM Coaches WHERE CoachTypeID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    coaches.add(mapResultSetToCoach(rs));
                }
            }
        }
        return coaches;
    }
    
    @Override
    public Optional<Coach> findByTrainIdAndCoachNumber(int trainId, int coachNumber) throws SQLException {
        String sql = "SELECT CoachID, TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain FROM Coaches WHERE TrainID = ? AND CoachNumber = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainId);
            ps.setInt(2, coachNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCoach(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Coach save(Coach coach) throws SQLException {
        String sql = "INSERT INTO Coaches (TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, coach.getTrainID());
            ps.setInt(2, coach.getCoachNumber());
            ps.setString(3, coach.getCoachName());
            ps.setInt(4, coach.getCoachTypeID());
            ps.setInt(5, coach.getCapacity());
            ps.setInt(6, coach.getPositionInTrain());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating coach failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    coach.setCoachID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating coach failed, no ID obtained.");
                }
            }
        }
        return coach;
    }

    @Override
    public boolean update(Coach coach) throws SQLException {
        String sql = "UPDATE Coaches SET TrainID = ?, CoachNumber = ?, CoachName = ?, CoachTypeID = ?, Capacity = ?, PositionInTrain = ? WHERE CoachID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coach.getTrainID());
            ps.setInt(2, coach.getCoachNumber());
            ps.setString(3, coach.getCoachName());
            ps.setInt(4, coach.getCoachTypeID());
            ps.setInt(5, coach.getCapacity());
            ps.setInt(6, coach.getPositionInTrain());
            ps.setInt(7, coach.getCoachID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int coachId) throws SQLException {
        String sql = "DELETE FROM Coaches WHERE CoachID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Main method for testing (optional)
    public static void main(String[] args) {
        CoachRepository coachRepository = new CoachRepositoryImpl();
        try {
            System.out.println("Testing CoachRepository...");
            // Example: Find all coaches for TrainID 1
            List<Coach> coachesOfTrain1 = coachRepository.findByTrainId(1);
            coachesOfTrain1.forEach(System.out::println);
            
            // Add more specific tests as needed
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
