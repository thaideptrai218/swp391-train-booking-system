package vn.vnrailway.model;

import java.time.LocalDateTime;
import java.util.Date;

public class Message {
    private int messageId;
    private int userId;
    private Integer staffId;
    private String content;
    private LocalDateTime timestamp;
    private String senderType;

    public Message(int messageId, int userId, Integer staffId, String content, LocalDateTime timestamp,
            String senderType) {
        this.messageId = messageId;
        this.userId = userId;
        this.staffId = staffId;
        this.content = content;
        this.timestamp = timestamp;
        this.senderType = senderType;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public Date getTimestampAsDate() {
        return timestamp != null ? Date.from(timestamp.atZone(java.time.ZoneId.systemDefault()).toInstant()) : null;
    }
}
