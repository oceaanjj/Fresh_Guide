package com.example.freshguide.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "floors",
    foreignKeys = @ForeignKey(
        entity = BuildingEntity.class,
        parentColumns = "id",
        childColumns = "building_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = @Index("building_id")
)
public class FloorEntity {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "building_id")
    public int buildingId;

    @ColumnInfo(name = "number")
    public int number;

    @ColumnInfo(name = "name")
    public String name;

    public FloorEntity(int id, int buildingId, int number, String name) {
        this.id = id;
        this.buildingId = buildingId;
        this.number = number;
        this.name = name;
    }
}
