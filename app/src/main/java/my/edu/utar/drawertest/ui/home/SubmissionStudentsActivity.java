package my.edu.utar.drawertest.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;

public class SubmissionStudentsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListsMainDemoAdapter adapter;
    private View formView;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.students_submission);

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
        adapter.setType(5);
        //**** FIREBASE LOGIN ****//
        String taskkey = getIntent().getStringExtra("TASK_KEY");

        // ************************//
        for(int i = 0; i < 10; i++) {
            adapter.addAssignmentList(taskkey, taskkey + i, "submission" + i, "So how about doing this?\n" +
                    "After the first project is finished, I will be video certified, and then I will help you buy a computer with the income from the second project.", "2021.5.1 - 2021.5.7");
        }

        mRecyclerView.setAdapter(adapter);
    }


}