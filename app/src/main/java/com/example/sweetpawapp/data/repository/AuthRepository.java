package com.example.sweetpawapp.data.repository;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sweetpawapp.App;
import com.example.sweetpawapp.data.local.AppDatabase;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.local.entity.UserEntity;
import com.example.sweetpawapp.data.model.auth.GoogleLoginRequest;
import com.example.sweetpawapp.data.model.auth.GoogleLoginResponse;
import com.example.sweetpawapp.data.model.auth.GoogleRegisterRequest;
import com.example.sweetpawapp.data.model.auth.GoogleRegisterResponse;
import com.example.sweetpawapp.data.model.auth.LoginResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;
import com.example.sweetpawapp.data.model.auth.LoginRequest;
import com.example.sweetpawapp.data.model.auth.RegisterRequest;
import com.example.sweetpawapp.data.model.auth.RegisterResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    public AuthRepository(SharedPreferences sharedPreferences) {
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
        this.sharedPreferences = sharedPreferences;
    }

    private static final String TAG = "AuthRepository";

    // Login
    public LiveData<LoginResponse> login(String email, String password, String fcmToken, String subscribedTopics) {
        Log.d(TAG, "Bắt đầu gọi API login: " + email);
        MutableLiveData<LoginResponse> result = new MutableLiveData<>();

        LoginRequest request = new LoginRequest(email, password, fcmToken, subscribedTopics);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "onResponse: " + response.code() + " body=" + response.body());

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.getToken() != null) {
                        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());

                        prefs.saveLastLoginInfo(
                                email,                   // Email người dùng nhập
                                password,                // Password người dùng nhập
                                loginResponse.getToken() // Token backend trả về
                        );

                        prefs.saveUserId(loginResponse.getUser().getId());
                        prefs.saveUserName(loginResponse.getUser().getHoTen());
                        prefs.saveUserPhone(loginResponse.getUser().getSoDienThoai());
                        if (loginResponse.getUser().getDiaChiList() != null) {
                            String jsonAddress = new Gson().toJson(loginResponse.getUser().getDiaChiList());
                            prefs.saveUserAddress(jsonAddress);
                        } else {
                            prefs.saveUserAddress("[]");
                        }

                        prefs.setLoggedIn(true);

                        Log.d(TAG, "Đã lưu token + thông tin user vào SharedPreferences");

                        Log.d("PreferencesManager", "Saved User Info:");
                        Log.d("PreferencesManager", "ID: " + prefs.getUserId());
                        Log.d("PreferencesManager", "Name: " + prefs.getUserName());
                        Log.d("PreferencesManager", "Phone: " + prefs.getUserPhone());
                        Log.d("PreferencesManager", "Address: " + prefs.getUserAddress());
                        Log.d("PreferencesManager", "Token: " + prefs.getToken());
                    }

                    result.setValue(loginResponse);
                } else {

                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                result.setValue(null);
            }
        });

        return result;
    }

    // Register
        public LiveData<Boolean> register(String hoTen, String email, String matKhau) {
            Log.d(TAG, "Bắt đầu gọi API register: " + email);
            MutableLiveData<Boolean> result = new MutableLiveData<>();
            RegisterRequest request = new RegisterRequest(hoTen, email, matKhau);

            apiService.register(request).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    Log.d(TAG, "onResponse: " + response.code() + " body=" + response.body());
                    if (response.isSuccessful() && response.body() != null) {
                        // Gán true nếu server báo success
                        result.setValue(response.body().isSuccess());
                        // Lưu thông tin vào entity thông qua DAO
                        UserEntity user = new UserEntity(
                                response.body().getId(),       // _id
                                response.body().getName(),     // HoTen
                                response.body().getEmail(),    // Email
                                null,              // SoDienThoai
                                null,              // NgaySinh
                                null,              // DiaChi
                                null,              // createdAt
                                null,              // updatedAt
                                0                  // version
                        );

                        // Lưu vào Room (chạy thread riêng để không block main)
                        new Thread(() -> {
                            AppDatabase db = AppDatabase.getInstance(App.getAppContext());
                            db.userDao().insertUser(user);
                            Log.d(TAG, "Đã lưu user vào SQLite: " + user.getEmail());
                        }).start();


                    } else {
                        // Server trả về lỗi, gán false
                        result.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage(), t);
                    // Request thất bại (lỗi mạng, 500,...)
                    result.setValue(false);
                }
            });

            return result;
        }

    public LiveData<GoogleLoginResponse> loginWithGoogle(String idToken, String fcmToken, String subscribedTopics) {
        MutableLiveData<GoogleLoginResponse> data = new MutableLiveData<>();

        GoogleLoginRequest request = new GoogleLoginRequest(idToken, fcmToken, subscribedTopics);

        apiService.loginWithGoogle(request).enqueue(new Callback<GoogleLoginResponse>() {
            @Override
            public void onResponse(Call<GoogleLoginResponse> call, Response<GoogleLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
                    prefs.saveUserId(response.body().getUser().getId());
                    prefs.saveToken(response.body().getToken());
                    prefs.saveUserName(response.body().getUser().getName());
                    prefs.setLoggedIn(true);

                    // vì Google không có password nên chỉ lưu email thôi
                    prefs.saveLastLoginInfo(response.body().getUser().getEmail(), "", response.body().getToken());
                    Log.d(TAG, "Đã lưu session Google vào SharedPreferences");

                    Log.d(TAG, "Google login thành công: " + response.body().toString());
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Google login thất bại, mã lỗi: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GoogleLoginResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối loginWithGoogle: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<GoogleRegisterResponse> registerWithGoogle(String idToken) {
        MutableLiveData<GoogleRegisterResponse> data = new MutableLiveData<>();

        GoogleRegisterRequest request = new GoogleRegisterRequest(idToken);
        apiService.registerWithGoogle(request).enqueue(new Callback<GoogleRegisterResponse>() {
            @Override
            public void onResponse(Call<GoogleRegisterResponse> call, Response<GoogleRegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Google register thành công: " + response.body().toString());
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Google register thất bại, mã lỗi: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GoogleRegisterResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối RegisterWithGoogle: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }


    // Logout
    public void logout() {
        sharedPreferences.edit().remove("access_token").apply();
    }

}
