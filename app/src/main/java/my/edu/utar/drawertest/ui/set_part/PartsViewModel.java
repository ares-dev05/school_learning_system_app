package my.edu.utar.drawertest.ui.set_part;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PartsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PartsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Task Distribution");
    }

    public LiveData<String> getText() {
        return mText;
    }
}