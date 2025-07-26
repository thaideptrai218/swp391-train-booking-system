package vn.vnrailway.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DashboardDAO {
    public int getTotalUsers() {
        return 0;
    }

    public int getUserCountByRole(String role) {
        return 0;
    }

    public Map<String, Integer> getGenderDistribution() {
        return null;
    }

    public Map<String, Integer> getAgeGroupDistribution() {
        return null;
    }

    public Map<String, Integer> getActiveStatusDistribution() {
        return null;
    }

    public Map<String, Integer> getLoginRatio(int days) {
        return null;
    }

    public Map<String, Integer> getDailyUserRegistrations(int days) {
        return null;
    }

    public Map<String, Integer> getMonthlyUserRegistrations(int year) {
        return null;
    }

    public List<Integer> getRegistrationYears() {
        return null;
    }

    public Map<String, Integer> getRefundRequestStatus() throws SQLException {
        return null;
    }

    public Map<String, Integer> getRefundRequestsOverTime(int days) throws SQLException {
        return null;
    }

    public Map<String, Integer> getFeedbackStatus() throws SQLException {
        return null;
    }

    public Map<String, Integer> getFeedbackByTopic() throws SQLException {
        return null;
    }

    public int getPendingRefundsCount() throws SQLException {
        return 0;
    }

    public int getPendingFeedbacksCount() throws SQLException {
        return 0;
    }

    public int getUnansweredUsersCount() throws SQLException {
        return 0;
    }

    public int getTotalTrainsCount() throws SQLException {
        return 0;
    }

    public double getTotalRevenue(int months) throws SQLException {
        return 0;
    }

    public double getExpectedRevenue() throws SQLException {
        return 0;
    }

    public double getActualRevenue(int months) throws SQLException {
        return 0;
    }

    public double getTotalRefunds() throws SQLException {
        return 0;
    }

    public Map<String, Integer> getPopularDepartureStations(int months) throws SQLException {
        return null;
    }

    public Map<String, Integer> getPopularArrivalStations(int months) throws SQLException {
        return null;
    }

    public List<Map<String, Object>> getPopularTrips() throws SQLException {
        return null;
    }

    public int getTotalTicketsSold() throws SQLException {
        return 0;
    }

    public int getRefundableTicketsCount() throws SQLException {
        return 0;
    }
}
