package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderItem implements Serializable {
    @SerializedName("product_id")
    private String productId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private int price;

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }
}
