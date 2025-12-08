const mongoose = require("mongoose");

// Schema cho từng tin nhắn
const messageSchema = new mongoose.Schema({
    sender: {
        type: mongoose.Schema.Types.ObjectId,
        refPath: "messages.senderModel",
        required: true
    },
    senderModel: {
        type: String,
        enum: ["User", "Admin"],
        required: true
    },
    content: { type: String, default: "" },
    media: [
        {
            type: {
                type: String,
                enum: ["image", "file", "video"],
                required: true
            },
            url: { type: String, required: true }
        }
    ],
    readBy: [
        {
            type: mongoose.Schema.Types.ObjectId,
            refPath: "messages.senderModel"
        }
    ],
    timestamp: { type: Date, default: Date.now }
}, { _id: true });

// Schema cho cuộc trò chuyện
const chatSchema = new mongoose.Schema({
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "User",
        required: true
    },
    admin: {
        type: mongoose.Schema.Types.ObjectId,
        ref: "Admin",
        required: true
    },
    messages: [messageSchema],
    lastMessage: { type: String, default: "" },
    updatedAt: { type: Date, default: Date.now }
});

// Model Chat
const Chat = mongoose.model("Chat", chatSchema, "CHAT");

module.exports = Chat;
