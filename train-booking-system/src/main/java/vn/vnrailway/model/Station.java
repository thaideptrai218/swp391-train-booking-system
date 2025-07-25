package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Station {
    private int stationID;
    private String stationName;
    private String address; // Có thể null
    private String city; // Có thể null
    private String region; // Có thể null
    private String phoneNumber;
    private boolean isLocked;
    private boolean isActive; // Thêm trường trạng thái hoạt động
    private String stationCode; // Thêm mã code, có thể null
}
