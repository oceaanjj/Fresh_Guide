package com.example.freshguide.ui.user;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.freshguide.R;
import com.example.freshguide.ui.view.CampusMapView;
import com.example.freshguide.viewmodel.HomeViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private CampusMapView campusMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        campusMap = view.findViewById(R.id.campus_map);

        NavController nav = Navigation.findNavController(view);

        setupSearch(view, nav);
        setupFloorChips(view);
        setupCampusMap(nav);
        setupFab(view);
        observeSync(view);

        viewModel.sync();
    }

    private void setupSearch(View view, NavController nav) {
        view.findViewById(R.id.layout_search).setOnClickListener(
                v -> nav.navigate(R.id.action_home_to_roomList));
    }

    private void setupFloorChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_floors);

        viewModel.getFloorNumbers().observe(getViewLifecycleOwner(), floors -> {
            chipGroup.removeAllViews();

            Chip all = makeFilterChip("All");
            all.setChecked(true);
            chipGroup.addView(all);

            if (floors != null) {
                for (Map.Entry<Integer, String> entry : floors.entrySet()) {
                    chipGroup.addView(makeFilterChip(entry.getValue()));
                }
            }
        });
    }

    /** Outlined pill-style chip matching the design. */
    private Chip makeFilterChip(String label) {
        Chip chip = new Chip(requireContext(),
                null, com.google.android.material.R.attr.chipStyle);
        chip.setText(label);
        chip.setCheckable(true);
        chip.setCheckedIconVisible(false);

        // Outline: green border, transparent fill when unchecked; filled when checked
        int green = requireContext().getColor(R.color.green_primary);

        chip.setChipStrokeColorResource(R.color.green_primary);
        chip.setChipStrokeWidth(2f);

        // Background state list: checked = green, unchecked = transparent
        ColorStateList bg = new ColorStateList(
                new int[][]{ new int[]{ android.R.attr.state_checked }, new int[]{} },
                new int[]{ green, Color.TRANSPARENT }
        );
        chip.setChipBackgroundColor(bg);

        // Text color: checked = white, unchecked = green
        ColorStateList tc = new ColorStateList(
                new int[][]{ new int[]{ android.R.attr.state_checked }, new int[]{} },
                new int[]{ Color.WHITE, green }
        );
        chip.setTextColor(tc);

        return chip;
    }

    private void setupCampusMap(NavController nav) {
        campusMap.setOnBuildingClickListener((code, name) -> {
            Bundle args = new Bundle();
            args.putString("buildingCode", code);
            args.putString("buildingName", name);
            nav.navigate(R.id.action_home_to_roomList, args);
        });
    }

    /** Re-centres the map to its default zoom/pan. */
    private void setupFab(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fab_compass);
        fab.setOnClickListener(v -> campusMap.resetView());
    }

    private void observeSync(View view) {
        viewModel.getSyncError().observe(getViewLifecycleOwner(), err -> {
            if (err != null && !err.isEmpty()) {
                Snackbar.make(view, "Sync failed: " + err, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
