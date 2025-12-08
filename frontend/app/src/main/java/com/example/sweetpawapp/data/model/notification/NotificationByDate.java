package com.example.sweetpawapp.data.model.notification;

import java.util.List;
import java.util.Map;

public class NotificationByDate {
    private boolean success;
    private Map<String, List<NotificationItem>> notificationsByDate; // key là ngày
    private int total;
    private int unreadCount;

    public NotificationByDate() {}

    public NotificationByDate(boolean success, Map<String, List<NotificationItem>> notificationsByDate, int total, int unreadCount) {
        this.success = success;
        this.notificationsByDate = notificationsByDate;
        this.total = total;
        this.unreadCount = unreadCount;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Map<String, List<NotificationItem>> getNotificationsByDate() { return notificationsByDate; }
    public void setNotificationsByDate(Map<String, List<NotificationItem>> notificationsByDate) { this.notificationsByDate = notificationsByDate; }


    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
