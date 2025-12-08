package com.example.sweetpawapp.data.model.chat;

public class ChatResponse<T> {
    private int status;
    private String message;
    private T data;

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
