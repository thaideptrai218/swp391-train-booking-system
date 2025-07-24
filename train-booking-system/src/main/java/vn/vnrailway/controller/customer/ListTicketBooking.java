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
            List<InfoPassengerDTO> allTickets = ticketRepository.findListTicketBooking(id);

            int pageSize = 5; // số vé mỗi trang
            int page = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
            }

            int totalItems = allTickets.size();
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);

            // Tính chỉ số bắt đầu và kết thúc
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalItems);

            // Tách danh sách vé theo trang
            List<InfoPassengerDTO> pagedTickets = allTickets.subList(fromIndex, toIndex);

            if (allTickets == null || allTickets.isEmpty()) {
                request.setAttribute("errorMessage", "Không có lịch sử đặt vé.");
                request.getRequestDispatcher("/WEB-INF/jsp/customer/listTicketBooking.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("fromIndex", fromIndex);
                request.setAttribute("infoPassenger", pagedTickets);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.getRequestDispatcher("/WEB-INF/jsp/customer/listTicketBooking.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi
            request.setAttribute("errorMessage", "Lỗi khi truy vấn vé từ cơ sở dữ liệu.");
        }
    }
}
