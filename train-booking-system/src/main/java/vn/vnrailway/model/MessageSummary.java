package vn.vnrailway.model;

public class MessageSummary {
    private int userId;
    private String fullName;
    private String email;
    private String lastMessage;
    private String senderType;

    public MessageSummary(int userId, String fullName, String email, String lastMessage) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.lastMessage = lastMessage;
    }

    public MessageSummary(int userId, String fullName, String email, String lastMessage, String senderType) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.lastMessage = lastMessage;
        this.senderType = senderType;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }
}
