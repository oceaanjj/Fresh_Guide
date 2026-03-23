package com.example.freshguide.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.freshguide.BuildConfig;
import com.example.freshguide.R;
import com.example.freshguide.util.SessionManager;
import com.example.freshguide.util.ThemePreferenceManager;

public class SettingsFragment extends Fragment {

    private SessionManager sessionManager;

    private View cardSchedule;
    private View cardNotifications;

    private Spinner spinnerDefaultView;
    private Spinner spinnerDefaultReminder;
    private SwitchCompat switchUse24Hour;
    private SwitchCompat switchStartWeekSunday;

    private SwitchCompat switchScheduleNotifications;
    private SwitchCompat switchSyncAlerts;
    private SwitchCompat switchAutoSync;
    private SwitchCompat switchWifiOnly;

    private TextView tvSyncVersionInfo;
    private TextView tvAppVersionInfo;
    private TextView tvRoleInfo;

    private RadioGroup themeRadioGroup;

    private boolean bindingValues;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = SessionManager.getInstance(requireContext());

        cardSchedule = view.findViewById(R.id.card_settings_schedule);
        cardNotifications = view.findViewById(R.id.card_settings_notifications);

        spinnerDefaultView = view.findViewById(R.id.spinner_default_view);
        spinnerDefaultReminder = view.findViewById(R.id.spinner_default_reminder);
        switchUse24Hour = view.findViewById(R.id.switch_use_24_hour);
        switchStartWeekSunday = view.findViewById(R.id.switch_start_week_sunday);

        switchScheduleNotifications = view.findViewById(R.id.switch_schedule_notifications);
        switchSyncAlerts = view.findViewById(R.id.switch_sync_alerts);
        switchAutoSync = view.findViewById(R.id.switch_auto_sync);
        switchWifiOnly = view.findViewById(R.id.switch_wifi_only);

        tvSyncVersionInfo = view.findViewById(R.id.tv_sync_version_info);
        tvAppVersionInfo = view.findViewById(R.id.tv_app_version_info);
        tvRoleInfo = view.findViewById(R.id.tv_role_info);

        themeRadioGroup = view.findViewById(R.id.theme_radio_group);

        setupExpandableSection(
                view.findViewById(R.id.header_schedule),
                view.findViewById(R.id.content_schedule),
                view.findViewById(R.id.indicator_schedule),
                true
        );
        setupExpandableSection(
                view.findViewById(R.id.header_notifications),
                view.findViewById(R.id.content_notifications),
                view.findViewById(R.id.indicator_notifications),
                false
        );
        setupExpandableSection(
                view.findViewById(R.id.header_data),
                view.findViewById(R.id.content_data),
                view.findViewById(R.id.indicator_data),
                false
        );
        setupExpandableSection(
                view.findViewById(R.id.header_about),
                view.findViewById(R.id.content_about),
                view.findViewById(R.id.indicator_about),
                false
        );

        setupScheduleControls();
        setupNotificationControls();
        setupDataControls(view.findViewById(R.id.btn_reset_preferences));
        setupThemeControls();
        setupAboutSection();
        applyRoleVisibility();

