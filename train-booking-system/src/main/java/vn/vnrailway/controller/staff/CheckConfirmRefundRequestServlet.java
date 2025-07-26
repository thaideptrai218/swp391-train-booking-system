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

        // Lấy tham số phân trang từ request
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        
        int currentPage = 1;
        int pageSize = 10; // Mặc định 10 bản ghi mỗi trang
        
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        
        if (pageSizeStr != null && !pageSizeStr.isEmpty()) {
            try {
                pageSize = Integer.parseInt(pageSizeStr);
                if (pageSize < 5) pageSize = 5;
                if (pageSize > 50) pageSize = 50;
            } catch (NumberFormatException e) {
                pageSize = 10;
            }
        }

        List<ConfirmRefundRequestDTO> confirmRefundRequests;
        try {
            // Lấy tất cả dữ liệu
            List<ConfirmRefundRequestDTO> allRequests = ticketRepository.getAllConfirmedRefundRequests();
            
            if (allRequests == null || allRequests.isEmpty()) {
                request.setAttribute("message", "Không có yêu cầu hoàn vé nào.");
                request.getRequestDispatcher("/WEB-INF/jsp/staff/listOfRefundTickets.jsp").forward(request, response);
                return;
            }
            
            // Tính toán phân trang
            int totalRecords = allRequests.size();
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            // Điều chỉnh currentPage nếu vượt quá tổng số trang
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }
            
            // Tính vị trí bắt đầu và kết thúc
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalRecords);
            
            // Lấy dữ liệu cho trang hiện tại
            confirmRefundRequests = allRequests.subList(startIndex, endIndex);
            
            // Set attributes cho JSP
            request.setAttribute("confirmRefundRequests", confirmRefundRequests);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("startRecord", startIndex + 1);
            request.setAttribute("endRecord", endIndex);
            
            request.getRequestDispatcher("/WEB-INF/jsp/staff/listOfRefundTickets.jsp").forward(request, response);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
