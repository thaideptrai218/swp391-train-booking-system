package vn.vnrailway.controller.manager;

import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.model.Station;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AddStationServlet", urlPatterns = { "/addStation" })
public class AddStationServlet extends HttpServlet {

    private StationRepository stationRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        stationRepository = new StationRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/manager/Station/addStation.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String command = request.getParameter("command");
        if ("add".equals(command)) {
            try {
                Station newStation = new Station();
                newStation.setStationCode(null);
                newStation.setStationName(request.getParameter("stationName"));
                newStation.setAddress(request.getParameter("address"));
                newStation.setCity(request.getParameter("city"));
                newStation.setRegion(request.getParameter("region"));
                newStation.setPhoneNumber(request.getParameter("phoneNumber"));
                stationRepository.save(newStation);
                response.sendRedirect("manageStations?message=Station+added+successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect("manageStations?message=Error+adding+station.");
            }
        } else {
            response.sendRedirect("manageStations");
        }
    }
}
