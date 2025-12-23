const express = require("express");
const router = express.Router();
const { sendToTokenController, sendToTopicController, getNotificationsController, MarkNotificationController, getUnreadNotificationsController } = require("../controllers/notificationController");
const authMiddleware = require("../middlewares/authMiddleware");

// Lấy tất cả notification của user
router.get("/", authMiddleware, getNotificationsController);

// Gửi notification đến một thiết bị cụ thể bằng token
router.post("/token", authMiddleware, sendToTokenController);

// Gửi notification theo topic
router.post("/topic", authMiddleware, sendToTopicController);

// Đánh dấu một notification là đã đọc
router.post("/mark-as-read/:notificationId", authMiddleware, MarkNotificationController);

// Lấy tất cả notification chưa đọc của user
router.get("/unread", authMiddleware, getUnreadNotificationsController);

module.exports = router;
