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
import com.example.freshguide.model.dto.RouteDto;
import com.example.freshguide.network.ApiClient;
import com.example.freshguide.network.ApiService;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRouteFormFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int routeId = getArguments() != null ? getArguments().getInt("routeId", -1) : -1;
        boolean isEdit = routeId != -1;

        EditText etName = view.findViewById(R.id.et_field1);
        EditText etOriginId = view.findViewById(R.id.et_field2);
        EditText etRoomId = view.findViewById(R.id.et_field3);
        Button btnSave = view.findViewById(R.id.btn_save);

        etName.setHint("Route Name");
        etOriginId.setHint("Origin ID");
        etRoomId.setHint("Destination Room ID");
        btnSave.setText(isEdit ? "Update Route" : "Create Route");

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String originId = etOriginId.getText().toString().trim();
            String roomId = etRoomId.getText().toString().trim();

            if (name.isEmpty() || originId.isEmpty() || roomId.isEmpty()) {
                Snackbar.make(view, "All fields are required", Snackbar.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("origin_id", Integer.parseInt(originId));
            body.put("destination_room_id", Integer.parseInt(roomId));

            ApiService api = ApiClient.getInstance(requireContext()).getApiService();
            Call<ApiResponse<RouteDto>> call = isEdit
                    ? api.adminUpdateRoute(routeId, body)
                    : api.adminCreateRoute(body);

            call.enqueue(new Callback<ApiResponse<RouteDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<RouteDto>> c, Response<ApiResponse<RouteDto>> r) {
                    if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                        Navigation.findNavController(view).popBackStack();
                    } else {
                        Snackbar.make(view, "Failed to save route", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<RouteDto>> c, Throwable t) {
                    Snackbar.make(view, "Network error", Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }
}
