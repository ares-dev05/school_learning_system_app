package my.edu.utar.drawertest.data;

import my.edu.utar.drawertest.ui.login.LoggedInUserView;

public class GlobalClass {
    private static final GlobalClass ourInstance = new GlobalClass();
    public static GlobalClass getInstance() {
        return ourInstance;
    }

    private LoggedInUserView userInfo;

    public LoggedInUserView getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(LoggedInUserView user) {
        userInfo = user;
    }
}