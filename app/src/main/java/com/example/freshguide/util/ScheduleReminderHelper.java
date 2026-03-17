package com.example.freshguide.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.freshguide.model.entity.ScheduleEntryEntity;
import com.example.freshguide.receiver.ScheduleReminderReceiver;

import java.util.Calendar;

public final class ScheduleReminderHelper {

    public static final String CHANNEL_ID = "schedule_reminders";
    public static final String EXTRA_SCHEDULE_ID = "schedule_id";

    private ScheduleReminderHelper() {
    }

    public static void ensureNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) {
            return;
        }

        NotificationChannel existing = manager.getNotificationChannel(CHANNEL_ID);
        if (existing != null) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Class Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Reminders before your classes start");
        manager.createNotificationChannel(channel);
    }

    public static void scheduleReminder(Context context, ScheduleEntryEntity entry) {
        if (entry == null || entry.id <= 0 || entry.reminderMinutes <= 0) {
            return;
        }

        long triggerAtMillis = computeNextReminderMillis(entry.dayOfWeek, entry.startMinutes, entry.reminderMinutes);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        PendingIntent pendingIntent = buildReminderPendingIntent(context, entry.id);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }

    public static void cancelReminder(Context context, int scheduleId) {
        if (scheduleId <= 0) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        PendingIntent pendingIntent = buildReminderPendingIntent(context, scheduleId);
        alarmManager.cancel(pendingIntent);
    }

    private static PendingIntent buildReminderPendingIntent(Context context, int scheduleId) {
        Intent intent = new Intent(context, ScheduleReminderReceiver.class);
        intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId);
        return PendingIntent.getBroadcast(
                context,
                scheduleId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static long computeNextReminderMillis(int dayOfWeek, int startMinutes, int reminderMinutes) {
        Calendar now = Calendar.getInstance();
        Calendar trigger = Calendar.getInstance();
        trigger.set(Calendar.DAY_OF_WEEK, toCalendarDay(dayOfWeek));
        trigger.set(Calendar.HOUR_OF_DAY, startMinutes / 60);
        trigger.set(Calendar.MINUTE, startMinutes % 60);
        trigger.set(Calendar.SECOND, 0);
        trigger.set(Calendar.MILLISECOND, 0);
        trigger.add(Calendar.MINUTE, -reminderMinutes);

        if (!trigger.after(now)) {
            trigger.add(Calendar.WEEK_OF_YEAR, 1);
        }
        return trigger.getTimeInMillis();
    }

    private static int toCalendarDay(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return Calendar.MONDAY;
            case 2:
                return Calendar.TUESDAY;
            case 3:
                return Calendar.WEDNESDAY;
            case 4:
                return Calendar.THURSDAY;
            case 5:
                return Calendar.FRIDAY;
            case 6:
                return Calendar.SATURDAY;
            case 7:
                return Calendar.SUNDAY;
            default:
                return Calendar.MONDAY;
        }
    }
}
