package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;

public class RouteStepDto {
    @SerializedName("id") public int id;
    @SerializedName("route_id") public int routeId;
    @SerializedName("order") public int orderNum;
    @SerializedName("instruction") public String instruction;
    @SerializedName("direction") public String direction;
    @SerializedName("landmark") public String landmark;
}
