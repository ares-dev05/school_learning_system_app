package my.edu.utar.drawertest.ui.list;


public class AssignmentsListObject {
    private String description;
    private String title;
    private String deadline;
    private String key;
    private String taskkey;

    public AssignmentsListObject(String taskkey, String key, String description, String title, String deadline) {
        this.description = description;
        this.title = title;
        this.deadline = deadline;
        this.key = key;
        this.taskkey = taskkey;
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

    public String getKey() {
        return key;
    }

    public String getTaskKey() {
        return taskkey;
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

    public void setKey(String key) {
        this.key = key;
    }
}
