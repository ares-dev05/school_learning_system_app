package my.edu.utar.drawertest.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;
import my.edu.utar.drawertest.ui.list.DynamicListAdapter;
import my.edu.utar.drawertest.ui.list.ListObject;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;

public class StudentActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ArrayList<ListObject> mList = new ArrayList<ListObject>();

    private LinearLayoutManager mLayoutManager;

    private DynamicListAdapter mDynamicListAdapter;
    private ListsMainDemoAdapter adapter;
    private String detailContext;
    private TextView mDescription;
    private String TAG = "---- Ares ---- StudentActivity ----";
    private View formView;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_tasklist);
        mDescription = (TextView) findViewById(R.id.student_description);
        formView = findViewById(R.id.main_layout);
        progressView = findViewById(R.id.register_progress);

        onInitAcitivity();

        // Initialize the list
        mDynamicListAdapter = new DynamicListAdapter();
        mDynamicListAdapter.setFirstList(mList);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Context self = this;
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


    public void onInitAcitivity() {
        showProgress(true);
        String taskId = getIntent().getStringExtra("TASK_KEY");
        String assignmentIdId = getIntent().getStringExtra("SUBMISSION_KEY");
        String description = getIntent().getStringExtra("DESCRIPTION");
        GlobalClass global = GlobalClass.getInstance();
        LoggedInUserView userInfo = global.getUserInfo();
        String selfKey = "";
        if(userInfo != null) {
            selfKey = userInfo.getKey();
        }

        final String self_key = selfKey;

        adapter = new ListsMainDemoAdapter(this);
        adapter.setType(3);
        // ***********
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("task")
                .document(taskId)
                .collection("submissions")
                .document(assignmentIdId)
                .collection("joined_students")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //**** FIREBASE LOGIN ****//
                                if(!self_key.equals((String)document.getId())) {
                                    adapter.addStudentList(taskId, assignmentIdId, document.getId(),(String) document.getData().get("name"), (String)document.getData().get("email"));
                                }

                            }
                            mRecyclerView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        showProgress(false);
                    }
                });


        mDescription.setText(description);
    }

}