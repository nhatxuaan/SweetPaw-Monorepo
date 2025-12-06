package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

public class PaymentStatusResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("orderId")
        private String orderId;

        @SerializedName("paymentStatus")
        private String paymentStatus;

        public String getOrderId() {
            return orderId;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }
    }
}
