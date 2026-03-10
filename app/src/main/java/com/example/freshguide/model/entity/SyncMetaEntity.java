package com.example.freshguide.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sync_meta")
public class SyncMetaEntity {

    @PrimaryKey
    public int id = 1; // singleton row

    @ColumnInfo(name = "version")
    public int version;

    @ColumnInfo(name = "synced_at")
    public String syncedAt;

    public SyncMetaEntity(int version, String syncedAt) {
        this.version = version;
        this.syncedAt = syncedAt;
    }
}
