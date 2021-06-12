package my.edu.utar.drawertest.ui.list;


public class ReviewListObject {
    private String user_key;
    private String name;
    private String rating1;
    private String rating2;
    private String rating3;

    private String description;

    public ReviewListObject(String user_key, String name, String rating1,String rating2,String rating3, String description) {
        this.user_key = user_key;
        this.name = name;
        this.rating1 = rating1;
        this.rating2 = rating2;
        this.rating3 = rating3;
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

    public String getRating1() {
        return rating1;
    }

    public String getRating2() {
        return rating2;
    }

    public String getRating3() {
        return rating3;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating1(String rating1) {
        this.rating1 = rating1;
    }

    public void setRating2(String rating2) {
        this.rating2 = rating2;
    }

    public void setRating3(String rating3) {
        this.rating3 = rating3;
    }

    public void setUserKey(String user_key) {
        this.user_key = user_key;
    }
}
