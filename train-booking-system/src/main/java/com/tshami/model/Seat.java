package com.tshami.model;

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
}
