package com.example.freshguide.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.freshguide.R;
import com.example.freshguide.model.entity.ScheduleEntryEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DayTimelineLayout extends FrameLayout {

    public interface OnEntryClickListener {
        void onEntryClick(ScheduleEntryEntity entry);
    }

    private static final int START_MINUTES = 8 * 60;
    private static final int END_MINUTES = 22 * 60;
    private static final int HOUR_HEIGHT_DP = 88;
    private static final int TIME_COLUMN_DP = 56;
    private static final int BLOCK_MIN_HEIGHT_DP = 72;
    private static final int BLOCK_MARGIN_DP = 8;

    private final int hourHeightPx;
    private final int timeColumnPx;
    private final int blockMinHeightPx;
    private final int blockMarginPx;

    @Nullable
    private final Typeface interMedium;
    @Nullable
    private final Typeface interBold;

    @Nullable
    private OnEntryClickListener listener;

    public DayTimelineLayout(@NonNull Context context) {
        this(context, null);
    }

    public DayTimelineLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        hourHeightPx = dpToPx(HOUR_HEIGHT_DP);
        timeColumnPx = dpToPx(TIME_COLUMN_DP);
        blockMinHeightPx = dpToPx(BLOCK_MIN_HEIGHT_DP);
        blockMarginPx = dpToPx(BLOCK_MARGIN_DP);

        interMedium = ResourcesCompat.getFont(context, R.font.inter_medium);
        interBold = ResourcesCompat.getFont(context, R.font.inter_bold);
    }

    public void setOnEntryClickListener(@Nullable OnEntryClickListener listener) {
        this.listener = listener;
    }

    public void setSchedules(@Nullable List<ScheduleEntryEntity> entries,
                             @Nullable Map<Integer, String> roomNameMap) {
        removeAllViews();

        List<ScheduleEntryEntity> safeEntries = new ArrayList<>();
        if (entries != null) {
            safeEntries.addAll(entries);
        }

        Collections.sort(safeEntries, Comparator.comparingInt(o -> o.startMinutes));

        int timelineHeight = ((END_MINUTES - START_MINUTES) / 60) * hourHeightPx;
        LayoutParams rootParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                timelineHeight + dpToPx(16)
        );
        setLayoutParams(rootParams);

        addHourGuides();
        addBlocks(safeEntries, roomNameMap);
    }

    private void addHourGuides() {
        Context context = getContext();

        for (int minutes = START_MINUTES; minutes <= END_MINUTES; minutes += 60) {
            int top = ((minutes - START_MINUTES) / 60) * hourHeightPx;

            TextView timeLabel = new TextView(context);
            LayoutParams labelParams = new LayoutParams(
                    timeColumnPx - dpToPx(10),
                    LayoutParams.WRAP_CONTENT
            );
            labelParams.gravity = Gravity.TOP | Gravity.START;
            labelParams.topMargin = top - dpToPx(8);
            timeLabel.setLayoutParams(labelParams);
            timeLabel.setText(formatHour(minutes));
            timeLabel.setTextColor(ContextCompat.getColor(context, R.color.schedule_time_text));
            timeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            timeLabel.setGravity(Gravity.END);
            if (interMedium != null) {
                timeLabel.setTypeface(interMedium);
            }

            View line = new View(context);
            LayoutParams lineParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    dpToPx(1)
            );
            lineParams.topMargin = top;
            lineParams.leftMargin = timeColumnPx;
            line.setLayoutParams(lineParams);
            line.setBackgroundColor(ContextCompat.getColor(context, R.color.schedule_grid_minor));

            addView(timeLabel);
            addView(line);
        }
    }

    private void addBlocks(List<ScheduleEntryEntity> entries, @Nullable Map<Integer, String> roomNameMap) {
        List<List<ScheduleEntryEntity>> columns = buildColumns(entries);
        int totalColumns = Math.max(1, columns.size());

        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            List<ScheduleEntryEntity> columnEntries = columns.get(columnIndex);

            for (ScheduleEntryEntity entry : columnEntries) {
                View block = createScheduleBlock(entry, roomNameMap);

                int top = minutesToTop(entry.startMinutes);
                int height = Math.max(blockMinHeightPx, durationToHeight(entry.startMinutes, entry.endMinutes));

                LayoutParams params = new LayoutParams(
                        calculateBlockWidth(totalColumns),
                        height
                );
                params.topMargin = top;
                params.leftMargin = timeColumnPx + blockMarginPx +
                        (columnIndex * (calculateBlockWidth(totalColumns) + blockMarginPx));

                block.setLayoutParams(params);
                block.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEntryClick(entry);
                    }
                });

                addView(block);
            }
        }
    }

    private int calculateBlockWidth(int totalColumns) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int sidePadding = dpToPx(40);
        int usable = screenWidth - sidePadding - timeColumnPx - ((totalColumns + 1) * blockMarginPx);
        if (totalColumns <= 1) {
            return Math.max(dpToPx(180), usable);
        }
        return Math.max(dpToPx(120), usable / totalColumns);
    }

    private View createScheduleBlock(ScheduleEntryEntity entry, @Nullable Map<Integer, String> roomNameMap) {
        Context context = getContext();

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
        root.setClickable(true);
        root.setFocusable(true);

        int backgroundColor = parseColorOrDefault(entry.colorHex, "#F8D1E2");
        int textColor = getContrastingTextColor(backgroundColor);
        int secondaryTextColor = getSecondaryTextColor(backgroundColor);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dpToPx(10));
        bg.setColor(backgroundColor);
        bg.setStroke(dpToPx(1), adjustStrokeColor(backgroundColor));
        root.setBackground(bg);

        TextView tvStart = new TextView(context);
        tvStart.setText(formatMinutes(entry.startMinutes));
        tvStart.setTextColor(secondaryTextColor);
        tvStart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        if (interMedium != null) {
            tvStart.setTypeface(interMedium);
        }

        TextView tvTitle = new TextView(context);
        tvTitle.setText(entry.title != null && !entry.title.isBlank() ? entry.title : "Class");
        tvTitle.setTextColor(textColor);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        if (interBold != null) {
            tvTitle.setTypeface(interBold);
        }

        TextView tvCode = new TextView(context);
        tvCode.setText(entry.courseCode != null && !entry.courseCode.isBlank() ? entry.courseCode : "");
        tvCode.setTextColor(secondaryTextColor);
        tvCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        if (interMedium != null) {
            tvCode.setTypeface(interMedium);
        }

        TextView tvBottom = new TextView(context);
        tvBottom.setText(buildBottomLine(entry, roomNameMap));
        tvBottom.setTextColor(secondaryTextColor);
        tvBottom.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        if (interMedium != null) {
            tvBottom.setTypeface(interMedium);
        }

        root.addView(tvStart);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        titleParams.topMargin = dpToPx(10);
        tvTitle.setLayoutParams(titleParams);
        root.addView(tvTitle);

        if (tvCode.getText() != null && tvCode.getText().length() > 0) {
            LinearLayout.LayoutParams codeParams = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            codeParams.topMargin = dpToPx(4);
            tvCode.setLayoutParams(codeParams);
            root.addView(tvCode);
        }

        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        bottomParams.topMargin = dpToPx(8);
        tvBottom.setLayoutParams(bottomParams);
        root.addView(tvBottom);

        return root;
    }

    private String buildBottomLine(ScheduleEntryEntity entry, @Nullable Map<Integer, String> roomNameMap) {
        if (entry.isOnline == 1) {
            if (entry.onlinePlatform != null && !entry.onlinePlatform.isBlank()) {
                return "Online • " + entry.onlinePlatform;
            }
            return "Online";
        }

        if (entry.roomId != null && roomNameMap != null && roomNameMap.containsKey(entry.roomId)) {
            return roomNameMap.get(entry.roomId);
        }

        if (entry.instructor != null && !entry.instructor.isBlank()) {
            return "Prof. " + entry.instructor;
        }

        return formatMinutes(entry.endMinutes);
    }

    private List<List<ScheduleEntryEntity>> buildColumns(List<ScheduleEntryEntity> entries) {
        List<List<ScheduleEntryEntity>> columns = new ArrayList<>();

        for (ScheduleEntryEntity entry : entries) {
            boolean placed = false;

            for (List<ScheduleEntryEntity> column : columns) {
                ScheduleEntryEntity last = column.get(column.size() - 1);
                if (entry.startMinutes >= last.endMinutes) {
                    column.add(entry);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                List<ScheduleEntryEntity> newColumn = new ArrayList<>();
                newColumn.add(entry);
                columns.add(newColumn);
            }
        }

        return columns;
    }

    private int minutesToTop(int minutes) {
        int clamped = Math.max(START_MINUTES, Math.min(END_MINUTES, minutes));
        return ((clamped - START_MINUTES) * hourHeightPx) / 60;
    }

    private int durationToHeight(int startMinutes, int endMinutes) {
        int duration = Math.max(30, endMinutes - startMinutes);
        return (duration * hourHeightPx) / 60;
    }

    private String formatHour(int minutes) {
        int hour24 = minutes / 60;
        int hour12 = hour24 % 12;
        if (hour12 == 0) hour12 = 12;
        String suffix = hour24 >= 12 ? "PM" : "AM";
        return String.format(Locale.getDefault(), "%d:00 %s", hour12, suffix);
    }

    private String formatMinutes(int minutes) {
        int hour24 = Math.max(0, Math.min(23, minutes / 60));
        int minute = Math.max(0, Math.min(59, minutes % 60));
        int hour12 = hour24 % 12;
        if (hour12 == 0) hour12 = 12;
        String suffix = hour24 >= 12 ? "PM" : "AM";
        return String.format(Locale.getDefault(), "%d:%02d %s", hour12, minute, suffix);
    }

    private int parseColorOrDefault(String input, String fallback) {
        try {
            return Color.parseColor(input);
        } catch (Exception e) {
            return Color.parseColor(fallback);
        }
    }

    private int getContrastingTextColor(int backgroundColor) {
        int red = Color.red(backgroundColor);
        int green = Color.green(backgroundColor);
        int blue = Color.blue(backgroundColor);
        double luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0;
        return luminance > 0.5 ? Color.parseColor("#1C1B1F") : Color.parseColor("#F5F5F5");
    }

    private int getSecondaryTextColor(int backgroundColor) {
        int red = Color.red(backgroundColor);
        int green = Color.green(backgroundColor);
        int blue = Color.blue(backgroundColor);
        double luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0;
        return luminance > 0.5 ? Color.parseColor("#5E5E5E") : Color.parseColor("#DDDDDD");
    }

    private int adjustStrokeColor(int backgroundColor) {
        int red = Math.max(0, Color.red(backgroundColor) - 20);
        int green = Math.max(0, Color.green(backgroundColor) - 20);
        int blue = Math.max(0, Color.blue(backgroundColor) - 20);
        return Color.rgb(red, green, blue);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}