const express = require("express");
const router = express.Router();
const chatController = require("../controllers/chatController");
const authMiddleware = require("../middlewares/authMiddleware");

// Lấy tất cả cuộc trò chuyện cho admin
router.get("/admin/all", authMiddleware, chatController.getAllChatsForAdminController);

// Gửi tin nhắn trong cuộc trò chuyện giữa user và admin
router.post("/message", authMiddleware, chatController.sendMessageController);

// Lấy hoặc tạo mới cuộc trò chuyện giữa user và admin
router.get("/chat-or-create", authMiddleware, chatController.getOrCreateChatController);


module.exports = router;