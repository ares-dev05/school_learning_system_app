package my.edu.utar.drawertest.ui.post;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import my.edu.utar.drawertest.MainActivity;
import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.common.LoadingFragment;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;
import my.edu.utar.drawertest.ui.home.AssignmentsActivity;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.w3c.dom.Text;

public class PostFormActivity extends AppCompatActivity {

    private ReviewViewModel mViewModel;

    public static PostFormActivity newInstance() {
        return new PostFormActivity();
    }

    public ExtendedFloatingActionButton submitBtn;
    public MaterialRatingBar ratingBar;

    public TextInputLayout result_description;
    public TextView tv_description;
    private TextView tv_uploadpath;
    public Button btn_upload;
    private int FILE_PICKER_REQUEST_CODE = 123;
    private byte[] upload_data;
    private String description;
    private String task_key;
    private String submission_key;
    private String selfkey;
    private String TAG = "---- PostFormActivity ----";
    private LoadingFragment loadingFragment = LoadingFragment.newInstance();
    private static final int STORAGE_PERMISSION_CODE = 123;
    private int PICK_PDF_REQUEST = 1;
    private String upload_extension;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListsMainDemoAdapter adapter;

    private View formView;
    private View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_form_activity);

        requestStoragePermission();
        onInitialize();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingFragment.show(getSupportFragmentManager(), "Submiting your result...");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference uploadfileRef = storageRef.child(task_key + "/" + submission_key + "/" + selfkey + "/uploadresult.pdf");
                UploadTask uploadTask = uploadfileRef.putBytes(upload_data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_SHORT).show();
                        loadingFragment.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        //**** FIREBASE LOGIN ****//
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        GlobalClass global = GlobalClass.getInstance();
                        LoggedInUserView userInfo = global.getUserInfo();
                        Map<String, Object> data = new HashMap<>();
                        data.put("post_result_description", result_description.getEditText().getText().toString());
                        data.put("post_result_url", task_key + "/" + submission_key + "/" + selfkey + "/uploadresult.pdf");
                        data.put("post_data_extension", upload_extension);
                        data.put("name", userInfo.getDisplayName());
                        data.put("email", userInfo.getEmail());

                        db.collection("task")
                                .document(task_key)
                                .collection("submissions")
                                .document(submission_key)
                                .collection("joined_students")
                                .document(selfkey)
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                        loadingFragment.dismiss();
                                    }
                                });
                    }
                });

                // ************************//
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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
        description = getIntent().getStringExtra("DESCRIPTION");
        task_key = getIntent().getStringExtra("TASK_KEY");
        submission_key = getIntent().getStringExtra("SUBMISSION_KEY");
        GlobalClass global = GlobalClass.getInstance();
        LoggedInUserView userInfo = global.getUserInfo();
        selfkey = userInfo.getKey();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_tasklist);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new ListsMainDemoAdapter(this);

        formView = findViewById(R.id.main_layout);
        progressView = findViewById(R.id.register_progress);

        submitBtn = (ExtendedFloatingActionButton) findViewById(R.id.post);
        tv_description = (TextView) findViewById(R.id.description);
        tv_uploadpath = (TextView) findViewById(R.id.upload_path);
        result_description = findViewById(R.id.result_description);
        tv_description.setText(description);
        btn_upload = (Button) findViewById(R.id.upload);
        btn_upload.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
        });

        adapter.setType(8);
        //**** FIREBASE LOGIN ****//
        showProgress(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(selfkey)
                .collection("joined_tasks")
                .document(task_key + submission_key)
                .collection("reviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                adapter.addReviewList(document.getId(), (String)document.getData().get("name"),  (String)document.getData().get("rating"),
                                        (String)document.getData().get("description"));
                            }
                            mRecyclerView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting rdocuments: ", task.getException());
                        }
                        showProgress(false);
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String selectedFilePath = FilePath.getPath(getApplicationContext(), uri);
            File file = new File(selectedFilePath);
            tv_uploadpath.setText(selectedFilePath);

            int size = (int) file.length();
            upload_data = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(upload_data, 0, upload_data.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            String path = null;
            try {
                path = FilePath.getPath(this, filePath);
            } catch (Exception e) {

            }
            if (path == null) {
                tv_uploadpath.setText(filePath.getPath());
            } else {
                tv_uploadpath.setText(path);
            }

            upload_extension = getfileExtension(filePath);

            try {
                ParcelFileDescriptor pfd = getContentResolver().
                        openFileDescriptor(filePath, "r");
                long fileSize = pfd.getStatSize();
                FileInputStream fileOutputStream =
                        new FileInputStream(pfd.getFileDescriptor());
                upload_data = new byte[(int) fileSize];
                fileOutputStream.read(upload_data, 0, (int) fileSize);
                // Let the document provider know you're done by closing the stream.
                fileOutputStream.close();
                pfd.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getfileExtension(Uri uri) {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }


}