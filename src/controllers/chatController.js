const chatService = require("../services/chatService");
const { sendToToken } = require("../services/notificationService");
const User = require("../models/userModel");
const Admin = require("../models/adminModel");

// Lấy tất cả cuộc trò chuyện cho admin
const getAllChatsForAdminController = async (req, res, next) => {
    try {
        const adminId =  req.body.adminId || req.admin.id; // admin đang login

        const chats = await chatService.getAllChatsForAdminService(adminId);

        return res.status(200).json({
            status: 200,
            message: "Lấy danh sách chat thành công",
            data: chats
        });
    } catch (error) {
        next(error);
    }
};

// Lấy hoặc tạo mới cuộc trò chuyện giữa user và admin
const getOrCreateChatController = async (req, res, next) => {
    try {
        console.log("req.user:", req.user);
        console.log("req.body:", req.body);
        const userId = req.user.id || req.body.userId;
        const admin = await Admin.findOne();
        const adminId = admin._id;
        const currentUserId = req.user.id;
        if (!adminId || !currentUserId || !userId) return res.status(400).json({ status: 400, message: "Thiếu adminId hoặc currentUserId hoặc userId" });
        console.log("userId:", userId + ", adminId:", adminId + ", currentUserId:", currentUserId);
        const io = req.app.get("io"); // lấy Socket.io instance

        const chat = await chatService.getOrCreateChatService(userId, adminId, io, currentUserId);

        return res.status(200).json({
            status: 200,
            message: "Lấy hoặc tạo chat thành công",
            data: chat
        });
    } catch (error) {
        console.error("Error in getOrCreateChatController:", error);
        next(error);
    }
};

// Gửi tin nhắn trong cuộc trò chuyện giữa user và admin
const sendMessageController = async (req, res, next) => {
    try {
        console.log("req.user:", req.user);
        console.log("req.body:", req.body);
        const { content, media } = req.body;
        const admin = await Admin.findOne();
        const adminId = admin._id;
        const userId = req.user.id || req.body.userId; // user đang login
        const senderId = req.user.id; // người gửi tin nhắn

        if (!adminId || !userId || !senderId) {
            return res.status(400).json({ status: 400, message: "Thiếu adminId hoặc userId hoặc senderId" });
        }

        const result = await chatService.sendMessageService({
            userId,
            adminId,
            senderId,
            content,
            media
        });

        const io = req.app.get("io");
        io.to(adminId).emit("userMessage", result);

        return res.status(200).json({
            status: 200,
            message: "Gửi tin nhắn thành công",
            data: result
        });
    } catch (error) {
        next(error);
    }
};




module.exports = {
    getOrCreateChatController,
    sendMessageController,
    getAllChatsForAdminController
};
