package my.edu.utar.drawertest.ui.dialog;

import my.edu.utar.drawertest.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

/** A simple two line list item. */
public class TwoLineItemViewHolder extends SingleLineItemViewHolder {

    public final TextView tv_title;
    public final TextView tv_description;

    public TwoLineItemViewHolder(@NonNull View view) {
        super(view);
        this.tv_title = itemView.findViewById(R.id.mtrl_list_item_title);
        this.tv_description = itemView.findViewById(R.id.mtrl_list_item_description);
    }

    @NonNull
    public static TwoLineItemViewHolder create(@NonNull ViewGroup parent) {
        return new TwoLineItemViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.material_list_item_two_line, parent, false));
    }
}
