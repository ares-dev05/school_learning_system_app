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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.DatePickerFragment;
import my.edu.utar.drawertest.ui.home.AssignmentsActivity;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;

public class UpdateActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_update);

        String task_key = getIntent().getStringExtra("TASK_KEY");

        tv_task_title = (TextView) findViewById(R.id.task_title);
        tv_task_description = (TextView) findViewById(R.id.task_description);

        edt_submissioin_period = (TextView) findViewById(R.id.submissioin_period);
        edt_submissioin_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                picker_role = 1;
            }
        });

        edt_review_period = (TextView) findViewById(R.id.review_period);
        edt_review_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                picker_role = 2;
            }
        });

        btn_delete = (Button) findViewById(R.id.btn_delete_task);
        btn_delete.setOnClickListener(view -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("task").document(task_key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    GlobalClass global = GlobalClass.getInstance();
                    LoggedInUserView userInfo = global.getUserInfo();
                    db.collection("users").document(userInfo.getKey()).collection("tasks").document(task_key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
            String submission_period = edt_submissioin_period.getText().toString();
            String review_period = edt_review_period.getText().toString();

            if (title.equals("")) {
                Toast.makeText(getApplicationContext(), "Please input the title field.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (description.equals("")) {
                Toast.makeText(getApplicationContext(), "Please input the description field.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (submission_period.equals("")) {
                Toast.makeText(getApplicationContext(), "Please input the submission period field.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (review_period.equals("")) {
                Toast.makeText(getApplicationContext(), "Please input the review period field.", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            GlobalClass global = GlobalClass.getInstance();
            LoggedInUserView userInfo = global.getUserInfo();

            Map<String, Object> data = new HashMap<>();
            data.put("title", title);
            data.put("description", description);
            data.put("submission_period", submission_period);
            data.put("review_period", review_period);
            data.put("user_id", userInfo.getKey());

            db.collection("task")
                    .document(task_key)
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            String taskID = task_key;

                            GlobalClass global = GlobalClass.getInstance();
                            LoggedInUserView userInfo = global.getUserInfo();

                            Map<String, Object> data = new HashMap<>();
                            data.put("title", title);
                            data.put("description", description);
                            data.put("submission_period", submission_period);
                            data.put("review_period", review_period);

                            db.collection("users")
                                    .document(userInfo.getKey())
                                    .collection("tasks")
                                    .document(taskID)
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(getApplicationContext(), AssignmentsActivity.class);
                                            intent.putExtra("TASK_KEY", taskID);
                                            intent.putExtra("REVIEW_PERIOD", review_period);
                                            intent.putExtra("SUBMISSION_PERIOD", submission_period);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });


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
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        boolean isJoined = true;
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            edt_submissioin_period.setText((String) document.getData().get("submission_period"));
                            edt_review_period.setText((String) document.getData().get("review_period"));
                            tv_task_title.setText((String) document.getData().get("title"));
                            tv_task_description.setText((String) document.getData().get("description"));
                        }
                    }
                });
    }

    public void onDateSet(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        if (year == c.get(Calendar.YEAR) && month == c.get(Calendar.MONTH) &&
                day == c.get(Calendar.DAY_OF_MONTH)) {
            if (picker_role == 1) {
                edt_submissioin_period.setText(year + "." + (month + 1) + "." + day);
            } else if (picker_role == 2) {
                edt_review_period.setText(year + "." + (month + 1) + "." + day);
            }
        } else {
            if (picker_role == 1) {
                edt_submissioin_period.setText(year + "." + (month + 1) + "." + day);
            } else if (picker_role == 2) {
                edt_review_period.setText(year + "." + (month + 1) + "." + day);
            }
        }
    }

    private void showDatePickerDialog() {
        if (fragment == null) {
            fragment = new DatePickerFragment(1);
        }
        fragment.show(getSupportFragmentManager(), "datePicker");
    }
}
