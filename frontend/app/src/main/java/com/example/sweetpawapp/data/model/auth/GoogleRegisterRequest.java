package com.example.sweetpawapp.data.model.auth;

public class GoogleRegisterRequest {
    private String idToken;

    public GoogleRegisterRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
