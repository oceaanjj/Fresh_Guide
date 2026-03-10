package com.example.freshguide.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshguide.R;
import com.example.freshguide.model.entity.RoomEntity;

public class RoomAdapter extends ListAdapter<RoomEntity, RoomAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(RoomEntity room);
    }

    private OnItemClickListener listener;

    public RoomAdapter() {
        super(DIFF);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        listener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomEntity room = getItem(position);
        holder.bind(room);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(room);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvCode;
        private final TextView tvType;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_room_name);
            tvCode = itemView.findViewById(R.id.tv_room_code);
            tvType = itemView.findViewById(R.id.tv_room_type);
        }

        void bind(RoomEntity room) {
            tvName.setText(room.name);
            tvCode.setText(room.code);
            tvType.setText(room.type != null ? room.type : "");
        }
    }

    private static final DiffUtil.ItemCallback<RoomEntity> DIFF = new DiffUtil.ItemCallback<RoomEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull RoomEntity a, @NonNull RoomEntity b) {
            return a.id == b.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull RoomEntity a, @NonNull RoomEntity b) {
            return a.name.equals(b.name) && a.code.equals(b.code);
        }
    };
}
