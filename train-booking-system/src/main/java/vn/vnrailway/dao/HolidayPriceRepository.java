package vn.vnrailway.dao;

import vn.vnrailway.model.HolidayPrice;

import java.util.List;

public interface HolidayPriceRepository {
    List<HolidayPrice> getAllHolidayPrices();

    List<HolidayPrice> getActiveHolidayPrices();

    void updateHolidayStatus(int id, boolean isActive);

    List<HolidayPrice> searchHolidayPriceByName(String name);

    HolidayPrice getHolidayPriceById(int id);

    void addHolidayPrice(HolidayPrice holidayPrice);

    void updateHolidayPrice(HolidayPrice holidayPrice);

    void deleteHolidayPrice(int id);
}
