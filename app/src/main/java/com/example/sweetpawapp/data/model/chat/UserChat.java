package com.example.sweetpawapp.data.model.chat;

import com.google.gson.annotations.SerializedName;

public class UserChat {
    @SerializedName("_id")
    private String userId;
    private String HoTen;
    private String Email;

    // getters và setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getHoTen() { return HoTen; }
    public void setHoTen(String hoTen) { HoTen = hoTen; }

    public String getEmail() { return Email; }
    public void setEmail(String email) { Email = email; }
}
