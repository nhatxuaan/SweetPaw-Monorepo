package com.example.sweetpawapp.data.model.product;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductDetail {
    @SerializedName("_id")
    private String mongoId;

    private int id;
    private String name;
    private String category;
    private String flavor;
    private int cost;
    private int price;
    private String url;
    private String des;
    private int stock;

    @SerializedName("sold_count")
    private int soldCount;

    @SerializedName("rating_avg")
    private float ratingAvg;

    private double weight;

    @SerializedName("ratings")
    private List<RatingItem> ratings;

    @SerializedName("avgRating")
    private float avgRating;

    public String getMongoId() {
        return mongoId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getFlavor() {
        return flavor;
    }

    public int getCost() {
        return cost;
    }

    public int getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }

    public String getDes() {
        return des;
    }

    public int getStock() {
        return stock;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public float getRatingAvg() {
        return ratingAvg;
    }

    public double getWeight() {
        return weight;
    }

    public List<RatingItem> getRatings() {
        return ratings;
    }

    public float getAvgRating() {
        return avgRating;
    }
}
