package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderDetailItem implements Serializable {
    @SerializedName("productId")
    private String productId;

    @SerializedName("name")
    private String name;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private int price;

    @SerializedName("image")
    private String image;

    @SerializedName("total")
    private int total;

    // Getters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }
    public String getImage() { return image; }
    public int getTotal() { return total; }
}
