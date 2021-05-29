package my.edu.utar.drawertest.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import io.github.kobakei.materialfabspeeddial.FabSpeedDial;
import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;
import my.edu.utar.drawertest.ui.list.DynamicListAdapter;
import my.edu.utar.drawertest.ui.list.ListObject;
import my.edu.utar.drawertest.ui.list.Utilities;

public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<ListObject> mList = new ArrayList<ListObject>();

    private LinearLayoutManager mLayoutManager;

    private DynamicListAdapter mDynamicListAdapter;
    private ListsMainDemoAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.my_tasklist);
        mList = Utilities.populateFirstList();

        // Initialize the list
        mDynamicListAdapter = new DynamicListAdapter();
        mDynamicListAdapter.setFirstList(mList);

        mLayoutManager = new LinearLayoutManager(root.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new ListsMainDemoAdapter();
        adapter.setType(1);
        mRecyclerView.setAdapter(adapter);

        FabSpeedDial fab = root.findViewById(R.id.fab);
        fab.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton floatingActionButton, @Nullable @org.jetbrains.annotations.Nullable TextView textView, int i) {
                if(i == R.id.addButton) {
                    showChangeLangDialog(root.getContext());
                }
            }
        });

        return root;

    }

    public void showChangeLangDialog(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText edt_title = (EditText) dialogView.findViewById(R.id.title_dlg);
        final EditText edt_description = (EditText) dialogView.findViewById(R.id.desciption_dlg);


        dialogBuilder.setTitle("Assignment dialog");
        dialogBuilder.setMessage("Please input the data below");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = edt_title.getText().toString();
                String description = edt_description.getText().toString();
                adapter.addList(title, description);
                mRecyclerView.setAdapter(adapter);
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