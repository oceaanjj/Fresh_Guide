package com.example.freshguide.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.freshguide.model.entity.UserProfileEntity;

@Dao
public interface UserProfileDao {

    @Query("SELECT * FROM user_profiles WHERE owner_student_id = :ownerStudentId LIMIT 1")
    LiveData<UserProfileEntity> observeByOwner(String ownerStudentId);

    @Query("SELECT * FROM user_profiles WHERE owner_student_id = :ownerStudentId LIMIT 1")
    UserProfileEntity getByOwnerSync(String ownerStudentId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(UserProfileEntity profile);

    @Query("DELETE FROM user_profiles WHERE owner_student_id = :ownerStudentId")
    void deleteByOwner(String ownerStudentId);
}
