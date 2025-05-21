package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coach {
    private int coachID;
    private int trainID; // FK
    // private Train train; // Object
    private int coachNumber; // Số thứ tự/định danh logic của toa
    private String coachName; // Tên hiển thị của toa
    private int coachTypeID; // FK
    // private CoachType coachType; // Object
    private int capacity;
    private int positionInTrain; // Vị trí toa trong đoàn tàu
}
