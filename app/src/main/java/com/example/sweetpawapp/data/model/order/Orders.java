package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Orders implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("items")
    private List<OrderItem> items;

    @SerializedName("total_price")
    private int totalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("ghn_order_code")
    private String ghnOrderCode;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("thumbnail_url")
    private String thumbnail_url;


    public String getId() {
        return id;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getGhnOrderCode() {
        return ghnOrderCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public String getThumbnail_url(){
        return thumbnail_url;
    }
}
