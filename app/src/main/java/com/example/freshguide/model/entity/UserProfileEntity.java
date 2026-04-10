package com.example.freshguide.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(
        tableName = "user_profiles",
        indices = {
                @Index("sync_state"),
                @Index("updated_at")
        }
)
public class UserProfileEntity {

    public static final int SYNC_STATE_DIRTY = 0;
    public static final int SYNC_STATE_CLEAN = 1;

    public static final int PHOTO_ACTION_NONE = 0;
    public static final int PHOTO_ACTION_UPLOAD = 1;
    public static final int PHOTO_ACTION_DELETE = 2;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "owner_student_id")
    public String ownerStudentId;

    @ColumnInfo(name = "full_name")
    public String fullName;

    @ColumnInfo(name = "course_section")
    public String courseSection;

    @ColumnInfo(name = "photo_local_path")
    public String photoLocalPath;

    @ColumnInfo(name = "photo_remote_url")
    public String photoRemoteUrl;

    @ColumnInfo(name = "sync_state", defaultValue = "1")
    public int syncState;

    @ColumnInfo(name = "pending_photo_action", defaultValue = "0")
    public int pendingPhotoAction;

    @ColumnInfo(name = "updated_at", defaultValue = "0")
    public long updatedAt;

    @ColumnInfo(name = "last_synced_at", defaultValue = "0")
    public long lastSyncedAt;
}
