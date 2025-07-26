package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.CoachRepository;
import vn.vnrailway.model.Coach;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public void addCoach(Coach coach) throws SQLException {
        String sql = "INSERT INTO Coaches (TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coach.getTrainID());
            ps.setInt(2, coach.getCoachNumber());
            ps.setString(3, coach.getCoachName());
            ps.setInt(4, coach.getCoachTypeID());
            ps.setInt(5, coach.getCapacity());
            ps.setInt(6, coach.getPositionInTrain());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean updateCoach(Coach coach) throws SQLException {
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
            return ps.executeUpdate() > 0;
        }
    }

    // Kiểm tra xem coach có ghế nào còn vé tham chiếu không
    public boolean hasTicketsInCoach(int coachId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Tickets t JOIN Seats s ON t.SeatID = s.SeatID WHERE s.CoachID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteCoach(int id) throws SQLException {
        if (hasTicketsInCoach(id)) {
            throw new IllegalStateException("Không thể xóa toa này vì đang có vé tham chiếu!");
        }
        String sql = "DELETE FROM Coaches WHERE CoachID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Coach> getAllCoaches() throws SQLException {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT * FROM Coaches";
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
    public Coach getCoachById(int id) throws SQLException {
        String sql = "SELECT * FROM Coaches WHERE CoachID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCoach(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Coach> findByTrainIdOrderByPositionInTrainDesc(int trainId) throws SQLException {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT * FROM Coaches WHERE TrainID = ? ORDER BY PositionInTrain DESC";
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
}
