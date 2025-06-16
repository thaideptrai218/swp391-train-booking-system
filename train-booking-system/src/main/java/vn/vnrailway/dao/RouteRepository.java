package vn.vnrailway.dao;

import vn.vnrailway.model.Route;
import vn.vnrailway.model.Station;
import vn.vnrailway.dto.RouteStationDetailDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RouteRepository {
        Optional<Route> findById(int routeId) throws SQLException;

        List<Route> findAll() throws SQLException;

        Route save(Route route) throws SQLException; // Returns the saved route, possibly with generated ID

        boolean update(Route route) throws SQLException; // Updates Route's own properties (name, description)

        boolean deleteById(int routeId) throws SQLException; // Deletes a route and its associated RouteStations

        Optional<Route> findByRouteName(String routeName) throws SQLException;

        // Methods for CRUD on RouteStationDetailDTO view and RouteStations
        List<RouteStationDetailDTO> getAllRouteStationDetails() throws SQLException;

        void addStationToRoute(int routeId, int stationId, int sequenceNumber, BigDecimal distanceFromStart,
                        int defaultStopTime) throws SQLException;

        boolean updateRouteStation(int routeId, int stationId, int sequenceNumber, BigDecimal distanceFromStart,
                        int defaultStopTime) throws SQLException;

        boolean removeStationFromRoute(int routeId, int stationId) throws SQLException;

        // Utility method to get all stations (for dropdowns, etc.)
        List<Station> getAllStations() throws SQLException;

        // Method to update the sequence of all stations in a route
        boolean updateRouteStationOrder(int routeId,
                        List<vn.vnrailway.controller.manager.ManageRoutesServlet.StationOrderUpdateDTO> stationsOrder)
                        throws SQLException;

        // Method to get the next available sequence number for a route
        int getNextSequenceNumberForRoute(int routeId) throws SQLException;

        List<RouteStationDetailDTO> findStationDetailsByRouteId(int routeId) throws SQLException;

        // Method to increment sequence numbers for stations from a certain point
        void incrementSequenceNumbersFrom(int routeId, int fromSequenceNumber) throws SQLException;
}
