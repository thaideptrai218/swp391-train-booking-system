package vn.vnrailway.controller.manager;

import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.model.Station;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "EditStationServlet", urlPatterns = { "/editStation" })
public class EditStationServlet extends HttpServlet {

    private StationRepository stationRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        stationRepository = new StationRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String stationIdStr = request.getParameter("id");
        if (stationIdStr == null) {
            response.sendRedirect("manageStations");
            return;
        }

        try {
            int stationId = Integer.parseInt(stationIdStr);
            Optional<Station> stationOpt = stationRepository.findById(stationId);
            if (stationOpt.isPresent()) {
                request.setAttribute("station", stationOpt.get());
                request.getRequestDispatcher("/WEB-INF/jsp/manager/Station/editStation.jsp").forward(request, response);
            } else {
                response.sendRedirect("manageStations");
            }
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect("manageStations");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String command = request.getParameter("command");
        if ("edit".equals(command)) {
            try {
                int editStationId = Integer.parseInt(request.getParameter("stationID"));
                Station existingStation = stationRepository.findById(editStationId)
                        .orElseThrow(() -> new SQLException("Station not found for ID: " + editStationId));
                // Không cập nhật stationCode nữa
                existingStation.setStationName(request.getParameter("stationName"));
                existingStation.setAddress(request.getParameter("address"));
                existingStation.setCity(request.getParameter("city"));
                existingStation.setRegion(request.getParameter("region"));
                existingStation.setPhoneNumber(request.getParameter("phoneNumber"));
                stationRepository.update(existingStation);
                response.sendRedirect("manageStations?message=Station+updated+successfully!");
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                response.sendRedirect("manageStations?message=Error+updating+station.");
            }
        } else {
            response.sendRedirect("manageStations");
        }
    }
}
