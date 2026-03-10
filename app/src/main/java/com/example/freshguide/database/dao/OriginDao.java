package com.example.freshguide.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.freshguide.model.entity.OriginEntity;

import java.util.List;

@Dao
public interface OriginDao {

    @Query("SELECT * FROM origins ORDER BY name ASC")
    LiveData<List<OriginEntity>> getAllOrigins();

    @Query("SELECT * FROM origins ORDER BY name ASC")
    List<OriginEntity> getAllSync();

    @Query("SELECT * FROM origins WHERE id = :id")
    OriginEntity getByIdSync(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OriginEntity origin);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<OriginEntity> origins);

    @Update
    void update(OriginEntity origin);

    @Delete
    void delete(OriginEntity origin);

    @Query("DELETE FROM origins")
    void deleteAll();
}
