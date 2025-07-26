package vn.vnrailway.controller.manager;

import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.model.Station;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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
        String activeFilter = request.getParameter("activeFilter");
        Boolean isActive = null;
        if (activeFilter == null || activeFilter.isEmpty() || "active".equals(activeFilter)) {
            isActive = true;
        } else if ("inactive".equals(activeFilter)) {
            isActive = false;
        }
        try {
            List<Station> stations = stationRepository.findByActive(isActive);
            request.setAttribute("stations", stations);
            request.setAttribute("activeFilter", activeFilter == null ? "active" : activeFilter);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi khi truy vấn: " + e.getMessage());
            e.printStackTrace();
        }
        request.getRequestDispatcher("/WEB-INF/jsp/manager/Station/manageStations.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String command = request.getParameter("command");
        String message = "";

        try {
            if (command == null) {
                message = "Không có lệnh nào được gửi.";
            } else if ("lockStation".equals(command) || "unlockStation".equals(command)) {
                int stationId = Integer.parseInt(request.getParameter("stationID"));
                boolean isLocked = "lockStation".equals(command);
                stationRepository.updateStationLocked(stationId, isLocked);
                message = isLocked ? "Ga đã được khóa." : "Ga đã được mở khóa.";

            } else if ("updateActiveStatus".equals(command)) {
                int stationId = Integer.parseInt(request.getParameter("stationID"));
                String isActiveParam = request.getParameter("isActive");
                boolean isActive = "true".equalsIgnoreCase(isActiveParam) || "1".equals(isActiveParam)
                        || "on".equalsIgnoreCase(isActiveParam);
                stationRepository.updateStationActive(stationId, isActive);
                message = isActive ? "Ga đã được kích hoạt." : "Ga đã bị vô hiệu hóa.";
            } else {
                switch (command) {
                    case "add":
                        String newStationName = request.getParameter("stationName");
                        if (stationRepository.findByStationName(newStationName).isPresent()) {
                            message = "Error: Ga với tên '" + newStationName + "' đã tồn tại.";
                        } else {
                            Station newStation = new Station();
                            newStation.setStationName(newStationName);
                            newStation.setAddress(request.getParameter("address"));
                            newStation.setCity(request.getParameter("city"));
                            newStation.setRegion(request.getParameter("region"));
                            newStation.setPhoneNumber(request.getParameter("phoneNumber"));
                            newStation.setActive("true".equals(request.getParameter("isActive")));
                            stationRepository.save(newStation);
                            message = "Thêm ga thành công!";
                        }
                        break;
                    case "edit":
                        int editStationId = Integer.parseInt(request.getParameter("stationID"));
                        String updatedStationName = request.getParameter("stationName");

                        Optional<Station> existingStationByCode = stationRepository
                                .findByStationName(updatedStationName);
                        if (existingStationByCode.isPresent()
                                && existingStationByCode.get().getStationID() != editStationId) {
                            message = "Error: Thử lại ga '" + updatedStationName + "' đã tồn tại.";
                        } else {
                            Station existingStation = stationRepository.findById(editStationId)
                                    .orElseThrow(() -> new SQLException("Station not found for ID: " + editStationId));
                            existingStation.setStationName(request.getParameter("stationName"));
                            existingStation.setAddress(request.getParameter("address"));
                            existingStation.setCity(request.getParameter("city"));
                            existingStation.setRegion(request.getParameter("region"));
                            existingStation.setPhoneNumber(request.getParameter("phoneNumber"));
                            existingStation.setActive("true".equals(request.getParameter("isActive")));
                            stationRepository.update(existingStation);

                            message = "Station updated successfully!";
                        }
                        break;
                    case "deleteStation":
                        int delStationId = Integer.parseInt(request.getParameter("stationID"));
                        Station delStation = stationRepository.findById(delStationId)
                                .orElseThrow(() -> new SQLException("Không tìm thấy ga với ID: " + delStationId));
                        if (delStation.isActive()) {
                            message = "Chỉ có thể xóa ga khi trạng thái Hoạt động = 0.";
                        } else {
                            stationRepository.deleteById(delStationId);
                            message = "Xóa ga thành công!";
                        }
                        break;
                    default:
                        message = "Unknown command: " + command;
                        break;
                }
            }
        } catch (SQLException | NumberFormatException e) {
            message = "Lỗi khi thực hiện hành động: " + e.getMessage();
            e.printStackTrace();
        }

        request.setAttribute("message", message);
        doGet(request, response); // Refresh the list of stations and display messages
    }
}
