package com.example.sweetpawapp;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this; // Lưu instance để dùng context toàn app
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
