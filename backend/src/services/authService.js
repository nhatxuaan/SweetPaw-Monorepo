const User = require("../models/userModel");
const { sendToToken } = require("./notificationService");
const { OAuth2Client } = require('google-auth-library');
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);
const { default: ApiError } = require("../utils/ApiError");


// API đăng ký
const register = async (userData) => {
  const { name, email, password } = userData;

  // Kiểm tra email đã tồn tại chưa
  const existingUser = await User.findOne({ Email: email });
  if (existingUser) {
    throw new ApiError(409,"Email đã được sử dụng");
  }

  // Tạo user mới
  const newUser = new User({ HoTen: name, Email: email, MatKhau: password }); // name: họ và tên
  await newUser.save(); // Tự động hash nhờ middleware pre('save') trong model

 
  return {
    Boolean: true,
    id: newUser._id,
    name: newUser.HoTen,
    email: newUser.Email,
    password: newUser.MatKhau
  };
};


// APi đăng nhập
const login = async(email, password, fcmToken, subscribedTopics) => {
    // Lấy truy vấn email có trong csdl k 
    const user = await User.findOne({Email: email});

    //console.log(user)

    // Kiểm tra email
    if (!user) throw new ApiError(404, "Không tìm thấy người dùng");

    // kiểm tra mật khẩu 
    const isMatch = await bcrypt.compare(password, user.MatKhau);
    console.log(user.MatKhau)
    if (!isMatch) throw new ApiError(401, "Sai mật khẩu");

    if (fcmToken) {
      user.fcmToken = fcmToken;
      await user.save();
      }
    if (subscribedTopics) {
      user.subscribedTopics = subscribedTopics;
      await user.save();
    }

    const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET,{ expiresIn: process.env.JWT_EXPIRES_IN });

    await sendToToken({
      fcmToken: user.fcmToken,
      title: "Chào mừng trở lại với SweetPaw!",
      body: "Chúc bạn có những trải nghiệm tuyệt vời.",
      data: { login: "true" },
      userId: user._id
    });

    return {
    Boolean: true,
    token,
    user: {
      id: user._id,
      fullName: user.HoTen,
      email: user.Email,
      phone: user.SoDienThoai,
      address: user.DiaChi || []
    }
    };


};

// Hàm xác minh token gg
async function verifyGoogleToken(idToken) {
  const ticket = await client.verifyIdToken({
    idToken,
    audience: process.env.GOOGLE_CLIENT_ID,
  });

  console.log("[Backend] Verify token xong, trả về ticket");

  return ticket.getPayload(); // { email, name, picture, sub }
}

// API đăng ký bằng gg
const registerWithGoogle = async (idToken) => {
  console.log("idToken nhận:", idToken);

  let payload;
  try {
    // Thêm timeout hoặc try/catch để chắc chắn promise không treo vô hạn
    payload = await verifyGoogleToken(idToken);
    console.log("Payload Google:", payload);
  } catch (err) {
    console.error("Lỗi verify token:", err.message || err);
    throw new ApiError(400, "Token Google không hợp lệ hoặc hết hạn");
  }

  if (!payload) throw new ApiError(400, "Token Google không hợp lệ hoặc hết hạn");

  const { email, name, sub } = payload;
  console.log("Dữ liệu từ Google:", { email, name, sub });

  const existingUser = await User.findOne({ Email: email });
  if (existingUser) throw new ApiError(401, "Email đã được sử dụng");

  const newUser = new User({ HoTen: name, Email: email, MatKhau: sub });
  await newUser.save();

  const token = jwt.sign(
    { id: newUser._id },
    process.env.JWT_SECRET,
    { expiresIn: process.env.JWT_EXPIRES_IN }
  );

  return {
    Boolean: true,
    token,
    user: {
      id: newUser._id,
      name: newUser.HoTen,
      email: newUser.Email,
    },
  };
};



// API đăng nhập bằng gg
const loginWithGoogle = async (idToken, fcmToken, subscribedTopics) => {
  console.log("idToken nhận:", idToken);
  try {
    const { email } = await verifyGoogleToken(idToken);

    const user = await User.findOne({ Email: email });
    if (!user) {
      throw new ApiError(404, "Người dùng chưa đăng ký bằng Google");
    }

    if (fcmToken) {
      user.fcmToken = fcmToken;
      await user.save();
    }
    if (subscribedTopics) {
      user.subscribedTopics = subscribedTopics;
      await user.save();
    }

    const token = jwt.sign(
      { id: user._id },
      process.env.JWT_SECRET,
      { expiresIn: process.env.JWT_EXPIRES_IN }
    );

    await sendToToken({
      fcmToken: user.fcmToken,
      title: "Chào mừng trở lại với SweetPaw!",
      body: "Chúc bạn có những trải nghiệm tuyệt vời.",
      data: { login: "true" },
      userId: user._id
    });

    return {
      Boolean: true,
      token,
      user: {
        id: user._id,
        name: user.HoTen,
        email: user.Email,
      },
    };
  } catch (err) {
    throw new ApiError(404, "Token Google không hợp lệ hoặc đã hết hạn");
  }
};

module.exports = {
  register,
  login,
  registerWithGoogle,
  loginWithGoogle
};

