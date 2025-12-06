package com.example.sweetpawapp.data.model.review;

public class DeleteRatingRequest {
    private String ratingId;

    public DeleteRatingRequest(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getRatingId() {
        return ratingId;
    }
}
