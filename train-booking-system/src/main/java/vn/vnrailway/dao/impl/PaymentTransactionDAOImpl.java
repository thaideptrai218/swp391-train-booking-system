package vn.vnrailway.dao.impl;

import vn.vnrailway.dao.PaymentTransactionDAO;
import vn.vnrailway.model.PaymentTransaction;
// import vn.vnrailway.config.DBContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date; // For model's Date type

public class PaymentTransactionDAOImpl implements PaymentTransactionDAO {

    private PaymentTransaction mapResultSetToPaymentTransaction(ResultSet rs) throws SQLException {
        PaymentTransaction pt = new PaymentTransaction();
        pt.setTransactionID(rs.getInt("TransactionID"));
        pt.setBookingID(rs.getInt("BookingID"));
        pt.setPaymentGatewayTransactionID(rs.getString("PaymentGatewayTransactionID"));
        pt.setPaymentGateway(rs.getString("PaymentGateway"));
        pt.setAmount(rs.getBigDecimal("Amount"));
        
        Timestamp tsDateTime = rs.getTimestamp("TransactionDateTime");
        if (tsDateTime != null) {
            pt.setTransactionDateTime(new Date(tsDateTime.getTime()));
        }
        
        pt.setStatus(rs.getString("Status"));
        pt.setNotes(rs.getString("Notes"));
        return pt;
    }

    @Override
    public long insertTransaction(Connection conn, PaymentTransaction transaction) throws SQLException {
        String sql = "INSERT INTO dbo.PaymentTransactions (BookingID, PaymentGatewayTransactionID, PaymentGateway, Amount, TransactionDateTime, Status, Notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, transaction.getBookingID());
            
            if (transaction.getPaymentGatewayTransactionID() != null) {
                ps.setString(2, transaction.getPaymentGatewayTransactionID());
            } else {
                ps.setNull(2, Types.NVARCHAR);
            }
            
            ps.setString(3, transaction.getPaymentGateway());
            ps.setBigDecimal(4, transaction.getAmount());
            
            if (transaction.getTransactionDateTime() != null) {
                ps.setTimestamp(5, new Timestamp(transaction.getTransactionDateTime().getTime()));
            } else {
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Default to now if not set
            }
            
            ps.setString(6, transaction.getStatus());
            
            if (transaction.getNotes() != null) {
                ps.setString(7, transaction.getNotes());
            } else {
                ps.setNull(7, Types.NVARCHAR);
            }
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1); // TransactionID
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public PaymentTransaction findByBookingIdAndGateway(Connection conn, int bookingId, String paymentGateway) throws SQLException {
        String sql = "SELECT * FROM dbo.PaymentTransactions WHERE BookingID = ? AND PaymentGateway = ? ORDER BY TransactionDateTime DESC"; // Get latest if multiple
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setString(2, paymentGateway);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // Return the first one (latest)
                    return mapResultSetToPaymentTransaction(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public PaymentTransaction findById(Connection conn, int transactionId) throws SQLException {
        String sql = "SELECT * FROM dbo.PaymentTransactions WHERE TransactionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPaymentTransaction(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean updateTransactionStatus(Connection conn, int bookingId, String paymentGateway, String newStatus, String gatewayTransactionId, String notes) throws SQLException {
        // Update the latest pending transaction for a booking and gateway
        String sql = "UPDATE dbo.PaymentTransactions SET Status = ?, PaymentGatewayTransactionID = ?, Notes = ?, TransactionDateTime = GETDATE() " +
                     "WHERE TransactionID = (SELECT TOP 1 TransactionID FROM dbo.PaymentTransactions " +
                                           "WHERE BookingID = ? AND PaymentGateway = ? AND Status = 'Pending' " +
                                           "ORDER BY TransactionDateTime DESC)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, gatewayTransactionId);
            ps.setString(3, notes);
            ps.setInt(4, bookingId);
            ps.setString(5, paymentGateway);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    @Override
    public boolean updateTransaction(Connection conn, PaymentTransaction transaction) throws SQLException {
        String sql = "UPDATE dbo.PaymentTransactions SET BookingID = ?, PaymentGatewayTransactionID = ?, PaymentGateway = ?, Amount = ?, TransactionDateTime = ?, Status = ?, Notes = ? WHERE TransactionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transaction.getBookingID());
            ps.setString(2, transaction.getPaymentGatewayTransactionID());
            ps.setString(3, transaction.getPaymentGateway());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setTimestamp(5, new Timestamp(transaction.getTransactionDateTime().getTime()));
            ps.setString(6, transaction.getStatus());
            ps.setString(7, transaction.getNotes());
            ps.setInt(8, transaction.getTransactionID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
}
