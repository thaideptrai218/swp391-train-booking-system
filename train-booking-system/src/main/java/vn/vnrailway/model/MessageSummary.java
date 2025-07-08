package vn.vnrailway.model;

public class MessageSummary {
    private int userId;
    private String fullName;
    private String email;
    private String lastMessage;

    public MessageSummary(int userId, String fullName, String email, String lastMessage) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.lastMessage = lastMessage;
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
}
