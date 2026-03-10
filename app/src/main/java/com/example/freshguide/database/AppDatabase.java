package com.example.freshguide.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.freshguide.database.dao.BuildingDao;
import com.example.freshguide.database.dao.FacilityDao;
import com.example.freshguide.database.dao.FloorDao;
import com.example.freshguide.database.dao.OriginDao;
import com.example.freshguide.database.dao.RoomDao;
import com.example.freshguide.database.dao.RouteDao;
import com.example.freshguide.database.dao.SyncMetaDao;
import com.example.freshguide.model.entity.BuildingEntity;
import com.example.freshguide.model.entity.FacilityEntity;
import com.example.freshguide.model.entity.FloorEntity;
import com.example.freshguide.model.entity.OriginEntity;
import com.example.freshguide.model.entity.RoomEntity;
import com.example.freshguide.model.entity.RoomFacilityCrossRef;
import com.example.freshguide.model.entity.RouteEntity;
import com.example.freshguide.model.entity.RouteStepEntity;
import com.example.freshguide.model.entity.SyncMetaEntity;

@Database(
    entities = {
        BuildingEntity.class,
        FloorEntity.class,
        RoomEntity.class,
        FacilityEntity.class,
        RoomFacilityCrossRef.class,
        OriginEntity.class,
        RouteEntity.class,
        RouteStepEntity.class,
        SyncMetaEntity.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "fresh_guide_db";
    private static volatile AppDatabase instance;

    public abstract BuildingDao buildingDao();
    public abstract FloorDao floorDao();
    public abstract RoomDao roomDao();
    public abstract FacilityDao facilityDao();
    public abstract OriginDao originDao();
    public abstract RouteDao routeDao();
    public abstract SyncMetaDao syncMetaDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DB_NAME
            ).build();
        }
        return instance;
    }
}
