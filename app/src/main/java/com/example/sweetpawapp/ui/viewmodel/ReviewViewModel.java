package com.example.sweetpawapp.ui.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweetpawapp.data.model.review.AddReviewRequest;
import com.example.sweetpawapp.data.model.review.AddReviewResponse;
import com.example.sweetpawapp.data.model.review.DeleteRatingRequest;
import com.example.sweetpawapp.data.model.review.UpdateReviewRequest;
import com.example.sweetpawapp.data.repository.ReviewRepository;

public class ReviewViewModel extends ViewModel {
    private ReviewRepository repository = new ReviewRepository();
    private MutableLiveData<AddReviewResponse> reviewLiveData = new MutableLiveData<>();
    private MutableLiveData<AddReviewResponse> updateReviewLiveData = new MutableLiveData<>();
    private MutableLiveData<AddReviewResponse> deleteReviewLiveData = new MutableLiveData<>();

    public MutableLiveData<AddReviewResponse> getReviewLiveData() {
        return reviewLiveData;
    }

    public MutableLiveData<AddReviewResponse> getUpdateReviewLiveData() {
        return updateReviewLiveData;
    }
    public MutableLiveData<AddReviewResponse> getDeleteReviewLiveData() {
        return deleteReviewLiveData;
    }

    public void createReview(AddReviewRequest req) {
        repository.createReview(req).observeForever(reviewLiveData::setValue);
    }

    public void updateReview(UpdateReviewRequest req) {
        repository.updateRating(req).observeForever(reviewLiveData::setValue);
    }
    public void deleteReview(DeleteRatingRequest req) {
        repository.deleteRating(req).observeForever(reviewLiveData::setValue);
    }

}
