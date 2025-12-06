package com.example.sweetpawapp.data.model.review;

import com.google.gson.annotations.SerializedName;

public class AddReviewRequest {
    @SerializedName("productId")
    private String productId;

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("stars")
    private int stars;

    @SerializedName("comment")
    private String comment;


    public AddReviewRequest(String productId, String orderId, int stars, String comment) {
        this.productId = productId;
        this.orderId = orderId;
        this.stars = stars;
        this.comment = comment;
    }
}
