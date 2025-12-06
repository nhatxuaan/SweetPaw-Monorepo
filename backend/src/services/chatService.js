const Chat = require("../models/chatModel");
const User = require("../models/userModel");
const Admin = require("../models/adminModel");
const { default: ApiError } = require("../utils/ApiError");


// Lấy tất cả cuộc trò chuyện cho admin
const getAllChatsForAdminService = async (adminId) => {
    const admin = await Admin.findById(adminId).lean();
    if (!admin) throw new ApiError(404,"Admin không tồn tại");

    const chats = await Chat.find({ admin: adminId })
        .populate("user", "HoTen Email")
        .sort({ updatedAt: -1 })
        .lean();

    return chats.map(c => ({
        chatId: c._id,
        adminName: admin.displayName || "SweetPaw",
        userId: c.user?._id || null,
        userName: c.user?.HoTen || "Người dùng đã bị xoá",
        userEmail: c.user?.Email || "",
        lastMessage: c.lastMessage,
        updatedAt: c.updatedAt
    }));
};


// Lấy hoặc tạo mới cuộc trò chuyện giữa user và admin
const getOrCreateChatService = async (userId, adminId, io, currentUserId) => {
    console.log("userId:", userId + ", adminId:", adminId + ", currentUserId:", currentUserId);
    let chat = await Chat.findOne(
        { user: userId, admin: adminId },
        { messages: { $slice: -50 } }
    )
    .populate("user", "HoTen Email")
    .populate("admin", "displayName email")
    .populate("messages.sender", "HoTen Email displayName");

    console.log("chat found:", chat ? "YES" : "NO");

    if (!chat) {
        console.log("Tạo Chat mới");
        chat = await Chat.create({
            user: userId,
            admin: adminId,
            messages: [],
            lastMessage: ""
        });

        chat = await Chat.populate(chat, [
            { path: "user", select: "HoTen Email" },
            { path: "admin", select: "displayName email" }
        ]);
    }

    chat.messages = chat.messages || [];
    console.log("độ dài message:", chat.messages.length);

    // --- Mark all unread messages as readBy current user ---
    let updated = false;
    const newlyReadMessages = [];
    chat.messages.forEach(msg => {
        if (!msg.readBy.includes(currentUserId)) {

            msg.readBy.push(currentUserId);
            updated = true;
            newlyReadMessages.push(msg._id); // lưu _id của message vừa mark
        }
    });

    console.log("tin nhắn mới đọc:", newlyReadMessages);

    if (updated) {
    await chat.save();
    console.log("Đã lưu trạng thái đoc");

    // --- Gộp messageIds theo sender khác userId để tránh spam ---
    const senderMap = {}; 

    newlyReadMessages.forEach(msgId => {
        const message = chat.messages.id(msgId);
        const senderId = message.sender.toString();
        if (senderId !== userId.toString()) {
            if (!senderMap[senderId]) senderMap[senderId] = [];
            senderMap[senderId].push(msgId);
        }
    });

    console.log("Gửi sự kiện messageRead đến các sender:", Object.keys(senderMap));

    // Emit duy nhất một lần cho mỗi sender
    Object.keys(senderMap).forEach(senderId => {
        console.log("Gửi sự kiện messageRead đến sender:", senderId);
        io.to(senderId).emit("messageRead", {
            chatId: chat._id,
            messageIds: senderMap[senderId],
            readerId: userId
        });
    });
}

    // Map lại tin nhắn để frontend biết read
    const messages = chat.messages.map(msg => ({
        ...msg.toObject(),
        read: msg.readBy.includes(userId)
    }));

    return {
        ...chat.toObject(),
        messages
    };
};


// Gửi tin nhắn trong cuộc trò chuyện giữa user và admin
const sendMessageService = async ({ userId, adminId, senderId, content, media }) => {
    if (
        senderId.toString() !== userId.toString() &&
        senderId.toString() !== adminId.toString()
    ) {
        throw new ApiError(400, "Người gửi không hợp lệ");
    }

    const [user, admin] = await Promise.all([
        User.findById(userId),
        Admin.findById(adminId)
    ]);
    if (!user) throw new ApiError(404, "User không tồn tại");
    if (!admin) throw new ApiError(404, "Admin không tồn tại");

    let chat = await Chat.findOne({ user: userId, admin: adminId });
    if (!chat) {
        chat = await Chat.create({
            user: userId,
            admin: adminId,
            messages: [],
            lastMessage: ""
        });
    }

    const senderModel = senderId.toString() === userId.toString() ? "User" : "Admin";

    const message = {
        sender: senderId,
        senderModel,
        content: content || "",
        media: media || [],
        timestamp: new Date(),
        readBy: [senderId] // sender mặc định đã đọc
    };

    const updatedChat = await Chat.findByIdAndUpdate(
        chat._id,
        {
            $push: { messages: message },
            $set: {
                lastMessage: content || "[Media]",
                updatedAt: new Date()
            }
        },
        { new: true }
    )
    .populate("messages.sender", "HoTen displayName")
    .lean();

    const sentMessage = updatedChat.messages.at(-1);

    return {
        chatId: updatedChat._id,
        message: {
            _id: sentMessage._id,
            sender: {
                _id: sentMessage.sender._id,
                name: sentMessage.sender.HoTen || sentMessage.sender.displayName
            },
            content: sentMessage.content,
            media: sentMessage.media,
            timestamp: sentMessage.timestamp,
            read: sentMessage.readBy.includes(senderId)
        }
    };
};





module.exports = {
    getOrCreateChatService,
    sendMessageService,
    getAllChatsForAdminService
};
