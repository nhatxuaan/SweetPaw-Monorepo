package com.example.sweetpawapp.data.network;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";

   // private static final String BASE_URL = "https://sweetpaw-bev1.azurewebsites.net/"; // nơi chứa url đến BE, url hiện tại là url tạm test máy local// private static final String BASE_URL = "https://sweetpaw-be.azurewebsites.net/";

    private static final String BASE_URL = "https://sweetpaw-be.azurewebsites.net/";


    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            Log.d(TAG, "Khởi tạo Retrofit với BASE_URL: " + BASE_URL);
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // chuyển đổi JSON <-> Object
                    .build();
        }
        else {
            Log.d(TAG, "Retrofit đã được khởi tạo trước đó");
        }
        return retrofit;
    }
}
