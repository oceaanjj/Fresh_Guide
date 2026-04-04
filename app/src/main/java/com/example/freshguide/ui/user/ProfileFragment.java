package com.example.freshguide.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.freshguide.LoginActivity;
import com.example.freshguide.R;
import com.example.freshguide.repository.AuthRepository;
import com.example.freshguide.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView tvName;
    private TextView tvStudentId;
    private TextView tvCourseSection;
    private TextView tvProfileDate;

    private ImageView imgProfilePhoto;
    private TextView tvProfileInitial;

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

        tvName = view.findViewById(R.id.tv_name);
        tvStudentId = view.findViewById(R.id.tv_student_id);
        tvCourseSection = view.findViewById(R.id.tv_course_section);
        tvProfileDate = view.findViewById(R.id.tv_profile_date);

        imgProfilePhoto = view.findViewById(R.id.img_profile_photo);
        tvProfileInitial = view.findViewById(R.id.tv_profile_initial);

        ImageButton btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        ImageButton btnMore = view.findViewById(R.id.btn_more);

        String studentId = session.getStudentId() != null ? session.getStudentId() : "—";
        String savedName = session.getUserName() != null ? session.getUserName().trim() : "";
        String courseSection = "BSCS 3A";

        if (!savedName.isEmpty()) {
            tvName.setText(savedName.toUpperCase(Locale.getDefault()));
        } else {
            tvName.setText("TEST STUDENT");
        }

        tvStudentId.setText(studentId);
        tvCourseSection.setText(courseSection);

        updateProfileInitial(tvName.getText().toString());

        imgProfilePhoto.setVisibility(View.GONE);
        tvProfileInitial.setVisibility(View.VISIBLE);

        setCurrentDate();

        btnEditProfile.setOnClickListener(v -> {
            EditProfileBottomSheet bottomSheet = EditProfileBottomSheet.newInstance(
                    studentId,
                    tvName.getText().toString(),
                    tvCourseSection.getText().toString()
            );

            bottomSheet.setOnProfileSavedListener((newFirstName, newMiddleInitial, newLastName, newCourseSection) -> {
                String updatedName = buildFullName(newFirstName, newMiddleInitial, newLastName);
                tvName.setText(updatedName);
                tvCourseSection.setText(newCourseSection);

                updateProfileInitial(updatedName);

                // Next step later:
                // save to SessionManager / SharedPreferences / database
            });

            bottomSheet.show(getParentFragmentManager(), "EditProfileBottomSheet");
        });

        btnMore.setOnClickListener(this::showProfileMenu);
    }

    private void updateProfileInitial(String displayName) {
        String initial = "U";

        if (displayName != null) {
            String trimmed = displayName.trim();
            if (!trimmed.isEmpty() && !trimmed.equals("—")) {
                initial = String.valueOf(trimmed.charAt(0)).toUpperCase(Locale.getDefault());
            }
        }

        tvProfileInitial.setText(initial);
    }

    private void setCurrentDate() {
        Calendar calendar = Calendar.getInstance();

        String dayName = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
        String monthName = new SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.getTime());
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String formattedDate = dayName + ", " + monthName + " " + day;
        tvProfileDate.setText(formattedDate);
    }

    private void showProfileMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchor);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClicked);
        popupMenu.show();
    }

    private boolean onMenuItemClicked(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new AuthRepository(requireContext()).logout();

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
            return true;
        }
        return false;
    }

    private String buildFullName(String firstName, String middleInitial, String lastName) {
        StringBuilder builder = new StringBuilder();

        if (firstName != null && !firstName.trim().isEmpty()) {
            builder.append(firstName.trim());
        }

        if (middleInitial != null && !middleInitial.trim().isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(middleInitial.trim().replace(".", "")).append(".");
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(lastName.trim());
        }

        if (builder.length() == 0) {
            return "—";
        }

        return builder.toString().toUpperCase(Locale.getDefault());
    }
}