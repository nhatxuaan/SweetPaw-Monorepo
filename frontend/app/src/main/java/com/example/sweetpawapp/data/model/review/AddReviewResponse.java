package com.example.sweetpawapp.data.model.review;

import com.google.gson.annotations.SerializedName;

public class AddReviewResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ReviewOrder data;

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public ReviewOrder getData() { return data; }
}
