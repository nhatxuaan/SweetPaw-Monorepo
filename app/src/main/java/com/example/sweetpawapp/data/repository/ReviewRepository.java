package com.example.sweetpawapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sweetpawapp.App;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.review.AddReviewRequest;
import com.example.sweetpawapp.data.model.review.AddReviewResponse;
import com.example.sweetpawapp.data.model.review.DeleteRatingRequest;
import com.example.sweetpawapp.data.model.review.ReviewOrder;
import com.example.sweetpawapp.data.model.review.ReviewOrderListResponse;
import com.example.sweetpawapp.data.model.review.UpdateReviewRequest;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ReviewRepository {
    private static final String TAG = "ReviewRepository";
    private final ApiService apiService;

    public ReviewRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    //Lấy tất cả review của user đang login (không filter product)
    public LiveData<List<ReviewOrder>> getAllReviews() {
        MutableLiveData<List<ReviewOrder>> liveData = new MutableLiveData<>();

        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token trống — chưa đăng nhập");
            liveData.setValue(null);
            return liveData;
        }

        Log.d(TAG, "Gọi API lấy tất cả review với token: " + token.substring(0, Math.min(10, token.length())) + "...");

        apiService.getAllReviews("Bearer " + token)
                .enqueue(new Callback<ReviewOrderListResponse>() {
                    @Override
                    public void onResponse(Call<ReviewOrderListResponse> call, Response<ReviewOrderListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<ReviewOrder> reviews = response.body().getData();
                            Log.d(TAG, "Lấy review thành công: " + (reviews != null ? reviews.size() : 0) + " review");
                            liveData.setValue(reviews);
                        } else {
                            Log.e(TAG, "Lỗi API: code " + response.code());
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewOrderListResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi mạng khi lấy review: " + t.getMessage(), t);
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }

    //Lấy review theo productId của user
    public LiveData<List<ReviewOrder>> getReviewsByProduct(String productId) {
        MutableLiveData<List<ReviewOrder>> liveData = new MutableLiveData<>();

        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token trống — chưa đăng nhập");
            liveData.setValue(null);
            return liveData;
        }

        Log.d(TAG, "Gọi API lấy review cho productId: " + productId);

        apiService.getReviewsByProduct("Bearer " + token, productId)
                .enqueue(new Callback<ReviewOrderListResponse>() {
                    @Override
                    public void onResponse(Call<ReviewOrderListResponse> call, Response<ReviewOrderListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<ReviewOrder> reviews = response.body().getData();
                            Log.d(TAG, "Lấy review thành công: " + (reviews != null ? reviews.size() : 0) + " review");
                            liveData.setValue(reviews);
                        } else {
                            Log.e(TAG, "Lỗi API: code " + response.code());
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewOrderListResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi mạng khi lấy review: " + t.getMessage(), t);
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }
    
    //Thêm review
    public MutableLiveData<AddReviewResponse> createReview(AddReviewRequest request) {
        MutableLiveData<AddReviewResponse> data = new MutableLiveData<>();
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();
        Log.d(TAG, "Gọi API thêm review" );
        apiService.createRating("Bearer " + token, request)
                .enqueue(new Callback<AddReviewResponse>() {
                    @Override
                    public void onResponse(Call<AddReviewResponse> call, Response<AddReviewResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body());
                            Log.d(TAG, "Thêm review thành công");
                        } else {
                            Log.e(TAG, "Lỗi API: code " + response.code());
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<AddReviewResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi mạng khi lấy review: " + t.getMessage(), t);
                        data.setValue(null);
                    }
                });

        return data;
    }

    //Cập nhật đánh giá
    public MutableLiveData<AddReviewResponse> updateRating(UpdateReviewRequest request) {

        MutableLiveData<AddReviewResponse> data = new MutableLiveData<>();

        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token trống — chưa đăng nhập");
            data.setValue(null);
            return data;
        }

        Log.d(TAG, "Gọi API cập nhật review với ratingId: " + request.getRatingId());

        apiService.updateRating("Bearer " + token, request)
                .enqueue(new Callback<AddReviewResponse>() {
                    @Override
                    public void onResponse(Call<AddReviewResponse> call, Response<AddReviewResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body());
                            Log.d(TAG, "Cập nhật review thành công");
                        } else {
                            Log.e(TAG, "Lỗi API update review: code " + response.code());
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<AddReviewResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi mạng khi cập nhật review: " + t.getMessage(), t);
                        data.setValue(null);
                    }
                });

        return data;
    }

    public MutableLiveData<AddReviewResponse> deleteRating(DeleteRatingRequest request) {

        MutableLiveData<AddReviewResponse> data = new MutableLiveData<>();

        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        Log.d(TAG, "Gọi API xóa review với ratingId: " + request.getRatingId());
        apiService.deleteRating("Bearer " + token, request)
                .enqueue(new Callback<AddReviewResponse>() {
                    @Override
                    public void onResponse(Call<AddReviewResponse> call, Response<AddReviewResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body());
                            Log.d(TAG, "Xóa review thành công");
                        } else {
                            Log.e(TAG, "Lỗi API xóa review: code " + response.code());
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<AddReviewResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi mạng khi xóa review: " + t.getMessage(), t);
                        data.setValue(null);
                    }
                });

        return data;
    }

}
