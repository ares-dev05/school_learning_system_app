package my.edu.utar.drawertest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import my.edu.utar.drawertest.ui.DatePickerFragment;

public class CreateActivity extends AppCompatActivity {

    public Button btn_create;
    public TextView edt_submissioin_period;
    public TextView edt_review_period;
    private DialogFragment fragment;
    private int picker_role = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        btn_create = (Button) findViewById(R.id.btn_create1);
        btn_create.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        edt_submissioin_period = (TextView) findViewById(R.id.submissioin_period);
        edt_submissioin_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                picker_role = 1;
            }
        });

        edt_review_period = (TextView) findViewById(R.id.review_period);
        edt_review_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                picker_role = 2;
            }
        });

    }

    public void onDateSet(int year, int month, int day){
        Calendar c = Calendar.getInstance();
        if(year == c.get(Calendar.YEAR) && month == c.get(Calendar.MONTH) &&
                day == c.get(Calendar.DAY_OF_MONTH)) {
            if(picker_role == 1) {
                edt_submissioin_period.setText(year + "." + (month + 1) + "." + day);
            } else if(picker_role == 2) {
                edt_review_period.setText(year + "." + (month + 1) + "." + day);
            }
        }
        else {
            if(picker_role == 1) {
                edt_submissioin_period.setText(year + "." + (month + 1) + "." + day);
            } else if(picker_role == 2) {
                edt_review_period.setText(year + "." + (month + 1) + "." + day);
            }
        }
    }

    private void showDatePickerDialog(){
        if(fragment == null) {
            fragment = new DatePickerFragment();
        }
        fragment.show(getSupportFragmentManager(), "datePicker");
    }
}