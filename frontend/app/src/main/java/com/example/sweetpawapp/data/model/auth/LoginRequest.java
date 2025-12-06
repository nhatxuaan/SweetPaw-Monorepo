package com.example.sweetpawapp.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("fcmToken")
    private String fcmToken;

    private String subscribedTopics;


    public LoginRequest(String email, String password, String fcmToken, String subscribedTopics) {
        this.email = email;
        this.password = password;
        this.fcmToken = fcmToken;
        this.subscribedTopics = subscribedTopics;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    public String getFcmToken(){ return fcmToken; }

    public String getSubscribedTopics() {
        return subscribedTopics;
    }

    public void setSubscribedTopics(String subscribedTopics) {
        this.subscribedTopics = subscribedTopics;
    }
}
