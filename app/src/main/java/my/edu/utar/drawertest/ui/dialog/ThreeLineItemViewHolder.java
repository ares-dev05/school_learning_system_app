package my.edu.utar.drawertest.ui.dialog;

import my.edu.utar.drawertest.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

/** A simple three line list item. */
public class ThreeLineItemViewHolder extends TwoLineItemViewHolder {

    public final TextView tv_title;
    public final TextView tv_description;
    public final TextView tv_deadline;

    public ThreeLineItemViewHolder(@NonNull View view) {
        super(view);
        this.tv_title = itemView.findViewById(R.id.mtrl_list_item_title);
        this.tv_description = itemView.findViewById(R.id.mtrl_list_item_description);
        this.tv_deadline = itemView.findViewById(R.id.mtrl_list_item_deadline);
    }

    @NonNull
    public static ThreeLineItemViewHolder create(@NonNull ViewGroup parent) {
        return new ThreeLineItemViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.material_list_item_three_line, parent, false));
    }


}
