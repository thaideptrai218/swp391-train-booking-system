package vn.vnrailway.controller.vip;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.dao.UserVIPCardRepository;
import vn.vnrailway.dao.impl.UserVIPCardRepositoryImpl;
import vn.vnrailway.model.User;
import vn.vnrailway.model.UserVIPCard;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/vip-success")
public class VIPSuccessServlet extends HttpServlet {
    private UserVIPCardRepository userVIPCardRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        userVIPCardRepository = new UserVIPCardRepositoryImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect("login"); // Redirect to login if not logged in
            return;
        }

        try {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            int vipCardTypeId = Integer.parseInt(request.getParameter("vipCardTypeId"));
            int durationMonths = Integer.parseInt(request.getParameter("durationMonths"));

            UserVIPCard userVIPCard = new UserVIPCard();
            userVIPCard.setUserID(loggedInUser.getUserID());
            userVIPCard.setVipCardTypeID(vipCardTypeId);
            userVIPCard.setPurchaseDate(LocalDateTime.now());
            userVIPCard.setExpiryDate(LocalDateTime.now().plusMonths(durationMonths));
            userVIPCard.setActive(true);
            // In a real application, a transaction reference from a payment gateway would be stored here.
            userVIPCard.setTransactionReference("mock_transaction_" + System.currentTimeMillis());

            userVIPCardRepository.save(userVIPCard);

            request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-success.jsp").forward(request, response);

        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Error processing VIP card purchase", e);
        }
    }
}
