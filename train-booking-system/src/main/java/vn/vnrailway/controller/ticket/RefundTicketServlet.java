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
import vn.vnrailway.dto.CheckInforRefundTicketDTO;
import vn.vnrailway.model.Booking;
import vn.vnrailway.model.Ticket;
import vn.vnrailway.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "RefundTicketServlet", urlPatterns = { "/refundTicket" })
public class RefundTicketServlet extends HttpServlet {
    private TicketRepository ticketRepository;

        // Mã đặt chỗ: chỉ chữ cái và số, ít nhất 1 ký tự
    private static final String BOOKING_CODE_REGEX = "^[a-zA-Z0-9]+$";

    // Số điện thoại: 10 số, bắt đầu bằng 0
    private static final String PHONE_REGEX = "^0\\d{9}$";

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

        String bookingCode = request.getParameter("bookingCode");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");

        bookingCode = (bookingCode != null) ? bookingCode.trim() : "";
        phoneNumber = (phoneNumber != null) ? phoneNumber.trim() : "";
        email = (email != null) ? email.trim() : "";

        request.setAttribute("bookingCode", bookingCode);
        request.setAttribute("phoneNumber", phoneNumber);
        request.setAttribute("email", email);

        if (!bookingCode.isEmpty() || !phoneNumber.isEmpty() && !email.isEmpty()) {
            if (bookingCode.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập mã đặt chỗ.");
            } else if (!bookingCode.matches("^[a-zA-Z0-9]+$")) {
                request.setAttribute("errorMessage", "Mã đặt chỗ chỉ được chứa chữ cái và số.");
            } else if (phoneNumber.isEmpty() && email.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập số điện thoại hoặc email.");
            } else if (!phoneNumber.isEmpty() && !phoneNumber.matches("^0\\d{9}$")) {
                request.setAttribute("errorMessage", "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0.");
            } else {
                try {
                    CheckInforRefundTicketDTO checkInforRefundTicketDTO = ticketRepository
                            .findInformationRefundTicketDetailsByCode(bookingCode, phoneNumber, email);
                    if (checkInforRefundTicketDTO != null) {

                        request.setAttribute("checkInforRefundTicketDTO", checkInforRefundTicketDTO);
                    } else {
                        request.setAttribute("errorMessage", "Không tìm thấy thông tin đặt chỗ với mã đã nhập.");
                        request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/refund-ticket.jsp").forward(request,
                                response);
                        return;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Có lỗi xảy ra khi tra cứu. Vui lòng thử lại sau.");
                }
            }
        }

        request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/refund-ticket.jsp").forward(request, response);
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
