package com.example.sweetpawapp.data.model.product;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("_id")
    private String mongoId;

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("category")
    private String category;
    @SerializedName("flavor")
    private String flavor;

    @SerializedName("cost")
    private double cost;
    @SerializedName("price")
    private double price;
    @SerializedName("url")
    private String ImageUrl;
    @SerializedName("des")
    private String descript;
    @SerializedName("stock")
    private int quantity;

    @SerializedName("sold_count")
    private int soldCount;
    @SerializedName("rating_avg")
    private double ratingAvg;
    @SerializedName("weight")
    private double weight;

    public Product() {
    }
    private boolean isFavorite = false;

    public Product(String mongoId, int id, String name, String category, String flavor, double cost, double price, String imageUrl, String descript, int quantity, int soldCount, double ratingAvg, double weight) {
        this.mongoId = mongoId;
        this.id = id;
        this.name = name;
        this.category = category;
        this.flavor = flavor;
        this.cost = cost;
        this.price = price;
        ImageUrl = imageUrl;
        this.descript = descript;
        this.quantity = quantity;
        this.soldCount = soldCount;
        this.ratingAvg = ratingAvg;
        this.weight = weight;
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}

