package com.example.freshguide.model.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private UserData user;

    public String getToken() { return token; }
    public String getRole()      { return user != null ? user.role : null; }
    public String getStudentId() { return user != null ? user.studentId : null; }
    public String getName()      { return user != null ? user.name : null; }
    public String getCourseSection() { return user != null ? user.courseSection : null; }
    public String getProfilePhotoUrl() { return user != null ? user.profilePhotoUrl : null; }

    public static class UserData {
        @SerializedName("student_id") public String studentId;
        @SerializedName("email")      public String email;
        @SerializedName("name")       public String name;
        @SerializedName("course_section") public String courseSection;
        @SerializedName("profile_photo_url") public String profilePhotoUrl;
        @SerializedName("role")       public String role;
    }
}
