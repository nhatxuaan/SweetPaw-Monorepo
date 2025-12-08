package com.example.sweetpawapp.data.model.cart;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCartResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private CartData data;

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public CartData getData() { return data; }

    public static class CartData {
        @SerializedName("items")
        private List<CartItem> items;

        public List<CartItem> getItems() { return items; }
    }


}
