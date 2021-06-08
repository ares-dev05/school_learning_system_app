package my.edu.utar.drawertest.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String email;
    private boolean isTeacher;

    public LoggedInUser(String userId, String displayName, String email, boolean isTeacher) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.isTeacher = isTeacher;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isTeacher() {
        return isTeacher;
    }
}