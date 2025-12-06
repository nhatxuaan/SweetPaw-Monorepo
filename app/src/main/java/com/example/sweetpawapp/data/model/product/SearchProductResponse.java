package com.example.sweetpawapp.data.model.product;

import java.util.List;

public class SearchProductResponse {
    private String message;
    private List<Product> data;

    public String getMessage() {
        return message;
    }

    public List<Product> getData() {
        return data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }
}
