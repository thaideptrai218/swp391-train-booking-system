package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.TrainRepository;
import vn.vnrailway.model.Train;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainRepositoryImpl implements TrainRepository {

    private Train mapResultSetToTrain(ResultSet rs) throws SQLException {
        Train train = new Train();
        train.setTrainID(rs.getInt("TrainID"));
        train.setTrainName(rs.getString("TrainName"));
        train.setTrainTypeID(rs.getInt("TrainTypeID"));
        train.setActive(rs.getBoolean("IsActive"));
        return train;
    }

    @Override
    public Optional<Train> findById(int trainId) throws SQLException {
        String sql = "SELECT TrainID, TrainName, TrainTypeID, IsActive FROM Trains WHERE TrainID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTrain(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Train> findByTrainName(String trainName) throws SQLException {
        String sql = "SELECT TrainID, TrainName, TrainTypeID, IsActive FROM Trains WHERE TrainName = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trainName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTrain(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Train> findAll() throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT TrainID, TrainName, TrainTypeID, IsActive FROM Trains";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                trains.add(mapResultSetToTrain(rs));
            }
        }
        return trains;
    }

    @Override
    public List<Train> findByTrainTypeId(int trainTypeId) throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT TrainID, TrainName, TrainTypeID, IsActive FROM Trains WHERE TrainTypeID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trains.add(mapResultSetToTrain(rs));
                }
            }
        }
        return trains;
    }

    @Override
    public List<Train> findByStatus(boolean isActive) throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT TrainID, TrainName, TrainTypeID, IsActive FROM Trains WHERE IsActive = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trains.add(mapResultSetToTrain(rs));
                }
            }
        }
        return trains;
    }

    @Override
    public Train save(Train train) throws SQLException {
        String sql = "INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, train.getTrainName());
            ps.setInt(2, train.getTrainTypeID());
            ps.setBoolean(3, train.isActive());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating train failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    train.setTrainID(generatedKeys.getInt(1));
                } else {
                    System.err.println("Creating train succeeded, but no ID was obtained. TrainID might not be auto-generated or configured to be returned.");
                }
            }
        }
        return train;
    }

    @Override
    public boolean update(Train train) throws SQLException {
        String sql = "UPDATE Trains SET TrainName = ?, TrainTypeID = ?, IsActive = ? WHERE TrainID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, train.getTrainName());
            ps.setInt(2, train.getTrainTypeID());
            ps.setBoolean(3, train.isActive());
            ps.setInt(4, train.getTrainID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int trainId) throws SQLException {
        String sql = "DELETE FROM Trains WHERE TrainID = ?";
        // Alternatively, to mark as inactive:
        // String sql = "UPDATE Trains SET IsActive = 0 WHERE TrainID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, trainId);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        TrainRepository trainRepository = new TrainRepositoryImpl();
        try {
            // Test findAll
            System.out.println("Testing findAll trains:");
            List<Train> trains = trainRepository.findAll();
            if (trains.isEmpty()) {
                System.out.println("No trains found.");
            } else {
                trains.forEach(t -> System.out.println(t));
            }

            // Test findById (assuming train with ID 1 exists)
            int testTrainId = 1; // Ensure this ID exists in your DB
            System.out.println("\nTesting findById for train ID: " + testTrainId);
            Optional<Train> trainOpt = trainRepository.findById(testTrainId);
            trainOpt.ifPresentOrElse(
                t -> System.out.println("Found train: " + t),
                () -> System.out.println("Train with ID " + testTrainId + " not found.")
            );

            // Test findByTrainName
            String testTrainName = "SE1"; // Ensure this name exists in your DB
            System.out.println("\nTesting findByTrainName for train name: " + testTrainName);
            Optional<Train> trainByNameOpt = trainRepository.findByTrainName(testTrainName);
            trainByNameOpt.ifPresentOrElse(
                t -> System.out.println("Found train by name: " + t),
                () -> System.out.println("Train with name '" + testTrainName + "' not found.")
            );

            // Example of saving a new train (uncomment and modify to test)
            /*
            System.out.println("\nTesting save new train:");
            // Assuming TrainType with ID 1 exists for this test
            Train newTrain = new Train(0, "TESTTRAIN_MAIN", 1, true); 
            Train savedTrain = trainRepository.save(newTrain);
            System.out.println("Saved train: " + savedTrain);

            if (savedTrain.getTrainID() > 0) {
                // Example of updating the train
                System.out.println("\nTesting update train ID: " + savedTrain.getTrainID());
                savedTrain.setActive(false);
                boolean updated = trainRepository.update(savedTrain);
                System.out.println("Update successful: " + updated);

                Optional<Train> updatedTrainOpt = trainRepository.findById(savedTrain.getTrainID());
                updatedTrainOpt.ifPresent(t -> System.out.println("Updated train details: " + t));

                // Example of deleting the train
                System.out.println("\nTesting delete train ID: " + savedTrain.getTrainID());
                boolean deleted = trainRepository.deleteById(savedTrain.getTrainID());
                System.out.println("Delete successful: " + deleted);
            }
            */

        } catch (SQLException e) {
            System.err.println("Error testing TrainRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
