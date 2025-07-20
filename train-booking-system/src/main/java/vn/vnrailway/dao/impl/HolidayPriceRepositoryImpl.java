package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.HolidayPriceRepository;
import vn.vnrailway.model.HolidayPrice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HolidayPriceRepositoryImpl implements HolidayPriceRepository {
    private Connection connection;

    public HolidayPriceRepositoryImpl() throws Exception {
        this.connection = new DBContext().getConnection();
    }

    @Override
    public List<HolidayPrice> getAllHolidayPrices() {
        List<HolidayPrice> holidayPrices = new ArrayList<>();
        String sql = "SELECT * FROM HolidayPrices";
        System.out.println("Executing query: " + sql);
        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.println("Found a holiday price in the database.");
                HolidayPrice holidayPrice = new HolidayPrice();
                holidayPrice.setId(rs.getInt("HolidayID"));
                holidayPrice.setHolidayName(rs.getString("HolidayName"));
                holidayPrice.setDescription(rs.getString("Description"));
                holidayPrice.setStartDate(rs.getDate("StartDate").toLocalDate());
                holidayPrice.setEndDate(rs.getDate("EndDate").toLocalDate());
                holidayPrice.setDiscountPercentage(rs.getFloat("DiscountPercentage"));
                holidayPrice.setActive(rs.getBoolean("IsActive"));
                holidayPrices.add(holidayPrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Returning " + holidayPrices.size() + " holiday prices.");
        return holidayPrices;
    }

    @Override
    public void updateHolidayStatus(int id, boolean isActive) {
        String sql = "UPDATE HolidayPrices SET IsActive = ? WHERE HolidayID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<HolidayPrice> getActiveHolidayPrices() {
        List<HolidayPrice> holidayPrices = new ArrayList<>();
        String sql = "SELECT * FROM HolidayPrices WHERE IsActive = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HolidayPrice holidayPrice = new HolidayPrice();
                holidayPrice.setId(rs.getInt("HolidayID"));
                holidayPrice.setHolidayName(rs.getString("HolidayName"));
                holidayPrice.setDescription(rs.getString("Description"));
                holidayPrice.setStartDate(rs.getDate("StartDate").toLocalDate());
                holidayPrice.setEndDate(rs.getDate("EndDate").toLocalDate());
                holidayPrice.setDiscountPercentage(rs.getFloat("DiscountPercentage"));
                holidayPrice.setActive(rs.getBoolean("IsActive"));
                holidayPrices.add(holidayPrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidayPrices;
    }

    @Override
    public List<HolidayPrice> searchHolidayPriceByName(String name) {
        List<HolidayPrice> holidayPrices = new ArrayList<>();
        String sql = "SELECT * FROM HolidayPrices WHERE HolidayName LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HolidayPrice holidayPrice = new HolidayPrice();
                    holidayPrice.setId(rs.getInt("HolidayID"));
                    holidayPrice.setHolidayName(rs.getString("HolidayName"));
                    holidayPrice.setDescription(rs.getString("Description"));
                    holidayPrice.setStartDate(rs.getDate("StartDate").toLocalDate());
                    holidayPrice.setEndDate(rs.getDate("EndDate").toLocalDate());
                    holidayPrice.setDiscountPercentage(rs.getFloat("DiscountPercentage"));
                    holidayPrice.setActive(rs.getBoolean("IsActive"));
                    holidayPrices.add(holidayPrice);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidayPrices;
    }

    @Override
    public HolidayPrice getHolidayPriceById(int id) {
        HolidayPrice holidayPrice = null;
        String sql = "SELECT * FROM HolidayPrices WHERE HolidayID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    holidayPrice = new HolidayPrice();
                    holidayPrice.setId(rs.getInt("HolidayID"));
                    holidayPrice.setHolidayName(rs.getString("HolidayName"));
                    holidayPrice.setDescription(rs.getString("Description"));
                    holidayPrice.setStartDate(rs.getDate("StartDate").toLocalDate());
                    holidayPrice.setEndDate(rs.getDate("EndDate").toLocalDate());
                    holidayPrice.setDiscountPercentage(rs.getFloat("DiscountPercentage"));
                    holidayPrice.setActive(rs.getBoolean("IsActive"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidayPrice;
    }

    @Override
    public void addHolidayPrice(HolidayPrice holidayPrice) {
        String sql = "INSERT INTO HolidayPrices (HolidayName, Description, StartDate, EndDate, DiscountPercentage, IsActive) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, holidayPrice.getHolidayName());
            ps.setString(2, holidayPrice.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(holidayPrice.getStartDate()));
            ps.setDate(4, java.sql.Date.valueOf(holidayPrice.getEndDate()));
            ps.setFloat(5, holidayPrice.getDiscountPercentage());
            ps.setBoolean(6, holidayPrice.isActive());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateHolidayPrice(HolidayPrice holidayPrice) {
        String sql = "UPDATE HolidayPrices SET HolidayName = ?, Description = ?, StartDate = ?, EndDate = ?, DiscountPercentage = ?, IsActive = ? WHERE HolidayID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, holidayPrice.getHolidayName());
            ps.setString(2, holidayPrice.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(holidayPrice.getStartDate()));
            ps.setDate(4, java.sql.Date.valueOf(holidayPrice.getEndDate()));
            ps.setFloat(5, holidayPrice.getDiscountPercentage());
            ps.setBoolean(6, holidayPrice.isActive());
            ps.setInt(7, holidayPrice.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteHolidayPrice(int id) {
        String sql = "DELETE FROM HolidayPrices WHERE HolidayID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
