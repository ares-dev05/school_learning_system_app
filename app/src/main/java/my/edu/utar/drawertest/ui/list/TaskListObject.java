package my.edu.utar.drawertest.ui.list;


public class TaskListObject {
    private String description;
    private String title;
    private String deadline;

    public TaskListObject(String description, String title, String deadline) {
        this.description = description;
        this.title = title;
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
