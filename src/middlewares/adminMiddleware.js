const jwt = require("jsonwebtoken");

const verifyToken =  async (req, res, next) => {
    try {
        const authHeader = req.headers.authorization;

        if (!authHeader || !authHeader.startsWith("Bearer ")) {
            return res.status(401).json({ error: "Không có token" });
        }

        const token = authHeader.split(" ")[1];
        const decoded = jwt.verify(token, process.env.JWT_SECRET);

        // decoded sẽ gồm: { id, role, iat, exp }
        req.user = decoded;

        next();
    } catch (error) {
        return res.status(401).json({ error: "Token không hợp lệ" });
    }
};

const verifyAdmin = async (req, res, next) => {
    if (req.user.role !== "admin") {
        return res.status(403).json({ error: "Bạn không có quyền truy cập" });
    }
    next();
};

module.exports = {
    verifyToken,
    verifyAdmin,
}
