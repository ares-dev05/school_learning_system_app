package my.edu.utar.drawertest.ui.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import my.edu.utar.drawertest.R;

/** A simple three line list item. */
public class ReviewLineItemViewHolder extends TwoLineItemViewHolder {

    public final TextView tv_name;
    public final TextView tv_description;
    public final MaterialRatingBar ratingBar;

    public ReviewLineItemViewHolder(@NonNull View view) {
        super(view);
        this.tv_name = itemView.findViewById(R.id.mtrl_list_item_title);
        this.tv_description = itemView.findViewById(R.id.mtrl_list_item_description);
        this.ratingBar = itemView.findViewById(R.id.rating);
    }

    @NonNull
    public static ReviewLineItemViewHolder create(@NonNull ViewGroup parent) {
        return new ReviewLineItemViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.material_list_item_rating_line, parent, false));
    }


}
