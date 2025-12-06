package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

public class OrderDetailResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private OrderDetail data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public OrderDetail getData() { return data; }
    public boolean isSuccess() { return code == 200; }
}

