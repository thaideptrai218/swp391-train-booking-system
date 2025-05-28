package vn.vnrailway.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int userID;
    private String userName;
    private String password;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String idCardNumber;
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public User(String userName, String password, String fullName, String email,
                String phoneNumber, String idCardNumber, String role) {
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.idCardNumber = idCardNumber;
        this.role = role;
    }
}
