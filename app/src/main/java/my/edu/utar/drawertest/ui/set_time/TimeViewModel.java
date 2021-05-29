package my.edu.utar.drawertest.ui.set_time;

import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private TextView subDate, revDate;



    public TimeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Edit Your Assignment Timeline");
    }




    public LiveData<String> getText() {
        return mText;
    }
}