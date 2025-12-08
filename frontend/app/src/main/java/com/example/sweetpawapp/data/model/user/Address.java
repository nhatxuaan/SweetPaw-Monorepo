package com.example.sweetpawapp.data.model.user;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {
    private String tenLoai;     // Ví dụ: "Nhà của ABC"
    private String soNha;       // Ví dụ: "134/56/2"
    private String tenDuong;    // Ví dụ: "Nguyễn Văn Trỗi"
    private String phuongXa;    // Ví dụ: "1"
    private String quanHuyen;   // Ví dụ: "Phú Nhuận"
    private String thanhPho;    // Ví dụ: "Hồ Chí Minh"
    private boolean macDinh;    // Có phải địa chỉ mặc định không

    public Address(String tenLoai, String soNha, String tenDuong, String phuongXa,
                   String quanHuyen, String thanhPho, boolean macDinh) {
        this.tenLoai = tenLoai;
        this.soNha = soNha;
        this.tenDuong = tenDuong;
        this.phuongXa = phuongXa;
        this.quanHuyen = quanHuyen;
        this.thanhPho = thanhPho;
        this.macDinh = macDinh;
    }

    // Parcelable constructor
    protected Address(Parcel in) {
        tenLoai = in.readString();
        soNha = in.readString();
        tenDuong = in.readString();
        phuongXa = in.readString();
        quanHuyen = in.readString();
        thanhPho = in.readString();
        macDinh = in.readByte() != 0;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(tenLoai);
        parcel.writeString(soNha);
        parcel.writeString(tenDuong);
        parcel.writeString(phuongXa);
        parcel.writeString(quanHuyen);
        parcel.writeString(thanhPho);
        parcel.writeByte((byte) (macDinh ? 1 : 0));
    }

    // Getter & Setter
    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
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

    // Trả về địa chỉ đầy đủ
    public String getFullDiaChi() {
        return soNha + " " + tenDuong + ", " + phuongXa + ", " + quanHuyen + ", " + thanhPho;
    }
}
