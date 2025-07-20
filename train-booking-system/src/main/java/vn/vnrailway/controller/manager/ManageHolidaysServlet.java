package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.HolidayPriceRepository;
import vn.vnrailway.dao.impl.HolidayPriceRepositoryImpl;
import vn.vnrailway.model.HolidayPrice;

@WebServlet("/manageHolidays")
public class ManageHolidaysServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private HolidayPriceRepository holidayPriceRepository;

    public ManageHolidaysServlet() {
        super();
        try {
            this.holidayPriceRepository = new HolidayPriceRepositoryImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "insert":
                    insertHoliday(request, response);
                    break;
                case "delete":
                    deleteHoliday(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "update":
                    updateHoliday(request, response);
                    break;
                default: // list
                    listHolidays(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void listHolidays(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        List<HolidayPrice> listHolidays = holidayPriceRepository.getAllHolidayPrices();
        request.setAttribute("listHolidays", listHolidays);
        request.getRequestDispatcher("/WEB-INF/jsp/manager/manageHolidays.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/manager/addHoliday.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        HolidayPrice existingHoliday = holidayPriceRepository.getHolidayPriceById(id);
        request.setAttribute("holiday", existingHoliday);
        request.getRequestDispatcher("/WEB-INF/jsp/manager/editHoliday.jsp").forward(request, response);
    }

    private void insertHoliday(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String name = request.getParameter("name");
        LocalDate startDate = getNullableLocalDateParameter(request, "startDate");
        LocalDate endDate = getNullableLocalDateParameter(request, "endDate");
        float coefficient = Float.parseFloat(request.getParameter("coefficient"));
        boolean isActive = "true".equals(request.getParameter("isActive"));

        if (name == null || name.trim().isEmpty() ||
                startDate == null || endDate == null) {
            request.setAttribute("errorMessage", "Các trường không được để trống.");
            showNewForm(request, response);
            return;
        }

        if (endDate.isBefore(startDate)) {
            request.setAttribute("errorMessage", "Ngày kết thúc không được trước ngày bắt đầu.");
            showNewForm(request, response);
            return;
        }

        HolidayPrice newHoliday = new HolidayPrice(0, name, startDate, endDate, coefficient, isActive);
        holidayPriceRepository.addHolidayPrice(newHoliday);
        response.sendRedirect(request.getContextPath() + "/manageHolidays");
    }

    private void updateHoliday(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        LocalDate startDate = getNullableLocalDateParameter(request, "startDate");
        LocalDate endDate = getNullableLocalDateParameter(request, "endDate");
        float coefficient = Float.parseFloat(request.getParameter("coefficient"));
        boolean isActive = "true".equals(request.getParameter("isActive"));

        if (name == null || name.trim().isEmpty() ||
                startDate == null || endDate == null) {
            request.setAttribute("errorMessage", "Các trường không được để trống.");
            showEditForm(request, response);
            return;
        }

        if (endDate.isBefore(startDate)) {
            request.setAttribute("errorMessage", "Ngày kết thúc không được trước ngày bắt đầu.");
            showEditForm(request, response);
            return;
        }

        HolidayPrice holidayToUpdate = new HolidayPrice(id, name, startDate, endDate, coefficient, isActive);
        holidayPriceRepository.updateHolidayPrice(holidayToUpdate);
        response.sendRedirect(request.getContextPath() + "/manageHolidays");
    }

    private void deleteHoliday(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        holidayPriceRepository.deleteHolidayPrice(id);
        response.sendRedirect(request.getContextPath() + "/manageHolidays");
    }

    private LocalDate getNullableLocalDateParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
