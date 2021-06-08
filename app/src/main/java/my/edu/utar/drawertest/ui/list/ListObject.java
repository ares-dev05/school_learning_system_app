package my.edu.utar.drawertest.ui.list;


public class ListObject {
    private String description;
    private String title;
    private String key;

    public ListObject(String key, String description, String title) {
        this.key = key;
        this.description = description;
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
