const express = require("express")
const { registerUser, loginUser, registerUserWithGoogle, loginUserWithGoogle } = require("../controllers/authController")
const router = express.Router()




// Endpoint đăng ký: POST /api/auth/register
router.post ("/register", registerUser);

// Endpoint đăng nhập: POST /api/auth/login
router.post("/login", loginUser);

// Endpoint đăng ký bằng gg: POST /api/auth/google-register
router.post("/google-register", registerUserWithGoogle);

// Endpoint đăng nhập bằng gg: POST /api/auth/google-login
router.post("/google-login", loginUserWithGoogle);



module.exports = router;