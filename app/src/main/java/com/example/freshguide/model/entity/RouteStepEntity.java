package com.example.freshguide.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "route_steps",
    foreignKeys = @ForeignKey(
        entity = RouteEntity.class,
        parentColumns = "id",
        childColumns = "route_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = @Index("route_id")
)
public class RouteStepEntity {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "route_id")
    public int routeId;

    @ColumnInfo(name = "order_num")
    public int orderNum;

    @ColumnInfo(name = "instruction")
    public String instruction;

    @ColumnInfo(name = "direction")
    public String direction;

    @ColumnInfo(name = "landmark")
    public String landmark;

    public RouteStepEntity(int id, int routeId, int orderNum, String instruction, String direction, String landmark) {
        this.id = id;
        this.routeId = routeId;
        this.orderNum = orderNum;
        this.instruction = instruction;
        this.direction = direction;
        this.landmark = landmark;
    }
}
