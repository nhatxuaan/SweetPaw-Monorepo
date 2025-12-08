package com.example.sweetpawapp.data.model.review;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewOrderListResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<ReviewOrder> data;

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public List<ReviewOrder> getData() { return data; }
}
