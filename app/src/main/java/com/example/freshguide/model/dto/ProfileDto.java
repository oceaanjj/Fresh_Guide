package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;

public class ProfileDto {

    @SerializedName("id")
    public int id;

    @SerializedName("student_id")
    public String studentId;

    @SerializedName("name")
    public String name;

    @SerializedName("course_section")
    public String courseSection;

    @SerializedName("profile_photo_url")
    public String profilePhotoUrl;

    @SerializedName("updated_at")
    public String updatedAt;
}
