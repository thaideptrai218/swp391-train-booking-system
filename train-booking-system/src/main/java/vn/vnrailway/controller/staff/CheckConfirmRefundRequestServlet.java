package vn.vnrailway.controller.staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.StaffDashboardDAO;
import vn.vnrailway.dao.TicketRepository;
import vn.vnrailway.dao.impl.TicketRepositoryImpl;
import vn.vnrailway.dto.ConfirmRefundRequestDTO;
import vn.vnrailway.dto.RefundRequestDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CheckConfirmRefundRequestServlet", urlPatterns = { "/checkConfirmRefundRequest" })
public class CheckConfirmRefundRequestServlet extends HttpServlet {
    private TicketRepository ticketRepository = new TicketRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ConfirmRefundRequestDTO> confirmRefundRequests;
        try {
            confirmRefundRequests = ticketRepository.getAllConfirmedRefundRequests();
            if (confirmRefundRequests == null || confirmRefundRequests.isEmpty()) {
                request.setAttribute("message", "Không có yêu cầu hoàn vé nào.");
                request.getRequestDispatcher("/WEB-INF/jsp/staff/listOfRefundTickets.jsp").forward(request, response);
            } else {
                request.setAttribute("confirmRefundRequests", confirmRefundRequests);
                request.getRequestDispatcher("/WEB-INF/jsp/staff/listOfRefundTickets.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
