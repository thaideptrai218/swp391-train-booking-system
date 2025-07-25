package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    private int routeID;
    private String routeName;
    private String description; // Có thể null
    private boolean isLocked;
    private boolean isActive; // Thêm trạng thái hoạt động
}
