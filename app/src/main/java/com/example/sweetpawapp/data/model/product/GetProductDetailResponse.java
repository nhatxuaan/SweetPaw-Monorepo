package com.example.sweetpawapp.data.model.product;

import com.google.gson.annotations.SerializedName;

public class GetProductDetailResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ProductDetail data;
    @SerializedName("avgRating")
    private double avgRating;

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public String getMessage() {
        return message;
    }

    public ProductDetail getData() {
        return data;
    }
}
