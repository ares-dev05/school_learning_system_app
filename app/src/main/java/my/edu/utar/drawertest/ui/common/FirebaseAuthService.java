package my.edu.utar.drawertest.ui.common;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;


public class FirebaseAuthService {
    public static final String TAG = "FirebaseAuthService";

    public interface FirebaseAuthServiceResult {
        void onSuccess();

        void onFailure(String message);
    }

    public static void createUser(String email, String password, final FirebaseAuthServiceResult result) {
        Log.i(TAG, "createUser");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "createUser.isFailure");
                            result.onFailure(getError(task.getException()));
                        } else {
                            Log.i(TAG, "createUser.isSucess: " + task.getResult().getUser().getUid());
                            result.onSuccess();
                        }

                    }

                });
    }

    public static void login(String email, String password, final FirebaseAuthServiceResult result) {
        Log.i(TAG, "login");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "login.isFailure");
                            result.onFailure(getError(task.getException()));
                        } else {
                            Log.i(TAG, "login sucess");
                            result.onSuccess();
                        }

                    }

                });
    }

    public static void changePassword(String oldPassword, final String newPassword, final FirebaseAuthServiceResult result) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            assert user != null;
        } catch (Exception e) {
            Log.e(TAG, "changePassword", e);
        }
        String email = user.getEmail();
        login(email, oldPassword, new FirebaseAuthServiceResult() {
            @Override
            public void onSuccess() {
                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    result.onFailure(getError(task.getException()));
                                } else {
                                    result.onSuccess();
                                }
                            }
                        });
            }

            @Override
            public void onFailure(String message) {
                result.onFailure(message);
            }
        });

    }

    public static void resetPassword(String email, final FirebaseAuthServiceResult result) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "resetPassword.isFailure");
                    result.onFailure(getError(task.getException()));

                } else {
                    Log.i(TAG, "resetPassword.isSuccessful");
                    result.onSuccess();
                }
            }
        });
    }

    @NonNull
    public static String getError(Exception ex) {
        try {
            throw ex;
        } catch (FirebaseAuthRecentLoginRequiredException e) {
            Log.e(TAG, "FirebaseAuthRecentLoginRequiredException: " + e.getErrorCode());
            switch (e.getErrorCode()) {
                case "ERROR_REQUIRES_RECENT_LOGIN":
                    return "This is a sensitive operation, to continue log in again";
                default:
                    return "An error occurred during the process, login and try again";

            }
        } catch (FirebaseAuthWeakPasswordException e) {
            Log.e(TAG, "FirebaseAuthWeakPasswordException: " + e.getErrorCode());
            switch (e.getErrorCode()) {
                case "ERROR_WEAK_PASSWORD":
                    return "The password must contain at least 6 characters.";
                default:
                    return "An error occurred during the process, please try again later";
            }
        } catch (FirebaseAuthInvalidCredentialsException e) {
            Log.e(TAG, "FirebaseAuthInvalidCredentialsException: " + e.getErrorCode());
            switch (e.getErrorCode()) {
                case "ERROR_INVALID_EMAIL":
                case "ERROR_WRONG_PASSWORD":
                    return "Invalid username and/or password(s).";
                default:
                    return "The data informed is invalid, contact us to solve your problem.";
            }
        } catch (ApiException e) {
            Log.e(TAG, "ApiException: " + e.getMessage());
            return "Connection to service was lost, please try again later";
        } catch (FirebaseAuthInvalidUserException e) {
            Log.e(TAG, "FirebaseAuthInvalidUserException: " + e.getErrorCode());
            switch (e.getErrorCode()) {
                case "ERROR_USER_NOT_FOUND":
                    return "Invalid username and/or password(s).";
                default:
                    return "The data informed is invalid, contact us to solve your problem.";
            }
        } catch (FirebaseAuthUserCollisionException e) {
            Log.e(TAG, "FirebaseAuthUserCollisionException: " + e.getErrorCode());
            switch (e.getErrorCode()) {
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "This email is already in use.";
                default:
                    return "It is not possible to use this email.";
            }

        } catch (FirebaseAuthActionCodeException e) {
            Log.e(TAG, "FirebaseAuthActionCodeException: " + e.getErrorCode());
            return "Failed to register new user: FirebaseAuthActionCodeException " + e.getErrorCode();
        } catch (FirebaseTooManyRequestsException e) {
            Log.e(TAG, "FirebaseTooManyRequestsException: " + e.getMessage());
            return "You made too many requests in a short period of time, try again later";
        } catch (FirebaseAuthEmailException e) {
            Log.e(TAG, "FirebaseAuthEmailException: " + e.getErrorCode());
            return "Failed to register new user: FirebaseAuthEmailException.";
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + ex);
            return "An error occurred during the process, please try again later";
        }
    }

    public static boolean isLoged() {
        boolean isLoged = false;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            isLoged = true;
        }
        Log.i(TAG, "isLoged: " + isLoged);
        return isLoged;
    }

    public static FirebaseUser getCurrentUserLoged() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser;
    }

    public static String getUid(){
        String uid = "";
        if(isLoged()){
            uid = FirebaseAuth.getInstance().getUid();
        }
        Log.i(TAG,"getUid: "+uid);
        return uid;
    }

    public static String getUserDisplayName(){
        Log.i(TAG,"getUserDisplayName");
        if(isLoged()){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser.getDisplayName() != null){
                return firebaseUser.getDisplayName();
            }else{
                return "";
            }
        }else{
            return "null";
        }
    }

    public static void onAuthStateChanged(final FirebaseAuthServiceResult result){
        Log.i(TAG, "onAuthStateChanged");
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i(TAG, "onAuthStateChanged: onAuthStateChanged");
                if(isLoged()){
                    result.onSuccess();
                }else{
                    result.onFailure("");
                }
            }
        });
    }
}
