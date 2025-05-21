package com.tshami.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainType {
    private int trainTypeID;
    private String typeName;
    private String description; // Có thể null
}
