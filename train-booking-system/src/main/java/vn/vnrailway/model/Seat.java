package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    private int seatID;
    private int coachID; // FK
    // private Coach coach; // Object
    private int seatNumber; // Số thứ tự/định danh logic của ghế trong toa
    private String seatName; // Tên hiển thị của ghế
    private int seatTypeID; // FK
    // private SeatType seatType; // Object
    private boolean isEnabled;

    public Seat(int coachId, String typeCode, String seatNumber) {
        this.coachID = coachId;
        this.seatTypeID = Integer.parseInt(typeCode);
        this.seatName = seatNumber;
        this.isEnabled = true;
    }

    public Seat(int id, int coachId, String typeCode, String seatNumber) {
        this.seatID = id;
        this.coachID = coachId;
        this.seatTypeID = Integer.parseInt(typeCode);
        this.seatName = seatNumber;
    }
}
