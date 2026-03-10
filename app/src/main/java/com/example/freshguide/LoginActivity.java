package com.example.freshguide;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.freshguide.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private boolean passwordVisible = false;
    private LoginViewModel viewModel;
    private AlertDialog registerDialog;
    private EditText registerStudentIdInput;
    private boolean registerFlowActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText inputUsername = findViewById(R.id.inputUsername);
        EditText inputPassword = findViewById(R.id.inputPassword);
        ImageButton btnTogglePassword = findViewById(R.id.btnTogglePassword);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        TextView tvLoginHint = findViewById(R.id.tv_login_hint);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Password visibility toggle
        btnTogglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                inputPassword.setTransformationMethod(SingleLineTransformationMethod.getInstance());
            } else {
                inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            inputPassword.setSelection(inputPassword.getText().length());
        });

        // Detect mode from username field: "@" = admin, otherwise student
        inputUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                boolean isAdminMode = inputUsername.getText().toString().contains("@");
                inputPassword.setVisibility(isAdminMode ? View.VISIBLE : View.GONE);
                btnTogglePassword.setVisibility(isAdminMode ? View.VISIBLE : View.GONE);
                tvLoginHint.setText(isAdminMode ? "Admin login" : "Student ID login");
            }
        });

        // Sign in
        btnSignIn.setOnClickListener(v -> {
            registerFlowActive = false;
            inputUsername.setError(null);
            String username = inputUsername.getText().toString().trim();
            boolean isAdmin = username.contains("@");

            if (isAdmin) {
                String password = inputPassword.getText().toString().trim();
                viewModel.loginAdmin(username, password);
            } else {
                viewModel.loginStudent(username);
            }
        });

        btnCreateAccount.setOnClickListener(v -> showRegisterDialog(inputUsername.getText().toString().trim()));

        // Observe login state
        viewModel.getState().observe(this, state -> {
            boolean loading = state == LoginViewModel.State.LOADING;
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            btnSignIn.setEnabled(!loading);
            btnCreateAccount.setEnabled(!loading);
            if (registerDialog != null && registerDialog.isShowing()) {
                Button positive = registerDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negative = registerDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                if (positive != null) positive.setEnabled(!loading);
                if (negative != null) negative.setEnabled(!loading);
            }

            if (state == LoginViewModel.State.SUCCESS_STUDENT || state == LoginViewModel.State.SUCCESS_ADMIN) {
                if (registerDialog != null && registerDialog.isShowing()) {
                    registerDialog.dismiss();
                }
                boolean onboardingDone = getSharedPreferences(
                        OnboardingActivity.PREFS_NAME, MODE_PRIVATE)
                        .getBoolean(OnboardingActivity.KEY_ONBOARDING_COMPLETE, false);

                Intent next = onboardingDone
                        ? new Intent(this, MainActivity.class)
                        : new Intent(this, OnboardingActivity.class);
                startActivity(next);
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, err -> {
            if (err != null && !err.isEmpty()) {
                if (registerFlowActive && registerDialog != null && registerDialog.isShowing() && registerStudentIdInput != null) {
                    registerStudentIdInput.setError(err);
                    Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
                } else {
                    inputUsername.setError(err);
                }
            }
        });
    }

    private void showRegisterDialog(String initialStudentId) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register, null);
        EditText inputStudentId = dialogView.findViewById(R.id.inputRegisterStudentId);

        if (initialStudentId != null && !initialStudentId.isBlank() && !initialStudentId.contains("@")) {
            inputStudentId.setText(initialStudentId);
            inputStudentId.setSelection(initialStudentId.length());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.register_dialog_title)
                .setView(dialogView)
                .setNegativeButton(R.string.action_cancel, (d, which) -> d.dismiss())
                .setPositiveButton(R.string.action_register, null)
                .create();

        dialog.setOnShowListener(d -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                inputStudentId.setError(null);
                String studentId = inputStudentId.getText().toString().trim();
                if (studentId.isEmpty()) {
                    inputStudentId.setError(getString(R.string.error_student_id_required));
                    return;
                }
                if (studentId.contains("@")) {
                    inputStudentId.setError(getString(R.string.error_student_id_format));
                    return;
                }

                registerFlowActive = true;
                registerDialog = dialog;
                registerStudentIdInput = inputStudentId;
                viewModel.registerStudent(studentId);
            });
        });

        dialog.setOnDismissListener(d -> {
            registerDialog = null;
            registerStudentIdInput = null;
            registerFlowActive = false;
        });

        dialog.show();
    }
}
