const express = require("express")
const { route } = require("./authRoutes")
const { getProfileUser, updateProfileUser, changePasswordUser, logoutUserController, deleteUserController, addAddressController, setDefaultAddressController, deleteAddressController, verifyOtp, resetPassword, checkEmailAndSendOtp, updateAddressController} = require("../controllers/userController");
const authMiddleware = require("../middlewares/authMiddleware");


const router = express.Router()

// Endpoint xem profile 
router.get("/me", authMiddleware, getProfileUser);

// Endpoint cập nhật profile 
router.patch("/me", authMiddleware, updateProfileUser)

//Endpoint đổi mật khẩu 
router.patch("/change-password", authMiddleware, changePasswordUser)

//Endpoint đăng xuất 
router.post("/logout", authMiddleware, logoutUserController);

// Endpoint xóa tài khoản
router.delete("/delete", authMiddleware, deleteUserController);

// Endpoint check email - gửi OTP
router.post("/checkemail-sendotp", checkEmailAndSendOtp);

// Endpoint xác minh OTP
router.post("/verify-otp", verifyOtp);

// Endpoint reset password
router.post("/reset-password", resetPassword);

//Endpoint add address  
router.put("/:id/add-address", authMiddleware, addAddressController);

//Endpoint address default 
router.patch("/:id/address/default", authMiddleware, setDefaultAddressController);

//Endpoint xóa địa chỉ
router.delete("/:id/address/deleteAddress", authMiddleware, deleteAddressController);

//Endpoint cập nhật địa chỉ
router.patch("/:id/address/updateAddress", authMiddleware, updateAddressController);

module.exports = router;