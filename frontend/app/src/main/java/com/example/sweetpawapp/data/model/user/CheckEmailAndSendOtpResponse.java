package com.example.sweetpawapp.data.model.user;

import com.google.gson.annotations.SerializedName;

public class CheckEmailAndSendOtpResponse {
    @SerializedName("boolean")
    private boolean success;
    @SerializedName("expiresIn")
    private String expiresIn;

    public CheckEmailAndSendOtpResponse() {
    }
    public CheckEmailAndSendOtpResponse(boolean success, String expiresIn) {
        this.success = success;
        this.expiresIn = expiresIn;
    }
    public boolean isSuccess() {
        return success;
    }

    public String getExpiresIn() {
        return expiresIn;
    }
}