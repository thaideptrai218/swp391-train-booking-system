package vn.vnrailway.controller.ticket;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.BookingRepository;
import vn.vnrailway.dao.TicketRepository;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.BookingRepositoryImpl;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.dao.impl.TicketRepositoryImpl;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.dto.CheckBookingDTO;
import vn.vnrailway.dto.InfoPassengerDTO;
import vn.vnrailway.model.Booking;
import vn.vnrailway.model.Ticket;
import vn.vnrailway.model.User;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "CheckTicketServlet", urlPatterns = { "/checkTicket" })
public class CheckTicketServlet extends HttpServlet {
    private TicketRepository ticketRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize the DAO. Consider dependency injection for a more robust
        // application.
        ticketRepository = new TicketRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to the booking check form
        response.setContentType("text/html;charset=UTF-8");
        String ticketCode = request.getParameter("ticketCode");
        String trainCode = request.getParameter("trainCode");
        String departureStation = request.getParameter("departureStation");
        String arrivalStation = request.getParameter("arrivalStation");
        String departureDate = request.getParameter("departureDate");
        String idNumber = request.getParameter("idNumber");

        ticketCode = (ticketCode != null) ? ticketCode.trim() : "";
        trainCode = (trainCode != null) ? trainCode.trim() : "";
        departureStation = (departureStation != null) ? departureStation.trim() : "";
        arrivalStation = (arrivalStation != null) ? arrivalStation.trim() : "";
        departureDate = (departureDate != null) ? departureDate.trim() : "";
        idNumber = (idNumber != null) ? idNumber.trim() : "";

        request.setAttribute("ticketCode", ticketCode);
        request.setAttribute("trainCode", trainCode);
        request.setAttribute("departureStation", departureStation);
        request.setAttribute("arrivalStation", arrivalStation);
        request.setAttribute("departureDate", departureDate);
        request.setAttribute("idNumber", idNumber);

        List<String> stationList = null;
        try {
            stationList = ticketRepository.getAllStationNames();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        request.setAttribute("stationList", stationList);

        if (ticketCode.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập mã vé để kiểm tra vé.");
            request.getRequestDispatcher("/WEB-INF/jsp/check-ticket/check-ticket.jsp").forward(request, response);
            return;
        } else if (trainCode.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập mác tàu để kiểm tra vé.");
            request.getRequestDispatcher("/WEB-INF/jsp/check-ticket/check-ticket.jsp").forward(request, response);
            return;
        } else if (departureStation.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập ga đi để kiểm tra vé.");
            request.getRequestDispatcher("/WEB-INF/jsp/check-ticket/check-ticket.jsp").forward(request, response);
            return;
        } else if (arrivalStation.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập ga đến để kiểm tra vé.");
            request.getRequestDispatcher("/WEB-INF/jsp/check-ticket/check-ticket.jsp").forward(request, response);
            return;
        } else if (departureDate.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập ngày khởi hành để kiểm tra vé.");
            request.getRequestDispatcher("/WEB-INF/jsp/check-ticket/check-ticket.jsp").forward(request, response);
            return;
        }

        if (!idNumber.isEmpty() && !idNumber.matches("^\\d{12}$")) {
            request.setAttribute("errorMessage", "Số giấy tờ phải là 12 chữ số nếu là vé người lớn, trẻ con không cần nhập. Vui lòng nhập lại.");
            request.getRequestDispatcher("/WEB-INF/jsp/check-ticket/check-ticket.jsp").forward(request, response);
            return;
        }

        try {
            InfoPassengerDTO infoPassenger = ticketRepository.findTicketByTicketCode(ticketCode);
            // request.setAttribute("infoPassenger", infoPassenger);
            if (infoPassenger == null) {
                request.setAttribute("errorMessage", "Không tìm thấy vé. Vui lòng nhập lại thông tin.");
                request.getRequestDispatcher("/WEB-INF/jsp/check-ticket/check-ticket.jsp").forward(request, response);
                return;
            } else {
                String dateFromPassenger = infoPassenger.getScheduledDepartureTime().substring(0, 10);

                boolean idCardMatches = false;

                if (infoPassenger.getPassengerIDCard() == null && idNumber.isEmpty()) {
                    // Trường hợp trẻ em, cả 2 đều không có số CMND/CCCD
                    idCardMatches = true;
                } else if (infoPassenger.getPassengerIDCard() != null
                        && infoPassenger.getPassengerIDCard().equals(idNumber)) {
                    // Trường hợp người lớn, so sánh số CMND/CCC
                    idCardMatches = true;
                }

                // chuyển ngày dạng dđ-mm-yyyy sang Date
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");

                try {
                    java.util.Date parsedDate = originalFormat.parse(departureDate);
                    departureDate = targetFormat.format(parsedDate); // => "18-06-2025"
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (infoPassenger.getTrainName().equals(trainCode) &&
                        infoPassenger.getStartStationName().equals(departureStation) &&
                        infoPassenger.getEndStationName().equals(arrivalStation) &&
                        dateFromPassenger.equals(departureDate) &&
                        idCardMatches) {
                    request.setAttribute("infoPassenger", infoPassenger);
                    request.getRequestDispatcher("/WEB-INF/jsp/check-ticket/check-ticket.jsp").forward(request,
                            response);
                    return;
                } else {
                    request.setAttribute("errorMessage", "Thông tin vé không khớp. Vui lòng kiểm tra lại.");
                    request.getRequestDispatcher("/WEB-INF/jsp/check-ticket/check-ticket.jsp").forward(request,
                            response);
                    return;
                }
            }
            // Kiểm tra thông tin vé

        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi
            request.setAttribute("errorMessage", "Lỗi khi truy vấn vé từ cơ sở dữ liệu.");
        }



        request.getRequestDispatcher("/WEB-INF/jsp/check-ticket/check-ticket.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle the booking check logic here
        // For now, just forward to the same JSP
        doGet(request, response);
    }

    public static void main(String[] args) {

    }
}