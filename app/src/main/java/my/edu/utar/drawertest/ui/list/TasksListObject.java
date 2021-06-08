package my.edu.utar.drawertest.ui.list;


public class TasksListObject {
    private String title;
    private String description;
    private String key;
    private String submission_deadline;
    private String review_deadline;

    public TasksListObject(String key, String title, String description, String submition_deadline, String review_deadline) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.submission_deadline = submition_deadline;
        this.review_deadline = review_deadline;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSubmissionDeadline() { return submission_deadline; }

    public String getReviewDeadline() { return review_deadline; }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSubmissionDealine(String date) {
        this.submission_deadline = date;
    }

    public void setReviewDealine(String date) {
        this.review_deadline = date;
    }
}
