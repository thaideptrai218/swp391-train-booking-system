package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.vnrailway.dao.RouteRepository;
import vn.vnrailway.dao.TrainRepository;
import vn.vnrailway.dao.TrainTypeRepository;
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.TripStationRepository;
import vn.vnrailway.dao.impl.RouteRepositoryImpl;
import vn.vnrailway.dao.impl.TrainRepositoryImpl;
import vn.vnrailway.dao.impl.TrainTypeRepositoryImpl;
import vn.vnrailway.dao.impl.TripRepositoryImpl;
import vn.vnrailway.dao.impl.TripStationRepositoryImpl;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.dto.ManageTripViewDTO;
import vn.vnrailway.dto.RouteStationDetailDTO;
import vn.vnrailway.model.Route;
import vn.vnrailway.model.Train;
import vn.vnrailway.model.TrainType;
import vn.vnrailway.model.Trip;
import vn.vnrailway.model.TripStation;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Optional;
import vn.vnrailway.dao.HolidayPriceRepository;
import vn.vnrailway.dao.impl.BookingRepositoryImpl;
import vn.vnrailway.dao.impl.HolidayPriceRepositoryImpl;
import vn.vnrailway.model.HolidayPrice;

@WebServlet("/manageTrips")
public class ManageTripsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TripRepository tripRepository;
    private RouteRepository routeRepository;
    private TrainRepository trainRepository;
    private TripStationRepository tripStationRepository;
    private TrainTypeRepository trainTypeRepository;
    private HolidayPriceRepository holidayPriceRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        tripRepository = new TripRepositoryImpl();
        routeRepository = new RouteRepositoryImpl();
        trainRepository = new TrainRepositoryImpl();
        tripStationRepository = new TripStationRepositoryImpl();
        trainTypeRepository = new TrainTypeRepositoryImpl(); // Added
        try {
            holidayPriceRepository = new HolidayPriceRepositoryImpl();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize HolidayPriceRepository", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        try {
            switch (action) {
                case "showAddForm":
                    showAddForm(request, response);
                    break;
                case "list":
                default:
                    listTrips(request, response);
                    break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Database access error: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/manager/manageTrips.jsp").forward(request, response);
        }
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Train> allTrains = trainRepository.getAllTrains();
        List<Route> allRoutes = routeRepository.findAll().stream().filter(Route::isActive).toList();
        List<HolidayPrice> allHolidays = holidayPriceRepository.getActiveHolidayPrices();
        request.setAttribute("allTrains", allTrains);
        request.setAttribute("allRoutes", allRoutes);
        request.setAttribute("allHolidays", allHolidays);
        request.getRequestDispatcher("/WEB-INF/jsp/manager/Trip/addTrip.jsp").forward(request, response);
    }

    private void listTrips(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String searchTerm = request.getParameter("searchTerm");
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        if (sortField == null || sortField.isEmpty()) {
            sortField = "tripID";
            sortOrder = "DESC";
        } else if ("tripID".equalsIgnoreCase(sortField) && (sortOrder == null || sortOrder.isEmpty())) {
            sortOrder = "DESC";
        } else if (sortOrder == null || sortOrder.isEmpty()
                || (!"ASC".equalsIgnoreCase(sortOrder) && !"DESC".equalsIgnoreCase(sortOrder))) {
            sortOrder = "DESC";
        }

        if (searchTerm == null) {
            searchTerm = "";
        }

        List<ManageTripViewDTO> listTrips = tripRepository.findAllForManagerView(searchTerm, sortField, sortOrder);
        String filterDate = request.getParameter("filterDate");
        if (filterDate != null && !filterDate.isEmpty()) {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
            java.time.LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);
            java.time.YearMonth thisMonth = java.time.YearMonth.now();
            listTrips = listTrips.stream().filter(trip -> {
                if (trip.getDepartureDateTime() == null)
                    return false;
                java.time.LocalDate depDate = trip.getDepartureDateTime().toLocalDate();
                switch (filterDate) {
                    case "TODAY":
                        return depDate.equals(today);
                    case "WEEK":
                        return !depDate.isBefore(startOfWeek) && !depDate.isAfter(endOfWeek);
                    case "MONTH":
                        return thisMonth.equals(java.time.YearMonth.from(depDate));
                    default:
                        return true;
                }
            }).toList();
        }
        String filterStatus = request.getParameter("filterStatus");
        if (filterStatus != null && !filterStatus.isEmpty()) {
            listTrips = listTrips.stream()
                    .filter(trip -> filterStatus.equals(trip.getTripStatus()))
                    .toList();
        } else {
            // Mặc định hiển thị trips có trạng thái "Lên Lịch" khi không có filter
            listTrips = listTrips.stream()
                    .filter(trip -> "Scheduled".equals(trip.getTripStatus()))
                    .toList();
        }
        List<HolidayPrice> allHolidays = holidayPriceRepository.getActiveHolidayPrices();
        request.setAttribute("listTrips", listTrips);
        request.setAttribute("allHolidays", allHolidays);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("currentSortField", sortField);
        request.setAttribute("currentSortOrder", sortOrder);

        if (request.getSession().getAttribute("successMessage") != null) {
            request.setAttribute("successMessage", request.getSession().getAttribute("successMessage"));
            request.getSession().removeAttribute("successMessage");
        }
        if (request.getSession().getAttribute("errorMessage") != null) {
            request.setAttribute("errorMessage", request.getSession().getAttribute("errorMessage"));
            request.getSession().removeAttribute("errorMessage");
        }
        request.getRequestDispatcher("/WEB-INF/jsp/manager/Trip/manageTrips.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("insertTrip".equals(action)) {
            try {
                int trainId = Integer.parseInt(request.getParameter("trainId"));
                int routeId = Integer.parseInt(request.getParameter("routeId"));
                LocalDateTime departureDateTime = LocalDateTime.parse(request.getParameter("departureDateTime"),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                if (departureDateTime.isBefore(LocalDateTime.now())) {
                    request.getSession().setAttribute("errorMessage",
                            "Không thể chọn ngày trong quá khứ. Vui lòng chọn một ngày trong tương lai.");
                    response.sendRedirect(request.getContextPath() + "/manageTrips?action=showAddForm");
                    return;
                }

                String holidayIdStr = request.getParameter("holidayId");
                boolean isHolidayTrip = false;
                BigDecimal basePriceMultiplier = BigDecimal.ONE;
                if (holidayIdStr != null && !holidayIdStr.isEmpty()) {
                    try {
                        int holidayId = Integer.parseInt(holidayIdStr);
                        HolidayPrice holiday = holidayPriceRepository.getHolidayPriceById(holidayId);
                        if (holiday != null) {
                            isHolidayTrip = true;
                            basePriceMultiplier = new BigDecimal(holiday.getDiscountPercentage())
                                    .divide(new BigDecimal("100"));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                String tripStatus = request.getParameter("tripStatus");

                Trip newTrip = new Trip();
                newTrip.setTrainID(trainId);
                newTrip.setRouteID(routeId);
                newTrip.setDepartureDateTime(departureDateTime);

                // Calculate ArrivalDateTime for the Trip
                LocalDateTime calculatedArrivalDateTime = null;
                StringBuilder errorDetailsBuilder = new StringBuilder(
                        "Could not calculate trip arrival time. Issues found: ");
                boolean calculationPossible = true;

                Optional<Train> trainOpt = trainRepository.findById(trainId);
                if (trainOpt.isEmpty()) {
                    errorDetailsBuilder.append("Train with ID ").append(trainId).append(" not found. ");
                    calculationPossible = false;
                } else {
                    Train train = trainOpt.get();
                    Optional<TrainType> trainTypeOpt = trainTypeRepository.findById(train.getTrainTypeID());
                    if (trainTypeOpt.isEmpty()) {
                        errorDetailsBuilder.append("TrainType with ID ").append(train.getTrainTypeID())
                                .append(" for Train '").append(train.getTrainName()).append("' not found. ");
                        calculationPossible = false;
                    } else {
                        TrainType trainType = trainTypeOpt.get();
                        BigDecimal averageVelocity = trainType.getAverageVelocity();
                        if (averageVelocity == null || averageVelocity.compareTo(BigDecimal.ZERO) <= 0) {
                            errorDetailsBuilder.append("AverageVelocity for TrainType '")
                                    .append(trainType.getTypeName()).append("' is missing or not positive. ");
                            calculationPossible = false;
                        } else {
                            // Proceed if averageVelocity is valid
                            List<RouteStationDetailDTO> routeStations = routeRepository
                                    .findStationDetailsByRouteId(routeId);
                            if (routeStations == null || routeStations.isEmpty()) {
                                errorDetailsBuilder.append("No stations found for Route ID ").append(routeId)
                                        .append(". ");
                                calculationPossible = false;
                            } else {
                                RouteStationDetailDTO lastStation = routeStations.stream()
                                        .max(Comparator.comparing(RouteStationDetailDTO::getDistanceFromStart,
                                                Comparator.nullsLast(BigDecimal::compareTo)))
                                        .orElse(null);

                                if (lastStation == null || lastStation.getDistanceFromStart() == null) {
                                    errorDetailsBuilder.append(
                                            "Could not determine the last station or its distance for Route ID ")
                                            .append(routeId).append(". ");
                                    calculationPossible = false;
                                } else {
                                    BigDecimal distanceToLastStation = lastStation.getDistanceFromStart();
                                    BigDecimal travelTimeHoursDecimal = distanceToLastStation.divide(averageVelocity, 4,
                                            RoundingMode.HALF_UP);
                                    long travelTimeSeconds = (long) (travelTimeHoursDecimal.doubleValue() * 3600);
                                    calculatedArrivalDateTime = departureDateTime.plusSeconds(travelTimeSeconds);
                                }
                            }
                        }
                    }
                }

                if (calculatedArrivalDateTime == null) {
                    if (!calculationPossible) { // Append general message if specific errors were already added
                        throw new ServletException(errorDetailsBuilder.toString());
                    } else { // Should not happen if calculationPossible was true and no specific error was
                             // caught, but as a fallback
                        throw new ServletException(
                                "Could not calculate trip arrival time due to an unexpected issue. Please check data for Train ID "
                                        + trainId + " and Route ID " + routeId + ".");
                    }
                }
                newTrip.setArrivalDateTime(calculatedArrivalDateTime);
                newTrip.setHolidayTrip(isHolidayTrip);
                newTrip.setTripStatus(tripStatus);
                newTrip.setBasePriceMultiplier(basePriceMultiplier);

                Trip savedTrip = tripRepository.save(newTrip); // Save trip and get its ID

                if (savedTrip.getTripID() > 0) {
                    // Fetch stations for the route
                    List<RouteStationDetailDTO> routeStations = routeRepository
                            .findStationDetailsByRouteId(savedTrip.getRouteID());

                    // Calculate scheduled times for all stations
                    LocalDateTime previousDepartureDateTime = newTrip.getDepartureDateTime();
                    BigDecimal previousEstimateTimeHours = BigDecimal.ZERO;
                    BigDecimal averageVelocity = null;
                    int trainTypeId = trainRepository.findById(trainId).get().getTrainTypeID();
                    TrainType trainType = trainTypeRepository.findById(trainTypeId).get();
                    averageVelocity = trainType.getAverageVelocity();

                    for (int i = 0; i < routeStations.size(); i++) {
                        RouteStationDetailDTO rsDetail = routeStations.get(i);
                        BigDecimal currentEstimateTimeHours = rsDetail.getDistanceFromStart() != null
                                ? rsDetail.getDistanceFromStart().divide(averageVelocity, 4, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;
                        int defaultStopTimeMinutes = rsDetail.getDefaultStopTime();
                        if (defaultStopTimeMinutes < 0)
                            defaultStopTimeMinutes = 0;

                        LocalDateTime arrivalAtCurrentStation;
                        LocalDateTime departureFromCurrentStation;

                        if (i == 0) {
                            arrivalAtCurrentStation = newTrip.getDepartureDateTime();
                            departureFromCurrentStation = newTrip.getDepartureDateTime();
                        } else {
                            BigDecimal travelTimeHoursDecimal = currentEstimateTimeHours
                                    .subtract(previousEstimateTimeHours);
                            long travelTimeSeconds = (long) (travelTimeHoursDecimal.doubleValue() * 3600);
                            arrivalAtCurrentStation = previousDepartureDateTime.plusSeconds(travelTimeSeconds);
                            departureFromCurrentStation = arrivalAtCurrentStation.plusMinutes(defaultStopTimeMinutes);
                        }
                        if (i == routeStations.size() - 1) {
                            departureFromCurrentStation = arrivalAtCurrentStation;
                        }

                        TripStation newTripStation = new TripStation();
                        newTripStation.setTripID(savedTrip.getTripID());
                        newTripStation.setStationID(rsDetail.getStationID());
                        newTripStation.setSequenceNumber(rsDetail.getSequenceNumber());
                        newTripStation.setScheduledArrival(arrivalAtCurrentStation);
                        newTripStation.setScheduledDeparture(departureFromCurrentStation);
                        newTripStation.setActualArrival(null);
                        newTripStation.setActualDeparture(null);
                        tripStationRepository.save(newTripStation);

                        previousDepartureDateTime = departureFromCurrentStation;
                        previousEstimateTimeHours = currentEstimateTimeHours;
                    }
                    request.getSession().setAttribute("successMessage",
                            "Trip and station schedule added successfully!");
                } else {
                    request.getSession().setAttribute("errorMessage",
                            "Trip added, but failed to get Trip ID to create station schedule.");
                }

            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Error adding trip: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else if ("updateHolidayStatus".equals(action)) {
            try {
                int tripId = Integer.parseInt(request.getParameter("tripId"));
                int isHolidayTripInt = Integer.parseInt(request.getParameter("isHolidayTrip"));
                boolean isHoliday = isHolidayTripInt == 1;
                String multiplierStr = request.getParameter("basePriceMultiplier");
                BigDecimal multiplier = BigDecimal.ONE;
                if (isHoliday && multiplierStr != null && !multiplierStr.isEmpty()) {
                    multiplier = new BigDecimal(multiplierStr);
                }
                tripRepository.updateTripHolidayStatus(tripId, isHoliday);
                tripRepository.updateTripBasePriceMultiplier(tripId, multiplier);
                String message = "Holiday status and multiplier updated for Trip ID: " + tripId;
                request.getSession().setAttribute("successMessage", message);
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Error updating holiday status: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else if ("updateBasePriceMultiplier".equals(action)) {
            try {
                int tripId = Integer.parseInt(request.getParameter("tripId"));
                String multiplierStr = request.getParameter("basePriceMultiplier");
                boolean isCurrentlyHoliday = Boolean.parseBoolean(request.getParameter("isHolidayTripHidden"));

                if (!isCurrentlyHoliday) {
                    request.getSession().setAttribute("errorMessage",
                            "Base Price Multiplier can only be set if 'Ngày Lễ' is 'Có'.");
                } else if (multiplierStr == null || multiplierStr.trim().isEmpty()) {
                    tripRepository.updateTripBasePriceMultiplier(tripId, new BigDecimal("1.0"));
                    request.getSession().setAttribute("successMessage",
                            "Base Price Multiplier reset to 1.0 for Trip ID: " + tripId);
                } else {
                    BigDecimal multiplier = new BigDecimal(multiplierStr);
                    if (multiplier.compareTo(BigDecimal.ZERO) < 0) {
                        request.getSession().setAttribute("errorMessage", "Base Price Multiplier cannot be negative.");
                    } else {
                        tripRepository.updateTripBasePriceMultiplier(tripId, multiplier);
                        request.getSession().setAttribute("successMessage",
                                "Base Price Multiplier updated for Trip ID: " + tripId);
                    }
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("errorMessage", "Invalid format for Trip ID or Multiplier.");
                e.printStackTrace();
            } catch (SQLException e) {
                request.getSession().setAttribute("errorMessage",
                        "Database error updating multiplier: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else if ("updateTripStatus".equals(action)) {
            try {
                int tripId = Integer.parseInt(request.getParameter("tripId"));
                String newStatus = request.getParameter("tripStatus");
                if (newStatus != null && !newStatus.trim().isEmpty()) {
                    tripRepository.updateTripStatus(tripId, newStatus);
                    // Nếu trạng thái là Hủy chuyến thì cập nhật vé và TempRefundRequests
                    if ("Hủy chuyến".equalsIgnoreCase(newStatus) || "Cancelled".equalsIgnoreCase(newStatus)) {
                        vn.vnrailway.dao.TicketRepository ticketRepository = new vn.vnrailway.dao.impl.TicketRepositoryImpl();
                        java.util.List<vn.vnrailway.model.Ticket> tickets = ticketRepository.findByTripId(tripId);
                        try (java.sql.Connection conn = vn.vnrailway.config.DBContext.getConnection()) {
                            conn.setAutoCommit(false);
                            // 1. Cập nhật TicketStatus = 'Cancelled' cho tất cả vé
                            String updateTicketStatusSql = "UPDATE Tickets SET TicketStatus = 'Cancelled' WHERE TripID = ?";
                            try (java.sql.PreparedStatement ps = conn.prepareStatement(updateTicketStatusSql)) {
                                ps.setInt(1, tripId);
                                ps.executeUpdate();
                            }
                            // Bỏ qua update TempRefundRequests
                            conn.commit();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        // Gửi email hoàn tiền cho mỗi khách (1 email/booking)
                        vn.vnrailway.dao.UserRepository userRepository = new UserRepositoryImpl();
                        vn.vnrailway.dao.BookingRepository bookingRepository = new BookingRepositoryImpl();
                        java.util.Set<Integer> bookingIds = new java.util.HashSet<>();
                        for (vn.vnrailway.model.Ticket ticket : tickets) {
                            bookingIds.add(ticket.getBookingID());
                        }
                        java.util.Set<String> sentEmails = new java.util.HashSet<>();
                        for (Integer bookingId : bookingIds) {
                            java.util.Optional<vn.vnrailway.model.Booking> bookingOpt = bookingRepository
                                    .findById(bookingId);
                            if (bookingOpt.isPresent()) {
                                vn.vnrailway.model.Booking booking = bookingOpt.get();
                                int userId = booking.getUserID();
                                java.util.Optional<vn.vnrailway.model.User> userOpt = userRepository.findById(userId);
                                if (userOpt.isPresent()) {
                                    vn.vnrailway.model.User user = userOpt.get();
                                    String email = user.getEmail();
                                    if (email != null && !email.isEmpty() && !sentEmails.contains(email)) {
                                        // Gửi email hoàn tiền
                                        try {
                                            java.util.Properties props = new java.util.Properties();
                                            props.put("mail.smtp.host", "smtp.gmail.com");
                                            props.put("mail.smtp.port", "587");
                                            props.put("mail.smtp.auth", "true");
                                            props.put("mail.smtp.starttls.enable", "true");
                                            final String EMAIL_FROM = "assasinhp619@gmail.com";
                                            final String EMAIL_PASSWORD = "slos bctt epxv osla";
                                            jakarta.mail.Session mailSession = jakarta.mail.Session.getInstance(props,
                                                    new jakarta.mail.Authenticator() {
                                                        @Override
                                                        protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                                                            return new jakarta.mail.PasswordAuthentication(EMAIL_FROM,
                                                                    EMAIL_PASSWORD);
                                                        }
                                                    });
                                            jakarta.mail.Message mimeMessage = new jakarta.mail.internet.MimeMessage(
                                                    mailSession);
                                            mimeMessage.setFrom(new jakarta.mail.internet.InternetAddress(EMAIL_FROM,
                                                    "Vetaure", "UTF-8"));
                                            mimeMessage.setRecipients(jakarta.mail.Message.RecipientType.TO,
                                                    jakarta.mail.internet.InternetAddress.parse(email));
                                            mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
                                            mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");
                                            mimeMessage.setSubject(jakarta.mail.internet.MimeUtility.encodeText(
                                                    "Chuyến tàu của bạn đã bị hủy - Vetaure", "UTF-8", "B"));
                                            String messageContent = String.format(
                                                    "<html>"
                                                            + "<body style='font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9;'>"
                                                            + "<div style='background-color: #ffffff; padding: 20px; border-radius: 10px; max-width: 600px; margin: auto;'>"
                                                            + "<h2 style='color: #e74c3c;'>Chuyến tàu đã bị hủy</h2>"
                                                            + "<p>Xin chào, <b>%s</b>!</p>"
                                                            + "<p>Chúng tôi xin thông báo chuyến tàu bạn đã đặt đã bị <strong>hủy</strong> vì lý do bất khả kháng.</p>"
                                                            + "<p>Để tiếp tục xử lý yêu cầu hoàn tiền của bạn, vui lòng phản hồi email này kèm theo <strong>số tài khoản ngân hàng</strong> để chúng tôi chuyển tiền hoàn.</p>"
                                                            + "<p>Xin cảm ơn!</p>"
                                                            + "<br/>"
                                                            + "<p>Trân trọng,</p>"
                                                            + "<p><strong>Đội ngũ Vetaure</strong></p>"
                                                            + "</div>"
                                                            + "</body>"
                                                            + "</html>",
                                                    user.getFullName());
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
                    request.getSession().setAttribute("successMessage", "Trip status updated for Trip ID: " + tripId);
                } else {
                    request.getSession().setAttribute("errorMessage", "Trip status cannot be empty.");
                }
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Error updating trip status: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else if ("countTickets".equals(action)) {
            try {
                int tripId = Integer.parseInt(request.getParameter("tripId"));
                vn.vnrailway.dao.TicketRepository ticketRepository = new vn.vnrailway.dao.impl.TicketRepositoryImpl();
                int ticketCount = ticketRepository.findByTripId(tripId).size();
                response.setContentType("application/json");
                response.getWriter().write("{\"ticketCount\":" + ticketCount + "}");
            } catch (Exception e) {
                response.setContentType("application/json");
                response.getWriter().write("{\"ticketCount\":0}");
            }
            return;
        } else if ("deleteTrip".equals(action)) {
            try {
                int tripId = Integer.parseInt(request.getParameter("tripId"));
                boolean deleted = tripRepository.deleteById(tripId);
                if (deleted) {
                    request.getSession().setAttribute("successMessage",
                            "Trip ID: " + tripId + " deleted successfully.");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to delete Trip ID: " + tripId
                            + ". It might have associated records or does not exist.");
                }
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Error deleting trip: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else if ("lockTrip".equals(action) || "unlockTrip".equals(action)) {
            try {
                int tripId = Integer.parseInt(request.getParameter("tripId"));
                boolean isLocked = "lockTrip".equals(action);
                tripRepository.updateTripLocked(tripId, isLocked);
                String msg = isLocked ? "Chuyến đi đã được khóa." : "Chuyến đi đã được mở khóa.";
                request.getSession().setAttribute("successMessage", msg);
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage",
                        "Lỗi khi cập nhật trạng thái khóa: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else {
            doGet(request, response);
        }
    }
}
