package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;

public class SyncVersionResponse {
    @SerializedName("version") public int version;
    @SerializedName("published_at") public String publishedAt;
}
