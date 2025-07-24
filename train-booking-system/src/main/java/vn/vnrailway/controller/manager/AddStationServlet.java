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
                String stationCode = request.getParameter("stationCode");
                String stationName = request.getParameter("stationName");
                String address = request.getParameter("address");
                String city = request.getParameter("city");
                String region = request.getParameter("region");
                String phoneNumber = request.getParameter("phoneNumber");

                String errorMessage = null;
                if (stationName == null || stationName.trim().isEmpty()) {
                    errorMessage = "Tên ga không được để trống.";
                } else if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                    errorMessage = "Số điện thoại không được để trống.";
                } else if (stationRepository.findAll().stream()
                        .anyMatch(s -> s.getStationName().equalsIgnoreCase(stationName))) {
                    errorMessage = "Tên ga đã tồn tại.";
                } else if (stationRepository.findAll().stream()
                        .anyMatch(s -> s.getPhoneNumber() != null && s.getPhoneNumber().equals(phoneNumber))) {
                    errorMessage = "Số điện thoại đã tồn tại.";
                }

                if (errorMessage != null) {
                    request.setAttribute("errorMessage", errorMessage);
                    request.setAttribute("stationCode", stationCode);
                    request.setAttribute("stationName", stationName);
                    request.setAttribute("address", address);
                    request.setAttribute("city", city);
                    request.setAttribute("region", region);
                    request.setAttribute("phoneNumber", phoneNumber);
                    request.getRequestDispatcher("/WEB-INF/jsp/manager/Station/addStation.jsp").forward(request,
                            response);
                    return;
                }

                Station newStation = new Station();
                newStation.setStationCode(stationCode);
                newStation.setStationName(stationName);
                newStation.setAddress(address);
                newStation.setCity(city);
                newStation.setRegion(region);
                newStation.setPhoneNumber(phoneNumber);
                newStation.setLocked(false);
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
