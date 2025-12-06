package com.example.sweetpawapp.data.model.user;

import com.google.gson.annotations.SerializedName;

public class CheckEmailAndSendOtpRequest {
    @SerializedName("email")
    private String email;

    public CheckEmailAndSendOtpRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
