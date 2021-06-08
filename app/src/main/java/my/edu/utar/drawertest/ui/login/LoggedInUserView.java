package my.edu.utar.drawertest.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String displayName;
    private String key;
    private String email;
    private boolean isTeacher;
    //... other data fields that may be accessible to the UI

    public LoggedInUserView(String displayName, String key, String email, boolean isTeacher) {
        this.displayName = displayName;
        this.key = key;
        this.email = email;
        this.isTeacher = isTeacher;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getKey() {
        return key;
    }

    public String getEmail() {
        return email;
    }

    public boolean isTeacher() {
        return isTeacher;
    }
}