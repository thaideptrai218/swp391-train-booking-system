package vn.vnrailway.controller.api.trip;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.dto.StationDTO;
import vn.vnrailway.model.Station; // Added import for Station model

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/stations/all")
public class GetAllStationsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private StationRepository stationRepository;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.stationRepository = new StationRepositoryImpl();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Station> stationModels = stationRepository.findAll();
            if (stationModels == null) {
                // This case should ideally not happen if findAll initializes the list properly
                System.err.println("StationRepository.findAll() returned null.");
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing station data: received null list.");
                return;
            }

            List<StationDTO> stationDTOs = new java.util.ArrayList<>();
            for (Station model : stationModels) {
                if (model == null) {
                    System.err.println("Encountered a null Station object in the list from findAll(). Skipping.");
                    continue; // Skip null models to prevent NPE during mapping
                }
                // Create StationDTO with correct field mapping for autocomplete
                stationDTOs.add(new StationDTO(model.getStationID(), model.getStationName(), model.getStationCode()));
            }
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), stationDTOs);

        } catch (SQLException e) {
            System.err.println("SQL Error in GetAllStationsServlet: " + e.getMessage());
            e.printStackTrace(); 
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error while retrieving stations.");
        } catch (Exception e) {
            System.err.println("Generic Error in GetAllStationsServlet: " + e.getMessage());
            e.printStackTrace(); 
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error while processing station data.");
        }
    }
}
