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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;

public class StudentPostActivity extends AppCompatActivity {

    private String TAG = "--- Ares ---";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListsMainDemoAdapter adapter;
    private String taskKey;
    private View formView;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_tasklist);
        taskKey = getIntent().getStringExtra("TASK_KEY");
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
        adapter.setType(6);
        showProgress(true);
        //**** FIREBASE LOGIN ****//
        // Only Joined Assignment list from student collection.
        GlobalClass global = GlobalClass.getInstance();
        LoggedInUserView userInfo = global.getUserInfo();
        final String selfkey = userInfo.getKey();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("task")
                .document(taskKey)
                .collection("submissions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                final String submission_key = document.getId();
                                final String title = (String) document.getData().get("title");
                                final String description = (String) document.getData().get("description");
                                final String review_period = (String) document.getData().get("review_period");
                                db.collection("task")
                                        .document(taskKey)
                                        .collection("submissions")
                                        .document(submission_key)
                                        .collection("joined_students")
                                        .document(selfkey)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    adapter.addAssignmentList(taskKey, submission_key, title, description, review_period);
                                                    mRecyclerView.setAdapter(adapter);

                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        showProgress(false);
                    }
                });

        // ************************//

    }

}