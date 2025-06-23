package vn.vnrailway.controller.ticket;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.TicketRepository;
import vn.vnrailway.dao.impl.TicketRepositoryImpl;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "RefundProcessingServlet", urlPatterns = { "/refundProcessing" })
public class RefundProcessingServlet extends HttpServlet {
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

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle the booking check logic here
        // For now, just forward to the same JSP
        String action = request.getParameter("action");
        String ticketCode = request.getParameter("ticketCode");

        if ("approve".equals(action)) {
            
        } else if ("reject".equals(action)) {
            try {
                ticketRepository.rejectRefundTicket(ticketCode);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        response.sendRedirect(request.getContextPath() + "/checkRefundTicket");
    }

    public static void main(String[] args) {

    }
}