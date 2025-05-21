package com.tshami.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; // Hoặc java.sql.Timestamp

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int userID;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String passwordHash; // Thường không lấy ra ngoài, nhưng có thể cần cho việc quản lý
    private String idCardNumber; // Có thể null
    private String role; // "Customer", "Staff", "Admin" (Có thể dùng Enum trong Java)
    private boolean isActive;
    private LocalDateTime createdAt; // Hoặc Timestamp
    private LocalDateTime lastLogin; // Có thể null, hoặc Timestamp
}
