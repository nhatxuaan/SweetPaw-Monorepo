package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

public class PaymentData {
    @SerializedName("orderId")
    private String orderId;

    @SerializedName("amount")
    private int amount;

    @SerializedName("qrUrl")
    private String qrUrl;

    @SerializedName("status")
    private String status;
    @SerializedName("createdAt")
    private String createdAt;

    public String getOrderId() { return orderId; }
    public int getAmount() { return amount; }
    public String getQrUrl() { return qrUrl; }
    public String getStatus() { return status; }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
