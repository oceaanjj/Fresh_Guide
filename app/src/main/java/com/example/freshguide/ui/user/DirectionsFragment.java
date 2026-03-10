package com.example.freshguide.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshguide.R;
import com.example.freshguide.ui.adapter.RouteStepAdapter;
import com.example.freshguide.viewmodel.DirectionsViewModel;
import com.google.android.material.snackbar.Snackbar;

public class DirectionsFragment extends Fragment {

    private DirectionsViewModel viewModel;
    private RouteStepAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Checklist 5.7: receive route data via args
        int roomId = requireArguments().getInt("roomId", -1);
        int originId = requireArguments().getInt("originId", -1);

        viewModel = new ViewModelProvider(this).get(DirectionsViewModel.class);

        adapter = new RouteStepAdapter();
        RecyclerView recycler = view.findViewById(R.id.recycler_steps);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        TextView tvRouteName = view.findViewById(R.id.tv_route_name);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
                progressBar.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE));

        viewModel.getRoute().observe(getViewLifecycleOwner(), route -> {
            if (route == null) return;
            tvRouteName.setText(route.name != null ? route.name : "Directions");
            adapter.setSteps(route.steps);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Snackbar.make(view, err, Snackbar.LENGTH_LONG).show();
        });

        if (roomId != -1 && originId != -1) {
            viewModel.loadRoute(roomId, originId);
        }
    }
}
