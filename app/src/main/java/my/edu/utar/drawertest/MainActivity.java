package my.edu.utar.drawertest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.home.StudentActivity;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;
import my.edu.utar.drawertest.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private boolean mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalClass global = GlobalClass.getInstance();
        LoggedInUserView userInfo = global.getUserInfo();
        mode = userInfo.isTeacher();

        if(mode) {
            setContentView(R.layout.activity_main_teacher);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_newTask, R.id.nav_review, R.id.nav_logOut)
                    .setDrawerLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_teacher);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

            NavigationUI.setupWithNavController(navigationView, navController);

            View headerLayout =
                    navigationView.inflateHeaderView(R.layout.nav_header_main);
            TextView nav_user_title = headerLayout.findViewById(R.id.nav_user_title);
            TextView nav_email_subtitle = (TextView) headerLayout.findViewById(R.id.nav_email_subtitle);
            String fullname = userInfo.getDisplayName();
            String email = userInfo.getEmail();
            nav_user_title.setText(fullname);
            nav_email_subtitle.setText(email);

            Menu menuNav = navigationView.getMenu();

            MenuItem logoutItem = menuNav.findItem(R.id.nav_logOut);
            logoutItem.setOnMenuItemClickListener(item -> {
                global.setUserInfo(null);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            });

        } else {
            setContentView(R.layout.activity_main_students);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home_students, R.id.nav_task_students, R.id.nav_postResult, R.id.nav_logOut)
                    .setDrawerLayout(drawer)
                    .build();


            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_students);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            View headerLayout =
                    navigationView.inflateHeaderView(R.layout.nav_header_main);
            TextView nav_user_title = headerLayout.findViewById(R.id.nav_user_title);
            TextView nav_email_subtitle = (TextView) headerLayout.findViewById(R.id.nav_email_subtitle);
            String fullname = userInfo.getDisplayName();
            String email = userInfo.getEmail();
            nav_user_title.setText(fullname);
            nav_email_subtitle.setText(email);

            Menu menuNav = navigationView.getMenu();

            MenuItem logoutItem = menuNav.findItem(R.id.nav_logOut);
            logoutItem.setOnMenuItemClickListener(item -> {
                global.setUserInfo(null);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {

        if(mode) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_teacher);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        } else {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_students);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }

    }
}