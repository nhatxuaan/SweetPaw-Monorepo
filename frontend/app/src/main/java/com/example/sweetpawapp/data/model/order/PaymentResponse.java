package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private PaymentData data;

    public String getMessage() { return message; }
    public PaymentData getData() { return data; }
}
