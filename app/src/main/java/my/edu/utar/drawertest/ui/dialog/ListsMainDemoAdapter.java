package my.edu.utar.drawertest.ui.dialog;

import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import my.edu.utar.drawertest.R;
import my.edu.utar.drawertest.ui.list.ListObject;
import my.edu.utar.drawertest.ui.list.TaskListObject;

public class ListsMainDemoAdapter extends RecyclerView.Adapter<SingleLineItemViewHolder> {

    static final int ITEM_SINGLE_LINE = 0;
    static final int ITEM_TWO_LINE = 1;
    static final int ITEM_THREE_LINE = 2;
    private ArrayList<TaskListObject> mThreeList = new ArrayList<TaskListObject>();
    private ArrayList<ListObject> mTwoList = new ArrayList<ListObject>();
    private int mType = 0;

    public void addThreeList(String title, String description, String deadline) {
        TaskListObject object = new TaskListObject(title, description, deadline);
        mThreeList.add(object);
    }

    public void setThreeList(ArrayList<TaskListObject> object) {
        mThreeList = object;
    }

    public ArrayList<TaskListObject> getThreeList() {
        return mThreeList;
    }

    public void addList(String title, String description) {
        ListObject object = new ListObject(title, description);
        mTwoList.add(object);
    }

    public void setList(ArrayList<ListObject> object) {
        mTwoList = object;
    }

    public ArrayList<ListObject> getList() {
        return mTwoList;
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
                return TwoLineItemViewHolder.create(parent);
            case ITEM_THREE_LINE:
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
            case ITEM_THREE_LINE:
                bindViewHolder((ThreeLineItemViewHolder) viewHolder, position);
                break;
            default: // fall out
        }

        viewHolder.itemView.setOnClickListener(
                v ->
                        Toast.makeText(v.getContext(), position + "th item is selected.", Toast.LENGTH_SHORT)
                                .show());
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
        holder.icon.setImageResource(R.drawable.parts);
    }

    public void bindViewHolder(ThreeLineItemViewHolder holder, int position)
    {
        final String description = mThreeList.get(position).getDescription();
        final String title = mThreeList.get(position).getTitle();
        final String deadline = mThreeList.get(position).getDeadline();

        holder.tv_title.setText(title);
        holder.tv_description.setText(description);
        holder.tv_deadline.setText(deadline);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @Override
    public int getItemCount() {
        switch (mType) {
            case 2:
                return mThreeList.toArray().length;
            default:
                return mTwoList.toArray().length;
        }

    }
}
