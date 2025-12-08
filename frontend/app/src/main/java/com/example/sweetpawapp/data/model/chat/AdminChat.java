package com.example.sweetpawapp.data.model.chat;

import com.google.gson.annotations.SerializedName;

public class AdminChat {
    @SerializedName("_id")
    private String adminId;
    private String displayName;
    private String email;

    // getters và setters
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
