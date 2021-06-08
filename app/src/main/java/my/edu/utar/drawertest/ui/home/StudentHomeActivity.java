package my.edu.utar.drawertest.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;

public class StudentHomeActivity extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListsMainDemoAdapter adapter;
    private String TAG = "--- Ares --- StudentHomeActivity ---";
    private View formView;
    private View progressView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.students_home, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.my_tasklist);
        formView = root.findViewById(R.id.main_layout);
        progressView = root.findViewById(R.id.register_progress);
        mLayoutManager = new LinearLayoutManager(root.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new ListsMainDemoAdapter(root.getContext());

        onInitialize();
        return root;
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
        adapter.setType(4);
        showProgress(true);
        //**** FIREBASE LOGIN ****//
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("task")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                adapter.addTasksList(document.getId(), (String)document.getData().get("title"),
                                        (String)document.getData().get("description"), (String)document.getData().get("submission_period"),
                                        (String)document.getData().get("review_period"));
                            }
                            mRecyclerView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        showProgress(false);
                    }
                });

        mRecyclerView.setAdapter(adapter);
    }


}