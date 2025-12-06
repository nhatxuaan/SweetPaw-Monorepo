package com.example.sweetpawapp.data.model.product;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ToggleFavoriteResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private FavoriteData data;

    // Getter & Setter
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public FavoriteData getData() { return data; }
    public void setData(FavoriteData data) { this.data = data; }

    // Inner class cho phần "data"
    public static class FavoriteData {
        @SerializedName("_id")
        private String id;

        @SerializedName("userId")
        private String userId;

        @SerializedName("products")
        private List<String> products;

        @SerializedName("updatedAt")
        private String updatedAt;

        @SerializedName("__v")
        private int v;

        // Getter & Setter
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public List<String> getProducts() { return products; }
        public void setProducts(List<String> products) { this.products = products; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

        public int getV() { return v; }
        public void setV(int v) { this.v = v; }
    }
}
