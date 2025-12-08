const userService = require("../services/userService")
const { default: ApiError } = require("../utils/ApiError");




// Xem profile
const getProfileUser = async(req, res, next) => {
    try {
        const userId = req.user.id; // lấy từ authMiddleware
        const user = await userService.getProfile(userId);

        const profile = {
            id: user._id,
            fullName: user.HoTen || "",
            email: user.Email || "",
            phone: user.SoDienThoai || "",
            address: user.DiaChi || "",
            birthday: user.NgaySinh || ""
        };

        res.status(200).json(profile);
    }
    catch (error) {
        next(error)
    }
};


// Cập nhật profile (PATCH)

const updateProfileUser = async (req, res, next) => {
    try {
        const userId = req.user.id; // lấy từ token
        const updateData = {};

        if (req.body.fullName) updateData.HoTen = req.body.fullName;
        if (req.body.email) updateData.Email = req.body.email;
        if (req.body.phone) updateData.SoDienThoai = req.body.phone;
        if (req.body.address) updateData.DiaChi = req.body.address;
        if (req.body.birthday) updateData.NgaySinh = req.body.birthday;

        const user = await userService.updateProfile(userId, updateData);

        // Trả về JSON đầy đủ các field FE cần, nếu DB chưa có thì để trống
        res.status(200).json({
        id: user._id,
        fullName: user.HoTen || "",
        email: user.Email || "",
        phone: user.SoDienThoai || "",
        address: user.DiaChi || "",
        birthday: user.NgaySinh || ""
        });

    }

    catch(error) {
        next(error)
    }
};

// Đổi mật khẩu 

const changePasswordUser = async (req, res, next) => {
  try {
    const userId = req.user.id; // lấy id từ token
    const { oldPassword, newPassword } = req.body; // ✅ lấy từ body

    if (!oldPassword || !newPassword) {
      return res.status(400).json({ error: "Cần nhập đầy đủ mật khẩu cũ và mật khẩu mới" });
    }

    const result = await userService.changePassword(userId, oldPassword, newPassword);

    res.status(200).json(result); // trả về message thành công

  } catch (error) {
    next(error)
  }
};

// Đăng xuất 

const logoutUserController = async (req, res, next) => {
  try {
    const userId = req.user.id; // lấy từ token
    const result = await userService.logoutUser(userId);

    res.status(200).json(result);
  } catch (error) {
    next(error)
  }
};

// Xóa tài khoản 

const deleteUserController = async(req, res, next) => {
    try {
        const userId = req.user.id; 
        const result = await userService.deleteUser(userId);

        res.status(200).json(result);
    }
    catch (error) {
        next(error)
    }
};

// check mail và gửi OTP
const checkEmailAndSendOtp = async (req, res, next) => {
  try {
    console.log("[checkEmailAndSendOtp] Body:", req.body);
    const { email } = req.body;

    if (!email) {
      console.warn("Thiếu email trong request!");
      return res.status(400).json({ message: "Vui lòng nhập email!" });
    }

    const result = await userService.checkEmailAndSendOtp(email);
    console.log("[checkEmailAndSendOtp] Kết quả:", result);
    res.status(200).json(result);
  } catch (err) {
    console.error("[checkEmailAndSendOtp] Lỗi:", err.message);
    next(err)
  }
};

// check otp
const verifyOtp = async (req, res, next) => {
  try {
    console.log("[verifyOtp] Body:", req.body);
    const { email, otp } = req.body;
    if (!email || !otp) {
      console.warn(" Thiếu email hoặc otp trong request!");
      return res.status(400).json({ message: "Vui lòng nhập đầy đủ email và mã OTP!" });
    }
    const result = await userService.verifyOtp(email, otp);
    console.log("[verifyOtp] Kết quả:", result);
    res.status(200).json(result);
  } catch (err) {
    console.error("[verifyOtp] Lỗi:", err.message);
    next(err)
  }
};

// reset password
const resetPassword = async (req, res, next) => {
  try {
    console.log("[resetPassword] Body:", req.body);
    const { email, newPassword, confirmPassword } = req.body;
    if (!email || !newPassword || !confirmPassword) {
      console.warn("Thiếu thông tin cần thiết trong request!");
      return res.status(400).json({ message: "Vui lòng nhập đầy đủ thông tin!" });
    }   
    const result = await userService.resetPassword(email, newPassword, confirmPassword);
    console.log("[resetPassword] Kết quả:", result);
    res.status(200).json(result);
  } catch (err) {
    console.error("[resetPassword] Lỗi:", err.message);
    next(err);
  }
};

const addAddressController = async (req, res, next) => {
   try {
    const userId = req.params.id;
    const address = req.body; // từ body

    const result = await userService.addAddress(userId, address);
    return res.status(200).json(result);
  } catch (error) {
    next(error);
  }
};

const setDefaultAddressController = async(req, res, next) => {
  try {
    const userId = req.params.id;
    const { index } = req.body; // index của địa chỉ được chọn

    console.log(`[setDefaultAddress] userId: ${userId}, index: ${index}`);

    const result = await userService.setDefaultAddress(userId, index);

    // Log chi tiết danh sách địa chỉ trả về
    if (result && result.DiaChi) {
      console.log(`[setDefaultAddress] Danh sách địa chỉ sau khi cập nhật:`);
      result.DiaChi.forEach((addr, i) => {
        console.log(`   [${i}] ${addr.TenDiaChi}, ${addr.SoNha} ${addr.TenDuong}, Mặc định: ${addr.MacDinh}`);
      });
    } else {
      console.warn("[setDefaultAddress] result hoặc result.DiaChi null");
    }
    
    return res.status(200).json(result);
  } catch(error) {next(error)}
};

const deleteAddressController = async(req, res, next) => {
  try{
    const userId = req.params.id;
    const index = req.body.index; // index của địa chỉ được chọn

    const result = await userService.deleteAddress(userId, index);
    return res.status(200).json(result);
  } catch (error) {next(error)}
}

// Sửa địa chỉ (không dùng asyncHandler)
const updateAddressController = async (req, res, next) => {
  try {
    const userId = req.params.id; // nếu muốn sửa cho chính user, có thể dùng req.user.id
    const index = Number(req.body.index); // index địa chỉ cần sửa
    const updateData = req.body.data;   // dữ liệu mới từ FE

    // Validate index
    if (isNaN(index)) {
      throw new ApiError(400, "Index địa chỉ phải là số");
    }

    const result = await userService.updateAddress(userId, index, updateData);

    return res.status(200).json({
      status: 200,
      message: result.message,
      data: result.diaChi,
    });
  } catch (error) {
    // Gửi lỗi cho middleware xử lý
    next(error);
  }
};



module.exports = {
    getProfileUser, 
    updateProfileUser,
    changePasswordUser, 
    logoutUserController, 
    deleteUserController,
    checkEmailAndSendOtp,
    verifyOtp,
    resetPassword, 
    addAddressController,
    setDefaultAddressController,
    deleteAddressController,
    updateAddressController,
};