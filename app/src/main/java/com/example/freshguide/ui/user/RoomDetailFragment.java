package com.example.freshguide.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.freshguide.R;
import com.example.freshguide.model.entity.FacilityEntity;
import com.example.freshguide.viewmodel.RoomDetailViewModel;
import com.google.android.material.snackbar.Snackbar;

public class RoomDetailFragment extends Fragment {

    public static final String ARG_ROOM_ID = "roomId";

    private RoomDetailViewModel viewModel;
    private int roomId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Checklist 5.2: receive data from args
        roomId = requireArguments().getInt(ARG_ROOM_ID, -1);
        viewModel = new ViewModelProvider(this).get(RoomDetailViewModel.class);

        TextView tvName = view.findViewById(R.id.tv_room_name);
        TextView tvCode = view.findViewById(R.id.tv_room_code);
        TextView tvType = view.findViewById(R.id.tv_room_type);
        TextView tvDescription = view.findViewById(R.id.tv_room_description);
        TextView tvFacilities = view.findViewById(R.id.tv_facilities);
        Button btnDirections = view.findViewById(R.id.btn_get_directions);

        viewModel.getRoom().observe(getViewLifecycleOwner(), room -> {
            if (room == null) return;
            tvName.setText(room.name);
            tvCode.setText(room.code);
            tvType.setText(room.type != null ? room.type : "");
            tvDescription.setText(room.description != null ? room.description : "");
        });

        viewModel.getFacilities().observe(getViewLifecycleOwner(), facilities -> {
            if (facilities == null || facilities.isEmpty()) {
                tvFacilities.setText("No facilities listed");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (FacilityEntity f : facilities) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(f.name);
            }
            tvFacilities.setText(sb.toString());
        });

        viewModel.getError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Snackbar.make(view, err, Snackbar.LENGTH_LONG).show();
        });

        NavController nav = Navigation.findNavController(view);

        // Checklist 5.3: navigate to origin picker, receive result back
        btnDirections.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("roomId", roomId);
            nav.navigate(R.id.action_roomDetail_to_originPicker, args);
        });

        // Listen for origin selection result (set by OriginPickerFragment)
        getParentFragmentManager().setFragmentResultListener(
                OriginPickerFragment.RESULT_KEY, getViewLifecycleOwner(),
                (requestKey, result) -> {
                    int originId = result.getInt(OriginPickerFragment.KEY_ORIGIN_ID, -1);
                    if (originId != -1) {
                        Bundle dirArgs = new Bundle();
                        dirArgs.putInt("roomId", roomId);
                        dirArgs.putInt("originId", originId);
                        nav.navigate(R.id.action_roomDetail_to_directions, dirArgs);
                    }
                });

        if (roomId != -1) {
            viewModel.loadRoom(roomId);
        }
    }
}
