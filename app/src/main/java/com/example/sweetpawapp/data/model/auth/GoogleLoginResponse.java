package com.example.sweetpawapp.data.model.auth;

public class GoogleLoginResponse {
    private boolean Boolean;
    private String token;
    private UserInfo user;


    // --- Inner class cho thông tin người dùng ---
    public static class UserInfo {
        private String id;
        private String name;
        private String email;

        // Getters & Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    // --- Getters & Setters cho lớp chính ---
    public boolean isBoolean() {
        return Boolean;
    }

    public void setBoolean(boolean aBoolean) {
        Boolean = aBoolean;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

}
