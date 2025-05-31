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

        if (bookingCode != null) {
            bookingCode = bookingCode.trim(); // loại bỏ khoảng trắng ở đầu/cuối
        }

        try {
            CheckBookingDTO checkBookingDTO = bookingRepository.findBookingDetailsByCode(bookingCode);
            if (checkBookingDTO != null) {
                request.setAttribute("bookingCode", bookingCode);
                request.setAttribute("checkBookingDTO", checkBookingDTO);
            } else {
                request.setAttribute("errorMessage", "No booking found with the provided code.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error retrieving booking details. Please try again later.");
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
