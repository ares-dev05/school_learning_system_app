package my.edu.utar.drawertest.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.data.GlobalClass;
import my.edu.utar.drawertest.ui.home.AssignmentsActivity;
import my.edu.utar.drawertest.ui.home.StudentActivity;
import my.edu.utar.drawertest.ui.home.StudentJoinActivity;
import my.edu.utar.drawertest.ui.home.StudentPostActivity;
import my.edu.utar.drawertest.ui.home.StudentsReviewActivity;
import my.edu.utar.drawertest.ui.home.StudentsSubmissionActivity;
import my.edu.utar.drawertest.ui.list.ListObject;
import my.edu.utar.drawertest.ui.list.StudentsListObject;
import my.edu.utar.drawertest.ui.list.AssignmentsListObject;
import my.edu.utar.drawertest.ui.list.TasksListObject;
import my.edu.utar.drawertest.ui.login.LoggedInUserView;
import my.edu.utar.drawertest.ui.post.PostFormActivity;
import my.edu.utar.drawertest.ui.home.TeacherReviewActivity;
import my.edu.utar.drawertest.ui.review.ReviewActivity;

public class ListsMainDemoAdapter extends RecyclerView.Adapter<SingleLineItemViewHolder> {

    static final int ITEM_SINGLE_LINE = 0;
    static final int ITEM_TWO_LINE = 1;
    static final int ITEM_ASSIGNMENTS_TYPE = 2;
    static final int ITEM_STUDENTS_LINE = 3;
    static final int ITEM_ALL_TASK_LINE = 4;
    static final int ITEM_STUDENT_ASSIGNMENTS_TYPE = 5;
    static final int ITEM_STUDENT_POST_TYPE = 6;
    static final int ITEM_POST_TASK_LINE = 7;
    private ArrayList<AssignmentsListObject> mAssignmentList = new ArrayList<AssignmentsListObject>();
    private ArrayList<ListObject> mTwoList = new ArrayList<ListObject>();
    private ArrayList<StudentsListObject> mStudentsList = new ArrayList<StudentsListObject>();
    private ArrayList<TasksListObject> mTasksList = new ArrayList<TasksListObject>();

    private int mType = 0;
    private Context mContext;
    private String TAG = "---- ListsMainDemoAdapter ----";

    public ListsMainDemoAdapter(Context context) {
        mContext = context;
    }

    public void addAssignmentList(String taskKey, String key, String title, String description, String deadline) {
        AssignmentsListObject object = new AssignmentsListObject(taskKey, key, description , title, deadline);
        mAssignmentList.add(object);
    }

    public void setAssignmentList(ArrayList<AssignmentsListObject> object) {
        mAssignmentList = object;
    }

    public ArrayList<AssignmentsListObject> getThreeList() {
        return mAssignmentList;
    }

    public void addList(String key, String title, String description) {
        ListObject object = new ListObject(key, title, description);
        mTwoList.add(object);
    }

    public void setList(ArrayList<ListObject> object) {
        mTwoList = object;
    }

    public ArrayList<ListObject> getList() {
        return mTwoList;
    }

    public void addStudentList(String taskKey, String submissionKey, String key, String name, String postData) {
        StudentsListObject object = new StudentsListObject(taskKey, submissionKey, key, name, postData);
        mStudentsList.add(object);
    }

    public void setStudentsList(ArrayList<StudentsListObject> object) {
        mStudentsList = object;
    }

    public ArrayList<StudentsListObject> getStudentsList() {
        return mStudentsList;
    }

    public void addTasksList(String key, String title, String description, String sub_time, String review_time) {
        TasksListObject object = new TasksListObject(key, title, description, sub_time, review_time);
        mTasksList.add(object);
    }

    public void setTasksList(ArrayList<TasksListObject> object) {
        mTasksList = object;
    }

    public ArrayList<TasksListObject> getTasksList() {
        return mTasksList;
    }

    public void setType(int type) {
        mType = type;
    }

