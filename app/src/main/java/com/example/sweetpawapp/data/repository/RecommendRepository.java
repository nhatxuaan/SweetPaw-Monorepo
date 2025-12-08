package com.example.sweetpawapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sweetpawapp.App;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.recommend.CartRecommendRequest;
import com.example.sweetpawapp.data.model.recommend.CartRecommendResponse;
import com.example.sweetpawapp.data.model.recommend.RecommendHomeResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendRepository {
    private final ApiService apiService;

    public RecommendRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    // Lấy sản phẩm recommendation cho Home
    public LiveData<RecommendHomeResponse> getHomeRecommend() {
        MutableLiveData<RecommendHomeResponse> data = new MutableLiveData<>();
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        apiService.getHomeRecommend("Bearer " + token)
                .enqueue(new Callback<RecommendHomeResponse>() {
                    @Override
                    public void onResponse(Call<RecommendHomeResponse> call, Response<RecommendHomeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("RecommendSuccess", "Response: " +  response.body());
                            data.setValue(response.body());
                        } else {
                            try {
                                String errorJson = response.errorBody().string();
                                Log.e("RecommendRepo", "API Error: " + errorJson);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RecommendHomeResponse> call, Throwable t) {
                        Log.e("RecommendRepo", "API call failed: " + t.getMessage());
                        data.setValue(null);
                    }
                });

        return data;
    }
    public LiveData<CartRecommendResponse> getRecommendFromCart(List<String> ids) {
        MutableLiveData<CartRecommendResponse> data = new MutableLiveData<>();
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        CartRecommendRequest request = new CartRecommendRequest(ids);

        apiService.getCartRecommend("Bearer " + token, request).enqueue(new Callback<CartRecommendResponse>() {
            @Override
            public void onResponse(Call<CartRecommendResponse> call, Response<CartRecommendResponse> response) {

                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    try {
                        Log.e("RecommendCart", "Error: " + response.errorBody().string());
                    } catch (Exception e) {}

                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<CartRecommendResponse> call, Throwable t) {
                Log.e("RecommendCart", "Fail: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

}
