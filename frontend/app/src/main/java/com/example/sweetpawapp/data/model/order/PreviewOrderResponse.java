package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

public class PreviewOrderResponse {
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
        @SerializedName("subtotal")
        private int subtotal;

        @SerializedName("shipping_fee")
        private int shippingFee;

        @SerializedName("discount_amount")
        private int discountAmount;

        @SerializedName("total")
        private int total;

        @SerializedName("address_used")
        private Object addressUsed; // hoặc tạo model AddressResponse nếu cần

        public int getSubtotal() {
            return subtotal;
        }

        public int getShippingFee() {
            return shippingFee;
        }

        public int getDiscountAmount() {
            return discountAmount;
        }

        public int getTotal() {
            return total;
        }

        public Object getAddressUsed() {
            return addressUsed;
        }
    }
}
