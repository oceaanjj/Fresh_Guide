package com.example.freshguide.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.freshguide.model.entity.FacilityEntity;
import com.example.freshguide.model.entity.RoomFacilityCrossRef;

import java.util.List;

@Dao
public interface FacilityDao {

    @Query("SELECT * FROM facilities ORDER BY name ASC")
    List<FacilityEntity> getAllSync();

    @Query("SELECT f.* FROM facilities f INNER JOIN room_facilities rf ON f.id = rf.facility_id WHERE rf.room_id = :roomId")
    List<FacilityEntity> getFacilitiesForRoom(int roomId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FacilityEntity facility);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FacilityEntity> facilities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCrossRef(RoomFacilityCrossRef crossRef);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCrossRefs(List<RoomFacilityCrossRef> crossRefs);

    @Update
    void update(FacilityEntity facility);

    @Delete
    void delete(FacilityEntity facility);

    @Query("DELETE FROM facilities")
    void deleteAll();

    @Query("DELETE FROM room_facilities")
    void deleteAllCrossRefs();
}
