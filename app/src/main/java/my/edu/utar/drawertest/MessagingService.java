package my.edu.utar.drawertest;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import my.edu.utar.drawertest.localdata.SharedPrefManager;
import my.edu.utar.drawertest.ui.common.FirebaseAuthService;


public class MessagingService extends FirebaseMessagingService {
    public static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        saveToken(token);
        if (FirebaseAuthService.isLoged())
            sendRegistrationToServer(getApplicationContext());
    }

    public void saveToken(String token) {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        sharedPrefManager.storeToken(token);
    }

    public static void sendRegistrationToServer(Context context) {
        Log.i(TAG, "sendRegistrationToServer");
        try{
            SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
            final String token = sharedPrefManager.getToken();
            if (FirebaseAuthService.isLoged()) {
                Map<String, Object> map = new ArrayMap<>();
                map.put("createdAt", Timestamp.now());
                FirebaseFirestore
                        .getInstance()
                        .collection("Users")
                        .document(FirebaseAuthService.getUid())
                        .collection("devices")
                        .document(token)
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i(TAG, "sendRegistrationToServer "+task.isSuccessful()+ " : " + token);
                            }
                        });
            }
        }catch (NullPointerException e){
            Log.e(TAG, "sendRegistrationToServer", e);
        }

    }
}
