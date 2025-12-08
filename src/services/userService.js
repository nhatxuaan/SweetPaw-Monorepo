const User = require("../models/userModel");
const sendEmail = require("../utils/emailUtils");
const OtpCode = require("../models/otpModel");
const crypto = require("crypto");
const bcrypt = require("bcrypt");
const { default: ApiError } = require("../utils/ApiError");

const ALLOWED_ADDR_KEYS = [
  "TenDiaChi",
  "SoNha",
  "TenDuong",
  "PhuongXa",
  "QuanHuyen",
  "ThanhPho",
];



// API xem profile (GET)
// URL: /api/users/me
// Method: GET
// Middleware: authMiddleware (cần token)

const getProfile = async (userId) => {
  // Tìm user theo _id trong MongoDB
  const user = await User.findById(userId).select("-MatKhau"); // bỏ trường password

  if (!user) {
    throw new ApiError(404, "Người dùng không tồn tại");
  }

  return user; // trả về thông tin user (id, name, email, ...)
};

// API cập nhật profile (PATCH)
// URL: /api/users/me
// Method: PUT hoặc PATCH (tùy muốn update toàn bộ hay một vài trường)
// Middleware: authMiddleware



// Cập nhật profile
const updateProfile = async (userId, updateData) => {
  const user = await User.findByIdAndUpdate(
    userId,
    updateData,
    { new: true, runValidators: true } // trả về document mới + validate dữ liệu
  ).select("-MatKhau"); // bỏ password khỏi kết quả

  if (!user) {
    throw new ApiError(404, "Người dùng không tồn tại");
  }

  return user;
};



//API đổi mật khẩu 
// URL: /api/user/change-password
// Method: PATCH 
// Middleware: authMiddleware

const changePassword = async (userId, oldPassword, newPassword) => {
    // Kiểm tra người dùng có tồn tại không
    const user = await User.findById(userId);
    if (!user) throw new ApiError(404, "Người dùng không tồn tại");

    // Kiểm tra mật khẩu cũ có đúng không
    const isMatch = await bcrypt.compare(oldPassword, user.MatKhau);
    if (!isMatch) throw new ApiError(403, "Mật khẩu cũ không đúng");

    // Hash mật khẩu mới và lưu 
    user.MatKhau = newPassword;
    await user.save(); // gọi pre save để hash pass mới và lưu 

    return { message: "Đổi mật khẩu thành công" };
};


// API đăng xuất
const logoutUser = async (userId) => {
  // Không cần xử lý gì với DB
  return { message: "Đăng xuất thành công" };
};

// API xóa tài khoản 
const deleteUser = async (userId) => {
  const user = await User.findByIdAndDelete(userId);
  if (!user) throw new ApiError(404, "Người dùng không tồn tại");
  return { message: "Tài khoản đã bị xóa" };
};


// check mail và gửi OTP
const checkEmailAndSendOtp = async (email) => {
  console.log("[Service] checkEmailAndSendOtp - Email nhận:", email);

  //  Kiểm tra người dùng tồn tại
  const user = await User.findOne({ Email: email });
  if (!user) {
    console.warn("Không tìm thấy user với email:", email);
    throw new ApiError(404, "Email không tồn tại trong hệ thống!");
  }

  // Tạo mã OTP
  const otpCode = crypto.randomInt(10000, 99999).toString();
  console.log("[Service] checkEmailAndSendOtp - OTP được tạo:", otpCode);

  // Lưu hoặc cập nhật OTP vào DB
  try {
    const result = await OtpCode.findOneAndUpdate(
      { Email: email },
      { otp: otpCode, createdAt: new Date() },
      { upsert: true, new: true }
    );
    console.log("[Service] OTP được lưu/cập nhật:", result);
  } catch (err) {
    console.error("Lỗi khi lưu OTP vào database:", err);
    throw new ApiError(500, "Lỗi lưu OTP vào hệ thống.");
  }

  // Gửi OTP qua email
  try {
    await sendEmail(
      email,
      "Mã OTP xác nhận quên mật khẩu",
      `Mã OTP của bạn là: ${otpCode}. Mã có hiệu lực trong 5 phút.`
    );
    console.log("Email OTP đã được gửi thành công tới:", email);
  } catch (err) {
    console.error("Lỗi gửi email:", err);
    throw new ApiError(400, "Không thể gửi OTP. Vui lòng thử lại.");
  }

  // Trả kết quả cho FE
  return {
    boolean: true,
    expiresIn: "5 phút",
  };
};

// check otp
const verifyOtp = async (email, otp) => {
  console.log("[Service] verifyOtp - Email:", email, " | OTP nhập:", otp);

  const record = await OtpCode.findOne({ Email: email });
  if (!record) {
    console.warn("OTP không tồn tại hoặc đã hết hạn:", email);
    throw new ApiError(404, "OTP đã hết hạn hoặc không tồn tại!");
  }

  console.log("OTP lưu trong DB:", record.otp);

  if (record.otp !== otp) {
    console.warn("OTP sai cho email:", email);
    throw new ApiError(400,"Mã OTP không chính xác!");
  }

  await OtpCode.deleteOne({ Email: email });
  console.log("OTP hợp lệ. Cho phép reset mật khẩu:", email);

  return {
    boolean: true,
    allowReset: true, // flag để FE cho phép đặt lại mật khẩu
  };
};

