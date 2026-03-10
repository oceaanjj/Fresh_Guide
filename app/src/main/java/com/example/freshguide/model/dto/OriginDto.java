package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;

public class OriginDto {
    @SerializedName("id") public int id;
    @SerializedName("name") public String name;
    @SerializedName("code") public String code;
    @SerializedName("description") public String description;
}
