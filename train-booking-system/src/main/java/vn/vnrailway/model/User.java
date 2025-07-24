package vn.vnrailway.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Class representing a User entity in the railway system.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userID;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private String idCardNumber;
    private String role; // Constrained to 'Customer', 'Staff', 'Admin'
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private java.time.LocalDate dateOfBirth;
    private boolean isGuestAccount;
    private String gender; // Added for gender, constrained to 'Male', 'Female', 'Other', or null
    private String address; // Already present, retained
    private String avatarPath;

    // No-argument constructor
    public User() {
    }

    // Constructor for creating new users (without userID, createdAt, lastLogin)
    public User(String fullName, String email, String phoneNumber, String passwordHash,
            String idCardNumber, String role, boolean isGuestAccount,
            LocalDate dateOfBirth, String gender, String address) {
        this.fullName = (fullName != null) ? fullName : "";
        this.email = (email != null) ? email : "";
        this.phoneNumber = (phoneNumber != null) ? phoneNumber : "";
        this.passwordHash = passwordHash; // Can be null for guest/social login
        this.idCardNumber = idCardNumber; // Can be null
        setRole(role); // Use setter for validation
        this.isActive = true;
        this.isGuestAccount = isGuestAccount;
        this.createdAt = LocalDateTime.now();
        this.dateOfBirth = dateOfBirth; // Can be null
        setGender(gender); // Use setter for validation
        this.address = (address != null) ? address : "";
    }

    // All-argument constructor
    public User(int userID, String fullName, String email, String phoneNumber,
            String passwordHash, String idCardNumber, String role, boolean isActive,
            LocalDateTime createdAt, LocalDateTime lastLogin, boolean isGuestAccount,
            LocalDate dateOfBirth, String gender, String address) {
        this.userID = userID;
        this.fullName = (fullName != null) ? fullName : "";
        this.email = (email != null) ? email : "";
        this.phoneNumber = (phoneNumber != null) ? phoneNumber : "";
        this.passwordHash = passwordHash; // Can be null
        this.idCardNumber = idCardNumber; // Can be null
        setRole(role); // Use setter for validation
        this.isActive = isActive;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.lastLogin = lastLogin; // Can be null
        this.isGuestAccount = isGuestAccount;
        this.dateOfBirth = dateOfBirth; // Can be null
        setGender(gender); // Use setter for validation
        this.address = (address != null) ? address : "";
    }

    public User(int userID, String fullName, String email, String phoneNumber,
            String passwordHash, String idCardNumber, String role, boolean isActive,
            LocalDateTime createdAt, LocalDateTime lastLogin, boolean isGuestAccount,
            LocalDate dateOfBirth, String gender, String address, String avatarPath) {
        this.userID = userID;
        this.fullName = (fullName != null) ? fullName : "";
        this.email = (email != null) ? email : "";
        this.phoneNumber = (phoneNumber != null) ? phoneNumber : "";
        this.passwordHash = passwordHash; // Can be null
        this.idCardNumber = idCardNumber; // Can be null
        setRole(role); // Use setter for validation
        this.isActive = isActive;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.lastLogin = lastLogin; // Can be null
        this.isGuestAccount = isGuestAccount;
        this.dateOfBirth = dateOfBirth; // Can be null
        setGender(gender); // Use setter for validation
        this.address = (address != null) ? address : "";
        this.avatarPath = avatarPath;
    }

    // Getters and Setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = (fullName != null) ? fullName : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email != null) ? email : "";
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = (phoneNumber != null) ? phoneNumber : "";
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash; // Can be null
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber; // Can be null
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role != null) {
            String trimmedRole = role.trim();
            if (trimmedRole.equalsIgnoreCase("Customer") || trimmedRole.equalsIgnoreCase("Staff")
                    || trimmedRole.equalsIgnoreCase("Admin") || trimmedRole.equalsIgnoreCase("Guest")
                    || trimmedRole.equalsIgnoreCase("Manager")) {
                this.role = trimmedRole;
            } else {
                // Log the issue instead of throwing an exception to prevent the page from
                // crashing.
                System.err.println(
                        "Warning: Invalid role found in database: '" + role + "'. Setting role to null for this user.");
                this.role = null;
            }
        } else {
            this.role = null;
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin; // Can be null
    }

    public boolean isGuestAccount() {
        return isGuestAccount;
    }

    public void setGuestAccount(boolean isGuestAccount) {
        this.isGuestAccount = isGuestAccount;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth; // Can be null
    }

    public Date getDateOfBirthAsDate() {
        return dateOfBirth != null ? Date.from(dateOfBirth.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (gender == null || gender.equals("Male") || gender.equals("Female") || gender.equals("Other")
                || gender.equals("Nam") || gender.equals("Nữ") || gender.equals("Khác")) {
            this.gender = gender;
        } else {
            throw new IllegalArgumentException("Gender must be 'Male', 'Female', 'Other', or null");
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = (address != null) ? address : "";
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = (avatarPath != null) ? avatarPath : "";
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", idCardNumber='" + idCardNumber + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                ", isGuestAccount=" + isGuestAccount + // Added
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                ", isGuestAccount=" + isGuestAccount +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                '}';
    }
}
