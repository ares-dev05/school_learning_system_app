package my.edu.utar.drawertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.DatePickerFragment;
import my.edu.utar.drawertest.ui.home.AssignmentsActivity;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;

public class CreateActivity extends AppCompatActivity {

    public Button btn_create;
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
        setContentView(R.layout.activity_create);

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

        btn_create = (Button) findViewById(R.id.btn_create_task);
        btn_create.setOnClickListener(view -> {
            String title = tv_task_title.getText().toString();
            String description = tv_task_description.getText().toString();
            String submission_period = edt_submissioin_period.getText().toString();
            String review_period = edt_review_period.getText().toString();

            if(title.equals("")) {
                Toast.makeText(getApplicationContext(), "Please input the title field.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(description.equals("")) {
                Toast.makeText(getApplicationContext(), "Please input the description field.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(submission_period.equals("")) {
                Toast.makeText(getApplicationContext(), "Please input the submission period field.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(review_period.equals("")) {
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
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            String taskID = documentReference.getId();

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
    }

    public void onDateSet(int year, int month, int day){
        Calendar c = Calendar.getInstance();
        if(year == c.get(Calendar.YEAR) && month == c.get(Calendar.MONTH) &&
                day == c.get(Calendar.DAY_OF_MONTH)) {
            if(picker_role == 1) {
                edt_submissioin_period.setText(year + "." + (month + 1) + "." + day);
            } else if(picker_role == 2) {
                edt_review_period.setText(year + "." + (month + 1) + "." + day);
            }
        }
        else {
            if(picker_role == 1) {
                edt_submissioin_period.setText(year + "." + (month + 1) + "." + day);
            } else if(picker_role == 2) {
                edt_review_period.setText(year + "." + (month + 1) + "." + day);
            }
        }
    }

    private void showDatePickerDialog(){
        if(fragment == null) {
            fragment = new DatePickerFragment();
        }
        fragment.show(getSupportFragmentManager(), "datePicker");
    }
}
