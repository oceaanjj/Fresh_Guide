package com.example.freshguide.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshguide.R;

import java.util.List;

public class TimeWheelAdapter extends RecyclerView.Adapter<TimeWheelAdapter.ViewHolder> {

    private static final float TEXT_SIZE_SELECTED = 35f;  // centre item — prominent
    private static final float TEXT_SIZE_ADJACENT = 29f;  // one step away
    private static final float TEXT_SIZE_OUTER    = 25f;  // two or more steps away

    private static final float ALPHA_SELECTED       = 1.00f;
    private static final float ALPHA_ADJACENT       = 0.50f;
    private static final float ALPHA_OUTER          = 0.28f;

    private final List<String> items;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public TimeWheelAdapter(List<String> items) {
        this.items = items;
    }

    public void setSelectedPosition(int newPosition) {
        if (newPosition == selectedPosition) return;

        int oldPosition = selectedPosition;
        selectedPosition = newPosition;

        // Refresh a window of items around the old and new positions so the
        // size/alpha transition looks smooth without a full notifyDataSetChanged().
        int start = Math.max(0, Math.min(oldPosition, newPosition) - 2);
        int end   = Math.min(getItemCount() - 1,
                Math.max(oldPosition, newPosition) + 2);
        notifyItemRangeChanged(start, end - start + 1);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_wheel_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(items.get(position));

        int distance = (selectedPosition == RecyclerView.NO_POSITION)
                ? Integer.MAX_VALUE
                : Math.abs(position - selectedPosition);

        boolean selected = distance == 0;

        // Text colour
        holder.textView.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(),
                selected ? R.color.time_picker_text_primary
                        : R.color.time_picker_text_secondary
        ));

        // Progressive size
        float textSize;
        float alpha;
        if (distance == 0) {
            textSize = TEXT_SIZE_SELECTED;
            alpha    = ALPHA_SELECTED;
        } else if (distance == 1) {
            textSize = TEXT_SIZE_ADJACENT;
            alpha    = ALPHA_ADJACENT;
        } else {
            textSize = TEXT_SIZE_OUTER;
            alpha    = ALPHA_OUTER;
        }

        holder.textView.setTextSize(textSize);
        holder.textView.setAlpha(alpha);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_wheel_text);
        }
    }
}