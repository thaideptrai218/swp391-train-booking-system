package vn.vnrailway.controller.customer;

import vn.vnrailway.dao.FeedbackDAO;
import vn.vnrailway.dao.TicketRepository;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.FeedbackDAOImpl;
import vn.vnrailway.dao.impl.TicketRepositoryImpl;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.dto.InfoPassengerDTO;
import vn.vnrailway.model.Feedback;
import vn.vnrailway.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.ZoneId;

@WebServlet("/listTicketBooking")
public class ListTicketBooking extends HttpServlet {
    private TicketRepository ticketRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        ticketRepository = new TicketRepositoryImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            HttpSession session = request.getSession();
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            if (loggedInUser != null) {
                id = String.valueOf(loggedInUser.getUserID());
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        }

        try {
            List<InfoPassengerDTO> infoPassenger = ticketRepository.findListTicketBooking(id);
            if (infoPassenger == null || infoPassenger.isEmpty()) {
                request.setAttribute("errorMessage", "Không có lịch sử đặt vé.");
                request.getRequestDispatcher("/WEB-INF/jsp/customer/listTicketBooking.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("infoPassenger", infoPassenger);
                request.getRequestDispatcher("/WEB-INF/jsp/customer/listTicketBooking.jsp").forward(request, response);
            }
            // Kiểm tra thông tin vé

        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi
            request.setAttribute("errorMessage", "Lỗi khi truy vấn vé từ cơ sở dữ liệu.");
        }
    }
}
