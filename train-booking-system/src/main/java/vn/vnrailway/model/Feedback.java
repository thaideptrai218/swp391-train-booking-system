package vn.vnrailway.model;

import java.util.Date;

public class Feedback {
    private Integer feedbackId;
    private Integer userId;
    private Integer feedbackTypeId;
    private String fullName;
    private String email;
    private String feedbackContent;
    private String ticketName;
    private String description;
    private Date submittedAt;
    private String status;
    private String response;
    private Date respondedAt;
    private Integer respondedByUserId;

    public Feedback() {
    }

    public Feedback(Integer feedbackId, Integer userId, Integer feedbackTypeId, String fullName, String email,
            String feedbackContent, Date submittedAt, String status, String response, Date respondedAt,
            Integer respondedByUserId, String ticketName, String description) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.feedbackTypeId = feedbackTypeId;
        this.fullName = fullName;
        this.email = email;
        this.feedbackContent = feedbackContent;
        this.submittedAt = submittedAt;
        this.status = status;
        this.response = response;
        this.respondedAt = respondedAt;
        this.respondedByUserId = respondedByUserId;
        this.ticketName = ticketName;
        this.description = description;
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFeedbackTypeId() {
        return feedbackTypeId;
    }

    public void setFeedbackTypeId(Integer feedbackTypeId) {
        this.feedbackTypeId = feedbackTypeId;
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

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Date respondedAt) {
        this.respondedAt = respondedAt;
    }

    public Integer getRespondedByUserId() {
        return respondedByUserId;
    }

    public void setRespondedByUserId(Integer respondedByUserId) {
        this.respondedByUserId = respondedByUserId;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}