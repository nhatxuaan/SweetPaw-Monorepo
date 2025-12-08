package com.example.sweetpawapp.data.model.user;

import com.google.gson.annotations.SerializedName;

public class DeleteAddressRequest {
    @SerializedName("index")
    private int index;

    public DeleteAddressRequest(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