    @NonNull
    @Override
    public SingleLineItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        position = mType;
        switch (getItemViewType(position)) {
            case ITEM_SINGLE_LINE:
                return SingleLineItemViewHolder.create(parent);
            case ITEM_TWO_LINE:
            case ITEM_STUDENTS_LINE:
                return TwoLineItemViewHolder.create(parent);
            case ITEM_ALL_TASK_LINE:
            case ITEM_POST_TASK_LINE:
            case ITEM_ASSIGNMENTS_TYPE:
            case ITEM_STUDENT_ASSIGNMENTS_TYPE:
            case ITEM_STUDENT_POST_TYPE:
                return ThreeLineItemViewHolder.create(parent);

            default: // fall out
        }
        throw new RuntimeException();
    }

    @Override
    public void onBindViewHolder(@NonNull SingleLineItemViewHolder viewHolder, int position) {
        switch (getItemViewType(mType)) {
            case ITEM_SINGLE_LINE:
                bind((SingleLineItemViewHolder) viewHolder);
                break;
            case ITEM_TWO_LINE:
                bindHolder((TwoLineItemViewHolder) viewHolder, position);
                break;
            case ITEM_STUDENTS_LINE:
                bindStudentsHolder((TwoLineItemViewHolder) viewHolder, position);
                break;
            case ITEM_ALL_TASK_LINE:
            case ITEM_POST_TASK_LINE:
                bindTasksHolder((ThreeLineItemViewHolder) viewHolder, position);
                break;
            case ITEM_ASSIGNMENTS_TYPE:
            case ITEM_STUDENT_POST_TYPE:
            case ITEM_STUDENT_ASSIGNMENTS_TYPE:
                bindAssigmentHolder((ThreeLineItemViewHolder) viewHolder, position);
                break;
            default: // fall out
        }

        viewHolder.itemView.setOnClickListener(
                v -> {
                    if(mType == ITEM_SINGLE_LINE) {

                    } else if(mType == ITEM_ASSIGNMENTS_TYPE) {


                        AssignmentsListObject selectedItem = mAssignmentList.get(position);
                        String taskkey = selectedItem.getTaskKey();
                        String key = selectedItem.getKey();

                        // ******** FIRE BASE LOGIC **********//
                        // Determine whether you are join or not.


                        Intent intent = new Intent(mContext, StudentActivity.class);
                        intent.putExtra("TASK_KEY", taskkey);
                        intent.putExtra("SUBMISSION_KEY", key);
                        intent.putExtra("DESCRIPTION", selectedItem.getDescription());
                        mContext.startActivity(intent);

                    } else if(mType == ITEM_STUDENTS_LINE) {
                        StudentsListObject selectedItem = mStudentsList.get(position);
                        String key = selectedItem.getKey();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("task")
                                .document(selectedItem.getTaskKey())
                                .collection("submissions")
                                .document(selectedItem.getSubmissionKey())
                                .collection("joined_students")
                                .document(key)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        boolean isDone = true;
                                        Object info = null;
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            info = document.getData();
                                            String url = (String)document.getData().get("post_result_url");
                                            if (info == null || url == null) {
                                                isDone = false;
                                            } else {
                                                Intent intent = new Intent(mContext, ReviewActivity.class);
                                                intent.putExtra("STUDENT_KEY", key);
                                                intent.putExtra("TASK_KEY", selectedItem.getTaskKey());
                                                intent.putExtra("SUBMISSION_KEY", selectedItem.getSubmissionKey());
                                                intent.putExtra("RESULT_ATTACH_FILE_URL", (String) document.getData().get("post_result_url"));
                                                intent.putExtra("RESULT_DESCRIPTION", (String) document.getData().get("post_result_description"));
                                                intent.putExtra("STUDENT_KEY", key);
                                                mContext.startActivity(intent);
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                            isDone = false;
                                        }
                                        if (!isDone) {
                                            Toast.makeText(v.getContext(), "This student haven't posted the result.", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }
                                });
                    } else if(mType == ITEM_POST_TASK_LINE) {
                        Intent intent = new Intent(mContext, StudentPostActivity.class);
                        TasksListObject selectedItem = mTasksList.get(position);
                        String key = selectedItem.getKey();
                        intent.putExtra("TASK_KEY", key);
                        mContext.startActivity(intent);
                    } else if(mType == ITEM_ALL_TASK_LINE) {
                        GlobalClass global = GlobalClass.getInstance();
                        LoggedInUserView userInfo = global.getUserInfo();
                        if(userInfo.isTeacher()) {
                            Intent intent = new Intent(mContext, TeacherReviewActivity.class);
                            TasksListObject selectedItem = mTasksList.get(position);
                            String key = selectedItem.getKey();
                            intent.putExtra("TASK_KEY", key);
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, StudentsSubmissionActivity.class);
                            TasksListObject selectedItem = mTasksList.get(position);
                            String key = selectedItem.getKey();
                            intent.putExtra("TASK_KEY", key);
                            mContext.startActivity(intent);
                        }
                    } else if(mType == ITEM_STUDENT_ASSIGNMENTS_TYPE) {
                        AssignmentsListObject selectedItem = mAssignmentList.get(position);
                        String taskkey = selectedItem.getTaskKey();
                        String key = selectedItem.getKey();
                        GlobalClass global = GlobalClass.getInstance();
                        LoggedInUserView userInfo = global.getUserInfo();
                        String selfKey = "";
                        if(userInfo != null) {
                            selfKey = userInfo.getKey();
                        }

                        // ******** FIRE BASE LOGIC **********//
                        // Determine whether you are join or not.
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("task")
                                .document(taskkey)
                                .collection("submissions")
                                .document(key)
                                .collection("joined_students")
                                .document(selfKey)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        boolean isJoined = true;
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            Object info = document.getData();
                                            if(info == null) {
                                                isJoined = false;
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                            isJoined = false;
                                        }

                                        if(isJoined) {
                                            Intent intent = new Intent(mContext, StudentActivity.class);
                                            intent.putExtra("TASK_KEY", taskkey);
                                            intent.putExtra("SUBMISSION_KEY", key);
                                            intent.putExtra("DESCRIPTION", selectedItem.getDescription());
                                            mContext.startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(mContext, StudentJoinActivity.class);
                                            intent.putExtra("TASK_KEY", taskkey);
                                            intent.putExtra("SUBMISSION_KEY", key);
                                            intent.putExtra("DESCRIPTION", selectedItem.getDescription());
                                            mContext.startActivity(intent);
                                        }
                                    }
                                });
                    } else if(mType == ITEM_STUDENT_POST_TYPE) {
                        AssignmentsListObject selectedItem = mAssignmentList.get(position);
                        String taskkey = selectedItem.getTaskKey();
                        String key = selectedItem.getKey();

                        // ******** FIRE BASE LOGIC **********//
                        // Determine whether you are join or not.

                        Intent intent = new Intent(mContext, PostFormActivity.class);
                        intent.putExtra("TASK_KEY", taskkey);
                        intent.putExtra("SUBMISSION_KEY", key);
                        intent.putExtra("DESCRIPTION", selectedItem.getDescription());
                        mContext.startActivity(intent);
                    }
        });
    }

    private void bind(SingleLineItemViewHolder vh) {
        vh.text.setText("R.string.mtrl_list_item_one_line");
//            vh.icon.setImageResource(R.drawable.logo_avatar_anonymous_40dp);
    }

    public void bindHolder(TwoLineItemViewHolder holder, int position)
    {
        final String description = mTwoList.get(position).getDescription();
        final String title = mTwoList.get(position).getTitle();

        holder.tv_title.setText(description);
        holder.tv_description.setText(title);
        holder.icon.setImageResource(R.drawable.baseline_subject_24);
    }

    public void bindStudentsHolder(TwoLineItemViewHolder holder, int position)
    {
        final String name = mStudentsList.get(position).getName();
        final String postData = mStudentsList.get(position).getPostData();

        holder.tv_title.setText(name);
        holder.tv_description.setText(postData);
        holder.icon.setImageResource(R.drawable.baseline_person_24);

    }

    public void bindTasksHolder(ThreeLineItemViewHolder holder, int position) {
        final String title = mTasksList.get(position).getTitle();
        final String description = mTasksList.get(position).getDescription();
        final String sub_time = mTasksList.get(position).getSubmissionDeadline();
        final String review_time = mTasksList.get(position).getReviewDeadline();

        holder.tv_title.setText(title);
        holder.tv_description.setText(description);
        holder.tv_deadline.setText(sub_time);
        holder.icon.setImageResource(R.drawable.baseline_subject_24);

    }

    public void bindAssigmentHolder(ThreeLineItemViewHolder holder, int position) {
        final String title = mAssignmentList.get(position).getTitle();
        final String description = mAssignmentList.get(position).getDescription();
        final String time = mAssignmentList.get(position).getDeadline();

        holder.tv_title.setText(title);
        holder.tv_description.setText(description);
        holder.tv_deadline.setText(time);
        holder.icon.setImageResource(R.drawable.baseline_subject_24);

    }


    public void bindViewHolder(ThreeLineItemViewHolder holder, int position)
    {
        final String description = mAssignmentList.get(position).getDescription();
        final String title = mAssignmentList.get(position).getTitle();
        final String deadline = mAssignmentList.get(position).getDeadline();

        holder.tv_title.setText(title);
        holder.tv_description.setText(description);
        holder.tv_deadline.setText(deadline);
        holder.icon.setImageResource(R.drawable.baseline_subject_24);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 8;
    }

    @Override
    public int getItemCount() {
        switch (mType) {
            case 2:
            case 5:
            case 6:
                return mAssignmentList.toArray().length;
            case 3:
                return mStudentsList.toArray().length;
            case 4:
            case 7:
                return mTasksList.toArray().length;

            default:
                return mTwoList.toArray().length;
        }

    }
}
