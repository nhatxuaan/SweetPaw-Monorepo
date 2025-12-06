package com.example.sweetpawapp.data.model.cart;

public class AddToCartRequest {
    private String userId;
    private String productId;
    private int quantity;

    public AddToCartRequest(String userId, String productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getUserId() { return userId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}
