package com.example.sweetpawapp.data.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateAddressResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<User.DiaChi> data;

    // Constructor rỗng
    public UpdateAddressResponse() {}

    // Getter & Setter
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User.DiaChi> getData() {
        return data;
    }

    public void setData(List<User.DiaChi> data) {
        this.data = data;
    }
}
