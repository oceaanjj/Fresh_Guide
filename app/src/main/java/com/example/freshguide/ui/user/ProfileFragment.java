package com.example.freshguide.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.freshguide.LoginActivity;
import com.example.freshguide.R;
import com.example.freshguide.repository.AuthRepository;
import com.example.freshguide.util.SessionManager;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager session = SessionManager.getInstance(requireContext());

        TextView tvStudentId = view.findViewById(R.id.tv_student_id);
        TextView tvRole = view.findViewById(R.id.tv_role);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvSyncVersion = view.findViewById(R.id.tv_sync_version);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        tvStudentId.setText(session.getStudentId() != null ? session.getStudentId() : "—");
        tvRole.setText(session.getRole() != null ? session.getRole() : "student");
        tvName.setText(session.getUserName() != null ? session.getUserName() : "—");
        int syncVersion = session.getSyncVersion();
        tvSyncVersion.setText(syncVersion >= 0 ? String.valueOf(syncVersion) : "Not synced");

        btnLogout.setOnClickListener(v -> {
            new AuthRepository(requireContext()).logout();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}
