package com.example.sweetpawapp.data.model.product;

import com.google.gson.annotations.SerializedName;

public class ToggleFavoriteRequest {
    @SerializedName("productId")
    private String productId;

    public ToggleFavoriteRequest(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