// reset password
const resetPassword = async (email, newPassword, confirmPassword) => {
  console.log("[Service] resetPassword - Email:", email);

  if (!email || !newPassword || !confirmPassword) {
    throw new ApiError(400, "Vui lòng nhập đầy đủ thông tin!");
  }

  if (newPassword !== confirmPassword) {
    console.warn("Mật khẩu xác nhận không khớp:", email);
    throw new ApiError(400, "Mật khẩu xác nhận không khớp!");
  }

  const user = await User.findOne({ Email: email });
  if (!user) {
    console.warn("Không tìm thấy user:", email);
    throw new ApiError(400, "Email không tồn tại trong hệ thống!");
  }
 
  user.MatKhau = newPassword;
  await user.save(); 
  console.log("Mật khẩu đã được đặt lại thành công cho:", email);
  return { boolean: true };
};


// Hàm lọc dữ liệu địa chỉ
function sanitizeAddress(payload) {
  if (!payload || typeof payload !== "object") return null;
  const addr = {};
  ALLOWED_ADDR_KEYS.forEach((k) => {
    if (payload[k] !== undefined) addr[k] = payload[k];
  });
  
  return addr;
}

// thêm địa chỉ
const addAddress = async (id, rawAddress) => {

   const address = sanitizeAddress(rawAddress);
  if (!address)
    throw new ApiError(400, "Dữ liệu địa chỉ không hợp lệ");

  const user = await User.findById(id);
  if (!user)
    throw new ApiError(404, "Không tìm thấy khách hàng");

  if (!Array.isArray(user.DiaChi)) user.DiaChi = [];

  // Thêm địa chỉ mới
  user.DiaChi.push(address);

  await user.save();

  return {
    message: "Thêm địa chỉ mới thành công",
    diaChi: user.DiaChi,
  };
};

const setDefaultAddress = async (userId, index) => {
  console.log(`[Service] setDefaultAddress - userId: ${userId}, index: ${index}`);
  const user = await User.findById(userId);
  if (!user) throw new ApiError(404, "Không tìm thấy khách hàng");

  if (!Array.isArray(user.DiaChi) || user.DiaChi.length === 0)
    throw new ApiError(404, "Khách hàng chưa có địa chỉ nào");

  if (index < 0 || index >= user.DiaChi.length)
    throw new ApiError(404, "Vị trí địa chỉ không hợp lệ");

  // Gán tất cả MacDinh = false
  user.DiaChi.forEach((a) => (a.MacDinh = false));

  // Set địa chỉ được chọn là mặc định
  user.DiaChi[index].MacDinh = true;

  await user.save();

  console.log(`[Service] Địa chỉ mặc định đã được cập nhật cho userId: ${userId}`);
  console.log("[Service] Danh sách địa chỉ sau khi cập nhật:");
  user.DiaChi.forEach((addr, i) => {
    console.log(`   [${i}] ${addr.TenDiaChi}, ${addr.SoNha} ${addr.TenDuong}, Mặc định: ${addr.MacDinh}`);
  });

  return {
    message: "Cập nhật địa chỉ mặc định thành công",
    diaChi: user.DiaChi,
  };

};

const deleteAddress = async(userId, index) => {
  const user = await User.findById(userId);
  if (!user) throw new ApiError(404, "Không tìm thấy khách hàng");

  if (!Array.isArray(user.DiaChi) || user.DiaChi.length === 0)
    throw new ApiError(404, "Khách hàng chưa có địa chỉ nào");

  if (index < 0 || index >= user.DiaChi.length)
    throw new ApiError(404, "Vị trí địa chỉ không hợp lệ");

  const wasDefault = user.DiaChi[index].MacDinh;

  user.DiaChi.splice(index, 1);

  if (wasDefault && user.DiaChi.length > 0) {
    user.DiaChi[0].MacDinh = true;
  }

  await user.save();

  return {
    message: "Xóa địa chỉ thành công", 
    diaChi: user.DiaChi,
  }
};

const updateAddress = async (userId, index, rawAddress) => {
  console.log(`[Service] updateAddress - userId: ${userId}, index: ${index}`);

  // Lọc dữ liệu đầu vào
  const updatedData = sanitizeAddress(rawAddress);
  if (!updatedData)
    throw new ApiError(400, "Dữ liệu địa chỉ không hợp lệ");

  // Tìm user
  const user = await User.findById(userId);
  if (!user) throw new ApiError(404, "Không tìm thấy khách hàng");

  if (!Array.isArray(user.DiaChi) || user.DiaChi.length === 0)
    throw new ApiError(404, "Khách hàng chưa có địa chỉ nào");

  if (index < 0 || index >= user.DiaChi.length)
    throw new ApiError(400, "Vị trí địa chỉ không hợp lệ");

  // Lấy địa chỉ cũ
  const oldAddress = user.DiaChi[index];

  // Giữ lại trạng thái mặc định nếu không gửi từ FE
  const keepDefaultStatus = oldAddress.MacDinh;

  // Cập nhật các field được phép
  Object.keys(updatedData).forEach(key => {
    user.DiaChi[index][key] = updatedData[key];
  });

  // Không cho phép sửa MacDinh ở API này
  user.DiaChi[index].MacDinh = keepDefaultStatus;

  await user.save();

  return {
    message: "Cập nhật địa chỉ thành công",
    diaChi: user.DiaChi,
  };
};



module.exports = {
  getProfile,
  updateProfile,
  changePassword, 
  logoutUser, 
  deleteUser,
  checkEmailAndSendOtp,
  verifyOtp,
  resetPassword, 
  addAddress,
  setDefaultAddress,
  deleteAddress,
  updateAddress,
};