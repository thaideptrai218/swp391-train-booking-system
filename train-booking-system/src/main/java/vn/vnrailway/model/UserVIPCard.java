package vn.vnrailway.model;

import java.time.LocalDateTime;

public class UserVIPCard {
    private int userVIPCardID;
    private int userID;
    private int vipCardTypeID;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiryDate;
    private boolean isActive;
    private String transactionReference;
    private VIPCardType vipCardType;

    public UserVIPCard() {
    }

    public UserVIPCard(int userID, int vipCardTypeID, LocalDateTime expiryDate, String transactionReference) {
        this.userID = userID;
        this.vipCardTypeID = vipCardTypeID;
        this.purchaseDate = LocalDateTime.now();
        this.expiryDate = expiryDate;
        this.isActive = true;
        this.transactionReference = transactionReference;
    }

    public UserVIPCard(int userVIPCardID, int userID, int vipCardTypeID, LocalDateTime purchaseDate, LocalDateTime expiryDate, boolean isActive, String transactionReference) {
        this.userVIPCardID = userVIPCardID;
        this.userID = userID;
        this.vipCardTypeID = vipCardTypeID;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.isActive = isActive;
        this.transactionReference = transactionReference;
    }

    public int getUserVIPCardID() {
        return userVIPCardID;
    }

    public void setUserVIPCardID(int userVIPCardID) {
        this.userVIPCardID = userVIPCardID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getVipCardTypeID() {
        return vipCardTypeID;
    }

    public void setVipCardTypeID(int vipCardTypeID) {
        this.vipCardTypeID = vipCardTypeID;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public boolean isValid() {
        return isActive && expiryDate.isAfter(LocalDateTime.now());
    }

    public VIPCardType getVipCardType() {
        return vipCardType;
    }

    public void setVipCardType(VIPCardType vipCardType) {
        this.vipCardType = vipCardType;
    }
}
