const mongoose = require("mongoose");

const otpSchema = new mongoose.Schema({
  Email: { type: String, required: true, unique: true },
  otp: { type: String, required: true },
  createdAt: { type: Date, default: Date.now, expires: 300 }, // Tự xoá sau 5 phút
});


const OtpCode = mongoose.model("OtpCode", otpSchema);
module.exports = OtpCode;
