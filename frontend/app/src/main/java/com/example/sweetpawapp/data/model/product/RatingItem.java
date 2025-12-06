package com.example.sweetpawapp.data.model.product;

import com.google.gson.annotations.SerializedName;

public class RatingItem {
    @SerializedName("_id")
    private String ratingId;

    private String productId;

    @SerializedName("userId")
    private UserId user;

    private String orderId;
    private int stars;
    private String comment;
    private String createdAt;

    public String getRatingId() {
        return ratingId;
    }

    public String getProductId() {
        return productId;
    }

    public UserId getUser() {
        return user;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getStars() {
        return stars;
    }

    public String getComment() {
        return comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static class UserId {
        @SerializedName("_id")
        private String userId;
        @SerializedName("HoTen")
        private String nameUser;

        public String getUserId() {
            return userId;
        }

        public String getNameUser() {
            return nameUser;
        }

        public void setNameUser(String nameUser) {
            this.nameUser = nameUser;
        }
    }
}
