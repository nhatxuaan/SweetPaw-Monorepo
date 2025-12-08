const express = require("express");
const router = express.Router();
const ratingController = require("../controllers/ratingController");
const authMiddleware = require("../middlewares/authMiddleware");

// lấy danh sách đánh giá với filter
router.get("/", authMiddleware, ratingController.getRatingsController);

// lưu đánh giá của một sản phẩm
router.post("/createrating", authMiddleware, ratingController.createRatingController);

// cập nhật đánh giá của một sản phẩm
router.put("/updaterating", authMiddleware, ratingController.updateRatingController);

// xóa đánh giá của một sản phẩm
router.delete("/deleterating", authMiddleware, ratingController.deleteRatingController);



module.exports = router;
