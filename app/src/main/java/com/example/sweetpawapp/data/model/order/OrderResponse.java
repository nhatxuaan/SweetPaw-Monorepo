package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private OrderDataWrapper data;
    public String getMessage() { return message; }
    public OrderDataWrapper getData() { return data; }

    public static class OrderDataWrapper {

        @SerializedName("code")
        private int code;

        @SerializedName("message")
        private String message;

        @SerializedName("data")
        private OrderInfo data;

        @SerializedName("orderId")
        private String orderId;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        @SerializedName("message_display")
        private String messageDisplay;

        public int getCode() { return code; }
        public String getMessage() { return message; }
        public OrderInfo getData() { return data; }
        public String getMessageDisplay() { return messageDisplay; }
    }

    public static class OrderInfo {
        @SerializedName("order_code")
        private String orderCode;

        @SerializedName("sort_code")
        private String sortCode;

        @SerializedName("trans_type")
        private String transType;

        @SerializedName("expected_delivery_time")
        private String expectedDelivery;

        @SerializedName("fee")
        private Fee fee;

        @SerializedName("total_fee")
        private int totalFee;

        @SerializedName("total_price")
        private int totalPrice;
        public String getOrderCode() { return orderCode; }
        public int getTotalPrice() { return totalPrice; }
    }

    public static class Fee {
        @SerializedName("main_service")
        private int mainService;
    }
}
