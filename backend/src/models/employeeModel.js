const mongoose = require("mongoose");

const employeeSchema = new mongoose.Schema({
  MaNV: {
    type: String,
    required: true
  },
  HoTen: {
    type: String,
    required: true
  },
  ChucVu: {
    type: String,
    required: true
  },
  SoDienThoai: {
    type: String,
    required: true
  },
  NgVaoLam: {
    type: Date
  },
  Email: {
    type: String
  },
  TenDangNhap: {
    type: String
  },
  MatKhau: {
    type: String
  },
  VaiTro: {
    type: String
  },
  TrangThai: {
    type: String
  }
}, { timestamps: true }); 

const Employee = mongoose.model("Employee", employeeSchema);
module.exports = Employee;
