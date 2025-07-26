package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.core.type.TypeReference; // Added for Jackson
import com.fasterxml.jackson.databind.ObjectMapper; // Added for Jackson

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.RouteRepository;
import vn.vnrailway.dao.impl.RouteRepositoryImpl;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.dto.RouteStationDetailDTO;
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.model.Route;
import vn.vnrailway.model.Station;
import vn.vnrailway.dao.TripRepository; // Added
import vn.vnrailway.dao.impl.TripRepositoryImpl; // Added

@WebServlet("/manageRoutes")
public class ManageRoutesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RouteRepository routeRepository;
    private TripRepository tripRepository;

    public ManageRoutesServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.routeRepository = new RouteRepositoryImpl();
        this.tripRepository = new TripRepositoryImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "editRoute":
                    showEditRouteForm(request, response);
                    break;
                case "showEditForm":
                    showEditFormOnManageRoutesPage(request, response);
                    break;
                case "editRouteStation":
                    showEditRouteStationForm(request, response);
                    break;
                default:
                    listRoutesAndStations(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in ManageRoutesServlet doGet", e);
        }
    }

    private void listRoutesAndStations(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String activeFilter = request.getParameter("activeFilter");
        Boolean isActive = null;
        if (activeFilter == null || activeFilter.isEmpty() || "active".equals(activeFilter)) {
            isActive = true;
        } else if ("inactive".equals(activeFilter)) {
            isActive = false;
        }

        List<RouteStationDetailDTO> routeDetails = routeRepository.getAllRouteStationDetails();
        List<Station> allStations = routeRepository.getAllStations().stream().toList();
        List<Route> allRoutes = routeRepository.findAllByActive(isActive);

        request.setAttribute("routeDetails", routeDetails);
        request.setAttribute("allStations", allStations);
        request.setAttribute("allRoutes", allRoutes);
        request.setAttribute("activeFilter", activeFilter == null ? "active" : activeFilter);
        request.getRequestDispatcher("/WEB-INF/jsp/manager/Route/manageRoutes.jsp").forward(request, response);
    }

    private void showEditRouteForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ServletException("Route not found with ID: " + routeId));
        request.setAttribute("routeToEdit", route);
        listRoutesAndStations(request, response); // To repopulate lists for the main page view
    }

    private void showEditRouteStationForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        int stationId = Integer.parseInt(request.getParameter("stationId"));

        // Find the specific RouteStationDetailDTO to edit
        RouteStationDetailDTO routeStationToEdit = routeRepository.getAllRouteStationDetails().stream()
                .filter(rsd -> rsd.getRouteID() == routeId && rsd.getStationID() == stationId)
                .findFirst()
                .orElseThrow(() -> new ServletException(
                        "RouteStation not found for Route ID: " + routeId + " and Station ID: " + stationId));

        request.setAttribute("routeStationToEdit", routeStationToEdit);
        listRoutesAndStations(request, response); // To repopulate lists for the main page view
    }

    private void showEditFormOnManageRoutesPage(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            int routeId = Integer.parseInt(request.getParameter("routeId"));
            Route routeToEdit = routeRepository.findById(routeId)
                    .orElseThrow(() -> new ServletException(
                            "Route not found with ID: " + routeId + " for editing on manage routes page."));
            request.setAttribute("routeToEditOnPage", routeToEdit);

            // Fetch stations for this route to display start and end points
            List<RouteStationDetailDTO> stationsInRoute = routeRepository.findStationDetailsByRouteId(routeId);
            if (stationsInRoute != null && !stationsInRoute.isEmpty()) {
                // Assuming stations are ordered by sequence number
                request.setAttribute("departureStationIdForEdit", stationsInRoute.get(0).getStationID());
                request.setAttribute("arrivalStationIdForEdit",
                        stationsInRoute.get(stationsInRoute.size() - 1).getStationID());
                // Also pass names for display if needed, though dropdowns will handle it
                request.setAttribute("departureStationNameForEdit", stationsInRoute.get(0).getStationName());
                request.setAttribute("arrivalStationNameForEdit",
                        stationsInRoute.get(stationsInRoute.size() - 1).getStationName());
            } else {
                // This case should ideally not happen if routes are created with start/end
                // stations.
                // If it can, we might need a different way to determine original stations or
                // prevent editing.
                request.setAttribute("errorMessage", "Không thể xác định trạm đầu/cuối cho tuyến này để chỉnh sửa.");
            }
            // Ensure allStations is available for the dropdowns
            request.setAttribute("allStations",
                    routeRepository.getAllStations().stream().filter(Station::isActive).toList());

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Route ID for editing.");
        } catch (ServletException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        // Still need to populate other lists for the page
        listRoutesAndStations(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/manageRoutes");
            return;
        }

        try {
            switch (action) {
                case "addRoute":
                    addRoute(request, response);
                    break;
                case "updateRoute":
                    updateRoute(request, response);
                    break;
                case "addStationToRoute":
                    addStationToRoute(request, response);
                    break;
                case "updateRouteStation":
                    updateRouteStation(request, response);
                    break;
                case "removeStationFromRoute":
                    removeStationFromRoute(request, response);
                    break;
                case "updateStationOrder":
                    updateStationOrder(request, response);
                    break;
                case "updateRouteActive":
                    int routeId = Integer.parseInt(request.getParameter("routeId"));
                    boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));
                    routeRepository.updateRouteActive(routeId, isActive);
                    // Nếu route bị khóa (isActive == false), cập nhật trạng thái các trip liên quan
                    if (!isActive) {
                        try {
                            List<vn.vnrailway.model.Trip> trips = tripRepository.findByRouteId(routeId);
                            vn.vnrailway.dao.TicketRepository ticketRepository = new vn.vnrailway.dao.impl.TicketRepositoryImpl();
                            vn.vnrailway.dao.BookingRepository bookingRepository = new vn.vnrailway.dao.impl.BookingRepositoryImpl();
                            vn.vnrailway.dao.UserRepository userRepository = new vn.vnrailway.dao.impl.UserRepositoryImpl();
                            java.util.Set<String> sentEmails = new java.util.HashSet<>();
                            for (vn.vnrailway.model.Trip trip : trips) {
                                tripRepository.updateTripStatus(trip.getTripID(), "Cancelled");
                                java.util.List<vn.vnrailway.model.Ticket> tickets = ticketRepository
                                        .findByTripId(trip.getTripID());
                                try (java.sql.Connection conn = vn.vnrailway.config.DBContext.getConnection()) {
                                    conn.setAutoCommit(false);
                                    // 1. Cập nhật TicketStatus = 'Cancelled' cho tất cả vé
                                    String updateTicketStatusSql = "UPDATE Tickets SET TicketStatus = 'Cancelled' WHERE TripID = ?";
                                    try (java.sql.PreparedStatement ps = conn.prepareStatement(updateTicketStatusSql)) {
                                        ps.setInt(1, trip.getTripID());
                                        ps.executeUpdate();
                                    }
                                    // 2. Cập nhật TempRefundRequests: AppliedPolicyID = null, FeeAmount = 0,
                                    // RequestedAt = null
                                    String updateTempRefundSql = "UPDATE TempRefundRequests SET AppliedPolicyID = NULL, FeeAmount = 0, RequestedAt = NULL WHERE TicketID = ?";
                                    try (java.sql.PreparedStatement ps = conn.prepareStatement(updateTempRefundSql)) {
                                        for (vn.vnrailway.model.Ticket t : tickets) {
                                            ps.setInt(1, t.getTicketID());
                                            ps.addBatch();
                                        }
                                        ps.executeBatch();
                                    }
                                    conn.commit();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                // Gửi email cho user đã đặt vé
                                java.util.Set<Integer> bookingIds = new java.util.HashSet<>();
                                for (vn.vnrailway.model.Ticket ticket : tickets) {
                                    bookingIds.add(ticket.getBookingID());
                                }
                                for (Integer bookingId : bookingIds) {
                                    java.util.Optional<vn.vnrailway.model.Booking> bookingOpt = bookingRepository
                                            .findById(bookingId);
                                    if (bookingOpt.isPresent()) {
                                        vn.vnrailway.model.Booking booking = bookingOpt.get();
                                        int userId = booking.getUserID();
                                        java.util.Optional<vn.vnrailway.model.User> userOpt = userRepository
                                                .findById(userId);
                                        if (userOpt.isPresent()) {
                                            vn.vnrailway.model.User user = userOpt.get();
                                            String email = user.getEmail();
                                            if (email != null && !email.isEmpty() && !sentEmails.contains(email)) {
                                                // Gửi email hủy chuyến
                                                try {
                                                    java.util.Properties props = new java.util.Properties();
                                                    props.put("mail.smtp.host", "smtp.gmail.com");
                                                    props.put("mail.smtp.port", "587");
                                                    props.put("mail.smtp.auth", "true");
                                                    props.put("mail.smtp.starttls.enable", "true");
                                                    final String EMAIL_FROM = "assasinhp619@gmail.com";
                                                    final String EMAIL_PASSWORD = "slos bctt epxv osla";
                                                    jakarta.mail.Session mailSession = jakarta.mail.Session
                                                            .getInstance(props, new jakarta.mail.Authenticator() {
                                                                @Override
                                                                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                                                                    return new jakarta.mail.PasswordAuthentication(
                                                                            EMAIL_FROM, EMAIL_PASSWORD);
                                                                }
                                                            });
                                                    jakarta.mail.Message mimeMessage = new jakarta.mail.internet.MimeMessage(
                                                            mailSession);
                                                    mimeMessage.setFrom(new jakarta.mail.internet.InternetAddress(
                                                            EMAIL_FROM, "Vetaure", "UTF-8"));
                                                    mimeMessage.setRecipients(jakarta.mail.Message.RecipientType.TO,
                                                            jakarta.mail.internet.InternetAddress.parse(email));
                                                    mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
                                                    mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");
                                                    mimeMessage.setSubject(jakarta.mail.internet.MimeUtility.encodeText(
                                                            "Chuyến tàu của bạn đã bị hủy - Vetaure", "UTF-8", "B"));
                                                    String messageContent = """
                                                                <html>
                                                                <body style='font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9;'>
                                                                    <div style='background-color: #ffffff; padding: 20px; border-radius: 10px; max-width: 600px; margin: auto;'>
                                                                        <h2 style='color: #e74c3c;'>Chuyến tàu đã bị hủy</h2>
                                                                        <p>Xin chào,</p>
                                                                        <p>Chúng tôi xin thông báo chuyến tàu bạn đã đặt đã bị <strong>hủy</strong> vì lý do bất khả kháng.</p>
                                                                        <p>Để tiếp tục xử lý yêu cầu hoàn tiền của bạn, vui lòng phản hồi email này kèm theo <strong>số tài khoản ngân hàng</strong> để chúng tôi chuyển tiền hoàn.</p>
                                                                        <p>Xin cảm ơn!</p>
                                                                        <br/>
                                                                        <p>Trân trọng,</p>
                                                                        <p><strong>Đội ngũ Vetaure</strong></p>
                                                                    </div>
                                                                </body>
                                                                </html>
                                                            """;
                                                    mimeMessage.setContent(messageContent, "text/html; charset=UTF-8");
                                                    jakarta.mail.Transport.send(mimeMessage);
                                                    sentEmails.add(email);
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                default:
                    response.sendRedirect(request.getContextPath() + "/manageRoutes");
                    break;
            }
        } catch (SQLException e) {
            // Consider setting an error message attribute and forwarding to an error page
            // or back to the form
            e.printStackTrace(); // Log the full stack trace
            String errorMessage;
            String rawSqlErrorMessage = e.getMessage();
            int sqlErrorCode = e.getErrorCode();

            // SQL Server error code for unique key violation is 2627.
            // SQL Server error code for primary key violation is 2601.
            if (sqlErrorCode == 2627 || sqlErrorCode == 2601) {
                if (rawSqlErrorMessage != null
                        && rawSqlErrorMessage.toLowerCase().contains("uq_routestations_routesequence")) {
                    // Specific message for sequence constraint, per user's request to map it to
                    // "station already in route"
                    errorMessage = "Không thể thêm trạm đã có trong tuyến đường.";
                } else if (rawSqlErrorMessage != null
                        && (rawSqlErrorMessage.toLowerCase().contains("uq_routestations_station")
                                || rawSqlErrorMessage.toLowerCase().contains("pk_routestations"))) {
                    // Specific message for station already in route (if these are the constraint
                    // names for RouteID, StationID)
                    errorMessage = "Không thể thêm trạm đã có trong tuyến đường.";
                } else {
                    // General unique key violation, use user's desired message
                    errorMessage = "Không thể thêm trạm đã có trong tuyến đường.";
                }
            } else {
                // Generic fallback for other SQL errors
                errorMessage = "Database operation failed: " + (rawSqlErrorMessage != null ? rawSqlErrorMessage
                        : "Unknown SQL error (" + sqlErrorCode + ")");
            }

            String routeIdForError = request.getParameter("routeId");
            if (routeIdForError == null) {
                routeIdForError = request.getParameter("routeIdForStation");
            }

            if (("updateRouteStation".equals(action) || "removeStationFromRoute".equals(action)
                    || "addStationToRoute".equals(action))
                    && routeIdForError != null && !routeIdForError.isEmpty()) {
                request.getSession().setAttribute("errorMessage", errorMessage);
                response.sendRedirect(request.getContextPath() + "/manager/routeDetail?routeId=" + routeIdForError);
            } else {
                request.setAttribute("errorMessage", errorMessage);
                try {
                    listRoutesAndStations(request, response);
                } catch (Exception ex) {
                    throw new ServletException("Error handling doPost after SQL exception", ex);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            String errorMessage = "Invalid number format: " + e.getMessage();
            String routeIdForError = request.getParameter("routeId");
            if (routeIdForError == null) {
                routeIdForError = request.getParameter("routeIdForStation");
            }

            if (("updateRouteStation".equals(action) || "removeStationFromRoute".equals(action)
                    || "addStationToRoute".equals(action))
                    && routeIdForError != null && !routeIdForError.isEmpty()) {
                request.getSession().setAttribute("errorMessage", errorMessage);
                response.sendRedirect(request.getContextPath() + "/manager/routeDetail?routeId=" + routeIdForError);
            } else {
                request.setAttribute("errorMessage", errorMessage);
                try {
                    listRoutesAndStations(request, response);
                } catch (Exception ex) {
                    throw new ServletException("Error handling doPost after NumberFormatException", ex);
                }
            }
        }
    }

    private void addRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int departureStationId = Integer.parseInt(request.getParameter("departureStationId"));
        int arrivalStationId = Integer.parseInt(request.getParameter("arrivalStationId"));
        String description = request.getParameter("description");

        // Kiểm tra trạng thái hoạt động của ga
        if (!isStationActive(departureStationId) || !isStationActive(arrivalStationId)) {
            request.setAttribute("errorMessage", "Chỉ được chọn các ga đang hoạt động làm điểm đi/đến.");
            listRoutesAndStations(request, response);
            return;
        }
        // Lấy tên ga đi
        StationRepository stationRepository = new StationRepositoryImpl();
        String departureStationName = stationRepository.findById(departureStationId)
                .map(Station::getStationName)
                .orElseThrow(() -> new ServletException("Không thấy ga đi!"));
        String arrivalStationName = stationRepository.findById(arrivalStationId)
                .map(Station::getStationName)
                .orElseThrow(() -> new ServletException("Không thấy ga đến!"));
        String routeName = departureStationName + " - " + arrivalStationName;

        Optional<Route> existingRouteOpt = routeRepository.findByRouteName(routeName);
        if (existingRouteOpt.isPresent()) {
            request.setAttribute("errorMessage", "Tuyến đường '" + routeName + "' đã tồn tại.");
            listRoutesAndStations(request, response);
            return;
        }

        Route newRoute = new Route();
        newRoute.setRouteName(routeName);
        newRoute.setDescription(description);
        newRoute.setLocked(false);
        newRoute.setActive(true);
        Route savedRoute = routeRepository.save(newRoute);
        if (savedRoute.getRouteID() > 0) {
            routeRepository.addStationToRoute(savedRoute.getRouteID(), departureStationId, 1, BigDecimal.ZERO, 0);
            routeRepository.addStationToRoute(savedRoute.getRouteID(), arrivalStationId, 2, BigDecimal.ZERO, 0);
        } else {
            throw new ServletException("Could not save the new route properly, ID not generated.");
        }
        request.getSession().setAttribute("successMessage", "Tuyến đường '" + routeName + "' đã được thêm thành công!");
        response.sendRedirect(request.getContextPath() + "/manageRoutes");
    }

    private void updateRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        String description = request.getParameter("description");
        int newDepartureStationId = Integer.parseInt(request.getParameter("newDepartureStationId"));
        int newArrivalStationId = Integer.parseInt(request.getParameter("newArrivalStationId"));

        if (newDepartureStationId == newArrivalStationId) {
            request.getSession().setAttribute("errorMessage", "Điểm đi và điểm đến không được trùng nhau.");
            response.sendRedirect(request.getContextPath() + "/manageRoutes?action=showEditForm&routeId=" + routeId
                    + "#editRouteFormContainer");
            return;
        }

        Optional<Route> routeOpt = routeRepository.findById(routeId);
        if (!routeOpt.isPresent()) {
            request.getSession().setAttribute("errorMessage", "Không tìm thấy tuyến đường để cập nhật.");
            response.sendRedirect(request.getContextPath() + "/manageRoutes");
            return;
        }
        Route routeToUpdate = routeOpt.get();

        List<Station> allStations = routeRepository.getAllStations();
        String newDepartureStationName = allStations.stream()
                .filter(s -> s.getStationID() == newDepartureStationId)
                .findFirst().map(Station::getStationName)
                .orElseThrow(() -> new ServletException("Ga đi mới không tìm thấy"));
        String newArrivalStationName = allStations.stream()
                .filter(s -> s.getStationID() == newArrivalStationId)
                .findFirst().map(Station::getStationName)
                .orElseThrow(() -> new ServletException("Ga đến mới không tìm thấy"));

        String newRouteName = newDepartureStationName + " - " + newArrivalStationName;

        if (!newRouteName.equals(routeToUpdate.getRouteName())) {
            Optional<Route> existingRouteWithNewName = routeRepository.findByRouteName(newRouteName);
            if (existingRouteWithNewName.isPresent() && existingRouteWithNewName.get().getRouteID() != routeId) {
                request.getSession().setAttribute("errorMessage",
                        "Tuyến đường với tên '" + newRouteName + "' đã tồn tại.");
                response.sendRedirect(request.getContextPath() + "/manageRoutes?action=showEditForm&routeId=" + routeId
                        + "#editRouteFormContainer");
                return;
            }
        }

        routeToUpdate.setRouteName(newRouteName);
        routeToUpdate.setDescription(description);
        routeRepository.update(routeToUpdate);

        List<RouteStationDetailDTO> currentStations = routeRepository.findStationDetailsByRouteId(routeId);
        if (currentStations.isEmpty()) {
            routeRepository.addStationToRoute(routeId, newDepartureStationId, 1, BigDecimal.ZERO, 0);
            routeRepository.addStationToRoute(routeId, newArrivalStationId, 2, BigDecimal.ZERO, 0);
        } else {
            RouteStationDetailDTO currentDeparture = currentStations.get(0);
            if (currentDeparture.getStationID() != newDepartureStationId) {
                routeRepository.removeStationFromRoute(routeId, currentDeparture.getStationID());
                routeRepository.addStationToRoute(routeId, newDepartureStationId, 1, BigDecimal.ZERO, 0);
            }

            RouteStationDetailDTO currentArrival = currentStations.get(currentStations.size() - 1);
            if (currentArrival.getStationID() != newArrivalStationId) {
                routeRepository.removeStationFromRoute(routeId, currentArrival.getStationID());
                int nextSequence = routeRepository.getNextSequenceNumberForRoute(routeId);
                routeRepository.addStationToRoute(routeId, newArrivalStationId, nextSequence, BigDecimal.ZERO, 0);
            }
        }

        request.getSession().setAttribute("successMessage", "Tuyến đường đã được cập nhật thành công!");
        response.sendRedirect(request.getContextPath() + "/manageRoutes");
    }

    private void addStationToRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int routeId = Integer.parseInt(request.getParameter("routeIdForStation"));
        int stationId = Integer.parseInt(request.getParameter("stationId"));
        BigDecimal distanceFromStart = new BigDecimal(request.getParameter("distanceFromStart"));
        int defaultStopTime = Integer.parseInt(request.getParameter("defaultStopTime"));

        List<RouteStationDetailDTO> currentStations = routeRepository.findStationDetailsByRouteId(routeId);
        int targetSequenceNumber;

        if (currentStations.size() < 2) {
            targetSequenceNumber = routeRepository.getNextSequenceNumberForRoute(routeId);
            routeRepository.addStationToRoute(routeId, stationId, targetSequenceNumber, distanceFromStart,
                    defaultStopTime);
        } else {
            targetSequenceNumber = currentStations.get(currentStations.size() - 1).getSequenceNumber();
            routeRepository.incrementSequenceNumbersFrom(routeId, targetSequenceNumber);
            routeRepository.addStationToRoute(routeId, stationId, targetSequenceNumber, distanceFromStart,
                    defaultStopTime);
        }

        request.getSession().setAttribute("successMessage", "Trạm đã được thêm vào tuyến thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/routeDetail?routeId=" + routeId);
    }

    private void updateRouteStation(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        int originalStationId = Integer.parseInt(request.getParameter("originalStationId"));
        int newStationId = Integer.parseInt(request.getParameter("stationId"));
        RouteStationDetailDTO existingRouteStation = routeRepository.getAllRouteStationDetails().stream()
                .filter(rsd -> rsd.getRouteID() == routeId && rsd.getStationID() == originalStationId)
                .findFirst()
                .orElseThrow(() -> new ServletException(
                        "Original RouteStation not found for Route ID: " + routeId + " and Station ID: "
                                + originalStationId));
        int sequenceNumber = existingRouteStation.getSequenceNumber();

        BigDecimal distanceFromStart = new BigDecimal(request.getParameter("distanceFromStart"));
        int defaultStopTime = Integer.parseInt(request.getParameter("defaultStopTime"));

        if (originalStationId != newStationId) {
            routeRepository.removeStationFromRoute(routeId, originalStationId);
            routeRepository.addStationToRoute(routeId, newStationId, sequenceNumber, distanceFromStart,
                    defaultStopTime);
        } else {
            routeRepository.updateRouteStation(routeId, originalStationId, sequenceNumber, distanceFromStart,
                    defaultStopTime);
        }
        request.getSession().setAttribute("successMessage", "Trạm trong tuyến đã được cập nhật thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/routeDetail?routeId=" + routeId);
    }

    private void removeStationFromRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        int stationId = Integer.parseInt(request.getParameter("stationId"));
        routeRepository.removeStationFromRoute(routeId, stationId);
        // Redirect back to the route detail page
        request.getSession().setAttribute("successMessage", "Trạm đã được xóa khỏi tuyến thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/routeDetail?routeId=" + routeId);
    }

    private void updateStationOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse;

        try {
            int routeId = Integer.parseInt(request.getParameter("routeId"));
            String stationsOrderJson = request.getParameter("stationsOrder");

            if (stationsOrderJson == null || stationsOrderJson.isEmpty()) {
                throw new IllegalArgumentException("Stations order data is missing.");
            }

            TypeReference<List<StationOrderUpdateDTO>> typeRef = new TypeReference<List<StationOrderUpdateDTO>>() {
            };
            List<StationOrderUpdateDTO> stationsOrder = objectMapper.readValue(stationsOrderJson, typeRef);

            boolean success = routeRepository.updateRouteStationOrder(routeId, stationsOrder);

            if (success) {
                jsonResponse = objectMapper.writeValueAsString(
                        Map.of("success", true, "message", "Thứ tự trạm đã được cập nhật thành công."));
            } else {
                jsonResponse = objectMapper.writeValueAsString(
                        Map.of("success", false, "message",
                                "Không thể cập nhật thứ tự trạm trong cơ sở dữ liệu. Một hoặc nhiều bản ghi không thành công."));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            jsonResponse = objectMapper.writeValueAsString(
                    Map.of("success", false, "message", "Lỗi định dạng ID tuyến đường: " + e.getMessage()));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            jsonResponse = objectMapper.writeValueAsString(
                    Map.of("success", false, "message", "Dữ liệu không hợp lệ: " + e.getMessage()));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) { // Catch Jackson's JsonProcessingException
            e.printStackTrace();
            jsonResponse = objectMapper.writeValueAsString(
                    Map.of("success", false, "message", "Lỗi xử lý dữ liệu yêu cầu (JSON): " + e.getMessage()));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse = objectMapper.writeValueAsString(
                    Map.of("success", false, "message", "Lỗi cơ sở dữ liệu: " + e.getMessage()));
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.getWriter().write(jsonResponse);
    }

    private boolean isStationActive(int stationId) throws SQLException {
        String sql = "SELECT IsActive FROM Stations WHERE StationID = ?";
        try (java.sql.Connection conn = vn.vnrailway.config.DBContext.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("IsActive");
                }
            }
        }
        return false;
    }

    public static class StationOrderUpdateDTO {
        private int stationId;
        private int sequenceNumber;

        public StationOrderUpdateDTO() {
        }

        public StationOrderUpdateDTO(int stationId, int sequenceNumber) {
            this.stationId = stationId;
            this.sequenceNumber = sequenceNumber;
        }

        public int getStationId() {
            return stationId;
        }

        public void setStationId(int stationId) {
            this.stationId = stationId;
        }

        public int getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
    }
}
