package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetail {

    @SerializedName("orderId")
    private String orderId;
    @SerializedName("order_code")
    private String orderCode;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("to_name")
    private String toName;

    @SerializedName("to_phone")
    private String toPhone;

    @SerializedName("to_address")
    private String toAddress;

    @SerializedName("note")
    private String note;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("display_status")
    private String displayStatus;

    @SerializedName("expected_delivery")
    private String expectedDelivery;

    @SerializedName("items")
    private List<OrderDetailItem> items;

    @SerializedName("subtotal")
    private int subtotal;

    @SerializedName("shipping_fee")
    private int shippingFee;

    @SerializedName("discountAmount")
    private int discount;

    @SerializedName("total_price")
    private int totalPrice;

    @SerializedName("ghn_tracking")
    private List<Object> ghnTracking;

    @SerializedName("payment_status")
    private String payment_status;



    // Getters
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getOrderCode() { return orderCode; }
    public String getCreatedAt() { return createdAt; }
    public String getToName() { return toName; }
    public String getToPhone() { return toPhone; }
    public String getToAddress() { return toAddress; }
    public String getNote() { return note; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDisplayStatus() { return displayStatus; }
    public String getExpectedDelivery() { return expectedDelivery; }
    public List<OrderDetailItem> getItems() { return items; }
    public int getSubtotal() { return subtotal; }
    public int getShippingFee() { return shippingFee; }
    public int getDiscount() { return discount; }
    public int getTotalPrice() { return totalPrice; }
    public List<Object> getGhnTracking() { return ghnTracking; }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }
}
