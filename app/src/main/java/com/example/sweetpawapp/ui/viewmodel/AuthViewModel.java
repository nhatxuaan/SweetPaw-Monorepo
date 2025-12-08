package com.example.sweetpawapp.ui.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sweetpawapp.data.model.auth.GoogleLoginResponse;
import com.example.sweetpawapp.data.model.auth.GoogleRegisterResponse;
import com.example.sweetpawapp.data.model.auth.LoginResponse;
import com.example.sweetpawapp.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        SharedPreferences sharedPreferences =
                application.getSharedPreferences("app_prefs", Application.MODE_PRIVATE);
        authRepository = new AuthRepository(sharedPreferences);
    }

    public LiveData<LoginResponse> login(String email, String password, String fcmToken, String subscribedTopics) {
        return authRepository.login(email, password, fcmToken, subscribedTopics);
    }
    public LiveData<Boolean> register(String hoTen, String email, String matKhau) {
        return authRepository.register(hoTen, email, matKhau);
    }
    public LiveData<GoogleLoginResponse> loginWithGoogle(String idToken, String fcmToken, String subscribedTopics) {
        return authRepository.loginWithGoogle(idToken, fcmToken, subscribedTopics);
    }
    public LiveData<GoogleRegisterResponse> registerWithGoogle(String idToken) {
        return authRepository.registerWithGoogle(idToken);
    }
}
