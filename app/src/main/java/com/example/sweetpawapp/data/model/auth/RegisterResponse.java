package com.example.sweetpawapp.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName("Boolean")
    private boolean success;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public boolean isSuccess() {
        return success;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
