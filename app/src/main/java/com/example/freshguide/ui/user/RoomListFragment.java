package com.example.freshguide.ui.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freshguide.R;
import com.example.freshguide.ui.adapter.RoomAdapter;
import com.example.freshguide.viewmodel.RoomListViewModel;

public class RoomListFragment extends Fragment {

    private RoomListViewModel viewModel;
    private RoomAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RoomListViewModel.class);

        adapter = new RoomAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_rooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Checklist 5.5: list item click events
        NavController nav = Navigation.findNavController(view);
        adapter.setOnItemClickListener(room -> {
            // Checklist 5.1: pass data via Bundle args
            Bundle args = new Bundle();
            args.putInt("roomId", room.id);
            args.putString("roomName", room.name);
            nav.navigate(R.id.action_roomList_to_roomDetail, args);
        });

        // Building filter from campus map tap
        Bundle args = getArguments();
        if (args != null) {
            String buildingCode = args.getString("buildingCode", "");
            String buildingName = args.getString("buildingName", "");
            if (!buildingCode.isEmpty()) {
                viewModel.setBuilding(buildingCode);
                TextView header = view.findViewById(R.id.tv_building_header);
                header.setText("Rooms in " + buildingName);
                header.setVisibility(View.VISIBLE);
            }
        }

        // Search input — checklist 3.1: user event handling
        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setQuery(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        viewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> adapter.submitList(rooms));
    }
}
