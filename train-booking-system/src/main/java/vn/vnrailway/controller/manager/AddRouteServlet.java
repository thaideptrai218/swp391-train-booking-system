package vn.vnrailway.controller.manager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.model.Station;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/manager/addRoute")
public class AddRouteServlet extends HttpServlet {
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
            List<Station> allStations = stationRepository.findAll();
            request.setAttribute("allStations", allStations);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Error fetching stations: " + e.getMessage());
        }
        request.getRequestDispatcher("/WEB-INF/jsp/manager/Route/addRoute.jsp").forward(request, response);
    }
}
