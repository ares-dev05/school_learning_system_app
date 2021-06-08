package my.edu.utar.drawertest.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.github.kobakei.materialfabspeeddial.FabSpeedDial;
import my.edu.utar.drawertest.MainActivity;
import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.RegisterActivity;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;
import my.edu.utar.drawertest.ui.list.DynamicListAdapter;

public class AssignmentsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;

    private DynamicListAdapter mDynamicListAdapter;
    private ListsMainDemoAdapter adapter;
    private String taskID;
    private String review_period;
    private String submission_period;
    private String TAG = "--- Ares ---";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        taskID = getIntent().getStringExtra("TASK_KEY");
        review_period = getIntent().getStringExtra("REVIEW_PERIOD");
        submission_period = getIntent().getStringExtra("SUBMISSION_PERIOD");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_tasklist);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new ListsMainDemoAdapter(this);
        adapter.setType(1);
        mRecyclerView.setAdapter(adapter);
        final Context self = this;

        FabSpeedDial fab = findViewById(R.id.fab);
        fab.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton floatingActionButton, @Nullable @org.jetbrains.annotations.Nullable TextView textView, int i) {
                if(i == R.id.addButton) {
                    showChangeLangDialog(self);
                } else if(i == R.id.post) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), "Successfully registered.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showChangeLangDialog(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);

        final TextInputLayout edt_title = (TextInputLayout) dialogView.findViewById(R.id.title_dlg);
        final TextInputLayout edt_description = (TextInputLayout) dialogView.findViewById(R.id.desciption_dlg);


        dialogBuilder.setTitle("Assignment dialog");
        dialogBuilder.setMessage("Please input the data below");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = edt_title.getEditText().getText().toString();
                String description = edt_description.getEditText().getText().toString();

                //**** FIREBASE LOGIN ****//
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> data = new HashMap<>();
                data.put("title", title);
                data.put("description", description);
                data.put("review_period", review_period);
                data.put("submission_period", submission_period);

                db.collection("task")
                        .document(taskID)
                        .collection("submissions")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                String submissioKey = documentReference.getId();
                                adapter.addList(submissioKey, title, description);
                                mRecyclerView.setAdapter(adapter);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(getApplicationContext(), "Unkown Error!!! " + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                // ************************//

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


}