package vn.vnrailway.controller.staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.StaffDashboardDAO;
import vn.vnrailway.dao.TicketRepository;
import vn.vnrailway.dao.impl.TicketRepositoryImpl;
import vn.vnrailway.dto.RefundRequestDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CheckRefundTicketServlet", urlPatterns = { "/checkRefundTicket" })
public class CheckRefundTicketServlet extends HttpServlet {
    private TicketRepository ticketRepository = new TicketRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<RefundRequestDTO> refundRequests;
        try {
            refundRequests = ticketRepository.getAllPendingRefundRequests();
            if (refundRequests == null || refundRequests.isEmpty()) {
                request.setAttribute("message", "Không có yêu cầu hoàn vé nào.");
                request.getRequestDispatcher("/WEB-INF/jsp/staff/refund-requests.jsp").forward(request, response);
            } else {
                request.setAttribute("refundRequests", refundRequests);
                request.getRequestDispatcher("/WEB-INF/jsp/staff/refund-requests.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
