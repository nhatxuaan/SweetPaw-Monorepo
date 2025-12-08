package com.example.sweetpawapp.data.model.user;

import java.util.List;

public class AddAddressResponse {
    private String message;
    private List<User.DiaChi> diaChi;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<User.DiaChi> getDiaChi() { return diaChi; }
    public void setDiaChi(List<User.DiaChi> diaChi) { this.diaChi = diaChi; }

}
