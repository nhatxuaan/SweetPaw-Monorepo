package com.example.sweetpawapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;

public class PreferencesManager {

    private static final String PREF_NAME = "sweetpaw_preferences";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_LAST_EMAIL = "last_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_ADDRESS = "user_address";
    private static final String KEY_LAST_PASSWORD = "last_password";

    private static final String KEY_TOKEN = "access_token";

    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static PreferencesManager instance;
    private final SharedPreferences prefs;

    private final Gson gson;

    private PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Singleton pattern để tái sử dụng trong toàn app
    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context.getApplicationContext());
        }
        return instance;
    }


    // ========== SAVE / GET BASIC INFO ==========
    public void saveUserId(String userId) {
        prefs.edit().putString(KEY_USER_ID, userId).apply();
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public void saveUserName(String name) {
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public void saveUserPhone(String phone) {
        prefs.edit().putString(KEY_USER_PHONE, phone).apply();
    }

    public String getUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "");
    }

    public void saveUserAddress(String address) {
        prefs.edit().putString(KEY_USER_ADDRESS, address).apply();
    }

    public String getUserAddress() {
        return prefs.getString(KEY_USER_ADDRESS, "");
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void setLoggedIn(boolean loggedIn) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // ========== SAVE LAST LOGIN INFO (for auto-fill) ==========

    public void saveLastLoginInfo(String email, String password, String token) {
        String encodedPassword = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
        prefs.edit()
                .putString(KEY_LAST_EMAIL, email)
                .putString(KEY_LAST_PASSWORD, encodedPassword)
                .putString(KEY_TOKEN, token)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public String getLastEmail() {
        return prefs.getString(KEY_LAST_EMAIL, "");
    }

    public String getLastPassword() {
        String encodedPass = prefs.getString(KEY_LAST_PASSWORD, "");
        if (encodedPass.isEmpty()) return "";
        return new String(Base64.decode(encodedPass, Base64.DEFAULT));
    }

    // ========== LOGOUT / RESET ==========

    // Đăng xuất
    public void logoutSession() {
        prefs.edit()
                .remove(KEY_TOKEN)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .apply();
    }

//    // Đăng xuất hoàn toàn → xóa sạch hết luôn
//    public void clearAll() {
//        prefs.edit().clear().apply();
//    }


}
