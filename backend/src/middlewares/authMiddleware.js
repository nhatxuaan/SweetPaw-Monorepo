const jwt = require("jsonwebtoken");

// kiểm tra token để cho đi vào controller

const authMiddleware = (req, res, next) => {
    try {
        const authHeader = req.headers.authorization;

        if (!authHeader || !authHeader.startsWith("Bearer ")) {
        return res.status(401).json({ error: "Không có token, truy cập bị từ chối" });
        }

        const token = authHeader.split(" ")[1]; // tách token ra
        const decoded = jwt.verify(token, process.env.JWT_SECRET); // giải mã token

        req.user = { id: decoded.id }; // lưu thông tin user vào req
        next(); // cho phép đi tiếp vào controller
    }
    catch(error) {
        res.status(401).json({ error: "Token không hợp lệ" });
    }
};



module.exports = authMiddleware;