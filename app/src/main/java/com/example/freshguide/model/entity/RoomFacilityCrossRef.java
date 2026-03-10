package com.example.freshguide.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "room_facilities",
    primaryKeys = {"room_id", "facility_id"},
    foreignKeys = {
        @ForeignKey(entity = RoomEntity.class,
            parentColumns = "id", childColumns = "room_id", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = FacilityEntity.class,
            parentColumns = "id", childColumns = "facility_id", onDelete = ForeignKey.CASCADE)
    },
    indices = {@Index("room_id"), @Index("facility_id")}
)
public class RoomFacilityCrossRef {

    @ColumnInfo(name = "room_id")
    public int roomId;

    @ColumnInfo(name = "facility_id")
    public int facilityId;

    public RoomFacilityCrossRef(int roomId, int facilityId) {
        this.roomId = roomId;
        this.facilityId = facilityId;
    }
}
