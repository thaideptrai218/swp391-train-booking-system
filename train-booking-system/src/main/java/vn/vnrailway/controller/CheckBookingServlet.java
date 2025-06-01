package vn.vnrailway.controller;

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
import vn.vnrailway.model.Booking;
import vn.vnrailway.model.Ticket;
import vn.vnrailway.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "CheckBookingServlet", urlPatterns = { "/checkBooking" })
public class CheckBookingServlet extends HttpServlet {
    private BookingRepository bookingRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize the DAO. Consider dependency injection for a more robust
        // application.
        bookingRepository = new BookingRepositoryImpl();
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
            } else if (phoneNumber.isEmpty() && email.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập số điện thoại hoặc email.");
            } else {
                try {
                    CheckBookingDTO checkBookingDTO = bookingRepository.findBookingDetailsByCode(bookingCode,
                            phoneNumber,
                            email);
                    if (checkBookingDTO != null) {
                        request.setAttribute("checkBookingDTO", checkBookingDTO);
                    } else {
                        request.setAttribute("errorMessage", "Không tìm thấy thông tin đặt chỗ với mã đã nhập.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    request.setAttribute("errorMessage", "Có lỗi xảy ra khi tra cứu. Vui lòng thử lại sau.");
                }
            }
        }

        request.getRequestDispatcher("/WEB-INF/jsp/check-booking/check-booking.jsp").forward(request, response);
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
