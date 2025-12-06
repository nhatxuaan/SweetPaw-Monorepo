package com.example.sweetpawapp.data.model.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CartItem implements Parcelable {
    @SerializedName("_id")
    private String id;
    @SerializedName("productId")
    private String productId;

    @SerializedName("name")
    private String name;
    @SerializedName("url")
    private String imageUrl; // rõ ràng hơn "url"
    @SerializedName("des")
    private String description;
    @SerializedName("category")
    private String category;
    @SerializedName("price")
    private int price;

    @SerializedName("quantity")
    private int quantity;
    @SerializedName("checked")
    private boolean selected;

    @SerializedName("weight")
    private int weight;
    @SerializedName("stock")
    private int stock;

    // Constructor rỗng (bắt buộc cho Gson)
    public CartItem() {
    }

    // Constructor đầy đủ
    public CartItem(String id, String productId, String name, String imageUrl, String description,
                    String category, int price, int quantity, boolean checked, int weight, int stock) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.selected = false;
        this.weight = weight;
        this.stock = stock;
    }

    // Parcelable constructor
    public CartItem(Parcel in) {
        id = in.readString();
        productId = in.readString();
        name = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        category = in.readString();
        price = in.readInt();
        quantity = in.readInt();
        selected = in.readByte() != 0;
        weight = in.readInt();
        weight = in.readInt();
    }

    // Parcelable creator
    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    // Getter / Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    // Parcelable methods

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(productId);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeInt(price);
        dest.writeInt(quantity);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeInt(weight);
        dest.writeInt(stock);
    }
}



