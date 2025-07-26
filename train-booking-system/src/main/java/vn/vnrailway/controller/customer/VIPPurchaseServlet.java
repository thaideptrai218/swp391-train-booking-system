package vn.vnrailway.controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.VIPCardTypeRepository;
import vn.vnrailway.dao.impl.VIPCardTypeRepositoryImpl;
import vn.vnrailway.model.VIPCardType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/vip-purchase")
public class VIPPurchaseServlet extends HttpServlet {
    private VIPCardTypeRepository vipCardTypeRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        vipCardTypeRepository = new VIPCardTypeRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<VIPCardType> vipCardTypes = vipCardTypeRepository.findAll();
            request.setAttribute("vipCardTypes", vipCardTypes);
            request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-purchase.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error while fetching VIP card types", e);
        }
    }
}
