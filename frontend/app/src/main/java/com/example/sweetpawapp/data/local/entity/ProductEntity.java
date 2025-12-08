package com.example.sweetpawapp.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Bảng "products" trong SQLite
@Entity(tableName = "products")
public class ProductEntity {

    // ---- Trường ----
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "_id")
    private String _id; // MongoDB ID

    @ColumnInfo(name = "id")
    private int id; // ID nội bộ (ví dụ: auto-increment hoặc mã sản phẩm)

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "flavor")
    private String flavor;

    @ColumnInfo(name = "cost")
    private double cost;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "des")
    private String des;

    @ColumnInfo(name = "stock")
    private int stock;

    @ColumnInfo(name = "sold_count")
    private int soldCount;

    @ColumnInfo(name = "rating_avg")
    private double ratingAvg;

    @ColumnInfo(name = "weight")
    private double weight;

    // ---- Constructor ----
    public ProductEntity(@NonNull String _id, int id, String name, String category, String flavor,
                         double cost, double price, String url, String des,
                         int stock, int soldCount, double ratingAvg, double weight) {
        this._id = _id;
        this.id = id;
        this.name = name;
        this.category = category;
        this.flavor = flavor;
        this.cost = cost;
        this.price = price;
        this.url = url;
        this.des = des;
        this.stock = stock;
        this.soldCount = soldCount;
        this.ratingAvg = ratingAvg;
        this.weight = weight;
    }

    // ---- Getters & Setters ----
    @NonNull
    public String get_id() { return _id; }
    public void set_id(@NonNull String _id) { this._id = _id; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFlavor() { return flavor; }
    public void setFlavor(String flavor) { this.flavor = flavor; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDes() { return des; }
    public void setDes(String des) { this.des = des; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getSoldCount() { return soldCount; }
    public void setSoldCount(int soldCount) { this.soldCount = soldCount; }

    public double getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(double ratingAvg) { this.ratingAvg = ratingAvg; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
