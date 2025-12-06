package com.example.sweetpawapp.data.model.notification;

import com.google.gson.annotations.SerializedName;

public class NotificationMarkReadResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("notification")
    private NotificationItem notification;

    // Getter và Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationItem getNotification() {
        return notification;
    }

    public void setNotification(NotificationItem notification) {
        this.notification = notification;
    }
}
