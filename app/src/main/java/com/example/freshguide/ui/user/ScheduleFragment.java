package com.example.freshguide.ui.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.freshguide.R;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.freshguide.R;
import com.example.freshguide.model.entity.RoomEntity;
import com.example.freshguide.model.entity.ScheduleEntryEntity;
import com.example.freshguide.ui.adapter.RoomDropdownAdapter;
import com.example.freshguide.ui.adapter.TimeWheelAdapter;
import com.example.freshguide.util.ScheduleReminderHelper;
import com.example.freshguide.util.SessionManager;
import com.example.freshguide.viewmodel.ScheduleViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScheduleFragment extends Fragment {

    private static final String TAG = "ScheduleFragment";

    private static final String[] DAY_LABELS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private static final String[] CLASS_TYPES = {"On-site", "Online"};
    private static final String[] PLATFORMS = {"Zoom", "Google Meet", "Microsoft Teams", "Other"};
    private static final String[] REMINDER_OPTIONS = {
            "No reminder", "5 mins before", "10 mins before", "15 mins before", "30 mins before"
    };

    // ── Timeline sizing constants ──────────────────────────────────────────────
    /** dp per minute — 90 dp per hour */
    private static final float DP_PER_MINUTE = 1.5f;
    /** Minimum block height in dp so very short classes are still readable */
    private static final int MIN_BLOCK_HEIGHT_DP = 48;
    /** Width of the time-label column in dp */
    private static final int LABEL_WIDTH_DP = 54;
    /** Number of day columns in the grid (Mon–Sat) */
    private static final int DAY_COLUMN_COUNT = 6;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (!granted && isAdded()) {
                    Toast.makeText(requireContext(), "Reminder notifications are disabled", Toast.LENGTH_SHORT).show();
                }
            });

    private ScheduleViewModel viewModel;
    private SessionManager sessionManager;
    private LiveData<List<ScheduleEntryEntity>> allSchedulesLiveData;

    private final List<ScheduleEntryEntity> allSchedules = new ArrayList<>();
    private final List<ScheduleEntryEntity> filteredDailySchedules = new ArrayList<>();

    // ── Views ──────────────────────────────────────────────────────────────────
    private View timelineScrollView;
    private FrameLayout timelineContainer;
    private View dailyDaySelectorContainer;
    private View emptyState;
    private View cardSummary;

    private TextView[] dailyDayViews;
    private TextView tvSummaryCode;
    private TextView tvSummaryTitle;
    private TextView tvSummaryProfessor;
    private TextView tvSummaryTime;
    private TextView tvSummaryLabel;
    private TextView tvDate;
    private TextView tvEmptyStateMessage;

    // ── Room data ──────────────────────────────────────────────────────────────
    private final List<RoomEntity> allRooms = new ArrayList<>();
    private final List<RoomEntity> roomOptions = new ArrayList<>();
    private final Map<Integer, String> roomNameMap = new HashMap<>();

    // ── Network ────────────────────────────────────────────────────────────────
    @Nullable
    private ConnectivityManager connectivityManager;
    @Nullable
    private ConnectivityManager.NetworkCallback scheduleNetworkCallback;
    private boolean networkCallbackRegistered = false;

    private int selectedDay = 1;

    private final List<ScheduleBlockBinding> scheduleBlocks = new ArrayList<>();

    // ══════════════════════════════════════════════════════════════════════════
    //  Lifecycle
    // ══════════════════════════════════════════════════════════════════════════

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        sessionManager = SessionManager.getInstance(requireContext());

        ScheduleReminderHelper.ensureNotificationChannel(requireContext());

        // ── Bind views ────────────────────────────────────────────────────────
        tvDate                  = view.findViewById(R.id.tv_schedule_date);
        timelineScrollView      = view.findViewById(R.id.timeline_scroll);
        timelineContainer       = view.findViewById(R.id.timeline_container);
        dailyDaySelectorContainer = view.findViewById(R.id.daily_day_selector_container);
        emptyState              = view.findViewById(R.id.empty_state);
        cardSummary             = view.findViewById(R.id.card_today_summary);

        tvSummaryCode      = view.findViewById(R.id.tv_summary_course_code);
        tvSummaryTitle     = view.findViewById(R.id.tv_summary_title);
        tvSummaryProfessor = view.findViewById(R.id.tv_summary_professor);
        tvSummaryTime      = view.findViewById(R.id.tv_summary_time);
        tvSummaryLabel     = view.findViewById(R.id.tv_summary_label);
        tvEmptyStateMessage = view.findViewById(R.id.tv_empty_state_message);

        dailyDayViews = new TextView[]{
                view.findViewById(R.id.daily_day_mon),
                view.findViewById(R.id.daily_day_tue),
                view.findViewById(R.id.daily_day_wed),
                view.findViewById(R.id.daily_day_thu),
                view.findViewById(R.id.daily_day_fri),
                view.findViewById(R.id.daily_day_sat)
        };

        tvDate.setText(new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
                .format(Calendar.getInstance().getTime()));

        // Shift the Mon–Sat day-selector row rightward by the label gutter width so
        // "Mon" sits directly above the first timeline column, not above the time labels.
        float density2 = getResources().getDisplayMetrics().density;
        int labelGutterPx = (int) (LABEL_WIDTH_DP * density2);
        ViewGroup.MarginLayoutParams daySelectorLp =
                (ViewGroup.MarginLayoutParams) dailyDaySelectorContainer.getLayoutParams();
        if (daySelectorLp != null) {
            daySelectorLp.leftMargin = labelGutterPx;
            dailyDaySelectorContainer.setLayoutParams(daySelectorLp);
        }

        view.findViewById(R.id.btn_add_schedule).setOnClickListener(v -> showScheduleFormDialog(null));
        view.findViewById(R.id.btn_empty_add_schedule).setOnClickListener(v -> showScheduleFormDialog(null));

        selectedDay = getDefaultDay();
        setupDailyDaySelector();
        applyDailyDaySelectionUi();

        loadRoomOptions();
        observeAllSchedules();
        viewModel.syncSchedules();
    }

    private void observeAllSchedules() {
        if (allSchedulesLiveData != null) {
            allSchedulesLiveData.removeObservers(getViewLifecycleOwner());
        }

        allSchedulesLiveData = viewModel.getAllSchedules();
        allSchedulesLiveData.observe(getViewLifecycleOwner(), schedules -> {
            allSchedules.clear();
            if (schedules != null) {
                allSchedules.addAll(schedules);
            }
            updateSummaryCard(allSchedules);
            renderScheduleContent();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.syncSchedules();

        int today = getDefaultDay();
        if (selectedDay != today) {
            selectedDay = today;
            applyDailyDaySelectionUi();
            renderScheduleContent();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        registerScheduleNetworkCallback();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterScheduleNetworkCallback();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Network callback
    // ══════════════════════════════════════════════════════════════════════════

    private void registerScheduleNetworkCallback() {
        if (!isAdded() || networkCallbackRegistered || Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }

        connectivityManager = (ConnectivityManager) requireContext()
                .getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;

        scheduleNetworkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                viewModel.syncSchedules();
            }
        };

        try {
            connectivityManager.registerDefaultNetworkCallback(scheduleNetworkCallback);
            networkCallbackRegistered = true;
        } catch (Exception ignored) {
            networkCallbackRegistered = false;
        }
    }

    private void unregisterScheduleNetworkCallback() {
        if (!networkCallbackRegistered || connectivityManager == null || scheduleNetworkCallback == null) return;
        try {
            connectivityManager.unregisterNetworkCallback(scheduleNetworkCallback);
        } catch (Exception ignored) {}
        networkCallbackRegistered = false;
        scheduleNetworkCallback = null;
        connectivityManager = null;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Day selector
    // ══════════════════════════════════════════════════════════════════════════

    private void setupDailyDaySelector() {
        for (TextView dayView : dailyDayViews) {
            dayView.setClickable(false);
            dayView.setFocusable(false);
            dayView.setEnabled(false);
        }
    }

    private void applyDailyDaySelectionUi() {
        for (int i = 0; i < dailyDayViews.length; i++) {
            TextView dayView = dailyDayViews[i];
            boolean isToday = (i + 1) == selectedDay;

            if (isToday) {
                dayView.setBackgroundResource(R.drawable.bg_schedule_day_selected);
                dayView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_primary));
            } else {
                dayView.setBackground(null);
                dayView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Schedule rendering — timeline view
    // ══════════════════════════════════════════════════════════════════════════

    private void renderScheduleContent() {
        filteredDailySchedules.clear();
        for (ScheduleEntryEntity entry : allSchedules) {
            if (entry.dayOfWeek == selectedDay) {
                filteredDailySchedules.add(entry);
            }
        }

        boolean hasSchedules    = !filteredDailySchedules.isEmpty();
        boolean hasAnySchedules = !allSchedules.isEmpty();

        dailyDaySelectorContainer.setVisibility(View.VISIBLE);
        timelineScrollView.setVisibility(hasSchedules ? View.VISIBLE : View.GONE);
        emptyState.setVisibility(hasSchedules ? View.GONE : View.VISIBLE);

        if (!hasSchedules) {
            tvEmptyStateMessage.setText(hasAnySchedules ? "No schedule found." : "No schedule yet.");
            return;
        }

        buildTimeline(filteredDailySchedules);
    }

    /**
     * Builds a weekly-style calendar timeline inside {@link #timelineContainer}.
     *
     * <p>Layout model:
     * <pre>
     *  |-- labelWidthPx --|-- col0 --|-- col1 --|-- col2 --|-- col3 --|-- col4 --|-- col5 --|
     *       time labels       Mon        Tue        Wed        Thu        Fri        Sat
     * </pre>
     * Each schedule block is placed horizontally within the column that matches
     * {@link #selectedDay} (1 = Mon … 6 = Sat).  Hour grid lines extend from the
     * right edge of the label gutter across the full six-column grid.
     */
    private void buildTimeline(List<ScheduleEntryEntity> entries) {
        if (!isAdded() || timelineContainer == null) return;
        timelineContainer.removeAllViews();

        // ── Determine visible time range ───────────────────────────────────────
        int minStart = Integer.MAX_VALUE;
        int maxEnd   = Integer.MIN_VALUE;
        for (ScheduleEntryEntity e : entries) {
            minStart = Math.min(minStart, e.startMinutes);
            maxEnd   = Math.max(maxEnd, e.endMinutes);
        }

        // Pad by one hour on each side, rounded to whole hours
        int startHour = Math.max(0,  (minStart / 60) - 1);
        int endHour   = Math.min(23, (maxEnd   / 60) + 2);
        int timelineStartMinutes = startHour * 60;

        float density      = requireContext().getResources().getDisplayMetrics().density;
        float pxPerMinute  = DP_PER_MINUTE * density;
        int   labelWidthPx = (int) (LABEL_WIDTH_DP * density);

        int totalMinutes  = (endHour - startHour + 1) * 60;
        int totalHeightPx = (int) (totalMinutes * pxPerMinute) + (int) (32 * density);

        // Enforce minimum height on the container so the ScrollView sizes correctly
        ViewGroup.LayoutParams lp = timelineContainer.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, totalHeightPx);
        } else {
            lp.height = totalHeightPx;
        }
        timelineContainer.setLayoutParams(lp);

        // ── Day-grid column geometry ───────────────────────────────────────────
        // Derive the usable grid width from the ScrollView's measured width (already
        // laid out at this point because renderScheduleContent() is called from
        // the LiveData observer which fires after the first layout pass).
        int scrollViewWidth = timelineScrollView.getWidth();
        if (scrollViewWidth <= 0) {
            // Fallback: use display width minus assumed 16 dp side padding × 2
            scrollViewWidth = (int) (requireContext().getResources()
                    .getDisplayMetrics().widthPixels - 32 * density);
        }
        int gridWidth      = scrollViewWidth - labelWidthPx; // width for all 6 columns
        int dayColumnWidth = gridWidth / DAY_COLUMN_COUNT;    // width of one day column

        // Horizontal inset inside each column so blocks have breathing room
        int colInset = (int) (3 * density);

        // Zero-based column index for the selected day (Mon=0 … Sat=5)
        int selectedColIndex = Math.max(0, Math.min(DAY_COLUMN_COUNT - 1, selectedDay - 1));

        // ── Colors ────────────────────────────────────────────────────────────
        int colorHint = ContextCompat.getColor(requireContext(), R.color.text_hint);
        // Grid lines and dividers are fully transparent — they visually disappear
        int colorInvisible = Color.TRANSPARENT;

        // Selected-day column highlight: use the fragment's background color so it
        // reads as a neutral "active column" rather than a colored overlay.
        // Falls back to a very subtle alpha-white/black if the resource is missing.
        int colHighlightColor;
        try {
            colHighlightColor = ContextCompat.getColor(requireContext(), R.color.background_default);
        } catch (Exception e) {
            boolean isDark = (getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
            colHighlightColor = isDark
                    ? Color.argb(30, 255, 255, 255)
                    : Color.argb(30, 0, 0, 0);
        }

        // ── Selected-day column highlight (drawn first so it sits behind everything) ──
        View colHighlight = new View(requireContext());
        FrameLayout.LayoutParams hlParams =
                new FrameLayout.LayoutParams(dayColumnWidth, totalHeightPx);
        hlParams.leftMargin = labelWidthPx + selectedColIndex * dayColumnWidth;
        colHighlight.setLayoutParams(hlParams);
        colHighlight.setBackgroundColor(colHighlightColor);
        timelineContainer.addView(colHighlight);

        // ── Hour labels + invisible grid lines ────────────────────────────────
        for (int hour = startHour; hour <= endHour; hour++) {
            int minutesFromStart = (hour - startHour) * 60;
            int topPx = (int) (minutesFromStart * pxPerMinute) + (int) (8 * density);

            // Time label — right-aligned strictly inside the label gutter.
            // Its width is capped to labelWidthPx so it never overlaps the day grid.
            TextView tvLabel = new TextView(requireContext());
            FrameLayout.LayoutParams labelParams =
                    new FrameLayout.LayoutParams(labelWidthPx, ViewGroup.LayoutParams.WRAP_CONTENT);
            labelParams.topMargin = topPx - (int) (7 * density);
            labelParams.leftMargin = 0; // anchored to the very left edge
            tvLabel.setLayoutParams(labelParams);
            tvLabel.setText(formatHour(hour));
            tvLabel.setTextSize(9f);
            tvLabel.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            // Right-pad so text doesn't touch the first column divider
            tvLabel.setPaddingRelative(0, 0, (int) (6 * density), 0);
            tvLabel.setTextColor(colorHint);
            timelineContainer.addView(tvLabel);

            // Grid line — transparent (invisible), kept for structural consistency
            View gridLine = new View(requireContext());
            FrameLayout.LayoutParams lineParams =
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            lineParams.topMargin  = topPx;
            lineParams.leftMargin = labelWidthPx;
            gridLine.setLayoutParams(lineParams);
            gridLine.setBackgroundColor(colorInvisible);
            timelineContainer.addView(gridLine);
        }

        // ── Vertical column dividers — transparent ────────────────────────────
        for (int col = 0; col <= DAY_COLUMN_COUNT; col++) {
            View divider = new View(requireContext());
            FrameLayout.LayoutParams divParams =
                    new FrameLayout.LayoutParams(1, totalHeightPx);
            divParams.leftMargin = labelWidthPx + col * dayColumnWidth;
            divider.setLayoutParams(divParams);
            divider.setBackgroundColor(colorInvisible);
            timelineContainer.addView(divider);
        }

        // ── Schedule blocks ────────────────────────────────────────────────────
        // All blocks for the selected day are placed inside the matching column.
        int blockLeft  = labelWidthPx + selectedColIndex * dayColumnWidth + colInset;
        int blockWidth = dayColumnWidth - colInset * 2;

        for (ScheduleEntryEntity entry : entries) {
            int topMinutes      = entry.startMinutes - timelineStartMinutes;
            int durationMinutes = entry.endMinutes - entry.startMinutes;

            int topPx    = (int) (topMinutes * pxPerMinute) + (int) (8 * density);
            int heightPx = Math.max(
                    (int) (durationMinutes * pxPerMinute),
                    (int) (MIN_BLOCK_HEIGHT_DP * density)
            );

            View block = buildScheduleBlock(entry, heightPx, density);

            FrameLayout.LayoutParams blockParams =
                    new FrameLayout.LayoutParams(blockWidth, heightPx);
            blockParams.topMargin  = topPx;
            blockParams.leftMargin = blockLeft;
            block.setLayoutParams(blockParams);

            timelineContainer.addView(block);
        }
    }

    /**
     * Inflates and populates a single calendar-style schedule block.
     * Background color comes from the entry's colorHex; text colors are
     * automatically adjusted for readability via luminance.
     */
    private View buildScheduleBlock(ScheduleEntryEntity entry, int heightPx, float density) {
        View block = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_schedule_timeline_block, timelineContainer, false);

        TextView tvStartTime = block.findViewById(R.id.tv_block_start_time);
        TextView tvEndTime   = block.findViewById(R.id.tv_block_end_time);
        TextView tvTitle     = block.findViewById(R.id.tv_block_title);
        TextView tvCode      = block.findViewById(R.id.tv_block_code);
        TextView tvLocation  = block.findViewById(R.id.tv_block_location);

        // ── Populate text ──────────────────────────────────────────────────────
        tvStartTime.setText(formatMinutes(entry.startMinutes));
        tvEndTime.setText(formatMinutes(entry.endMinutes));
        tvTitle.setText(entry.title != null && !entry.title.isBlank() ? entry.title : "Class");

        String code = (entry.courseCode != null && !entry.courseCode.isBlank()) ? entry.courseCode : "";
        tvCode.setText(code);
        tvCode.setVisibility(code.isEmpty() ? View.GONE : View.VISIBLE);

        String location;
        if (entry.isOnline == 1) {
            location = "Online" +
                    (entry.onlinePlatform != null && !entry.onlinePlatform.isBlank()
                            ? " • " + entry.onlinePlatform : "");
        } else if (entry.roomId != null && roomNameMap.containsKey(entry.roomId)) {
            location = roomNameMap.get(entry.roomId);
        } else {
            location = "";
        }
        tvLocation.setText(location);
        tvLocation.setVisibility(location.isEmpty() ? View.GONE : View.VISIBLE);

        // ── Background (rounded rect, user color) ──────────────────────────────
        int bgColor;
        try {
            bgColor = Color.parseColor(entry.colorHex != null ? entry.colorHex : "#F2F2F2");
        } catch (Exception e) {
            bgColor = Color.parseColor("#F2F2F2");
        }

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(10f * density);
        bg.setColor(bgColor);
        // Stroke: 80% alpha of a slightly darkened background color
        bg.setStroke(1, adjustColorAlpha(darkenColor(bgColor, 0.85f), 0.6f));
        block.setBackground(bg);

        // ── Text colors based on background luminance ──────────────────────────
        int primaryText   = getContrastingTextColor(bgColor);
        int secondaryText = getSecondaryTextColor(bgColor);

        tvStartTime.setTextColor(secondaryText);
        tvEndTime.setTextColor(secondaryText);
        tvTitle.setTextColor(primaryText);
        tvCode.setTextColor(secondaryText);
        tvLocation.setTextColor(secondaryText);

        // ── Compact mode: hide extras if block is too short ────────────────────
        boolean isCompact = heightPx < (int) (52 * density);
        if (isCompact) {
            tvEndTime.setVisibility(View.GONE);
            tvLocation.setVisibility(View.GONE);
        }

        boolean isTiny = heightPx < (int) (36 * density);
        if (isTiny) {
            tvCode.setVisibility(View.GONE);
            tvStartTime.setVisibility(View.GONE);
        }

        block.setOnClickListener(v -> showScheduleDetailDialog(entry));
        return block;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Summary card
    // ══════════════════════════════════════════════════════════════════════════

    private void updateSummaryCard(@Nullable List<ScheduleEntryEntity> schedules) {
        cardSummary.setVisibility(View.VISIBLE);

        if (schedules == null || schedules.isEmpty()) {
            tvSummaryLabel.setText("NO SCHEDULE YET");
            tvSummaryTitle.setText("GET STARTED");
            tvSummaryProfessor.setText("ADD YOUR FIRST CLASS TO BUILD YOUR SCHEDULE.");
            tvSummaryTime.setText("YOUR CLASSES WILL APPEAR HERE.");
            tvSummaryCode.setText("");
            resetSummaryCardColors();
            return;
        }

        ScheduleEntryEntity todayEntry = findTodaySummaryEntry(schedules);

        if (todayEntry == null) {
            tvSummaryLabel.setText("YOU HAVE NO CLASS SCHEDULE TODAY");
            tvSummaryTitle.setText("FREE TIME");
            tvSummaryProfessor.setText("USE THIS TIME TO REST, STUDY, OR EXPLORE.");
            tvSummaryTime.setText("YOUR NEXT SCHEDULE WILL APPEAR HERE.");
            tvSummaryCode.setText("");
            resetSummaryCardColors();
            return;
        }

        // ── Populate text ──────────────────────────────────────────────────────
        tvSummaryLabel.setText("YOU HAVE CLASS SCHEDULE TODAY");
        tvSummaryTitle.setText(todayEntry.title != null && !todayEntry.title.isBlank()
                ? todayEntry.title.toUpperCase(Locale.getDefault())
                : "CLASS");

        if (todayEntry.instructor != null && !todayEntry.instructor.isBlank()) {
            tvSummaryProfessor.setText(("PROF. " + todayEntry.instructor)
                    .toUpperCase(Locale.getDefault()));
        } else if (todayEntry.courseCode != null && !todayEntry.courseCode.isBlank()) {
            tvSummaryProfessor.setText(todayEntry.courseCode.toUpperCase(Locale.getDefault()));
        } else {
            tvSummaryProfessor.setText("TODAY'S CLASS");
        }

        tvSummaryTime.setText(
                (formatMinutes(todayEntry.startMinutes) + " - " + formatMinutes(todayEntry.endMinutes))
                        .toUpperCase(Locale.getDefault()));
        tvSummaryCode.setText("");

        // ── Dynamic card color from schedule colorHex ──────────────────────────
        try {
            int bgColor = Color.parseColor(
                    todayEntry.colorHex != null ? todayEntry.colorHex : "#F5F5F5");

            MaterialCardView card = (MaterialCardView) cardSummary;
            card.setCardBackgroundColor(bgColor);

            // Stroke: subtle version of the bg color
            card.setStrokeColor(adjustColorAlpha(darkenColor(bgColor, 0.80f), 0.5f));
            card.setStrokeWidth(1);

            // Text colors
            int primaryText   = getContrastingTextColor(bgColor);
            int secondaryText = getSecondaryTextColor(bgColor);
            tvSummaryTitle.setTextColor(primaryText);
            tvSummaryLabel.setTextColor(secondaryText);
            tvSummaryProfessor.setTextColor(secondaryText);
            tvSummaryTime.setTextColor(secondaryText);

        } catch (Exception e) {
            resetSummaryCardColors();
        }
    }

    /** Restores the summary card to its default theme colors. */
    private void resetSummaryCardColors() {
        MaterialCardView card = (MaterialCardView) cardSummary;
        card.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.background_subtle));
        card.setStrokeColor(
                ContextCompat.getColor(requireContext(), R.color.border_default));
        card.setStrokeWidth(1);

        tvSummaryTitle.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_primary));
        tvSummaryLabel.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_hint));
        tvSummaryProfessor.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_secondary));
        tvSummaryTime.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_hint));
    }

    @Nullable
    private ScheduleEntryEntity findTodaySummaryEntry(@Nullable List<ScheduleEntryEntity> schedules) {
        if (schedules == null || schedules.isEmpty()) return null;

        int today = getDefaultDay();
        Calendar now = Calendar.getInstance();
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

        ScheduleEntryEntity nextClass = null;
        ScheduleEntryEntity fallback  = null;

        for (ScheduleEntryEntity entry : schedules) {
            if (entry.dayOfWeek != today) continue;

            if (fallback == null || entry.startMinutes < fallback.startMinutes) {
                fallback = entry;
            }
            if (entry.startMinutes >= currentMinutes &&
                    (nextClass == null || entry.startMinutes < nextClass.startMinutes)) {
                nextClass = entry;
            }
        }

        return nextClass != null ? nextClass : fallback;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Room loading
    // ══════════════════════════════════════════════════════════════════════════

    private void loadRoomOptions() {
        viewModel.loadRooms(rooms -> {
            if (!isAdded()) return;

            requireActivity().runOnUiThread(() -> {
                allRooms.clear();
                allRooms.addAll(rooms);

                roomOptions.clear();
                roomNameMap.clear();

                for (RoomEntity room : allRooms) {
                    if (isCampusAreaCode(room.code)) continue;
                    roomOptions.add(room);
                    roomNameMap.put(room.id, buildRoomDisplay(room));
                }

                renderScheduleContent();
            });
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Schedule form dialog  (UNCHANGED from original)
    // ══════════════════════════════════════════════════════════════════════════

    private void showScheduleFormDialog(@Nullable ScheduleEntryEntity existing) {
        if (!isAdded()) return;

        View formView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_schedule_form, null, false);

        EditText etSubjectName = formView.findViewById(R.id.et_subject_name);
        EditText etSubjectCode = formView.findViewById(R.id.et_subject_code);
        EditText etProfessor   = formView.findViewById(R.id.et_professor);
        EditText etNotes       = formView.findViewById(R.id.et_notes);

        MaterialAutoCompleteTextView dropdownClassType = formView.findViewById(R.id.dropdown_class_type);
        MaterialAutoCompleteTextView dropdownPlatform  = formView.findViewById(R.id.dropdown_platform);
        MaterialAutoCompleteTextView dropdownReminder  = formView.findViewById(R.id.dropdown_reminder);

        EditText etRoomSearch            = formView.findViewById(R.id.et_room_search);
        ImageView btnClearRoomSearch     = formView.findViewById(R.id.btn_clear_room_search);
        RecyclerView recyclerRoomDropdown = formView.findViewById(R.id.recycler_room_dropdown);

        LinearLayout roomGroup        = formView.findViewById(R.id.room_group);
        LinearLayout onlineGroup      = formView.findViewById(R.id.online_group);
        LinearLayout scheduleContainer = formView.findViewById(R.id.schedule_container);
        ImageView btnAddScheduleBlock  = formView.findViewById(R.id.btn_add_schedule_block);

        TextView btnCancel = formView.findViewById(R.id.btn_sheet_cancel);
        TextView btnSave   = formView.findViewById(R.id.btn_sheet_save);

        dropdownClassType.setAdapter(new ArrayAdapter<>(
                requireContext(), R.layout.item_dropdown_simple, CLASS_TYPES));
        dropdownPlatform.setAdapter(new ArrayAdapter<>(
                requireContext(), R.layout.item_dropdown_simple, PLATFORMS));
        dropdownReminder.setAdapter(new ArrayAdapter<>(
                requireContext(), R.layout.item_dropdown_simple, REMINDER_OPTIONS));

        dropdownClassType.setOnItemClickListener((parent, view, position, id) -> {
            boolean online = position == 1;
            roomGroup.setVisibility(online ? View.GONE : View.VISIBLE);
            onlineGroup.setVisibility(online ? View.VISIBLE : View.GONE);
        });

        recyclerRoomDropdown.setLayoutManager(new LinearLayoutManager(requireContext()));
        RoomDropdownAdapter roomDropdownAdapter = new RoomDropdownAdapter(room -> {
            etRoomSearch.setText(buildRoomDisplay(room));
            etRoomSearch.setTag(room);
            recyclerRoomDropdown.setVisibility(View.GONE);
            btnClearRoomSearch.setVisibility(View.VISIBLE);
        });
        recyclerRoomDropdown.setAdapter(roomDropdownAdapter);
        roomDropdownAdapter.submitList(new ArrayList<>(roomOptions));

        etRoomSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !roomOptions.isEmpty())
                recyclerRoomDropdown.setVisibility(View.VISIBLE);
        });
        etRoomSearch.setOnClickListener(v -> {
            if (!roomOptions.isEmpty())
                recyclerRoomDropdown.setVisibility(View.VISIBLE);
        });
        etRoomSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s != null ? s.toString().trim().toLowerCase(Locale.ROOT) : "";
                btnClearRoomSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                List<RoomEntity> filtered = new ArrayList<>();
                for (RoomEntity room : roomOptions) {
                    String name = room.name != null ? room.name.toLowerCase(Locale.ROOT) : "";
                    String code = room.code != null ? room.code.toLowerCase(Locale.ROOT) : "";
                    if (query.isEmpty() || name.contains(query) || code.contains(query))
                        filtered.add(room);
                }
                roomDropdownAdapter.submitList(filtered);
                recyclerRoomDropdown.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        btnClearRoomSearch.setOnClickListener(v -> {
            etRoomSearch.setText("");
            etRoomSearch.setTag(null);
            btnClearRoomSearch.setVisibility(View.GONE);
            roomDropdownAdapter.submitList(new ArrayList<>(roomOptions));
            recyclerRoomDropdown.setVisibility(View.GONE);
        });

        int[] selectedColorIndex = {0};
        setupColorPicker(formView, selectedColorIndex, 0);

        scheduleBlocks.clear();
        scheduleContainer.removeAllViews();

        if (existing != null) {
            etSubjectName.setText(existing.title != null ? existing.title : "");
            etSubjectCode.setText(existing.courseCode != null ? existing.courseCode : "");
            etProfessor.setText(existing.instructor != null ? existing.instructor : "");
            etNotes.setText(existing.notes != null ? existing.notes : "");

            dropdownClassType.setText(existing.isOnline == 1 ? "Online" : "On-site", false);
            if (existing.isOnline == 1) {
                onlineGroup.setVisibility(View.VISIBLE);
                roomGroup.setVisibility(View.GONE);
                dropdownPlatform.setText(
                        existing.onlinePlatform != null ? existing.onlinePlatform : PLATFORMS[0], false);
            } else {
                onlineGroup.setVisibility(View.GONE);
                roomGroup.setVisibility(View.VISIBLE);
                if (existing.roomId != null) {
                    for (RoomEntity room : roomOptions) {
                        if (room.id == existing.roomId) {
                            etRoomSearch.setText(buildRoomDisplay(room));
                            etRoomSearch.setTag(room);
                            btnClearRoomSearch.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            }
            dropdownReminder.setText(
                    REMINDER_OPTIONS[reminderToPosition(existing.reminderMinutes)], false);
            if (existing.colorHex != null) {
                for (int i = 0; i < getScheduleColors().length; i++) {
                    if (getScheduleColors()[i].equalsIgnoreCase(existing.colorHex)) {
                        selectedColorIndex[0] = i;
                        break;
                    }
                }
            }
            setupColorPicker(formView, selectedColorIndex, selectedColorIndex[0]);
            addScheduleBlock(scheduleContainer, existing.dayOfWeek,
                    existing.startMinutes, existing.endMinutes, false);
        } else {
            dropdownClassType.setText(CLASS_TYPES[0], false);
            dropdownReminder.setText(
                    REMINDER_OPTIONS[reminderToPosition(
                            sessionManager.getDefaultReminderMinutes())], false);
            roomGroup.setVisibility(View.VISIBLE);
            onlineGroup.setVisibility(View.GONE);
            addScheduleBlock(scheduleContainer, selectedDay, -1, -1, false);
        }

        btnAddScheduleBlock.setOnClickListener(v ->
                addScheduleBlock(scheduleContainer, getDefaultDay(), -1, -1, true));

        BottomSheetDialog dialog = new BottomSheetDialog(
                requireContext(), R.style.ThemeOverlay_FreshGuide_BottomSheet);
        dialog.setContentView(formView);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            try {
                String title = etSubjectName.getText().toString().trim();
                if (title.isEmpty()) {
                    etSubjectName.setError("Subject name is required");
                    return;
                }

                boolean online = "Online".contentEquals(dropdownClassType.getText());
                Integer roomId = null;
                String platform = null;

                if (online) {
                    platform = dropdownPlatform.getText() != null
                            ? dropdownPlatform.getText().toString().trim() : null;
                    if (platform == null || platform.isEmpty()) {
                        Toast.makeText(requireContext(), "Please choose an online platform", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Object selectedRoom = etRoomSearch.getTag();
                    if (!(selectedRoom instanceof RoomEntity)) {
                        Toast.makeText(requireContext(), "Please choose a room location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    roomId = ((RoomEntity) selectedRoom).id;
                }

                int reminderPosition = 0;
                String reminderValue = dropdownReminder.getText() != null
                        ? dropdownReminder.getText().toString() : REMINDER_OPTIONS[0];
                for (int i = 0; i < REMINDER_OPTIONS.length; i++) {
                    if (REMINDER_OPTIONS[i].equalsIgnoreCase(reminderValue)) {
                        reminderPosition = i;
                        break;
                    }
                }

                int reminderMinutes = positionToReminder(reminderPosition);
                if (!sessionManager.isScheduleNotificationsEnabled()) reminderMinutes = 0;

                int colorIndex = selectedColorIndex[0];
                if (colorIndex < 0 || colorIndex >= getScheduleColors().length) colorIndex = 0;

                if (scheduleBlocks.isEmpty()) {
                    Toast.makeText(requireContext(), "Please add a schedule block", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (ScheduleBlockBinding block : scheduleBlocks) {
                    if (block.selectedDay < 1 || block.selectedDay > 6) {
                        Toast.makeText(requireContext(), "Please choose a schedule day", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (block.startMinutes < 0 || block.endMinutes < 0) {
                        Toast.makeText(requireContext(), "Please select start and end time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (block.endMinutes <= block.startMinutes) {
                        Toast.makeText(requireContext(), "End time must be later than start time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                long now = System.currentTimeMillis();
                int totalToSave = scheduleBlocks.size();
                int[] savedCount = {0};
                boolean[] errorShown = {false};

                for (int i = 0; i < scheduleBlocks.size(); i++) {
                    ScheduleBlockBinding block = scheduleBlocks.get(i);
                    ScheduleEntryEntity entry = new ScheduleEntryEntity(
                            title,
                            normalize(etSubjectCode.getText().toString()),
                            normalize(etProfessor.getText().toString()),
                            normalize(etNotes.getText().toString()),
                            getScheduleColors()[colorIndex],
                            block.selectedDay,
                            block.startMinutes,
                            block.endMinutes,
                            online ? 1 : 0,
                            roomId,
                            platform,
                            reminderMinutes,
                            (existing != null && i == 0) ? existing.createdAt : now,
                            now
                    );
                    if (existing != null && i == 0) {
                        entry.id             = existing.id;
                        entry.remoteId       = existing.remoteId;
                        entry.clientUuid     = existing.clientUuid;
                        entry.ownerStudentId = existing.ownerStudentId;
                        entry.syncState      = existing.syncState;
                        entry.pendingDelete  = existing.pendingDelete;
                    }

                    maybeRequestNotificationPermission(entry.reminderMinutes);

                    final int finalColorIndex = colorIndex;
                    viewModel.saveSchedule(entry, new ScheduleViewModel.OperationCallback() {
                        @Override
                        public void onSuccess(ScheduleEntryEntity savedEntry) {
                            if (!isAdded() || getActivity() == null) return;
                            getActivity().runOnUiThread(() -> {
                                savedCount[0]++;
                                if (savedCount[0] == totalToSave && !errorShown[0]) {
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(),
                                            totalToSave > 1 ? "Schedules saved" : "Schedule saved",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onError(String message) {
                            Log.e(TAG, "saveSchedule failed: " + message);
                            if (!isAdded() || getActivity() == null || errorShown[0]) return;
                            errorShown[0] = true;
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Create/Save schedule flow crashed", e);
                if (isAdded())
                    Toast.makeText(requireContext(), "Unable to save schedule right now", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setOnShowListener(d -> {
            View bottomSheet = dialog.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                bottomSheet.setLayoutParams(params);
            }
        });

        dialog.show();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Schedule block form helpers  (UNCHANGED from original)
    // ══════════════════════════════════════════════════════════════════════════

    private void addScheduleBlock(LinearLayout container,
                                  int initialDay,
                                  int initialStartMinutes,
                                  int initialEndMinutes,
                                  boolean removable) {
        View blockView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_schedule_block, container, false);

        ScheduleBlockBinding binding = new ScheduleBlockBinding();
        binding.root         = blockView;
        binding.label        = blockView.findViewById(R.id.tv_schedule_label);
        binding.delete       = blockView.findViewById(R.id.btn_delete_schedule);
        binding.dayContainer = blockView.findViewById(R.id.day_container);
        binding.btnStart     = blockView.findViewById(R.id.btn_start);
        binding.btnEnd       = blockView.findViewById(R.id.btn_end);
        binding.selectedDay  = initialDay;
        binding.startMinutes = initialStartMinutes;
        binding.endMinutes   = initialEndMinutes;

        setupDayChips(binding);

        if (initialStartMinutes >= 0) {
            binding.btnStart.setText(formatMinutes(initialStartMinutes));
            binding.btnStart.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.schedule_time_button));
        }
        if (initialEndMinutes >= 0) {
            binding.btnEnd.setText(formatMinutes(initialEndMinutes));
            binding.btnEnd.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.schedule_time_button));
        }

        binding.btnStart.setOnClickListener(v -> showTimePickerForBlock(binding, true));
        binding.btnEnd.setOnClickListener(v -> showTimePickerForBlock(binding, false));

        binding.delete.setVisibility(removable ? View.VISIBLE : View.GONE);
        binding.delete.setOnClickListener(v -> {
            container.removeView(binding.root);
            scheduleBlocks.remove(binding);
            refreshScheduleLabels();
        });

        scheduleBlocks.add(binding);
        container.addView(blockView);
        refreshScheduleLabels();
    }

    private void refreshScheduleLabels() {
        for (int i = 0; i < scheduleBlocks.size(); i++) {
            scheduleBlocks.get(i).label.setText("Schedule " + (i + 1));
            scheduleBlocks.get(i).delete.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
        }
    }

    private void setupDayChips(ScheduleBlockBinding binding) {
        binding.dayContainer.removeAllViews();
        binding.dayChips.clear();

        Typeface interMedium = ResourcesCompat.getFont(requireContext(), R.font.inter_medium);

        for (int i = 0; i < DAY_LABELS.length; i++) {
            int dayValue = i + 1;

            FrameLayout slot = new FrameLayout(requireContext());
            LinearLayout.LayoutParams slotParams = new LinearLayout.LayoutParams(0, dpToPx(40));
            slotParams.weight = 1f;
            slot.setLayoutParams(slotParams);

            TextView chip = new TextView(requireContext());
            FrameLayout.LayoutParams chipParams = new FrameLayout.LayoutParams(dpToPx(44), dpToPx(32));
            chipParams.gravity = Gravity.CENTER;
            chip.setLayoutParams(chipParams);
            chip.setText(DAY_LABELS[i]);
            chip.setGravity(Gravity.CENTER);
            chip.setTextSize(12);
            chip.setTypeface(interMedium);

            boolean selected = dayValue == binding.selectedDay;
            chip.setBackgroundResource(selected
                    ? R.drawable.bg_form_chip_selected
                    : R.drawable.bg_form_chip_unselected);
            chip.setTextColor(ContextCompat.getColor(requireContext(),
                    selected ? android.R.color.white : R.color.green_primary));
            chip.setScaleX(selected ? 1.05f : 1f);
            chip.setScaleY(selected ? 1.05f : 1f);

            chip.setOnClickListener(v -> {
                binding.selectedDay = dayValue;
                updateBlockDaySelection(binding);
            });

            slot.addView(chip);
            binding.dayContainer.addView(slot);
            binding.dayChips.add(chip);
        }
    }

    private void updateBlockDaySelection(ScheduleBlockBinding binding) {
        for (int i = 0; i < binding.dayChips.size(); i++) {
            TextView chip = binding.dayChips.get(i);
            boolean selected = (i + 1) == binding.selectedDay;
            chip.setBackgroundResource(selected
                    ? R.drawable.bg_form_chip_selected
                    : R.drawable.bg_form_chip_unselected);
            chip.setTextColor(ContextCompat.getColor(requireContext(),
                    selected ? android.R.color.white : R.color.green_primary));
            chip.animate()
                    .scaleX(selected ? 1.05f : 1f)
                    .scaleY(selected ? 1.05f : 1f)
                    .setDuration(120)
                    .start();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Time picker dialog  (UNCHANGED from original)
    // ══════════════════════════════════════════════════════════════════════════

    private void showTimePickerForBlock(ScheduleBlockBinding binding, boolean isStart) {
        if (!isAdded()) return;

        final int minTotalMinutes = (!isStart && binding.startMinutes >= 0)
                ? binding.startMinutes : -1;

        int savedValue = isStart ? binding.startMinutes : binding.endMinutes;
        Calendar now = Calendar.getInstance();
        int nowTotal  = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

        int initialTotalMinutes;
        if (savedValue >= 0) {
            initialTotalMinutes = savedValue;
        } else if (minTotalMinutes >= 0 && nowTotal <= minTotalMinutes) {
            initialTotalMinutes = Math.min(minTotalMinutes + 1, 24 * 60 - 1);
        } else {
            initialTotalMinutes = nowTotal;
        }

        int initialHour24 = initialTotalMinutes / 60;
        int initialMinute = initialTotalMinutes % 60;
        int initialAmPm   = initialHour24 >= 12 ? 1 : 0;
        int initialHour12 = initialHour24 % 12;
        if (initialHour12 == 0) initialHour12 = 12;

        View pickerView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_time_picker_custom, null, false);

        RecyclerView rvHour   = pickerView.findViewById(R.id.rv_hour);
        RecyclerView rvMinute = pickerView.findViewById(R.id.rv_minute);
        TextView btnAm        = pickerView.findViewById(R.id.btn_am);
        TextView btnPm        = pickerView.findViewById(R.id.btn_pm);
        TextView btnConfirm   = pickerView.findViewById(R.id.btn_time_confirm);

        final int[] selectedAmPm = {initialAmPm};

        List<String> hourItems = Arrays.asList(
                "01","02","03","04","05","06","07","08","09","10","11","12");
        List<String> minuteItems = new ArrayList<>();
        for (int i = 0; i < 60; i++)
            minuteItems.add(String.format(Locale.getDefault(), "%02d", i));

        TimeWheelAdapter hourAdapter   = new TimeWheelAdapter(hourItems);
        TimeWheelAdapter minuteAdapter = new TimeWheelAdapter(minuteItems);

        LinearSnapHelper hourSnapHelper   = setupWheelRecycler(rvHour,   hourAdapter,   initialHour12 - 1);
        LinearSnapHelper minuteSnapHelper = setupWheelRecycler(rvMinute, minuteAdapter, initialMinute);

        updateAmPmButtons(btnAm, btnPm, selectedAmPm[0]);
        btnAm.setOnClickListener(v -> { selectedAmPm[0] = 0; updateAmPmButtons(btnAm, btnPm, 0); });
        btnPm.setOnClickListener(v -> { selectedAmPm[0] = 1; updateAmPmButtons(btnAm, btnPm, 1); });

        final int[] snappedHourIdx   = {initialHour12 - 1};
        final int[] snappedMinuteIdx = {initialMinute};

        if (minTotalMinutes >= 0) {
            RecyclerView.OnScrollListener correctionListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                    if (newState != RecyclerView.SCROLL_STATE_IDLE) return;
                    rv.post(() -> {
                        snappedHourIdx[0]   = getSnappedAdapterPosition(rvHour,   hourSnapHelper);
                        snappedMinuteIdx[0] = getSnappedAdapterPosition(rvMinute, minuteSnapHelper);

                        int hour12   = snappedHourIdx[0] + 1;
                        int hour24   = (selectedAmPm[0] == 0)
                                ? (hour12 == 12 ? 0  : hour12)
                                : (hour12 == 12 ? 12 : hour12 + 12);
                        int combined = hour24 * 60 + snappedMinuteIdx[0];
                        if (combined > minTotalMinutes) return;

                        int minHour24 = minTotalMinutes / 60;
                        if (hour24 == minHour24) {
                            int correctedMinute = (minTotalMinutes % 60) + 1;
                            if (correctedMinute < 60) {
                                LinearLayoutManager lm =
                                        (LinearLayoutManager) rvMinute.getLayoutManager();
                                if (lm != null) {
                                    lm.scrollToPositionWithOffset(correctedMinute, 0);
                                    minuteAdapter.setSelectedPosition(correctedMinute);
                                    snappedMinuteIdx[0] = correctedMinute;
                                }
                            }
                        }
                    });
                }
            };
            rvHour.addOnScrollListener(correctionListener);
            rvMinute.addOnScrollListener(correctionListener);
        }

        BottomSheetDialog dialog = new BottomSheetDialog(
                requireContext(), R.style.CustomBottomSheetDialog);
        dialog.setContentView(pickerView);
        dialog.setCancelable(true);

        dialog.setOnShowListener(d -> {
            FrameLayout bottomSheet = dialog.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet == null) return;
            bottomSheet.setBackgroundColor(Color.TRANSPARENT);
            int margin = dpToPx(10);
            CoordinatorLayout.LayoutParams lp =
                    (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            lp.setMargins(margin, 0, margin, margin);
            bottomSheet.setLayoutParams(lp);
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
            behavior.setDraggable(true);
        });

        btnConfirm.setOnClickListener(v -> {
            int selectedHourIndex   = getSnappedAdapterPosition(rvHour,   hourSnapHelper);
            int selectedMinuteIndex = getSnappedAdapterPosition(rvMinute, minuteSnapHelper);
            int selectedHour12      = selectedHourIndex + 1;
            int hour24 = (selectedAmPm[0] == 0)
                    ? (selectedHour12 == 12 ? 0  : selectedHour12)
                    : (selectedHour12 == 12 ? 12 : selectedHour12 + 12);
            int totalMinutes = hour24 * 60 + selectedMinuteIndex;

            if (minTotalMinutes >= 0 && totalMinutes <= minTotalMinutes) {
                Toast.makeText(requireContext(),
                        "End time must be after " + formatMinutes(minTotalMinutes),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (isStart) {
                binding.startMinutes = totalMinutes;
                binding.btnStart.setText(formatMinutes(totalMinutes));
                binding.btnStart.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.schedule_time_button));
            } else {
                binding.endMinutes = totalMinutes;
                binding.btnEnd.setText(formatMinutes(totalMinutes));
                binding.btnEnd.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.schedule_time_button));
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateAmPmButtons(TextView btnAm, TextView btnPm, int selectedAmPm) {
        boolean isAmSelected = selectedAmPm == 0;
        btnAm.setBackgroundResource(isAmSelected
                ? R.drawable.bg_form_chip_selected : R.drawable.bg_form_chip_unselected);
        btnPm.setBackgroundResource(!isAmSelected
                ? R.drawable.bg_form_chip_selected : R.drawable.bg_form_chip_unselected);
        btnAm.setTextColor(ContextCompat.getColor(requireContext(),
                isAmSelected ? android.R.color.white : R.color.green_primary));
        btnPm.setTextColor(ContextCompat.getColor(requireContext(),
                !isAmSelected ? android.R.color.white : R.color.green_primary));
    }

    private LinearSnapHelper setupWheelRecycler(RecyclerView recyclerView,
                                                TimeWheelAdapter adapter,
                                                int initialPosition) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
        recyclerView.setNestedScrollingEnabled(false);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.post(() -> {
            layoutManager.scrollToPositionWithOffset(initialPosition, 0);
            recyclerView.post(() -> {
                int snapped = getSnappedAdapterPosition(recyclerView, snapHelper);
                adapter.setSelectedPosition(
                        snapped != RecyclerView.NO_POSITION ? snapped : initialPosition);
            });
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv.post(() -> adapter.setSelectedPosition(
                            getSnappedAdapterPosition(rv, snapHelper)));
                }
            }
        });

        return snapHelper;
    }

    private int getSnappedAdapterPosition(RecyclerView recyclerView, LinearSnapHelper snapHelper) {
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm == null) return 0;
        View snapView = snapHelper.findSnapView(lm);
        if (snapView == null) return 0;
        int pos = lm.getPosition(snapView);
        return pos == RecyclerView.NO_POSITION ? 0 : pos;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Schedule detail dialog  (UNCHANGED from original)
    // ══════════════════════════════════════════════════════════════════════════

    private void showScheduleDetailDialog(ScheduleEntryEntity entry) {
        if (!isAdded()) return;

        View detailView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_schedule_detail, null, false);

        TextView tvCode      = detailView.findViewById(R.id.tv_detail_code);
        TextView tvTitle     = detailView.findViewById(R.id.tv_detail_title);
        TextView tvProfessor = detailView.findViewById(R.id.tv_detail_professor);
        TextView tvSchedule  = detailView.findViewById(R.id.tv_detail_schedule);
        TextView tvLocation  = detailView.findViewById(R.id.tv_detail_location);
        TextView tvReminder  = detailView.findViewById(R.id.tv_detail_reminder);
        TextView tvNotes     = detailView.findViewById(R.id.tv_detail_notes);

        tvCode.setText(entry.courseCode != null && !entry.courseCode.isBlank()
                ? entry.courseCode : "No code");
        tvTitle.setText(entry.title != null ? entry.title : "Class");
        tvProfessor.setText(entry.instructor != null && !entry.instructor.isBlank()
                ? "Prof. " + entry.instructor : "Professor not set");
        tvSchedule.setText(dayToLabel(entry.dayOfWeek) + " • " +
                formatMinutes(entry.startMinutes) + " - " + formatMinutes(entry.endMinutes));

        if (entry.isOnline == 1) {
            tvLocation.setText("Location: Online" +
                    (entry.onlinePlatform != null ? " • " + entry.onlinePlatform : ""));
        } else {
            tvLocation.setText("Location: " + resolveLocation(entry));
        }

        tvReminder.setText(entry.reminderMinutes > 0
                ? "Reminder: " + entry.reminderMinutes + " minutes before"
                : "Reminder: Off");
        tvNotes.setText(entry.notes != null && !entry.notes.isBlank() ? entry.notes : "No notes");

        AlertDialog detailDialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(detailView).create();

        TextView btnDelete     = detailView.findViewById(R.id.btn_detail_delete);
        TextView btnEdit       = detailView.findViewById(R.id.btn_detail_edit);
        TextView btnDirections = detailView.findViewById(R.id.btn_detail_directions);

        if (entry.isOnline == 1 || entry.roomId == null) {
            btnDirections.setVisibility(View.GONE);
        }

        btnEdit.setOnClickListener(v -> {
            detailDialog.dismiss();
            showScheduleFormDialog(entry);
        });

        btnDelete.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete schedule")
                        .setMessage("Remove this class from your schedule?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialog, which) ->
                                viewModel.deleteSchedule(entry, new ScheduleViewModel.SimpleCallback() {
                                    @Override
                                    public void onSuccess() {
                                        if (!isAdded()) return;
                                        requireActivity().runOnUiThread(() -> {
                                            detailDialog.dismiss();
                                            Toast.makeText(requireContext(),
                                                    "Schedule deleted", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                    @Override
                                    public void onError(String message) {
                                        if (!isAdded()) return;
                                        requireActivity().runOnUiThread(() ->
                                                Toast.makeText(requireContext(),
                                                        message, Toast.LENGTH_SHORT).show());
                                    }
                                }))
                        .show());

        btnDirections.setOnClickListener(v -> {
            if (entry.roomId == null) return;
            NavController navController = Navigation.findNavController(requireView());
            Bundle args = new Bundle();
            args.putInt("roomId", entry.roomId);
            args.putString("roomName", roomNameMap.get(entry.roomId));
            navController.navigate(R.id.action_schedule_to_roomDetail, args);
            detailDialog.dismiss();
        });

        detailDialog.show();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Color picker  (UNCHANGED from original)
    // ══════════════════════════════════════════════════════════════════════════

    private void setupColorPicker(View formView, int[] selectedColorIndex, int currentSelected) {
        int[] colorViewIds = {
                R.id.color_0, R.id.color_1, R.id.color_2,
                R.id.color_3, R.id.color_4, R.id.color_5
        };
        for (int i = 0; i < colorViewIds.length; i++) {
            int index = i;
            formView.findViewById(colorViewIds[i]).setOnClickListener(v -> {
                selectedColorIndex[0] = index;
                applyColorSelection(formView, selectedColorIndex[0]);
            });
        }
        selectedColorIndex[0] = currentSelected;
        applyColorSelection(formView, currentSelected);
    }

    private void applyColorSelection(View formView, int selectedIndex) {
        int[] colorViewIds = {
                R.id.color_0, R.id.color_1, R.id.color_2,
                R.id.color_3, R.id.color_4, R.id.color_5
        };
        for (int i = 0; i < colorViewIds.length; i++) {
            View colorView = formView.findViewById(colorViewIds[i]);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(Color.parseColor(getScheduleColors()[i]));
            drawable.setStroke(1, Color.parseColor(i == selectedIndex ? "#BDBDBD" : "#D0D0D0"));
            colorView.setBackground(drawable);
            float targetScale = i == selectedIndex ? 1.18f : 1f;
            colorView.animate()
                    .scaleX(targetScale).scaleY(targetScale)
                    .setDuration(180)
                    .setInterpolator(new OvershootInterpolator(0.7f))
                    .start();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Helper utilities
    // ══════════════════════════════════════════════════════════════════════════

    private void maybeRequestNotificationPermission(int reminderMinutes) {
        if (reminderMinutes <= 0) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return;
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) return;
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private int getDefaultDay() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:    return 1;
            case Calendar.TUESDAY:   return 2;
            case Calendar.WEDNESDAY: return 3;
            case Calendar.THURSDAY:  return 4;
            case Calendar.FRIDAY:    return 5;
            case Calendar.SATURDAY:  return 6;
            default:                 return 1;
        }
    }

    private String buildRoomDisplay(RoomEntity room) {
        String name = room.name != null ? room.name : "Room";
        if (room.code != null && !room.code.isBlank())
            return name + " (" + room.code + ")";
        return name;
    }

    private String resolveLocation(ScheduleEntryEntity entry) {
        if (entry.roomId != null && roomNameMap.containsKey(entry.roomId))
            return roomNameMap.get(entry.roomId);
        return "Room not set";
    }

    private boolean isCampusAreaCode(String code) {
        if (code == null) return false;
        String n = code.trim().toUpperCase(Locale.ROOT);
        return "COURT".equals(n) || "ENT".equals(n) || "EXIT".equals(n);
    }

    private int positionToReminder(int position) {
        switch (position) {
            case 1: return 5;
            case 2: return 10;
            case 3: return 15;
            case 4: return 30;
            default: return 0;
        }
    }

    private int reminderToPosition(int reminderMinutes) {
        switch (reminderMinutes) {
            case 5:  return 1;
            case 10: return 2;
            case 15: return 3;
            case 30: return 4;
            default: return 0;
        }
    }

    private String dayToLabel(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > DAY_LABELS.length) return "Mon";
        return DAY_LABELS[dayOfWeek - 1];
    }

    private String normalize(String value) {
        if (value == null) return null;
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private String formatMinutes(int minutes) {
        int hour24 = Math.max(0, Math.min(23, minutes / 60));
        int minute = Math.max(0, Math.min(59, minutes % 60));
        int hour12 = hour24 % 12;
        if (hour12 == 0) hour12 = 12;
        return String.format(Locale.getDefault(), "%d:%02d %s",
                hour12, minute, hour24 >= 12 ? "PM" : "AM");
    }

    /** Formats an hour integer (0–23) as a readable 12-hour label, e.g. "9 AM", "12 PM". */
    private String formatHour(int hour) {
        if (hour == 0 || hour == 24) return "12 AM";
        if (hour == 12)              return "12 PM";
        if (hour > 12)               return (hour - 12) + " PM";
        return hour + " AM";
    }

    private String[] getScheduleColors() {
        boolean isDark = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        if (isDark) {
            return new String[]{"#3A3A3A","#4D2A3A","#2A3D4D","#3A2D4D","#2D3A2D","#4D4A2A"};
        } else {
            return new String[]{"#F2F2F2","#F8D1E2","#D7E8FF","#EFD8F7","#D7F1D5","#EDF58F"};
        }
    }

    // ── Color math helpers ─────────────────────────────────────────────────────

    /**
     * Returns dark text for light backgrounds, light text for dark backgrounds,
     * using the WCAG relative-luminance formula.
     */
    private int getContrastingTextColor(int bgColor) {
        double lum = (0.299 * Color.red(bgColor)
                + 0.587 * Color.green(bgColor)
                + 0.114 * Color.blue(bgColor)) / 255.0;
        return lum > 0.5 ? Color.parseColor("#1C1B1F") : Color.parseColor("#E6E6E6");
    }

    /** Secondary (slightly less prominent) text that still contrasts with the background. */
    private int getSecondaryTextColor(int bgColor) {
        double lum = (0.299 * Color.red(bgColor)
                + 0.587 * Color.green(bgColor)
                + 0.114 * Color.blue(bgColor)) / 255.0;
        return lum > 0.5 ? Color.parseColor("#5E5E5E") : Color.parseColor("#B8B8B8");
    }

    /** Multiplies alpha channel by {@code factor} (0–1). */
    private int adjustColorAlpha(int color, float factor) {
        int alpha = Math.min(255, Math.round(Color.alpha(color) * factor));
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Darkens a color by multiplying its RGB channels by {@code factor} (0–1).
     * Alpha is preserved. Used to derive subtle stroke colors.
     */
    private int darkenColor(int color, float factor) {
        return Color.argb(
                Color.alpha(color),
                Math.round(Color.red(color)   * factor),
                Math.round(Color.green(color) * factor),
                Math.round(Color.blue(color)  * factor)
        );
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Inner class — form schedule-block binding
    // ══════════════════════════════════════════════════════════════════════════

    private static class ScheduleBlockBinding {
        View root;
        TextView label;
        ImageView delete;
        LinearLayout dayContainer;
        TextView btnStart;
        TextView btnEnd;

        final List<TextView> dayChips = new ArrayList<>();

        int selectedDay  = 1;
        int startMinutes = -1;
        int endMinutes   = -1;
    }
}