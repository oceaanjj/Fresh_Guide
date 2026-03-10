package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FloorDto {
    @SerializedName("id") public int id;
    @SerializedName("building_id") public int buildingId;
    @SerializedName("number") public int number;
    @SerializedName("name") public String name;
    @SerializedName("rooms") public List<RoomDto> rooms;
}
