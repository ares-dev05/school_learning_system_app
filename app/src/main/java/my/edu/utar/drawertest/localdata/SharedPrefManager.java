package my.edu.utar.drawertest.localdata;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    public static final String TAG = "SharedPrefManager";

    private static final String SHARED_PREF_NAME = "sharedPrefName";
    private static final String KEY_ACCESS_TOKEN = "token";

    private static Context mContext;
    private static SharedPrefManager mInstance;

    public SharedPrefManager(Context context) {
        this.mContext = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null)
            mInstance = new SharedPrefManager(context);
        return mInstance;
    }

    public boolean storeToken(String token) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
        return true;
    }

    public String getToken() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }
}
