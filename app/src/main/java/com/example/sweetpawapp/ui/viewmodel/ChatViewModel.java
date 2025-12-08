package com.example.sweetpawapp.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweetpawapp.data.model.chat.Chat;
import com.example.sweetpawapp.data.model.chat.MessageRequest;
import com.example.sweetpawapp.data.model.chat.MessageResponse;
import com.example.sweetpawapp.data.network.RetrofitClient;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.repository.ChatRepository;

public class ChatViewModel extends ViewModel {
    private static final String TAG = "CHAT";

    private ChatRepository repository;


    public ChatViewModel() {
        repository = new ChatRepository();
    }

    // Phương thức lấy hoặc tạo chat
    public LiveData<Chat> getOrCreateChat(String token) {
        Log.d(TAG, "Lấy hoặc tạo chat với token: " + token);
        return repository.getOrCreateChat(token);
    }

    public LiveData<MessageResponse> sendMessage(String token, MessageRequest request) {
        Log.d(TAG, "Gửi tin nhắn với token: " + token);
        return repository.sendMessage(token, request);
    }
}
