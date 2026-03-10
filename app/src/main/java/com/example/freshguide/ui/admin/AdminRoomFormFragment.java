package com.example.freshguide.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.freshguide.R;
import com.example.freshguide.model.dto.ApiResponse;
import com.example.freshguide.model.dto.RoomDto;
import com.example.freshguide.network.ApiClient;
import com.example.freshguide.network.ApiService;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRoomFormFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_room_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int roomId = getArguments() != null ? getArguments().getInt("roomId", -1) : -1;
        boolean isEdit = roomId != -1;

        EditText etName = view.findViewById(R.id.et_room_name);
        EditText etCode = view.findViewById(R.id.et_room_code);
        EditText etType = view.findViewById(R.id.et_room_type);
        EditText etFloorId = view.findViewById(R.id.et_floor_id);
        EditText etDescription = view.findViewById(R.id.et_room_description);
        Button btnSave = view.findViewById(R.id.btn_save);

        btnSave.setText(isEdit ? "Update Room" : "Create Room");

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String code = etCode.getText().toString().trim();
            String type = etType.getText().toString().trim();
            String floorIdStr = etFloorId.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            if (name.isEmpty() || code.isEmpty() || floorIdStr.isEmpty()) {
                Snackbar.make(view, "Name, code, and floor ID are required", Snackbar.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("code", code);
            body.put("type", type);
            body.put("floor_id", Integer.parseInt(floorIdStr));
            body.put("description", desc);

            ApiService api = ApiClient.getInstance(requireContext()).getApiService();
            Call<ApiResponse<RoomDto>> call = isEdit
                    ? api.adminUpdateRoom(roomId, body)
                    : api.adminCreateRoom(body);

            call.enqueue(new Callback<ApiResponse<RoomDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<RoomDto>> c, Response<ApiResponse<RoomDto>> r) {
                    if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                        Navigation.findNavController(view).popBackStack();
                    } else {
                        Snackbar.make(view, "Failed to save room", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<RoomDto>> c, Throwable t) {
                    Snackbar.make(view, "Network error", Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }
}
