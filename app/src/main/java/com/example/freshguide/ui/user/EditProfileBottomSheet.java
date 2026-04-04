package com.example.freshguide.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.freshguide.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditProfileBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_STUDENT_ID = "arg_student_id";
    private static final String ARG_FULL_NAME = "arg_full_name";
    private static final String ARG_COURSE_SECTION = "arg_course_section";

    private OnProfileSavedListener onProfileSavedListener;

    public interface OnProfileSavedListener {
        void onProfileSaved(String firstName, String middleInitial, String lastName, String courseSection);
    }

    public static EditProfileBottomSheet newInstance(String studentId, String fullName, String courseSection) {
        EditProfileBottomSheet bottomSheet = new EditProfileBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_STUDENT_ID, studentId);
        args.putString(ARG_FULL_NAME, fullName);
        args.putString(ARG_COURSE_SECTION, courseSection);
        bottomSheet.setArguments(args);
        return bottomSheet;
    }

    public void setOnProfileSavedListener(OnProfileSavedListener listener) {
        this.onProfileSavedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvEditStudentId = view.findViewById(R.id.tv_edit_student_id);
        EditText etFirstName = view.findViewById(R.id.et_first_name);
        EditText etMiddleInitial = view.findViewById(R.id.et_middle_initial);
        EditText etLastName = view.findViewById(R.id.et_last_name);
        EditText etCourseSection = view.findViewById(R.id.et_course_section);

        ImageView imgEditProfilePhoto = view.findViewById(R.id.img_edit_profile_photo);
        TextView tvEditProfileInitial = view.findViewById(R.id.tv_edit_profile_initial);

        View btnChangePhoto = view.findViewById(R.id.btn_change_photo);
        View btnCancel = view.findViewById(R.id.btn_sheet_cancel);
        View btnSave = view.findViewById(R.id.btn_save_profile);

        String studentId = getArguments() != null
                ? getArguments().getString(ARG_STUDENT_ID, "—")
                : "—";

        String fullName = getArguments() != null
                ? getArguments().getString(ARG_FULL_NAME, "")
                : "";

        String courseSection = getArguments() != null
                ? getArguments().getString(ARG_COURSE_SECTION, "")
                : "";

        tvEditStudentId.setText(studentId);
        etCourseSection.setText(courseSection);

        populateNameFields(fullName, etFirstName, etMiddleInitial, etLastName);

        String initial = getInitialFromName(fullName);
        tvEditProfileInitial.setText(initial);

        imgEditProfilePhoto.setVisibility(View.GONE);
        tvEditProfileInitial.setVisibility(View.VISIBLE);

        btnChangePhoto.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Photo change will be added next.", Toast.LENGTH_SHORT).show()
        );

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            clearErrors(etFirstName, etMiddleInitial, etLastName, etCourseSection);

            String firstName = getText(etFirstName);
            String middleInitial = getText(etMiddleInitial);
            String lastName = getText(etLastName);
            String updatedCourseSection = getText(etCourseSection);

            boolean hasError = false;

            if (TextUtils.isEmpty(firstName)) {
                etFirstName.setError("Required");
                etFirstName.requestFocus();
                hasError = true;
            }

            if (TextUtils.isEmpty(lastName)) {
                etLastName.setError("Required");
                if (!hasError) {
                    etLastName.requestFocus();
                }
                hasError = true;
            }

            if (TextUtils.isEmpty(updatedCourseSection)) {
                etCourseSection.setError("Required");
                if (!hasError) {
                    etCourseSection.requestFocus();
                }
                hasError = true;
            }

            if (hasError) {
                return;
            }

            middleInitial = normalizeMiddleInitial(middleInitial);

            if (onProfileSavedListener != null) {
                onProfileSavedListener.onProfileSaved(
                        firstName,
                        middleInitial,
                        lastName,
                        updatedCourseSection
                );
            }

            Toast.makeText(requireContext(), "Profile updated.", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    private String getText(EditText editText) {
        return editText.getText() != null
                ? editText.getText().toString().trim()
                : "";
    }

    private void clearErrors(EditText... editTexts) {
        if (editTexts == null) return;

        for (EditText editText : editTexts) {
            if (editText != null) {
                editText.setError(null);
            }
        }
    }

    private String normalizeMiddleInitial(String middleInitial) {
        if (middleInitial == null) {
            return "";
        }

        String cleaned = middleInitial.trim().replace(".", "").toUpperCase();

        if (cleaned.length() > 1) {
            cleaned = cleaned.substring(0, 1);
        }

        return cleaned;
    }

    private String getInitialFromName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty() || fullName.equals("—")) {
            return "U";
        }

        String trimmed = fullName.trim();
        return String.valueOf(trimmed.charAt(0)).toUpperCase();
    }

    private void populateNameFields(String fullName,
                                    EditText etFirstName,
                                    EditText etMiddleInitial,
                                    EditText etLastName) {

        if (fullName == null || fullName.trim().isEmpty()) {
            return;
        }

        String cleanedName = fullName.trim().replaceAll("\\s+", " ");
        String[] parts = cleanedName.split(" ");

        if (parts.length == 1) {
            etFirstName.setText(parts[0]);
            return;
        }

        if (parts.length == 2) {
            etFirstName.setText(parts[0]);
            etLastName.setText(parts[1]);
            return;
        }

        etFirstName.setText(parts[0]);

        String secondPart = parts[1].replace(".", "");

        if (secondPart.length() <= 2) {
            etMiddleInitial.setText(secondPart);

            StringBuilder lastNameBuilder = new StringBuilder();
            for (int i = 2; i < parts.length; i++) {
                if (i > 2) {
                    lastNameBuilder.append(" ");
                }
                lastNameBuilder.append(parts[i]);
            }
            etLastName.setText(lastNameBuilder.toString());
        } else {
            StringBuilder lastNameBuilder = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                if (i > 1) {
                    lastNameBuilder.append(" ");
                }
                lastNameBuilder.append(parts[i]);
            }
            etLastName.setText(lastNameBuilder.toString());
        }
    }
}