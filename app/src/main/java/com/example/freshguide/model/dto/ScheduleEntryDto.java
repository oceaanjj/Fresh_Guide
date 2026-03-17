package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;

public class ScheduleEntryDto {
    @SerializedName("id") public int id;
    @SerializedName("client_uuid") public String clientUuid;
    @SerializedName("title") public String title;
    @SerializedName("course_code") public String courseCode;
    @SerializedName("instructor") public String instructor;
    @SerializedName("notes") public String notes;
    @SerializedName("color_hex") public String colorHex;
    @SerializedName("day_of_week") public int dayOfWeek;
    @SerializedName("start_minutes") public int startMinutes;
    @SerializedName("end_minutes") public int endMinutes;
    @SerializedName("is_online") public boolean isOnline;
    @SerializedName("room_id") public Integer roomId;
    @SerializedName("online_platform") public String onlinePlatform;
    @SerializedName("reminder_minutes") public int reminderMinutes;
    @SerializedName("created_at") public String createdAt;
    @SerializedName("updated_at") public String updatedAt;
}
