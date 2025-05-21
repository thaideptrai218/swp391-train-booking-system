package com.tshami.model;

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
}
