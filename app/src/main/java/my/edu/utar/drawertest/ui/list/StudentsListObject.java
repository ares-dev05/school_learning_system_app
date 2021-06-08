package my.edu.utar.drawertest.ui.list;


public class StudentsListObject {
    private String name;
    private String postData;
    private String key;
    private String taskKey;
    private String submissionKey;

    public StudentsListObject(String taskKey, String submissionKey, String key, String name, String postData) {
        this.taskKey = taskKey;
        this.submissionKey = submissionKey;
        this.key = key;
        this.name = name;
        this.postData = postData;
    }

    public String getTaskKey() {return taskKey;}

    public String getSubmissionKey() {return submissionKey;}

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getPostData() {
        return postData;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPostData(String data) {
        this.postData = data;
    }
}
