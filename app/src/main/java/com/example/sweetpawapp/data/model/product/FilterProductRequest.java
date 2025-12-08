package com.example.sweetpawapp.data.model.product;

import java.util.List;

public class FilterProductRequest {
    private List<String> priceRange;
    private int rating;
    private String keyword;

    public FilterProductRequest(List<String> priceRange, int rating, String keyword) {
        this.priceRange = priceRange;
        this.rating = rating;
        this.keyword = keyword;
    }

    public List<String> getPriceRange() {
        return priceRange;
    }

    public int getRating() {
        return rating;
    }

    public String getKeyword() {
        return keyword;
    }
}

