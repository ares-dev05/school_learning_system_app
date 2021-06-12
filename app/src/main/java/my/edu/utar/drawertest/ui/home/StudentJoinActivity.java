package my.edu.utar.drawertest.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import my.edu.utar.drawertest.MainActivity;
import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;
import my.edu.utar.drawertest.ui.list.DynamicListAdapter;
import my.edu.utar.drawertest.ui.list.ListObject;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;

public class StudentJoinActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<ListObject> mList = new ArrayList<ListObject>();

    private LinearLayoutManager mLayoutManager;

    private DynamicListAdapter mDynamicListAdapter;
    private ListsMainDemoAdapter adapter;
    private String detailContext;
    private TextView mDescription;
    private ExtendedFloatingActionButton btn_join;
    private String TAG = "--- ARES ----";
    private String submission_period;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_join);

        mDescription = (TextView) findViewById(R.id.submission_description);
        btn_join = (ExtendedFloatingActionButton) findViewById(R.id.join);
        onInitAcitivity();
    }

    public void onInitAcitivity() {
        String taskId = getIntent().getStringExtra("TASK_KEY");
        String assignmentId = getIntent().getStringExtra("SUBMISSION_KEY");
        String description = getIntent().getStringExtra("DESCRIPTION");
        GlobalClass global = GlobalClass.getInstance();
        LoggedInUserView userInfo = global.getUserInfo();
        String selfKey = userInfo.getKey();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("task")
                .document(taskId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        boolean isJoined = true;
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            submission_period = (String) document.getData().get("submission_period");
                        }
                    }
                });

        mDescription.setText(description);
        btn_join.setOnClickListener(v -> {
            // **** FIRE BASE **** //
            Map<String, Object> joinInfo = new HashMap<>();
            joinInfo.put("name", userInfo.getDisplayName());
            joinInfo.put("email", userInfo.getEmail());

            // Add a new document with a generated ID
            db.collection("task")
                    .document(taskId)
                    .collection("submissions")
                    .document(assignmentId)
                    .collection("joined_students")
                    .document(selfKey)
                    .set(joinInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> data = new HashMap<>();
                            data.put("submission_period", submission_period);

                            db.collection("users")
                                    .document(selfKey)
                                    .collection("joined_tasks")
                                    .document(taskId + assignmentId)
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Successfully joined.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
                                            intent.putExtra("TASK_KEY", taskId);
                                            intent.putExtra("SUBMISSION_KEY", assignmentId);
                                            intent.putExtra("DESCRIPTION", description);
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
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
            // ******************* //



        });
    }

}