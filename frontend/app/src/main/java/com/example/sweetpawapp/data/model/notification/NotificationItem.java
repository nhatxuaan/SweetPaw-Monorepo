package com.example.sweetpawapp.data.model.notification;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class NotificationItem {
    @SerializedName("_id")
    private String notificationId;
    private String title;
    private String body;
    private JsonObject data;
    private String type;
    @SerializedName("isRead")
    private boolean isRead;
    private String createdAt;
    private String expireAt;

    public NotificationItem() {}
    public NotificationItem(String notificationId, String title, String body, JsonObject data, String type, boolean isRead, String createdAt, String expireAt ) {
        this.notificationId = notificationId;
        this.title = title;
        this.body = body;
        this.data = data;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public JsonObject getData() {
        return data;
    }
    public void setData(JsonObject data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }
}
