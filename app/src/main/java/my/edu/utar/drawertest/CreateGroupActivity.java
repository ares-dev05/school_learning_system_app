package my.edu.utar.drawertest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CreateGroupActivity extends Fragment {

    public Button btn_join, btn_create;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_join, container, false);

        btn_join= (Button) root.findViewById(R.id.btn_join);
        btn_join.setOnClickListener(view -> {
            Intent intent = new Intent(root.getContext(), MainActivity.class);
            startActivity(intent);
        });

        btn_create= (Button) root.findViewById(R.id.btn_create);
        btn_create.setOnClickListener(view -> {
            Intent intent = new Intent(root.getContext(), CreateActivity.class);
            startActivity(intent);
        });

        return root;
    }
}