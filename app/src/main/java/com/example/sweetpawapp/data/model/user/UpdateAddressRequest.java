package com.example.sweetpawapp.data.model.user;

import com.google.gson.annotations.SerializedName;

public class UpdateAddressRequest {
    @SerializedName("index")
    private int index;

    @SerializedName("data")
    private User.DiaChi data;

    public UpdateAddressRequest(int index, User.DiaChi data) {
        this.index = index;
        this.data = data;
    }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public User.DiaChi getData() { return data; }
    public void setData(User.DiaChi data) { this.data = data; }
}
