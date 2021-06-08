package my.edu.utar.drawertest.ui.login;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

import my.edu.utar.drawertest.MainActivity;
import my.edu.utar.drawertest.MessagingService;
import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.RegisterActivity;
import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.common.FirebaseAuthService;
import my.edu.utar.drawertest.ui.common.LoadingFragment;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    public Button btn_sign_in, btn_register;
    private TextInputLayout tv_email, tv_password;
    private String TAG = "Ares";
    private boolean lockflag = false;
    private LoadingFragment loadingFragment = LoadingFragment.newInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(FirebaseAuthService.isLoged()){
            goToMain();
        }

        tv_email = (TextInputLayout) findViewById(R.id.username);
        tv_password = (TextInputLayout) findViewById(R.id.password);

        btn_register= (Button) findViewById(R.id.register);
        btn_register.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
                });

        btn_sign_in = (Button) findViewById(R.id.btn_login);
        btn_sign_in.setOnClickListener(v -> {
            if(!lockflag && validateForm()) {
                lockflag = true;
                login();
            }
        });
    }

    public void login(){
        Log.i(TAG,"login");
        loadingFragment.show(getSupportFragmentManager(), "Signing...");
        String email = tv_email.getEditText().getText().toString();
        String password = tv_password.getEditText().getText().toString();
        FirebaseAuthService.login(email, password, new FirebaseAuthService.FirebaseAuthServiceResult() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"login.onSuccess");
                clearForm();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .whereEqualTo("email", email)
                        .whereEqualTo("password", password)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    boolean flag = false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Object list = document.getData().get("tasks");
                                        GlobalClass global = GlobalClass.getInstance();
                                        LoggedInUserView model = new LoggedInUserView((String)document.getData().get("name"),
                                                document.getId(), (String)document.getData().get("email"),
                                                document.getData().get("role").equals("teacher")?true:false);

                                        global.setUserInfo(model);

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                        startActivity(intent);
                                        finish();
                                        flag = true;
                                    }
                                    if(!flag) {
                                        Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_SHORT).show();
                                }
                                lockflag = false;
                                loadingFragment.dismiss();
                            }
                        });
                lockflag = false;
            }
            @Override
            public void onFailure(String message) {
                Log.i(TAG,"login.onFailure: "+message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                loadingFragment.dismiss();
                lockflag = false;
            }
        });
    }


    void clearForm(){
        tv_email.getEditText().setText("");
        tv_password.getEditText().setText("");
    }

    private void goToMain(){
        loadingFragment.show(getSupportFragmentManager(), "Signing...");
        MessagingService.sendRegistrationToServer(this);
        FirebaseUser user = FirebaseAuthService.getCurrentUserLoged();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean flag = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Object list = document.getData().get("tasks");
                                GlobalClass global = GlobalClass.getInstance();
                                LoggedInUserView model = new LoggedInUserView((String)document.getData().get("name"),
                                        document.getId(), (String)document.getData().get("email"),
                                        document.getData().get("role").equals("teacher")?true:false);

                                global.setUserInfo(model);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                startActivity(intent);
                                finish();
                                flag = true;
                            }
                            if(!flag) {
                                Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_SHORT).show();
                                loadingFragment.dismiss();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_SHORT).show();
                            loadingFragment.dismiss();
                        }
                        lockflag = false;

                    }
                });

    }

    boolean validateForm() {
        Log.i(TAG, "validateForm");
        boolean b = true;
        tv_email.setErrorEnabled(false);
        tv_password.setErrorEnabled(false);
        View focus = null;
        String password = tv_password.getEditText().getText().toString();
        String email = tv_email.getEditText().getText().toString();

        if (password.length() < 6) {
            b = false;
            tv_password.setErrorEnabled(true);
            tv_password.setError("Password must contain at least 6 characters");
            focus = tv_password;
        }

        if (!email.contains("@")) {
            b = false;
            tv_email.setErrorEnabled(true);
            tv_email.setError("Enter a valid email address");
            focus = tv_email;
        }
        if (!b) {
            focus.requestFocus();
        }
        Log.i(TAG, "validateForm: " + b);
        return b;
    }


}