const authService = require("../services/authService");


// controller đăng ký 
const registerUser = async(req, res, next) => {
    try {
        // gọi service đăng ký 
        const result = await authService.register(req.body);
         res.status(200).json(result);
        console.log("Đăng ký thành công");
    }
    catch(error) {
        next(error)
    }
};

// controller đăng nhập 
const loginUser = async(req, res, next) => {
    try {
        const {email, password, fcmToken, subscribedTopics} = req.body;
        const result = await authService.login(email, password, fcmToken, subscribedTopics);
        res.status(201).json(result);
    }
    catch(error) {
        next(error)
    }

};

// controller đăng ký bằng gg
const registerUserWithGoogle = async(req, res, next) => {
    try {
        const { idToken } = req.body; // Android phải gửi { "idToken": "..." }
        const result = await authService.registerWithGoogle(idToken);
        res.status(201).json(result);
    } catch(error) {
        next(error);
    }
};

// controller đăng nhập bằng gg
const loginUserWithGoogle = async(req, res, next) => {

    try {
        const { idToken, fcmToken, subscribedTopics } = req.body;
        const result = await authService.loginWithGoogle(idToken, fcmToken, subscribedTopics);
        res.status(200).json(result);
    } catch(error) {
        next(error);
    }

};


module.exports = {
    registerUser, 
    loginUser,
    registerUserWithGoogle,
    loginUserWithGoogle,

};