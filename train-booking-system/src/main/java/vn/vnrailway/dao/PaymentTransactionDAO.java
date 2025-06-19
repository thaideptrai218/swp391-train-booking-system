package vn.vnrailway.dao;

import vn.vnrailway.model.PaymentTransaction;
import java.sql.Connection;
import java.sql.SQLException;

public interface PaymentTransactionDAO {

    /**
     * Inserts a new payment transaction record into the database.
     *
     * @param conn The database connection.
     * @param transaction The PaymentTransaction object to insert.
     * @return The generated TransactionID of the newly created record, or -1 if insertion failed.
     * @throws SQLException If a database access error occurs.
     */
    long insertTransaction(Connection conn, PaymentTransaction transaction) throws SQLException;

    /**
     * Finds a payment transaction by its associated BookingID.
     * Assumes one primary pending transaction per booking for a specific gateway.
     *
     * @param conn The database connection.
     * @param bookingId The BookingID associated with the transaction.
     * @param paymentGateway The payment gateway (e.g., "VNPAY").
     * @return The PaymentTransaction object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    PaymentTransaction findByBookingIdAndGateway(Connection conn, int bookingId, String paymentGateway) throws SQLException;
    
    /**
     * Finds a payment transaction by its primary key ID.
     * @param conn The database connection.
     * @param transactionId The ID of the transaction.
     * @return The PaymentTransaction object if found, null otherwise.
     * @throws SQLException
     */
    PaymentTransaction findById(Connection conn, int transactionId) throws SQLException;

    /**
     * Updates the status and gateway transaction ID of an existing payment transaction.
     *
     * @param conn The database connection.
     * @param bookingId The BookingID to identify the transaction (could also use transactionID).
     * @param paymentGateway The payment gateway.
     * @param newStatus The new status (e.g., "Success", "Failed").
     * @param gatewayTransactionId The transaction ID from the payment gateway.
     * @param notes Optional notes for the update.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updateTransactionStatus(Connection conn, int bookingId, String paymentGateway, String newStatus, String gatewayTransactionId, String notes) throws SQLException;

    /**
     * Updates an entire PaymentTransaction object.
     * @param conn The database connection.
     * @param transaction The PaymentTransaction object with updated information.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException
     */
    boolean updateTransaction(Connection conn, PaymentTransaction transaction) throws SQLException;
}
