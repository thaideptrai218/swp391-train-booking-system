package vn.vnrailway.model;

import java.time.LocalDateTime;
import java.io.Serializable;

// Removed Lombok annotations as they seem to be causing issues in the environment.
// Getters, setters, and constructors will be explicitly defined.
public class User implements Serializable {
    private static final long serialVersionUID = 1L; // Added for Serializable
    private int userID;
    private String userName;
    private String passwordHash; // Renamed 'password' to 'passwordHash' for consistency
    private String fullName;
    private String email;
    private String phoneNumber;
    private String idCardNumber;
    private String address; // Added for customer profile display
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // No-argument constructor
    public User() {
    }

    // Constructor for creating new users (without userID, createdAt, lastLogin)
    public User(String userName, String passwordHash, String fullName, String email,
            String phoneNumber, String idCardNumber, String address, String role) {
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.idCardNumber = idCardNumber;
        this.address = address;
        this.role = role;
        this.isActive = true; // Default to active for new users
        this.createdAt = LocalDateTime.now(); // Default creation time
    }

    // All-argument constructor (useful for mapping from DB)
    public User(int userID, String userName, String passwordHash, String fullName, String email,
            String phoneNumber, String idCardNumber, String address, String role, boolean isActive,
            LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.userID = userID;
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.idCardNumber = idCardNumber;
        this.address = address;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", userName='" + userName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", idCardNumber='" + idCardNumber + '\'' +
                ", address='" + address + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
