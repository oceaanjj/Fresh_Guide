package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RoomDto {
    @SerializedName("id") public int id;
    @SerializedName("floor_id") public int floorId;
    @SerializedName("name") public String name;
    @SerializedName("code") public String code;
    @SerializedName("type") public String type;
    @SerializedName("description") public String description;
    @SerializedName("facilities") public List<FacilityDto> facilities;
}
