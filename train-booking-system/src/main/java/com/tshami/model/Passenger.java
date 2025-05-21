package com.tshami.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate; // Hoặc java.sql.Date

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {
    private int passengerID;
    private String fullName;
    private String idCardNumber; // Có thể null
    private int passengerTypeID; // FK
    // private PassengerType passengerType; // Có thể có đối tượng này nếu bạn muốn eager/lazy loading
    private LocalDate dateOfBirth; // Có thể null
    private Integer userID; // FK, Integer để cho phép null
}
