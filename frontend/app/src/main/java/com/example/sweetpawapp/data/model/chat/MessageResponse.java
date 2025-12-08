package com.example.sweetpawapp.data.model.chat;


public class MessageResponse {
    private int status;
    private String message;
    private MessageData data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public MessageData getData() {
        return data;
    }
}

