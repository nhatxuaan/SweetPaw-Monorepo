package com.example.sweetpawapp.data.model.product;

import java.util.List;

public class ProductResponse {
    private int status;
    private String message;
    private List<Product> data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Product> getData() {
        return data;
    }
}