        bindSavedValues();
    }

    private void setupExpandableSection(View header, View content, TextView indicator, boolean expanded) {
        content.setVisibility(expanded ? View.VISIBLE : View.GONE);
        indicator.setText(expanded ? "v" : ">");
        header.setOnClickListener(v -> {
            boolean isExpanded = content.getVisibility() == View.VISIBLE;
            content.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            indicator.setText(isExpanded ? ">" : "v");
        });
    }

    private void setupScheduleControls() {
        String[] viewModes = {"Weekly", "Daily"};
        spinnerDefaultView.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                viewModes
        ));

        String[] reminders = {
                "No reminder",
                "5 mins before",
                "10 mins before",
                "15 mins before",
                "30 mins before"
        };
        spinnerDefaultReminder.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                reminders
        ));

        spinnerDefaultView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bindingValues) return;
                sessionManager.setScheduleViewMode(position == 0
                        ? SessionManager.VIEW_MODE_WEEKLY
                        : SessionManager.VIEW_MODE_DAILY);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerDefaultReminder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bindingValues) return;
                sessionManager.setDefaultReminderMinutes(positionToReminder(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        switchUse24Hour.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (bindingValues) return;
            sessionManager.setUse24HourTime(isChecked);
        });

        switchStartWeekSunday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (bindingValues) return;
            sessionManager.setStartWeekOnSunday(isChecked);
        });
    }

    private void setupNotificationControls() {
        switchScheduleNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (bindingValues) return;
            sessionManager.setScheduleNotificationsEnabled(isChecked);
        });

        switchSyncAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (bindingValues) return;
            sessionManager.setSyncAlertsEnabled(isChecked);
        });
    }

    private void setupDataControls(View resetButton) {
        switchAutoSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (bindingValues) return;
            sessionManager.setAutoSyncEnabled(isChecked);
        });

        switchWifiOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (bindingValues) return;
            sessionManager.setWifiOnlySync(isChecked);
        });

        resetButton.setOnClickListener(v -> {
            sessionManager.resetAppPreferences();
            bindSavedValues();
            Toast.makeText(requireContext(), "Preferences reset", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupThemeControls() {
        // Set current selection
        int currentTheme = ThemePreferenceManager.getThemeMode(requireContext());
        switch (currentTheme) {
            case ThemePreferenceManager.THEME_LIGHT:
                themeRadioGroup.check(R.id.radio_theme_light);
                break;
            case ThemePreferenceManager.THEME_DARK:
                themeRadioGroup.check(R.id.radio_theme_dark);
                break;
            case ThemePreferenceManager.THEME_SYSTEM:
            default:
                themeRadioGroup.check(R.id.radio_theme_system);
                break;
        }

        // Listen for changes
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (bindingValues) return;

            int newTheme;
            if (checkedId == R.id.radio_theme_light) {
                newTheme = ThemePreferenceManager.THEME_LIGHT;
            } else if (checkedId == R.id.radio_theme_dark) {
                newTheme = ThemePreferenceManager.THEME_DARK;
            } else {
                newTheme = ThemePreferenceManager.THEME_SYSTEM;
            }

            ThemePreferenceManager.saveThemeMode(requireContext(), newTheme);
            ThemePreferenceManager.applyTheme(newTheme);
            requireActivity().recreate(); // Recreate activity to apply theme
        });
    }

    private void setupAboutSection() {
        tvAppVersionInfo.setText("App version: " + BuildConfig.VERSION_NAME);
        tvRoleInfo.setText("Role: " + (sessionManager.isAdmin() ? "admin" : "student"));
    }

    private void applyRoleVisibility() {
        boolean isAdmin = sessionManager.isAdmin();
        cardSchedule.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
        cardNotifications.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
    }

    private void bindSavedValues() {
        bindingValues = true;

        String mode = sessionManager.getScheduleViewMode();
        spinnerDefaultView.setSelection(SessionManager.VIEW_MODE_DAILY.equals(mode) ? 1 : 0, false);
        spinnerDefaultReminder.setSelection(reminderToPosition(sessionManager.getDefaultReminderMinutes()), false);

        switchUse24Hour.setChecked(sessionManager.shouldUse24HourTime());
        switchStartWeekSunday.setChecked(sessionManager.shouldStartWeekOnSunday());
        switchScheduleNotifications.setChecked(sessionManager.isScheduleNotificationsEnabled());
        switchSyncAlerts.setChecked(sessionManager.isSyncAlertsEnabled());
        switchAutoSync.setChecked(sessionManager.isAutoSyncEnabled());
        switchWifiOnly.setChecked(sessionManager.isWifiOnlySync());

        int syncVersion = sessionManager.getSyncVersion();
        tvSyncVersionInfo.setText(syncVersion >= 0
                ? "Data version: " + syncVersion
                : "Data version: Not synced");

        bindingValues = false;
    }

    private int positionToReminder(int position) {
        switch (position) {
            case 1:
                return 5;
            case 2:
                return 10;
            case 3:
                return 15;
            case 4:
                return 30;
            default:
                return 0;
        }
    }

    private int reminderToPosition(int reminderMinutes) {
        switch (reminderMinutes) {
            case 5:
                return 1;
            case 10:
                return 2;
            case 15:
                return 3;
            case 30:
                return 4;
            default:
                return 0;
        }
    }
}
