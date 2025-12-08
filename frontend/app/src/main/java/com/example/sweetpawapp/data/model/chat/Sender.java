package com.example.sweetpawapp.data.model.chat;

import com.google.gson.annotations.SerializedName;

public class Sender {

    @SerializedName("_id")
    private String senderId;
    private String name;

    public String getId() {
        return senderId;
    }

    public String getName() {
        return name;
    }
}
