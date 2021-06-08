package my.edu.utar.drawertest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.common.FirebaseAuthService;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;
import my.edu.utar.drawertest.OnFragmentInteraction;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout fullnameTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout confirmPasswordTextInputLayout;
    private AppCompatButton btn_submit;
    private RadioGroup modeRadio;
    private String TAG = "-- -- ARES -- --";
    private boolean lockflag = false;
    private String mode;
    private View formView;
    private View progressView;
    private OnFragmentInteraction mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        onInitialize();
    }

    public void onInitialize() {
        formView = findViewById(R.id.register_form);
        progressView = findViewById(R.id.register_progress);
        fullnameTextInputLayout = (TextInputLayout) findViewById(R.id.full_name);
        emailTextInputLayout = (TextInputLayout) findViewById(R.id.email);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.password);
        confirmPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.confirm_password);
        btn_submit = (AppCompatButton) findViewById(R.id.signup_button);
        modeRadio = (RadioGroup) findViewById(R.id.profile_mode_radiogroup);
        modeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(TAG, "onCheckkedChanged: checkedId: " + checkedId);
                switch (checkedId) {
                    case R.id.profile_teacher_raidobutton:
                        mode = "teacher";
                        break;
                    case R.id.profile_student_radiobutton:
                        mode = "student";
                        break;
                }
            }
        });
        btn_submit.setOnClickListener(v -> {
            if(lockflag && !validateForm()) {
                return;
            }
            lockflag = true;
            showProgress(true);

            String password = passwordTextInputLayout.getEditText().getText().toString();
            String email = emailTextInputLayout.getEditText().getText().toString();

            FirebaseAuthService.createUser(email, password, new FirebaseAuthService.FirebaseAuthServiceResult() {
                @Override
                public void onSuccess() {
                    showProgress(false);
                    registerUserOnFirestore();
                }

                @Override
                public void onFailure(String message) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void showProgress(Boolean show) {
        if (show) {
            formView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
        } else {
            formView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
        }
    }

    boolean validateForm() {
        Log.i(TAG, "validateForm");
        boolean b = true;
        fullnameTextInputLayout.setErrorEnabled(false);
        passwordTextInputLayout.setErrorEnabled(false);
        emailTextInputLayout.setErrorEnabled(false);
        confirmPasswordTextInputLayout.setErrorEnabled(false);
        View focus = null;
        String password = passwordTextInputLayout.getEditText().getText().toString();
        String confirm_password = confirmPasswordTextInputLayout.getEditText().getText().toString();
        String email = emailTextInputLayout.getEditText().getText().toString();
        String fullname = fullnameTextInputLayout.getEditText().getText().toString();

        if (password.length() < 6) {
            b = false;
            passwordTextInputLayout.setErrorEnabled(true);
            passwordTextInputLayout.setError("Password must contain at least 6 characters");
            focus = passwordTextInputLayout;
        }

        if (!password.equals(confirm_password)) {
            b = false;
            confirmPasswordTextInputLayout.setErrorEnabled(true);
            confirmPasswordTextInputLayout.setError("Confirm password does not match.");
            focus = confirmPasswordTextInputLayout;
        }

        if (mode == null)
            mode = "";
        if (mode.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select the mode", Toast.LENGTH_SHORT).show();
            b = false;
        }

        if(fullname.length() < 3){
            b = false;
            fullnameTextInputLayout.setErrorEnabled(true);
            fullnameTextInputLayout.setError("Enter a longer name");
            focus = fullnameTextInputLayout;
        }

        if (!email.contains("@")) {
            b = false;
            emailTextInputLayout.setErrorEnabled(true);
            emailTextInputLayout.setError("Enter a valid email address");
            focus = emailTextInputLayout;
        }
        if (!b) {
            focus.requestFocus();
        }
        Log.i(TAG, "validateForm: " + b);
        return b;
    }

    void registerUserOnFirestore() {
        Log.i(TAG, "registerUserOnFirestore");
        String password = passwordTextInputLayout.getEditText().getText().toString();
        String confirm_password = confirmPasswordTextInputLayout.getEditText().getText().toString();
        String email = emailTextInputLayout.getEditText().getText().toString();
        String fullname = fullnameTextInputLayout.getEditText().getText().toString();
        //********** FIREBASE LOGIC *****************//
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("name", fullname);
        user.put("email", email);
        user.put("password", password);
        user.put("role", mode);
        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Successfully registration", Toast.LENGTH_SHORT).show();
                        GlobalClass global = GlobalClass.getInstance();
                        LoggedInUserView viewModel = new LoggedInUserView(fullname, documentReference.getId(), email, mode == "teacher"?true:false);
                        global.setUserInfo(viewModel);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        startActivity(intent);
                        finish();
                        lockflag = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(), "Unfortunetly registration failed.", Toast.LENGTH_SHORT).show();
                        lockflag = false;
                    }
                });
        //*******************************************//
    }
}