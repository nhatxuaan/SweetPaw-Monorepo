package com.example.sweetpawapp.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public RegisterRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Optional: getter nếu cần
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
