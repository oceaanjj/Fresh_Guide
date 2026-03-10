package com.example.freshguide.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshguide.R;
import com.example.freshguide.model.dto.RouteStepDto;

import java.util.ArrayList;
import java.util.List;

public class RouteStepAdapter extends RecyclerView.Adapter<RouteStepAdapter.ViewHolder> {

    private List<RouteStepDto> steps = new ArrayList<>();

    public void setSteps(List<RouteStepDto> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route_step, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(steps.get(position), position + 1);
    }

    @Override
    public int getItemCount() { return steps.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvStep;
        private final TextView tvInstruction;
        private final TextView tvLandmark;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStep = itemView.findViewById(R.id.tv_step_number);
            tvInstruction = itemView.findViewById(R.id.tv_step_instruction);
            tvLandmark = itemView.findViewById(R.id.tv_step_landmark);
        }

        void bind(RouteStepDto step, int number) {
            tvStep.setText(String.valueOf(number));
            tvInstruction.setText(step.instruction);
            if (step.landmark != null && !step.landmark.isEmpty()) {
                tvLandmark.setVisibility(View.VISIBLE);
                tvLandmark.setText("Landmark: " + step.landmark);
            } else {
                tvLandmark.setVisibility(View.GONE);
            }
        }
    }
}
