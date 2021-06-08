package my.edu.utar.drawertest.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;

public class StudentsSubmissionActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListsMainDemoAdapter adapter;
    private String TAG = "--- Ares ---";
    private String taskID;
    private View formView;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_review);
        taskID = getIntent().getStringExtra("TASK_KEY");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_tasklist);
        formView = findViewById(R.id.main_layout);
        progressView = findViewById(R.id.register_progress);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new ListsMainDemoAdapter(this);

        onInitialize();
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

    public void onInitialize() {
        showProgress(true);
        adapter.setType(5);
        GlobalClass global = GlobalClass.getInstance();
        LoggedInUserView userInfo = global.getUserInfo();

        //**** FIREBASE LOGIN ****//
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("task")
                .document(taskID)
                .collection("submissions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                adapter.addAssignmentList(taskID, (String)document.getId(), (String)document.getData().get("title"), (String)document.getData().get("description")
                                        , (String)document.getData().get("review_period") + " ~ " + (String)document.getData().get("submission_period"));
                            }
                            mRecyclerView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        showProgress(false);
                    }
                });
    }


}