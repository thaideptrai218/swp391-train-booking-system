package vn.vnrailway.controller.api.seat;

import vn.vnrailway.dao.TemporarySeatHoldDAO;
import vn.vnrailway.dao.impl.TemporarySeatHoldDAOImpl;
import vn.vnrailway.dto.ApiResponse;
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
import java.util.Map;

@WebServlet("/api/seats/releaseAllBySession")
public class ReleaseAllSeatsBySessionApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemporarySeatHoldDAO temporarySeatHoldDAO;

    public ReleaseAllSeatsBySessionApiServlet() {
        this.temporarySeatHoldDAO = new TemporarySeatHoldDAOImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null) {
            JsonUtils.toJson(ApiResponse.error("No active session found."), response.getWriter());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String sessionId = httpSession.getId();

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Start transaction

            int releasedCount = temporarySeatHoldDAO.releaseAllHoldsForSession(conn, sessionId); // Using existing method

            conn.commit(); // Commit transaction

            if (releasedCount > 0) {
                JsonUtils.toJson(ApiResponse.success(Map.of("releasedCount", releasedCount), releasedCount + " active seat hold(s) for the session released successfully."), response.getWriter());
            } else {
                JsonUtils.toJson(ApiResponse.success(Map.of("releasedCount", 0), "No active seat holds found for this session to release."), response.getWriter());
            }
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (SQLException e) {
            handleSQLException(conn, e, response);
        } catch (Exception e) {
            // Rollback in case of generic exception during DB operations if conn was established
            if (conn != null) {
                try {
                    if (!conn.getAutoCommit()) { // Check before rollback
                        conn.rollback();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace(); // Log rollback failure
                }
            }
            handleGenericException(e, response);
        } finally {
            closeConnection(conn);
        }
    }

    private void handleSQLException(Connection conn, SQLException e, HttpServletResponse response) throws IOException {
        e.printStackTrace();
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) { // Check before rollback
                     conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace(); // Log rollback failure
            }
        }
        JsonUtils.toJson(ApiResponse.error("Database error during mass release: " + e.getMessage()), response.getWriter());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void handleGenericException(Exception e, HttpServletResponse response) throws IOException {
        e.printStackTrace();
        JsonUtils.toJson(ApiResponse.error("An unexpected error occurred during mass release: " + e.getMessage()), response.getWriter());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) { // Ensure autoCommit is reset if it was changed
                    conn.setAutoCommit(true);
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Log close failure
            }
        }
    }
}
