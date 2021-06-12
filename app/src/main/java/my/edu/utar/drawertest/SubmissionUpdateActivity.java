package my.edu.utar.drawertest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.DatePickerFragment;
import my.edu.utar.drawertest.ui.home.AssignmentsActivity;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;

public class SubmissionUpdateActivity extends AppCompatActivity {

    public Button btn_update;
    public Button btn_delete;
    public TextView edt_submissioin_period;
    public TextView edt_review_period;
    private DialogFragment fragment;
    private TextView tv_task_title;
    private TextView tv_task_description;
    private int picker_role = -1;
    private String TAG = "--- Ares ---";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_update);

        String task_key = getIntent().getStringExtra("TASK_KEY");
        String assignment_key = getIntent().getStringExtra("ASSIGNMENT_KEY");


        tv_task_title = (TextView) findViewById(R.id.task_title);
        tv_task_description = (TextView) findViewById(R.id.task_description);


        btn_delete = (Button) findViewById(R.id.btn_delete_task);
        btn_delete.setOnClickListener(view -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("task").document(task_key).collection("submissions").document(assignment_key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    Toast.makeText(getApplicationContext(), "Successfully deleted.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    startActivity(intent);
                    finish();

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error deleting document", e);
                    Toast.makeText(getApplicationContext(), "Failed delete.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btn_update = (Button) findViewById(R.id.btn_update_task);
        btn_update.setOnClickListener(view -> {
            String title = tv_task_title.getText().toString();
            String description = tv_task_description.getText().toString();

            if (title.equals("")) {
                Toast.makeText(getApplicationContext(), "Please input the title field.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (description.equals("")) {
                Toast.makeText(getApplicationContext(), "Please input the description field.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            GlobalClass global = GlobalClass.getInstance();
            LoggedInUserView userInfo = global.getUserInfo();

            Map<String, Object> data = new HashMap<>();
            data.put("title", title);
            data.put("description", description);

            db.collection("task")
                    .document(task_key)
                    .collection("submissions")
                    .document(assignment_key)
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Successfully updated.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(getApplicationContext(), "Unkown Error!!! " + e, Toast.LENGTH_SHORT).show();
                        }
                    });

        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("task")
                .document(task_key)
                .collection("submissions")
                .document(assignment_key)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        boolean isJoined = true;
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            tv_task_title.setText((String) document.getData().get("title"));
                            tv_task_description.setText((String) document.getData().get("description"));
                        }
                    }
                });
    }

}
