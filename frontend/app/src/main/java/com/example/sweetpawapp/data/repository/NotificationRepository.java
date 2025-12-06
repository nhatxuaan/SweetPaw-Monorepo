package com.example.sweetpawapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.notification.NotificationByDate;
import com.example.sweetpawapp.data.model.notification.NotificationItem;
import com.example.sweetpawapp.data.model.notification.NotificationMarkReadResponse;
import com.example.sweetpawapp.data.model.notification.NotificationResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {
    private static final String TAG = "THÔNG BÁO";

    private ApiService apiService;

    public NotificationRepository() { this.apiService = RetrofitClient.getClient().create(ApiService.class); }

    // Lấy tất cả notifications
    public void getAllNotifications(String token, MutableLiveData<NotificationByDate> liveData) {
        apiService.getNotifications(token).enqueue(new Callback<NotificationByDate>() {
            @Override
            public void onResponse(Call<NotificationByDate> call, Response<NotificationByDate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    Log.e(TAG, "getAllNotifications API error: " + response.code());
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<NotificationByDate> call, Throwable t) {
                Log.e(TAG, "getAllNotifications Network error: " + t.getMessage());
                liveData.postValue(null);
            }
        });
    }

    // Lấy notifications chưa đọc
    public void getUnreadNotifications(String token, MutableLiveData<NotificationByDate> liveData) {
        apiService.getUnreadNotifications(token).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().getNotificationsByDate());
                } else {
                    Log.e(TAG, "getUnreadNotifications API error: " + response.code());
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                Log.e(TAG, "getUnreadNotifications Network error: " + t.getMessage());
                liveData.postValue(null);
            }
        });
    }


    public LiveData<NotificationItem> markNotificationAsRead(String token, String notificationId) {
        MutableLiveData<NotificationItem> result = new MutableLiveData<>();

        Log.d(TAG, "Gọi API đánh dấu đã đọc, notificationId=" + notificationId);

        apiService.markAsRead(token, notificationId).enqueue(new Callback<NotificationMarkReadResponse>() {
            @Override
            public void onResponse(Call<NotificationMarkReadResponse> call, Response<NotificationMarkReadResponse> response) {

                Log.d(TAG, "API markAsRead onResponse, code=" + response.code() + ", body=" + response.body());

                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){

                    Log.d(TAG, "Đánh dấu đã đọc thành công trên server: " + response.body().getNotification().getNotificationId());

                    result.setValue(response.body().getNotification());
                } else {

                    Log.e(TAG, "Lỗi server khi đánh dấu đã đọc: code=" + response.code() + ", body=" + response.errorBody());

                    result.setValue(null); // lỗi server
                }
            }

            @Override
            public void onFailure(Call<NotificationMarkReadResponse> call, Throwable t) {

                Log.e(TAG, "Lỗi mạng khi đánh dấu đã đọc: " + t.getMessage());

                result.setValue(null); // lỗi mạng
            }
        });

        return result;
    }

}
