package com.example.freshguide.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.freshguide.model.entity.SyncMetaEntity;

@Dao
public interface SyncMetaDao {

    @Query("SELECT * FROM sync_meta WHERE id = 1 LIMIT 1")
    SyncMetaEntity get();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(SyncMetaEntity meta);

    @Query("SELECT version FROM sync_meta WHERE id = 1")
    int getVersion();
}
