package com.example.sweetpawapp.data.model.user;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordResponse {
    @SerializedName("boolean")
    private boolean success;

    public ResetPasswordResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
