package com.example.sweetpawapp.data.model.review;

import com.google.gson.annotations.SerializedName;

public class ReviewOrder {
    @SerializedName("ratingId")
    private String ratingId;

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("productId")
    private String productId;

    @SerializedName("comment")
    private String comment;

    @SerializedName("stars")
    private int stars;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("userId")
    private String userId;

    @SerializedName("_id")
    private String id_created;

    private String productName;



    public ReviewOrder() {}

    public ReviewOrder(String ratingId, String orderId, String productId, String comment, int stars, String createdAt) {
        this.ratingId = ratingId;
        this.orderId = orderId;
        this.productId = productId;
        this.comment = comment;
        this.stars = stars;
        this.createdAt = createdAt;
    }

    // Getters và Setters
    public String getRatingId() { return ratingId; }
    public void setRatingId(String ratingId) { this.ratingId = ratingId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }


    public String getId_created() {
        return id_created;
    }

    public void setId_created(String id_created) {
        this.id_created = id_created;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
