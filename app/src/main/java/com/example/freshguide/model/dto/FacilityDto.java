package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FacilityDto {
    @SerializedName("id") public int id;
    @SerializedName("name") public String name;
    @SerializedName("icon") public String icon;
}
