package com.example.freshguide.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCampusAreaFormFragment extends Fragment {

    private static final String LABEL_FACILITIES = "Facilities";
    private static final String LABEL_HOURS = "Hours";
    private static final String LABEL_BOOKING = "Booking";
    private static final String LABEL_DIRECTORY = "Directory";
    private static final String LABEL_EMERGENCY = "Emergency";
    private static final String LABEL_CAMPUS_INFO = "Campus Info";
    private static final String LABEL_NOTES = "Notes";

    private int roomId = -1;
    private String roomCode = "";

    private EditText etName;
    private TextView tvCode;
    private EditText etFloorId;
    private EditText etFacilities;
    private EditText etHours;
    private EditText etBooking;
    private EditText etDirectory;
    private EditText etEmergency;
    private EditText etCampusInfo;
    private EditText etNotes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_campus_area_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomId = getArguments() != null ? getArguments().getInt("roomId", -1) : -1;

        etName = view.findViewById(R.id.et_area_name);
        tvCode = view.findViewById(R.id.tv_area_code);
        etFloorId = view.findViewById(R.id.et_area_floor_id);
        etFacilities = view.findViewById(R.id.et_area_facilities);
        etHours = view.findViewById(R.id.et_area_hours);
        etBooking = view.findViewById(R.id.et_area_booking);
        etDirectory = view.findViewById(R.id.et_area_directory);
        etEmergency = view.findViewById(R.id.et_area_emergency);
        etCampusInfo = view.findViewById(R.id.et_area_campus_info);
        etNotes = view.findViewById(R.id.et_area_notes);
        Button btnSave = view.findViewById(R.id.btn_save_campus_area);

        if (roomId <= 0) {
            Snackbar.make(view, "Invalid campus area", Snackbar.LENGTH_LONG).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        btnSave.setOnClickListener(v -> saveCampusArea(view));
        loadCampusArea(view);
    }

    private void loadCampusArea(View view) {
        ApiClient.getInstance(requireContext()).getApiService()
                .getRoom(roomId).enqueue(new Callback<ApiResponse<RoomDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<RoomDto>> call,
                                           Response<ApiResponse<RoomDto>> response) {
                        if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                            Snackbar.make(view, "Failed to load campus area", Snackbar.LENGTH_LONG).show();
                            Navigation.findNavController(view).popBackStack();
                            return;
                        }

                        RoomDto room = response.body().getData();
                        if (room == null) {
                            Snackbar.make(view, "Campus area not found", Snackbar.LENGTH_LONG).show();
                            Navigation.findNavController(view).popBackStack();
                            return;
                        }

                        roomCode = room.code != null ? room.code.trim().toUpperCase(Locale.ROOT) : "";

                        etName.setText(room.name != null ? room.name : "");
                        tvCode.setText(roomCode.isEmpty() ? "Code" : roomCode);
                        etFloorId.setText(String.valueOf(room.floorId));

                        populateSections(room.description);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<RoomDto>> call, Throwable t) {
                        Snackbar.make(view, "Network error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                        Navigation.findNavController(view).popBackStack();
                    }
                });
    }

    private void saveCampusArea(View view) {
        String name = etName.getText().toString().trim();
        String floorId = etFloorId.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }
        if (floorId.isEmpty()) {
            etFloorId.setError("Floor ID is required");
            return;
        }

        int parsedFloorId;
        try {
            parsedFloorId = Integer.parseInt(floorId);
        } catch (NumberFormatException ex) {
            etFloorId.setError("Invalid floor ID");
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("code", roomCode);
        body.put("type", "other");
        body.put("floor_id", parsedFloorId);
        body.put("location", "Campus Area");
        body.put("description", buildDescription());

        ApiService api = ApiClient.getInstance(requireContext()).getApiService();
        api.adminUpdateRoom(roomId, body).enqueue(new Callback<ApiResponse<RoomDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<RoomDto>> call, Response<ApiResponse<RoomDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Snackbar.make(view, "Campus area updated", Snackbar.LENGTH_SHORT).show();
                    Navigation.findNavController(view).popBackStack();
                } else {
                    Snackbar.make(view, "Failed to update campus area", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RoomDto>> call, Throwable t) {
                Snackbar.make(view, "Network error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void populateSections(String description) {
        if (description == null || description.trim().isEmpty()) {
            return;
        }

        String[] lines = description.split("\\n");
        StringBuilder notesFallback = new StringBuilder();
        for (String line : lines) {
            String trimmed = line == null ? "" : line.trim();
            if (trimmed.isEmpty()) continue;

            int colonIndex = trimmed.indexOf(':');
            if (colonIndex <= 0) {
                appendLine(notesFallback, trimmed);
                continue;
            }

            String key = trimmed.substring(0, colonIndex).trim().toLowerCase(Locale.ROOT);
            String value = trimmed.substring(colonIndex + 1).trim();

            switch (key) {
                case "facilities":
                    etFacilities.setText(value);
                    break;
                case "hours":
                    etHours.setText(value);
                    break;
                case "booking":
                    etBooking.setText(value);
                    break;
                case "directory":
                    etDirectory.setText(value);
                    break;
                case "emergency":
                    etEmergency.setText(value);
                    break;
                case "campus info":
                    etCampusInfo.setText(value);
                    break;
                case "notes":
                    etNotes.setText(value);
                    break;
                default:
                    appendLine(notesFallback, trimmed);
                    break;
            }
        }

        if (etNotes.getText().toString().trim().isEmpty() && notesFallback.length() > 0) {
            etNotes.setText(notesFallback.toString());
        }
    }

    private String buildDescription() {
        StringBuilder sb = new StringBuilder();
        appendSection(sb, LABEL_FACILITIES, etFacilities.getText().toString());
        appendSection(sb, LABEL_HOURS, etHours.getText().toString());
        appendSection(sb, LABEL_BOOKING, etBooking.getText().toString());
        appendSection(sb, LABEL_DIRECTORY, etDirectory.getText().toString());
        appendSection(sb, LABEL_EMERGENCY, etEmergency.getText().toString());
        appendSection(sb, LABEL_CAMPUS_INFO, etCampusInfo.getText().toString());
        appendSection(sb, LABEL_NOTES, etNotes.getText().toString());
        return sb.toString().trim();
    }

    private void appendSection(StringBuilder sb, String label, String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) return;

        if (sb.length() > 0) {
            sb.append("\n");
        }
        sb.append(label).append(": ").append(trimmed);
    }

    private void appendLine(StringBuilder sb, String value) {
        if (value == null || value.trim().isEmpty()) return;
        if (sb.length() > 0) {
            sb.append("\n");
        }
        sb.append(value.trim());
    }
}
