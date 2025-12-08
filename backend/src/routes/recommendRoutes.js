const express = require("express");
const router = express.Router();
const recommendController = require("../controllers/recommendController");
const authMiddleware = require("../middlewares/authMiddleware");

// Lấy danh sách sản phẩm gợi ý 
router.get("/home", authMiddleware, recommendController.getRecommendationHomeController)
router.post("/cart", authMiddleware, recommendController.getRecommendationCartController);

module.exports = router;