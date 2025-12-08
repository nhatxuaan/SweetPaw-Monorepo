package com.example.sweetpawapp.data.model.recommend;

import java.util.List;

public class CartRecommendRequest {
    public List<String> cartProductIds;

    public CartRecommendRequest(List<String> cartProductIds) {
        this.cartProductIds = cartProductIds;
    }
}
