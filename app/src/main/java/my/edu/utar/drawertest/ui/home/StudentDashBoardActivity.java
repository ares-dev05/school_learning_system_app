package my.edu.utar.drawertest.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;

public class StudentDashBoardActivity extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListsMainDemoAdapter adapter;
    private String TAG = "--- Ares --- TeacherTasksActivity ---";
    private TextView tv_avatar_name;
    private TextView all_tasks_tv;
    private TextView progress_tasks_tv;
    private TextView expired_tasks_tv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_dashboard, container, false);
        tv_avatar_name = (TextView) root.findViewById(R.id.avatar_name);
        all_tasks_tv = (TextView) root.findViewById(R.id.all_tasks);
        progress_tasks_tv = (TextView) root.findViewById(R.id.progress_tasks);
        expired_tasks_tv = (TextView) root.findViewById(R.id.expired_tasks);

        onInitialize();
        return root;
    }

    public void onInitialize() {
        //**** FIREBASE LOGIN ****//
        // Must display the all the task.
        GlobalClass global = GlobalClass.getInstance();
        LoggedInUserView userInfo = global.getUserInfo();
        tv_avatar_name.setText("Hello " + userInfo.getDisplayName());

        //**** FIREBASE LOGIN ****//
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userInfo.getKey())
                .collection("joined_tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int allCount = 0, ongoingCount  = 0, finishedCount = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                allCount++;

                                Date today = new Date();
                                try {
                                    String time = (String)document.getData().get("submission_period");
                                    Date submission_period = new SimpleDateFormat("yyyy.MM.dd").parse(time);
                                    if(today.compareTo(submission_period) > 0) {
                                        finishedCount++;
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            all_tasks_tv.setText(String.valueOf(allCount));
                            progress_tasks_tv.setText(String.valueOf(allCount - finishedCount));
                            expired_tasks_tv.setText(String.valueOf(finishedCount));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }


}