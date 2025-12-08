package com.example.sweetpawapp.data.model.user;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class User {

    @SerializedName("id")
    private String id;

    @SerializedName("fullName")
    private String hoTen;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String soDienThoai;

    @SerializedName("address")
    private List<DiaChi> diaChiList;


    // ======= Getter & Setter =======

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public List<DiaChi> getDiaChiList() {
        return diaChiList;
    }

    public void setDiaChiList(List<DiaChi> diaChiList) {
        this.diaChiList = diaChiList;
    }


    // ======= Nested class DiaChi =======
    public static class DiaChi {

        @SerializedName("TenDiaChi")
        private String tenDiaChi;

        @SerializedName("SoNha")
        private String soNha;

        @SerializedName("TenDuong")
        private String tenDuong;

        @SerializedName("PhuongXa")
        private String phuongXa;

        @SerializedName("QuanHuyen")
        private String quanHuyen;

        @SerializedName("ThanhPho")
        private String thanhPho;

        @SerializedName("MacDinh")
        private boolean macDinh;

        public DiaChi(String tenDiaChi, String soNha, String tenDuong, String phuongXa,
                      String quanHuyen, String thanhPho, boolean macDinh) {
            this.tenDiaChi = tenDiaChi;
            this.soNha = soNha;
            this.tenDuong = tenDuong;
            this.phuongXa = phuongXa;
            this.quanHuyen = quanHuyen;
            this.thanhPho = thanhPho;
            this.macDinh = macDinh;
        }

        // ======= Getter & Setter =======

        public DiaChi() {}
        public String getTenDiaChi() {
            return tenDiaChi;
        }

        public void setTenDiaChi(String tenDiaChi) {
            this.tenDiaChi = tenDiaChi;
        }

        public String getSoNha() {
            return soNha;
        }

        public void setSoNha(String soNha) {
            this.soNha = soNha;
        }

        public String getTenDuong() {
            return tenDuong;
        }

        public void setTenDuong(String tenDuong) {
            this.tenDuong = tenDuong;
        }

        public String getPhuongXa() {
            return phuongXa;
        }

        public void setPhuongXa(String phuongXa) {
            this.phuongXa = phuongXa;
        }

        public String getQuanHuyen() {
            return quanHuyen;
        }

        public void setQuanHuyen(String quanHuyen) {
            this.quanHuyen = quanHuyen;
        }

        public String getThanhPho() {
            return thanhPho;
        }

        public void setThanhPho(String thanhPho) {
            this.thanhPho = thanhPho;
        }

        public boolean isMacDinh() {
            return macDinh;
        }

        public void setMacDinh(boolean macDinh) {
            this.macDinh = macDinh;
        }
    }
}
