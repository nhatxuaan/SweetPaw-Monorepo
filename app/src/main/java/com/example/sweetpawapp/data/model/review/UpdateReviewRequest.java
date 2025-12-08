package com.example.sweetpawapp.data.model.review;

import com.google.gson.annotations.SerializedName;

public class UpdateReviewRequest {
    @SerializedName("ratingId")
    private String ratingId;

    @SerializedName("stars")
    private int stars;

    @SerializedName("comment")
    private String comment;

    public UpdateReviewRequest(String ratingId, int stars, String comment) {
        this.ratingId = ratingId;
        this.stars = stars;
        this.comment = comment;
    }

    // Getter & Setter
    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
