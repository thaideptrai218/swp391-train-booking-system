package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.vnrailway.dao.CoachRepository;
import vn.vnrailway.dao.CoachTypeRepository;
import vn.vnrailway.dao.SeatRepository;
import vn.vnrailway.dao.SeatTypeRepository;
import vn.vnrailway.dao.TrainRepository;
import vn.vnrailway.dao.TrainTypeRepository;
import vn.vnrailway.dao.impl.CoachRepositoryImpl;
import vn.vnrailway.dao.impl.CoachTypeRepositoryImpl;
import vn.vnrailway.dao.impl.SeatRepositoryImpl;
import vn.vnrailway.dao.impl.SeatTypeRepositoryImpl;
import vn.vnrailway.dao.impl.TrainRepositoryImpl;
import vn.vnrailway.dao.impl.TrainTypeRepositoryImpl;
import vn.vnrailway.model.Coach;
import vn.vnrailway.model.CoachType;
import vn.vnrailway.model.Seat;
import vn.vnrailway.model.SeatType;
import vn.vnrailway.model.Train;
import vn.vnrailway.model.TrainType;

@WebServlet("/manager/manage-trains-seats")
public class ManageTrainsSeatsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TrainRepository trainRepository;
    private CoachRepository coachRepository;
    private SeatRepository seatRepository;
    private TrainTypeRepository trainTypeRepository;
    private CoachTypeRepository coachTypeRepository;
    private SeatTypeRepository seatTypeRepository;

    public void init() {
        trainRepository = new TrainRepositoryImpl();
        coachRepository = new CoachRepositoryImpl();
        seatRepository = new SeatRepositoryImpl();
        trainTypeRepository = new TrainTypeRepositoryImpl();
        coachTypeRepository = new CoachTypeRepositoryImpl();
        seatTypeRepository = new SeatTypeRepositoryImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            if ("delete_train".equals(action)) {
                deleteTrain(request, response);
                return;
            } else if ("delete_coach".equals(action)) {
                deleteCoach(request, response);
                return;
            } else if ("delete_seat".equals(action)) {
                deleteSeat(request, response);
                return;
            } else if ("lock_train".equals(action)) {
                int trainId = Integer.parseInt(request.getParameter("trainId"));
                trainRepository.updateTrainLocked(trainId, true);
                response.sendRedirect("manage-trains-seats");
                return;
            } else if ("unlock_train".equals(action)) {
                int trainId = Integer.parseInt(request.getParameter("trainId"));
                trainRepository.updateTrainLocked(trainId, false);
                response.sendRedirect("manage-trains-seats");
                return;
            }

            List<Train> listTrain = trainRepository.getAllTrains();
            List<Coach> listCoach = coachRepository.getAllCoaches();
            List<Seat> listSeat = seatRepository.getAllSeats();
            List<TrainType> listTrainType = trainTypeRepository.getAllTrainTypes();
            List<CoachType> listCoachType = coachTypeRepository.getAllCoachTypes();
            List<SeatType> listSeatType = seatTypeRepository.getAllSeatTypes();

            Map<Integer, List<Coach>> coachesByTrain = listCoach.stream()
                    .collect(Collectors.groupingBy(Coach::getTrainID));
            Map<Integer, List<Seat>> seatsByCoach = listSeat.stream().collect(Collectors.groupingBy(Seat::getCoachID));

            request.setAttribute("listTrain", listTrain);
            request.setAttribute("coachesByTrain", coachesByTrain);
            request.setAttribute("seatsByCoach", seatsByCoach);
            request.setAttribute("listTrainType", listTrainType);
            request.setAttribute("listCoachType", listCoachType);
            request.setAttribute("listSeatType", listSeatType);

            request.getRequestDispatcher("/WEB-INF/jsp/manager/Train/manageTrainsSeats.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            switch (action) {
                case "insert_train":
                    insertTrain(request, response);
                    break;
                case "insert_coach":
                    insertCoach(request, response);
                    break;
                case "insert_seat":
                    insertSeat(request, response);
                    break;
                case "update_train":
                    updateTrain(request, response);
                    break;
                case "update_coach":
                    updateCoach(request, response);
                    break;
                case "update_seat":
                    updateSeat(request, response);
                    break;
            }
            response.sendRedirect("manage-trains-seats");
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void insertTrain(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String trainCode = request.getParameter("trainCode");
        String typeCode = request.getParameter("typeCode");
        Train newTrain = new Train(trainCode, typeCode);
        trainRepository.addTrain(newTrain);
    }

    private void insertCoach(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String trainCode = request.getParameter("trainCode");
        String typeCode = request.getParameter("typeCode");
        int coachNumber = Integer.parseInt(request.getParameter("coachNumber"));
        String coachName = request.getParameter("coachName");
        Train train = trainRepository.getTrainByTrainCode(trainCode);

        // Lấy danh sách các vị trí và số hiệu đã có
        List<Coach> existingCoaches = coachRepository.findByTrainIdOrderByPositionInTrainDesc(train.getTrainID());
        java.util.Set<Integer> usedPositions = existingCoaches.stream()
            .map(Coach::getPositionInTrain)
            .collect(java.util.stream.Collectors.toSet());
        java.util.Set<Integer> usedCoachNumbers = existingCoaches.stream()
            .map(Coach::getCoachNumber)
            .collect(java.util.stream.Collectors.toSet());

        int position = coachNumber;
        if (usedPositions.contains(position)) {
            position = 1;
            while (usedPositions.contains(position) && position <= 1000) {
                position++;
            }
            if (position > 1000) {
                throw new SQLException("Tất cả vị trí toa đã bị chiếm, không thể thêm toa mới.");
            }
        }

        int finalCoachNumber = coachNumber;
        if (usedCoachNumbers.contains(finalCoachNumber)) {
            finalCoachNumber = 1;
            while (usedCoachNumbers.contains(finalCoachNumber) && finalCoachNumber <= 1000) {
                finalCoachNumber++;
            }
            if (finalCoachNumber > 1000) {
                throw new SQLException("Tất cả số hiệu toa đã bị chiếm, không thể thêm toa mới.");
            }
        }

        Coach newCoach = new Coach(train.getTrainID(), finalCoachNumber, coachName, Integer.parseInt(typeCode));
        newCoach.setPositionInTrain(position);
        newCoach.setCapacity(0); // Default capacity, adjust if needed
        coachRepository.addCoach(newCoach);
    }

    private void insertSeat(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int coachId = Integer.parseInt(request.getParameter("coachId"));
        String typeCode = request.getParameter("typeCode");
        String rowLetter = request.getParameter("rowLetter");
        int seatsPerRow = 1;
        String prefix = "";
        try {
            seatsPerRow = Integer.parseInt(request.getParameter("seatsPerRow"));
            prefix = request.getParameter("prefix");
            if (prefix == null) prefix = "";
        } catch (Exception ignored) {}
        if (rowLetter == null || rowLetter.isEmpty()) rowLetter = "A";
        // Lấy danh sách seatName và seatNumber đã tồn tại trong coach
        List<Seat> existingSeats = seatRepository.findByCoachId(coachId);
        java.util.Set<String> existingNames = new java.util.HashSet<>();
        java.util.Set<Integer> existingNumbers = new java.util.HashSet<>();
        int maxSeatNumber = 0;
        for (Seat s : existingSeats) {
            existingNames.add(s.getSeatName());
            existingNumbers.add(s.getSeatNumber());
            if (s.getSeatNumber() > maxSeatNumber) maxSeatNumber = s.getSeatNumber();
        }
        for (int c = 1; c <= seatsPerRow; c++) {
            String seatName = prefix + rowLetter.toUpperCase() + c;
            int seatNumber = ++maxSeatNumber;
            if (!existingNames.contains(seatName) && !existingNumbers.contains(seatNumber)) {
                Seat newSeat = new Seat();
                newSeat.setCoachID(coachId);
                newSeat.setSeatTypeID(Integer.parseInt(typeCode));
                newSeat.setSeatName(seatName);
                newSeat.setSeatNumber(seatNumber);
                newSeat.setEnabled(true);
                seatRepository.addSeat(newSeat);
            }
        }
    }

    private void updateTrain(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String trainCode = request.getParameter("trainCode");
        String typeCode = request.getParameter("typeCode");
        Train train = new Train(trainCode, typeCode);
        trainRepository.updateTrain(train);
    }

    private void updateCoach(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String trainCode = request.getParameter("trainCode");
        String typeCode = request.getParameter("typeCode");
        int coachNumber = Integer.parseInt(request.getParameter("coachNumber"));
        String coachName = request.getParameter("coachName");
        Train train = trainRepository.getTrainByTrainCode(trainCode);
        Coach coach = new Coach(id, train.getTrainID(), coachNumber, coachName, Integer.parseInt(typeCode));
        coachRepository.updateCoach(coach);
    }

    private void updateSeat(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        int coachId = Integer.parseInt(request.getParameter("coachId"));
        String typeCode = request.getParameter("typeCode");
        String seatNumber = request.getParameter("seatNumber");
        Seat seat = new Seat(id, coachId, typeCode, seatNumber);
        seatRepository.updateSeat(seat);
    }

    private void deleteTrain(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String trainCode = request.getParameter("trainCode");
        trainRepository.deleteTrain(trainCode);
        response.sendRedirect("manage-trains-seats");
    }

    private void deleteCoach(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        coachRepository.deleteCoach(id);
        response.sendRedirect("manage-trains-seats");
    }

    private void deleteSeat(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        seatRepository.deleteSeat(id);
        response.sendRedirect("manage-trains-seats");
    }
}
