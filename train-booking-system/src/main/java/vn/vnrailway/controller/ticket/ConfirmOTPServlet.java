package vn.vnrailway.controller.ticket;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.dao.TicketRepository;
import vn.vnrailway.dao.impl.TicketRepositoryImpl;
import vn.vnrailway.model.Ticket;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "ConfirmOTPServlet", urlPatterns = { "/confirmOTP" })
public class ConfirmOTPServlet extends HttpServlet {
    private TicketRepository ticketRepository = new TicketRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/confirm-OTP.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String inputOTP = request.getParameter("otp");
        HttpSession session = request.getSession();
        String storedOTP = (String) session.getAttribute("otp");
        Long otpTimestamp = (Long) session.getAttribute("otpTimestamp");

        // Check if session is valid
        if (storedOTP == null || otpTimestamp == null) {
            request.setAttribute("errorMessage", "Phiên làm việc đã hết hạn. Vui lòng thử lại.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/refund-ticket.jsp").forward(request, response);
            return;
        }

        // Check OTP expiration (5 minutes)
        if (System.currentTimeMillis() - otpTimestamp > 300000) {
            request.setAttribute("errorMessage", "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/refund-ticket.jsp").forward(request, response);
            return;
        }

        if (storedOTP.equals(inputOTP)) {
            session.setAttribute("otpVerified", true);
            String[] ticketInfos = (String[]) session.getAttribute("ticketInfos");
            for (String ticketInfo : ticketInfos) {

                try {
                    ticketRepository.insertTempRefundRequests(ticketInfo);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            session.setAttribute("refundSuccessMessage", "Xác nhận mã OTP thành công. Vui lòng chờ xử lý hoàn tiền trong vòng 24 giờ.");
            response.sendRedirect(request.getContextPath());
        } else {
            request.setAttribute("message", "Mã OTP không chính xác.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/confirm-OTP.jsp").forward(request, response);
        }
    }
}
