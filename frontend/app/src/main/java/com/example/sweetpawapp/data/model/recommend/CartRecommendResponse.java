package com.example.sweetpawapp.data.model.recommend;

import com.example.sweetpawapp.data.model.product.Product;

import java.util.List;

public class CartRecommendResponse {
    public boolean success;
    public List<Product> products;

//    public static class ProductRecommend {
//        public String _id;
//        public int id;
//        public String name;
//        public String category;
//        public String flavor;
//        public int cost;
//        public int price;
//        public String url;
//        public String des;
//        public int stock;
//        public int sold_count;
//        public double rating_avg;
//        public double weight;
//        public double score;
//
//        public String get_id() {
//            return _id;
//        }
//
//        public void set_id(String _id) {
//            this._id = _id;
//        }
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getCategory() {
//            return category;
//        }
//
//        public void setCategory(String category) {
//            this.category = category;
//        }
//
//        public String getFlavor() {
//            return flavor;
//        }
//
//        public void setFlavor(String flavor) {
//            this.flavor = flavor;
//        }
//
//        public int getCost() {
//            return cost;
//        }
//
//        public void setCost(int cost) {
//            this.cost = cost;
//        }
//
//        public int getPrice() {
//            return price;
//        }
//
//        public void setPrice(int price) {
//            this.price = price;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//
//        public void setUrl(String url) {
//            this.url = url;
//        }
//
//        public String getDes() {
//            return des;
//        }
//
//        public void setDes(String des) {
//            this.des = des;
//        }
//
//        public int getStock() {
//            return stock;
//        }
//
//        public void setStock(int stock) {
//            this.stock = stock;
//        }
//
//        public int getSold_count() {
//            return sold_count;
//        }
//
//        public void setSold_count(int sold_count) {
//            this.sold_count = sold_count;
//        }
//
//        public double getRating_avg() {
//            return rating_avg;
//        }
//
//        public void setRating_avg(double rating_avg) {
//            this.rating_avg = rating_avg;
//        }
//
//        public double getWeight() {
//            return weight;
//        }
//
//        public void setWeight(double weight) {
//            this.weight = weight;
//        }
//
//        public double getScore() {
//            return score;
//        }
//
//        public void setScore(double score) {
//            this.score = score;
//        }
//    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
