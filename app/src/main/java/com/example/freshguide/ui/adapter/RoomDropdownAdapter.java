package com.example.freshguide.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshguide.R;
import com.example.freshguide.model.entity.RoomEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomDropdownAdapter extends RecyclerView.Adapter<RoomDropdownAdapter.RoomViewHolder> {

    public interface OnRoomClickListener {
        void onRoomClick(RoomEntity room);
    }

    private final List<RoomEntity> items = new ArrayList<>();
    private final OnRoomClickListener listener;

    public RoomDropdownAdapter(OnRoomClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<RoomEntity> rooms) {
        items.clear();
        if (rooms != null) {
            items.addAll(rooms);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room_dropdown, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomEntity room = items.get(position);

        holder.title.setText(room.name != null ? room.name : "Room");

        String code = room.code != null ? room.code.trim() : "";
        String subtitle = code.isEmpty()
                ? "Campus room"
                : code + " • UCC";
        holder.subtitle.setText(subtitle);

        holder.itemView.setOnClickListener(v -> listener.onRoomClick(room));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        final ImageView pin;
        final TextView title;
        final TextView subtitle;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            pin = itemView.findViewById(R.id.img_room_pin);
            title = itemView.findViewById(R.id.tv_room_title);
            subtitle = itemView.findViewById(R.id.tv_room_subtitle);
        }
    }
}