package com.example.sweetpawapp.data.model.user;

public class UpdateProfileRequest {
    private String fullName;
    private String email;
    private String phone;

    private String address;
    private String birthday;
    public UpdateProfileRequest(String fullName, String email, String phone, String address, String birthday) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.birthday = birthday;
    }
    public UpdateProfileRequest(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }
    // Getter & Setter
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getBirthday() { return birthday; }
}
