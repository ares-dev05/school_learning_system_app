package my.edu.utar.drawertest.ui.review;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import my.edu.utar.drawertest.MainActivity;
import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.common.LoadingFragment;
import my.edu.utar.drawertest.ui.dialog.ListsMainDemoAdapter;
import my.edu.utar.drawertest.ui.home.AssignmentsActivity;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;
import my.edu.utar.drawertest.ui.post.FilePath;

public class ReviewActivity extends AppCompatActivity {

    private ReviewViewModel mViewModel;

    public static ReviewActivity newInstance() {
        return new ReviewActivity();
    }

    public ExtendedFloatingActionButton submitBtn;
    public MaterialRatingBar punctuality_ratingBar;
    public MaterialRatingBar completeness_ratingBar;
    public MaterialRatingBar participation_ratingBar;
    public MaterialRatingBar av_punctuality_ratingBar;
    public MaterialRatingBar av_completeness_ratingBar;
    public MaterialRatingBar av_participation_ratingBar;
    public MaterialRatingBar overall_ratingBar;
    public TextView punctuality_tv;
    public TextView completeness_tv;
    public TextView participation_tv;
    private LinearLayout security_review;
    private LinearLayout av_layout;

    public TextInputLayout reviewText;
    public ExtendedFloatingActionButton detach_file_tv;
    public ExtendedFloatingActionButton detail_btn;

    public TextView description_tv;
    public TextView review_txt_static_tv;
    private String down_url;
    private View formView;
    private View progressView;
    private String TAG = "---- Ares ---- ReviewActivity -----";
    private static final int STORAGE_PERMISSION_CODE = 123;
    private int FILE_PICKER_REQUEST_CODE = 12;
    private int PICK_PDF_REQUEST = 1;
    private static final int CREATE_FILE = 2;
    private LoadingFragment loadingFragment = LoadingFragment.newInstance();
    private String download_extention;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListsMainDemoAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_fragment);

        onInitialize();
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
        formView = findViewById(R.id.register_form);
        progressView = findViewById(R.id.register_progress);
        showProgress(true);
        String student_key = getIntent().getStringExtra("STUDENT_KEY");
        String task_key = getIntent().getStringExtra("TASK_KEY");
        String submission_key = getIntent().getStringExtra("SUBMISSION_KEY");
        String result_attach_file_url = getIntent().getStringExtra("RESULT_ATTACH_FILE_URL");
        String result_description = getIntent().getStringExtra("RESULT_DESCRIPTION");

        GlobalClass global = GlobalClass.getInstance();
        LoggedInUserView userInfo = global.getUserInfo();
        String selfKey = userInfo.getKey();
        boolean role = userInfo.isTeacher();


        submitBtn = (ExtendedFloatingActionButton) findViewById(R.id.submit);
        punctuality_ratingBar = (MaterialRatingBar) findViewById(R.id.punctuality_rating);
        completeness_ratingBar = (MaterialRatingBar) findViewById(R.id.completeness_rating);
        participation_ratingBar = (MaterialRatingBar) findViewById(R.id.discussion_rating);
        av_punctuality_ratingBar = (MaterialRatingBar) findViewById(R.id.av_punctuality_rating);
        av_completeness_ratingBar = (MaterialRatingBar) findViewById(R.id.av_completeness_rating);
        av_participation_ratingBar = (MaterialRatingBar) findViewById(R.id.av_discussion_rating);
        overall_ratingBar = (MaterialRatingBar) findViewById(R.id.overall_rating);

        reviewText = (TextInputLayout) findViewById(R.id.review_txt);
        detach_file_tv = (ExtendedFloatingActionButton) findViewById(R.id.detach_file_url);
        detail_btn = (ExtendedFloatingActionButton) findViewById(R.id.detail_btn);
        description_tv = (TextView) findViewById(R.id.description);
        description_tv.setText(result_description);

        security_review = (LinearLayout)findViewById(R.id.security_review);
