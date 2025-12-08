package com.example.sweetpawapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sweetpawapp.data.model.chat.Chat;
import com.example.sweetpawapp.data.model.chat.ChatResponse;
import com.example.sweetpawapp.data.model.chat.MessageRequest;
import com.example.sweetpawapp.data.model.chat.MessageResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    private static final String TAG = "CHAT";

    private ApiService apiService;

    public ChatRepository() {
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public LiveData<Chat> getOrCreateChat(String token) {
        MutableLiveData<Chat> liveData = new MutableLiveData<>();

        apiService.getOrCreateChat(token)
                .enqueue(new Callback<ChatResponse<Chat>>() {
                    @Override
                    public void onResponse(Call<ChatResponse<Chat>> call, Response<ChatResponse<Chat>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "API phản hồi: Success" + response.body().getData());
                            liveData.setValue(response.body().getData());
                        } else {
                            Log.e(TAG, "API phản hồi: Error" + response.code());
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ChatResponse<Chat>> call, Throwable t) {
                        Log.e(TAG, "Lỗi mạng: " + t.getMessage());
                        liveData.setValue(null);
                    }
                });

        return liveData;
    }

    // Hàm gửi tin nhắn
    public LiveData<MessageResponse> sendMessage(String token, MessageRequest request) {
        MutableLiveData<MessageResponse> data = new MutableLiveData<>();

        Log.d(TAG, "sendMessage() called");
        Log.d(TAG, "Token: " + token);
        Log.d(TAG, "MessageRequest: media=" + request.getMedia() +
                ", content=" + request.getContent());

        apiService.sendMessage(token, request)
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

                        Log.d(TAG, "HTTP CODE: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "sendMessage SUCCESS: " + response.body().getMessage());
                            data.setValue(response.body());
                        } else {
                            Log.e(TAG, "sendMessage FAILED - code: " + response.code());
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        Log.e(TAG, "sendMessage ERROR: " + t.getMessage());
                        data.setValue(null);
                    }
                });

        return data;
    }
}

