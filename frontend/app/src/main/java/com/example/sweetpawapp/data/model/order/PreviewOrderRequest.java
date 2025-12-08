package com.example.sweetpawapp.data.model.order;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PreviewOrderRequest {
    @SerializedName("discount_code")
    private String discount_code;

    @SerializedName("to_name")
    private String toName;

    @SerializedName("to_phone")
    private String toPhone;

    @SerializedName("to_address")
    private String toAddress;

    @SerializedName("to_ward_name")
    private String toWardName;

    @SerializedName("to_district_name")
    private String toDistrictName;

    @SerializedName("to_province_name")
    private String toProvinceName;

    @SerializedName("note")
    private String note;

    @SerializedName("items")
    private List<Item> items;

    public PreviewOrderRequest(String toName, String toPhone, String toAddress, String toWardName,
                               String toDistrictName, String toProvinceName, String note, List<Item> items, String discount_code) {
        this.toName = toName;
        this.toPhone = toPhone;
        this.toAddress = toAddress;
        this.toWardName = toWardName;
        this.toDistrictName = toDistrictName;
        this.toProvinceName = toProvinceName;
        this.note = note;
        this.items = items;
        this.discount_code = discount_code;
    }

    // Lớp con Item
    public static class Item {
        @SerializedName("productId")
        private String productId;

        @SerializedName("name")
        private String name;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("price")
        private int price;

        @SerializedName("weight")
        private int weight;

        public Item(String productId, String name, int quantity, int price, int weight) {
            this.productId = productId;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.weight = weight;
        }
    }
}
