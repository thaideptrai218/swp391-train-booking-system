package vn.vnrailway.controller.api.seat;

import vn.vnrailway.dao.TemporarySeatHoldDAO;
import vn.vnrailway.dao.impl.TemporarySeatHoldDAOImpl;
import vn.vnrailway.dto.ApiResponse;
import vn.vnrailway.dto.ReleaseRequestDTO;
import vn.vnrailway.utils.DBContext;
import vn.vnrailway.utils.JsonUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/api/seats/release")
public class ReleaseSeatApiServlet extends HttpServlet {

    private TemporarySeatHoldDAO temporarySeatHoldDAO;

    public ReleaseSeatApiServlet() {
        this.temporarySeatHoldDAO = new TemporarySeatHoldDAOImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        Connection conn = null;
        try {
            ReleaseRequestDTO releaseRequest = JsonUtils.parse(request.getReader(), ReleaseRequestDTO.class);

            if (releaseRequest == null || releaseRequest.getTripId() == 0 || releaseRequest.getSeatId() == 0 ||
                releaseRequest.getLegOriginStationId() == 0 || releaseRequest.getLegDestinationStationId() == 0) {
                JsonUtils.toJson(ApiResponse.error("Invalid request payload. All fields are required."), response.getWriter());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            HttpSession httpSession = request.getSession(false); // false: don't create if it doesn't exist
            if (httpSession == null) {
                JsonUtils.toJson(ApiResponse.error("No active session found."), response.getWriter());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Or FORBIDDEN
                return;
            }
            String sessionId = httpSession.getId();

            conn = DBContext.getConnection();
            // For a simple delete, explicit transaction management might be overkill,
            // but can be added if DAO method requires it or if multiple operations were involved.
            // conn.setAutoCommit(false); // If transaction needed

            boolean released = temporarySeatHoldDAO.releaseHold(conn,
                    releaseRequest.getTripId(),
                    releaseRequest.getSeatId(),
                    releaseRequest.getLegOriginStationId(),
                    releaseRequest.getLegDestinationStationId(),
                    sessionId);

            if (released) {
                // conn.commit(); // If transaction used
                JsonUtils.toJson(ApiResponse.success("Seat hold released successfully."), response.getWriter());
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                // conn.rollback(); // If transaction used
                // This could mean the hold didn't exist, expired, or didn't belong to this session
                JsonUtils.toJson(ApiResponse.error("Hold not found, already expired, or not authorized to release."), response.getWriter());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Or SC_FORBIDDEN if it's an auth issue
            }

        } catch (SQLException e) {
            handleSQLException(conn, e, response);
        } catch (Exception e) {
            handleGenericException(e, response);
        } finally {
            closeConnection(conn);
        }
    }
    
    private void handleSQLException(Connection conn, SQLException e, HttpServletResponse response) throws IOException {
        e.printStackTrace(); 
        // No rollback here if not managing transaction in this servlet directly for simple delete
        JsonUtils.toJson(ApiResponse.error("Database error: " + e.getMessage()), response.getWriter());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void handleGenericException(Exception e, HttpServletResponse response) throws IOException {
        e.printStackTrace(); 
        JsonUtils.toJson(ApiResponse.error("An unexpected error occurred: " + e.getMessage()), response.getWriter());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                // if (conn.getAutoCommit() == false) conn.setAutoCommit(true); // If transactions were managed here
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
