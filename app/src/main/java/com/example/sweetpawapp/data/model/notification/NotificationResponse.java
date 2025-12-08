package com.example.sweetpawapp.data.model.notification;

import com.google.gson.annotations.SerializedName;

public class NotificationResponse {

    private boolean success;

    @SerializedName("notifications")
    private NotificationByDate notificationsByDateWrapper;

    public boolean isSuccess() { return success; }

    public NotificationByDate getNotificationsByDate() {
        return notificationsByDateWrapper;
    }
}
