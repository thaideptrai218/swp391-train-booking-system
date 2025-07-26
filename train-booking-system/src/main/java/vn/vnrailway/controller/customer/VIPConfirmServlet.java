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

@WebServlet("/vip-confirm")
public class VIPConfirmServlet extends HttpServlet {
    private VIPCardTypeRepository vipCardTypeRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        vipCardTypeRepository = new VIPCardTypeRepositoryImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int vipCardTypeId = Integer.parseInt(request.getParameter("vipCardTypeId"));
            VIPCardType vipCardType = vipCardTypeRepository.findById(vipCardTypeId)
                    .orElseThrow(() -> new ServletException("VIP Card Type not found with ID: " + vipCardTypeId));

            request.setAttribute("vipCardType", vipCardType);
            request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-confirm.jsp").forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Error processing VIP card confirmation", e);
        }
    }
}
