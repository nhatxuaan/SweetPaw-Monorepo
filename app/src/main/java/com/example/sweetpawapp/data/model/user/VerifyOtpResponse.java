package com.example.sweetpawapp.data.model.user;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpResponse {
    @SerializedName("boolean")
    private boolean success;

    @SerializedName("allowReset")
    private boolean allowReset;

    public boolean isSuccess() {
        return success;
    }

    public boolean isAllowReset() {
        return allowReset;
    }
}