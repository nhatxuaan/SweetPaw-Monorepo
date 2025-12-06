package com.example.sweetpawapp.data.model.cart;

public class UpdateCartQuantityRequest {
    private String productId;
    private int change;

    public UpdateCartQuantityRequest(String productId, int change) {
        this.productId = productId;
        this.change = change;
    }

    public String getProductId() {
        return productId;
    }

    public int getChange() {
        return change;
    }
}
