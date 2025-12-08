package com.example.sweetpawapp.data.model.recommend;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecommendHomeResponse {
    public boolean success;
    public List<ProductRecommend> products;

    public static class ProductRecommend {
        @SerializedName("_id")
        public String _id;

        public int id;
        public String name;
        public String category;
        public String flavor;
        public int cost;
        public int price;
        public String url;
        public String des;
        public int stock;

        @SerializedName("sold_count")
        public int soldCount;

        @SerializedName("rating_avg")
        public float ratingAvg;

        public float weight;
        public float score;
    }
}
