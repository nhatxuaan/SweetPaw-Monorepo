const mongoose = require("mongoose");
const bcrypt = require("bcrypt");

const UserSchema = new mongoose.Schema({
  MaKH: {
    type: String
  },
  HoTen: {
    type: String,
    required: true
  },
  SoDienThoai: {
    type: String
  },
  Email: {
    type: String,
    required: true
  },
  fcmToken: { 
    type: String, 
    default: null 
  },
  subscribedTopics: [
    {
      type: String,
      default: null
    }
  ],
  DiaChi: [
  {
    _id: false,
    TenDiaChi: String, // VD: Nhà riêng, Công ty
    SoNha: String,
    TenDuong: String,
    PhuongXa: String,
    QuanHuyen: String,
    ThanhPho: String,
    MacDinh: { type: Boolean, default: false }
  }
],
  NgaySinh: {
    type: Date
  },
  TenDangNhap: {
    type: String
  },
  MatKhau: {
    type: String,
    required: true
  },
  TrangThai: {
    type: String
  }
}, { timestamps: true });

//  Mã hóa mật khẩu trước khi lưu
UserSchema.pre("save", async function (next) {
  if (!this.isModified("MatKhau")) return next();
  const salt = await bcrypt.genSalt(10);
  this.MatKhau = await bcrypt.hash(this.MatKhau, salt);
  next();
});

const User = mongoose.model("User", UserSchema, "KHACHHANG");
module.exports = User;
