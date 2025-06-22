package vn.vnrailway.model;

public class Feedback {
    private String title;
    private String comment;

    public Feedback(String title, String comment) {
        this.title = title;
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }
}
