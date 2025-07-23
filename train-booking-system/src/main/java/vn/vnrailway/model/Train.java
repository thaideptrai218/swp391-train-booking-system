package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Train {
    private int trainID;
    private String trainName; // Mã tàu: SE1, TN2
    private int trainTypeID; // FK
    // private TrainType trainType; // Object
    private boolean isActive;
    private boolean isLocked;

    public Train(String trainName, String typeCode) {
        this.trainName = trainName;
        this.trainTypeID = Integer.parseInt(typeCode);
        this.isActive = true;
        this.isLocked = false;
    }

    public boolean isLocked() {
        return isLocked;
    }
    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }
    public boolean getIsLocked() {
        return isLocked;
    }
}
