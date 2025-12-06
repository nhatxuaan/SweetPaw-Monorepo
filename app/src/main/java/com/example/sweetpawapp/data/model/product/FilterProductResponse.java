package com.example.sweetpawapp.data.model.product;

import java.util.List;

public class FilterProductResponse {
    private boolean success;
    private String message;
    private List<Product> data;

    public FilterProductResponse() {}

    public FilterProductResponse(boolean success, String message, List<Product> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getter & Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Product> getData() {
        return data;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }
}
