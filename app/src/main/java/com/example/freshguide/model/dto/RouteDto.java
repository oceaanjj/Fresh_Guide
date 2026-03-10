package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RouteDto {
    @SerializedName("id") public int id;
    @SerializedName("origin_id") public int originId;
    @SerializedName("destination_room_id") public int destinationRoomId;
    @SerializedName("name") public String name;
    @SerializedName("description") public String description;
    @SerializedName("steps") public List<RouteStepDto> steps;
}
