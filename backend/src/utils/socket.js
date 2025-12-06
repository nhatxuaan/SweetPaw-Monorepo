const { Server } = require ("socket.io");

global.activeChatUsers = {};
global.onlineUsers = {};

let io = null;

function initSocket(server) {
    io = new Server(server, {
        cors: {
            origin: "*",
            methods: ["GET", "POST"]
        }
    });

    io.on("connection", (socket) => {
        console.log("Client connected:", socket.id);

        // Xử lý tham gia phòng
        socket.on("joinRoom", (userId) => {
            if (!userId) return;
            socket.join(userId);
            onlineUsers[userId] = socket.id;
            console.log(`User ${userId} joined room ${userId}`);
        });

        // Xử lý mở chat
        socket.on("openChat", ({ userId, chatId }) => {
            if (!userId || !chatId) return;

            activeChatUsers[userId] = chatId;
            console.log(`User ${userId} is viewing chat ${chatId}`);
        });

        socket.on("sendMessage", (msg) => {
            console.log("Realtime message:", msg);

            const { senderModel, userId } = msg;

            if (senderModel === "User") {
                io.emit("userMessage", msg); 
            }

            if (senderModel === "Admin") {
                if (userId && onlineUsers[userId]) {
                    io.to(onlineUsers[userId]).emit("adminMessage", msg);
                }
            }
        });

        // Xử lý đóng chat
        socket.on("closeChat", ({ userId }) => {
            if (userId && activeChatUsers[userId]) {
                delete activeChatUsers[userId];
                console.log(`User ${userId} closed chat`);
            }
        });

        // Xử lý ngắt kết nối
        socket.on("disconnect", () => {
            console.log("Client disconnected:", socket.id);

            // Xóa khỏi onlineUsers
            for (const uid in onlineUsers) {
                if (onlineUsers[uid] === socket.id) {
                    delete onlineUsers[uid];
                }
            }

            // Xóa khỏi activeChatUsers 
            for (const uid in activeChatUsers) {
                if (onlineUsers[uid] === socket.id) {
                    delete activeChatUsers[uid];
                }
            }
        });
    });

    return io;
}

function getIO() {
    if (!io) {
        throw new Error("Socket.io chưa được khởi tạo!");
    }
    return io;
}

module.exports = { initSocket, getIO };
