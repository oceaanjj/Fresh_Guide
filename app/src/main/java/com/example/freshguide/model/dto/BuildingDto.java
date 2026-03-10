package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BuildingDto {
    @SerializedName("id") public int id;
    @SerializedName("name") public String name;
    @SerializedName("code") public String code;
    @SerializedName("description") public String description;
    @SerializedName("floors") public List<FloorDto> floors;
}
