package my.edu.utar.drawertest.ui.set_time;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import my.edu.utar.drawertest.R;

public class TimeFragment extends Fragment {

    private TimeViewModel timeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        timeViewModel =
                new ViewModelProvider(this).get(TimeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timeline, container, false);
        final TextView textView = root.findViewById(R.id.textView12);

        timeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}