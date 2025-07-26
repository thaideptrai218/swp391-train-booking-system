package vn.vnrailway.dao;

import vn.vnrailway.model.Route;
import vn.vnrailway.model.Station;
import vn.vnrailway.controller.manager.ManageRoutesServlet.StationOrderUpdateDTO;
import vn.vnrailway.dto.RouteStationDetailDTO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RouteRepository {
        Optional<Route> findById(int routeId) throws SQLException;

        List<Route> findAll() throws SQLException;

        Route save(Route route) throws SQLException;

        boolean update(Route route) throws SQLException;

        Optional<Route> findByRouteName(String routeName) throws SQLException;

        List<RouteStationDetailDTO> getAllRouteStationDetails() throws SQLException;

        void addStationToRoute(int routeId, int stationId, int sequenceNumber, BigDecimal distanceFromStart,
                        int defaultStopTime) throws SQLException;

        boolean updateRouteStation(int routeId, int stationId, int sequenceNumber, BigDecimal distanceFromStart,
                        int defaultStopTime) throws SQLException;

        boolean removeStationFromRoute(int routeId, int stationId) throws SQLException;

        List<Station> getAllStations() throws SQLException;

        boolean updateRouteStationOrder(int routeId, List<StationOrderUpdateDTO> stationsOrder)
                        throws SQLException;

        int getNextSequenceNumberForRoute(int routeId) throws SQLException;

        List<RouteStationDetailDTO> findStationDetailsByRouteId(int routeId) throws SQLException;

        void incrementSequenceNumbersFrom(int routeId, int fromSequenceNumber) throws SQLException;

        boolean updateRouteActive(int routeId, boolean isActive) throws SQLException;

        List<Route> findAllByActive(Boolean isActive) throws SQLException;
}
