package my.edu.utar.drawertest.ui.list;


public class ReviewListObject {
    private String user_key;
    private String name;
    private String rating;
    private String description;

    public ReviewListObject(String user_key, String name, String rating, String description) {
        this.user_key = user_key;
        this.name = name;
        this.rating = rating;
        this.description = description;
    }

    public String getUserKey() {
        return user_key;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setUserKey(String user_key) {
        this.user_key = user_key;
    }
}
