package com.example.sweetpawapp.data.model.notification;

import java.util.List;

public class DateSection {
    private String date;
    private List<NotificationItem> list;

    public DateSection(String date, List<NotificationItem> list) {
        this.date = date;
        this.list = list;
    }

    public String getDate() {
        return date;
    }

    public List<NotificationItem> getList() {
        return list;
    }
}