//        av_layout = (LinearLayout)findViewById(R.id.av_layout);

        punctuality_tv = (TextView) findViewById(R.id.punctuality_rating_label);
        completeness_tv = (TextView) findViewById(R.id.completeness_rating_label);
        participation_tv = (TextView) findViewById(R.id.discussion_rating_label);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_tasklist);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new ListsMainDemoAdapter(this);

        detail_btn.setOnClickListener(v -> {
            if(mRecyclerView.getVisibility() == View.GONE) {
                mRecyclerView.setVisibility(View.VISIBLE);
//                av_layout.setVisibility(View.VISIBLE);
                security_review.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                security_review.setVisibility(View.VISIBLE);
//                av_layout.setVisibility(View.VISIBLE);
            }
        });

        adapter.setType(8);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("task")
                .document(task_key)
                .collection("submissions")
                .document(submission_key)
                .collection("joined_students")
                .document(student_key)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        boolean isDone = true;
                        Object info = null;
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            info = document.getData();

                            if (info == null) {
                                isDone = false;
                            } else {
                                try {
                                    description_tv.setText((String) document.getData().get("post_result_description"));
                                    down_url = (String) document.getData().get("post_result_url");
                                    download_extention = (String) document.getData().get("post_data_extension");
                                } catch (Exception e) {

                                }

                            }
                        }

                        db.collection("users")
                                .document(student_key)
                                .collection("joined_tasks")
                                .document(task_key + submission_key)
                                .collection("reviews")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        boolean isDone = false;
                                        if (task.isSuccessful()) {
                                            float completeness_rating_all = 0;
                                            float participation_rating_all = 0;
                                            float punctuality_rating_all = 0;
                                            int all_count = 0;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());

                                                float completeness_rating = Float.parseFloat((String) document.getData().get("completeness_rating"));
                                                float participation_rating = Float.parseFloat((String) document.getData().get("participation_rating"));
                                                float punctuality_rating = Float.parseFloat((String) document.getData().get("punctuality_rating"));

                                                completeness_rating_all += completeness_rating;
                                                participation_rating_all += participation_rating;
                                                punctuality_rating_all += punctuality_rating;
                                                all_count++;

                                                if (document.getId().equals(selfKey)) {
                                                    isDone = true;
                                                    adapter.addReviewList(document.getId(), (String) document.getData().get("name"), String.valueOf(completeness_rating),
                                                            String.valueOf(participation_rating),
                                                            String.valueOf(punctuality_rating),
                                                            (String) document.getData().get("description"));
                                                } else if (role) {
                                                    adapter.addReviewList(document.getId(), (String) document.getData().get("name"), String.valueOf(completeness_rating),
                                                            String.valueOf(participation_rating),
                                                            String.valueOf(punctuality_rating),
                                                            (String) document.getData().get("description"));
                                                }
                                            }
                                            mRecyclerView.setAdapter(adapter);

                                            if (isDone) {
                                                punctuality_ratingBar.setVisibility(View.GONE);
                                                completeness_ratingBar.setVisibility(View.GONE);
                                                participation_ratingBar.setVisibility(View.GONE);
                                                punctuality_tv.setVisibility(View.GONE);
                                                completeness_tv.setVisibility(View.GONE);
                                                participation_tv.setVisibility(View.GONE);

                                                reviewText.setVisibility(View.GONE);
                                                submitBtn.setVisibility(View.GONE);

                                                if(role) {
                                                    av_punctuality_ratingBar.setEnabled(false);
                                                    av_completeness_ratingBar.setEnabled(false);
                                                    av_participation_ratingBar.setEnabled(false);
                                                    av_punctuality_ratingBar.setRating(completeness_rating_all / all_count);
                                                    av_completeness_ratingBar.setRating(participation_rating_all / all_count);
                                                    av_participation_ratingBar.setRating(punctuality_rating_all / all_count);
                                                    float av = (completeness_rating_all + participation_rating_all + punctuality_rating_all) / 3 / all_count;
                                                    overall_ratingBar.setRating(av);
                                                    detail_btn.setVisibility(View.VISIBLE);
                                                    security_review.setVisibility(View.VISIBLE);
                                                } else {
                                                    mRecyclerView.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }

                                        showProgress(false);
                                    }
                                });
                    }
                });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //**** FIREBASE LOGIN ****//
                loadingFragment.show(getSupportFragmentManager(), "Submiting the result...");
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> data = new HashMap<>();

                data.put("punctuality_rating", String.valueOf(punctuality_ratingBar.getRating()));
                data.put("completeness_rating", String.valueOf(completeness_ratingBar.getRating()));
                data.put("participation_rating", String.valueOf(participation_ratingBar.getRating()));

                data.put("description", reviewText.getEditText().getText().toString());

                db.collection("users")
                        .document(selfKey)
                        .collection("joined_tasks")
                        .document(task_key + submission_key)
                        .collection("send_reviews")
                        .document(student_key)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("name", userInfo.getDisplayName());
                                data.put("punctuality_rating", String.valueOf(punctuality_ratingBar.getRating()));
                                data.put("completeness_rating", String.valueOf(completeness_ratingBar.getRating()));
                                data.put("participation_rating", String.valueOf(participation_ratingBar.getRating()));
                                data.put("description", reviewText.getEditText().getText().toString());
                                db.collection("users")
                                        .document(student_key)
                                        .collection("joined_tasks")
                                        .document(task_key + submission_key)
                                        .collection("reviews")
                                        .document(selfKey)
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "The operation was successful.", Toast.LENGTH_SHORT).show();
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
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                                loadingFragment.dismiss();
                            }
                        });
                // ************************//
            }
        });

        detach_file_tv.setOnClickListener(v -> {
            requestStoragePermission();

            createFile(null);

        });
    }

    private void createFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        intent.putExtra(Intent.EXTRA_TITLE, "download." + download_extention);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, CREATE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK) {

        } else if (requestCode == CREATE_FILE && resultCode == RESULT_OK) {
            loadingFragment.show(getSupportFragmentManager(), "Downloading...");
            try {
                Uri filePath = data.getData();

                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageRef = storage.getReference();
                StorageReference imageRef = storageRef.child(down_url);

                final long ONE_MEGABYTE = 1024 * 1024 * 1024;
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        try {
                            ParcelFileDescriptor pfd = getContentResolver().
                                    openFileDescriptor(filePath, "w");
                            FileOutputStream fileOutputStream =
                                    new FileOutputStream(pfd.getFileDescriptor());
                            fileOutputStream.write(bytes);
                            // Let the document provider know you're done by closing the stream.
                            fileOutputStream.close();
                            pfd.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Permission Error", Toast.LENGTH_SHORT).show();
                            loadingFragment.dismiss();
                            return;
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Permission Error", Toast.LENGTH_SHORT).show();
                            loadingFragment.dismiss();
                            e.printStackTrace();
                            return;
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Permission Error", Toast.LENGTH_SHORT).show();
                            loadingFragment.dismiss();
                            e.printStackTrace();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Successfully downloaded.", Toast.LENGTH_SHORT).show();
                        loadingFragment.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        loadingFragment.dismiss();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                loadingFragment.dismiss();
            }


        }

    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can write the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}