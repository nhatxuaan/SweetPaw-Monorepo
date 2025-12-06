package com.example.sweetpawapp.data.model.auth;

import com.google.gson.annotations.SerializedName;
import com.example.sweetpawapp.data.model.user.User;

public class LoginResponse {

    @SerializedName("Boolean")
    private boolean success;

    @SerializedName("token")
    private String token;

    @SerializedName("user")
    private User user;

    // Getter
    public boolean isSuccess() {
        return success;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

}
