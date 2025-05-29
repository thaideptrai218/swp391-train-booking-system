package vn.vnrailway.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.BookingRepository;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.BookingRepositoryImpl;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.Booking;
import vn.vnrailway.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "CheckBookingServlet", urlPatterns = { "/checkBooking" })
public class CheckBookingServlet extends HttpServlet {
    private BookingRepository bookingRepository;
    private UserRepository userRepository; // Assuming you have a UserRepository for user details

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize the DAO. Consider dependency injection for a more robust
        // application.
        bookingRepository = new BookingRepositoryImpl();
        userRepository = new UserRepositoryImpl(); // Initialize UserRepository
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to the booking check form
        response.setContentType("text/html;charset=UTF-8");

        String bookingCode = request.getParameter("bookingCode"); 

        try {
            Optional<Booking> booking = bookingRepository.findByBookingCode(bookingCode);

            if (booking.isPresent()) {
                // xử lý booking ở đây
                request.setAttribute("booking", booking.get());
                try {
                    Optional<User> user = userRepository.findById(booking.get().getUserID());

                    user.ifPresent(u -> request.setAttribute("user", u));
                } catch (Exception e) {
                    e.printStackTrace(); // Hoặc log ra hệ thống
                    request.setAttribute("error", "Lỗi khi xử lý booking.");
                }
            } else {
                request.setAttribute("error", "Không tìm thấy booking.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Hoặc log ra hệ thống
            request.setAttribute("error", "Lỗi truy vấn cơ sở dữ liệu.");
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
