package vn.vnrailway.controller.manager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.VIPCardTypeRepository;
import vn.vnrailway.dao.impl.VIPCardTypeRepositoryImpl;
import vn.vnrailway.model.VIPCardType;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/manageVIPCardTypes")
public class ManageVIPCardTypesServlet extends HttpServlet {
    private VIPCardTypeRepository vipCardTypeRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        vipCardTypeRepository = new VIPCardTypeRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                default:
                    listVIPCardTypes(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            switch (action) {
                case "insert":
                    insertVIPCardType(request, response);
                    break;
                case "update":
                    updateVIPCardType(request, response);
                    break;
                case "delete":
                    deleteVIPCardType(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void listVIPCardTypes(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<VIPCardType> listVIPCardTypes = vipCardTypeRepository.findAll();
        request.setAttribute("listVIPCardTypes", listVIPCardTypes);
        request.getRequestDispatcher("/WEB-INF/jsp/manager/manageVIPCardTypes.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/manager/vip_card_type_form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        VIPCardType existingVIPCardType = vipCardTypeRepository.findById(id).orElseThrow(() -> new ServletException("VIP Card Type not found with ID: " + id));
        request.setAttribute("vipCardType", existingVIPCardType);
        request.getRequestDispatcher("/WEB-INF/jsp/manager/vip_card_type_form.jsp").forward(request, response);
    }

    private void insertVIPCardType(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String typeName = request.getParameter("typeName");
        BigDecimal price = new BigDecimal(request.getParameter("price"));
        BigDecimal discountPercentage = new BigDecimal(request.getParameter("discountPercentage"));
        int durationMonths = Integer.parseInt(request.getParameter("durationMonths"));
        String description = request.getParameter("description");

        VIPCardType newVIPCardType = new VIPCardType(0, typeName, price, discountPercentage, durationMonths, description);
        vipCardTypeRepository.save(newVIPCardType);
        response.sendRedirect("manageVIPCardTypes");
    }

    private void updateVIPCardType(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String typeName = request.getParameter("typeName");
        BigDecimal price = new BigDecimal(request.getParameter("price"));
        BigDecimal discountPercentage = new BigDecimal(request.getParameter("discountPercentage"));
        int durationMonths = Integer.parseInt(request.getParameter("durationMonths"));
        String description = request.getParameter("description");

        VIPCardType vipCardType = new VIPCardType(id, typeName, price, discountPercentage, durationMonths, description);
        vipCardTypeRepository.update(vipCardType);
        response.sendRedirect("manageVIPCardTypes");
    }

    private void deleteVIPCardType(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        vipCardTypeRepository.deleteById(id);
        response.sendRedirect("manageVIPCardTypes");
    }
}
