package com.example.freshguide.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "facilities")
public class FacilityEntity {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "icon")
    public String icon;

    public FacilityEntity(int id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }
}
