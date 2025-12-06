package com.example.sweetpawapp.data.model.auth;

public class GoogleLoginRequest {
    private String idToken;   // Token do Google Sign-In cung cấp
    private String email;     // gửi thêm email nếu server cần
    private String name;      // gửi thêm tên người dùng
    private String fcmToken;

    private String subscribedTopics;


    // --- Constructors ---
    public GoogleLoginRequest(String idToken, String fcmToken, String subscribedTopics) {
        this.idToken = idToken;
        this.fcmToken = fcmToken;
        this.subscribedTopics = subscribedTopics;
    }

    public GoogleLoginRequest(String idToken, String email, String name, String fcmToken, String subscribedTopics) {
        this.idToken = idToken;
        this.email = email;
        this.name = name;
        this.fcmToken = fcmToken;
        this.subscribedTopics = subscribedTopics;
    }

    // --- Getters & Setters ---
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFcmToken(){ return fcmToken; }

    public String setFcmToken(String fcmToken) { return fcmToken; }

    public String getSubscribedTopics() {
        return subscribedTopics;
    }

    public void setSubscribedTopics(String subscribedTopics) {
        this.subscribedTopics = subscribedTopics;
    }
}
