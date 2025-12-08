package com.example.sweetpawapp.data.model.order;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrdersListResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Orders> data;


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Orders> getData() {
        return data;
    }

    public boolean isSuccess() {
        return code == 200;  // Tự kiểm tra thành công thay vì đọc key success
    }

}
