package vn.vnrailway.controller.manager;

import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.model.Station;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional; // Added this import
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageStationsServlet", urlPatterns = { "/manageStations" })
public class ManageStationsServlet extends HttpServlet {

    private StationRepository stationRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        stationRepository = new StationRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Station> stations = stationRepository.findAll();
            request.setAttribute("stations", stations);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Error retrieving stations: " + e.getMessage());
            e.printStackTrace();
        }
        request.getRequestDispatcher("/WEB-INF/jsp/manager/manageStations.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String command = request.getParameter("command");
        String message = "";

        try {
            if (command == null) {
                message = "No command specified.";
            } else {
                switch (command) {
                    case "add":
                        String newStationCode = request.getParameter("stationCode");
                        if (stationRepository.findByStationCode(newStationCode).isPresent()) {
                            message = "Error: Station with code '" + newStationCode + "' already exists.";
                        } else {
                            Station newStation = new Station();
                            newStation.setStationCode(newStationCode);
                            newStation.setStationName(request.getParameter("stationName"));
                            newStation.setAddress(request.getParameter("address"));
                            newStation.setCity(request.getParameter("city"));
                            newStation.setRegion(request.getParameter("region"));
                            newStation.setPhoneNumber(request.getParameter("phoneNumber"));
                            stationRepository.save(newStation);
                            message = "Station added successfully!";
                        }
                        break;
                    case "edit":
                        int editStationId = Integer.parseInt(request.getParameter("stationID"));
                        String updatedStationCode = request.getParameter("stationCode");

                        Optional<Station> existingStationByCode = stationRepository
                                .findByStationCode(updatedStationCode);
                        if (existingStationByCode.isPresent()
                                && existingStationByCode.get().getStationID() != editStationId) {
                            message = "Error: Another station with code '" + updatedStationCode + "' already exists.";
                        } else {
                            Station existingStation = stationRepository.findById(editStationId)
                                    .orElseThrow(() -> new SQLException("Station not found for ID: " + editStationId));
                            existingStation.setStationCode(updatedStationCode);
                            existingStation.setStationName(request.getParameter("stationName"));
                            existingStation.setAddress(request.getParameter("address"));
                            existingStation.setCity(request.getParameter("city"));
                            existingStation.setRegion(request.getParameter("region"));
                            existingStation.setPhoneNumber(request.getParameter("phoneNumber"));
                            stationRepository.update(existingStation);
                            message = "Station updated successfully!";
                        }
                        break;
                    case "delete":
                        int deleteStationId = Integer.parseInt(request.getParameter("stationID"));
                        stationRepository.deleteById(deleteStationId);
                        message = "Station deleted successfully!";
                        break;
                    default:
                        message = "Unknown command: " + command;
                        break;
                }
            }
        } catch (SQLException | NumberFormatException e) {
            message = "Error performing action: " + e.getMessage();
            e.printStackTrace();
        }

        request.setAttribute("message", message);
        doGet(request, response); // Refresh the list of stations and display messages
    }
}
