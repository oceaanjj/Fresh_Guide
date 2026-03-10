package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Outer response from GET /api/sync/bootstrap.
 * No ApiResponse wrapper — returned bare.
 * {
 *   "version": 1,
 *   "published_at": "...",
 *   "data": { "buildings": [...], "facilities": [...], "origins": [...], "routes": [...] }
 * }
 */
public class BootstrapResponse {
    @SerializedName("version") public int version;
    @SerializedName("published_at") public String publishedAt;
    @SerializedName("data") public BootstrapData data;

    public static class BootstrapData {
        /** Buildings with nested floors → rooms → facilities */
        @SerializedName("buildings") public List<BuildingDto> buildings;
        /** Flat facility list (all campus facilities) */
        @SerializedName("facilities") public List<FacilityDto> facilities;
        @SerializedName("origins") public List<OriginDto> origins;
        /** Routes with nested steps */
        @SerializedName("routes") public List<RouteDto> routes;
    }
}
