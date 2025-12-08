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
       socket.on("joinRoom", (data) => {
            if (!data || typeof data !== "object") {
                console.log("joinRoom: nhận data không hợp lệ:", data);
                return;
            }

            // User:  data.userId
            // Admin: data.adminId
            const userId = data.userId || data.adminId;

            if (!userId) {
                console.log("joinRoom thiếu userId hoặc adminId");
                return;
            }

            const role = data.role || "User";

            // Join phòng
            socket.join(userId);

            // Lưu online
            onlineUsers[userId] = {
                socketId: socket.id,
                role
            };

            console.log(`${role} ${userId} joined room ${userId}`);
        });

        // Xử lý mở chat
        socket.on("openChat", (data) => {
            const userId = data.userId;
            const chatId = data.chatId;
            if (!userId || !chatId) return;

            activeChatUsers[userId] = chatId;
            console.log(`User ${userId} is viewing chat ${chatId}`);
        });

        socket.on("sendMessage", async (msg) => {
            try {
                console.log("Nhận tin nhắn realtime:", msg);

                const { senderModel, userId } = msg;

                // === Lưu DB tại đây nếu bạn cần ===
                // await saveMessageToDB(msg);

                // User gửi → gửi cho tất cả admin
                if (senderModel === "User") {
                    for (const uid in onlineUsers) {
                        if (onlineUsers[uid].role === "Admin") {
                        io.to(onlineUsers[uid].socketId).emit("userMessage", msg);
                        }
                    }
                    console.log("Gửi tin nhắn đến tất cả admin");
                } else if (senderModel === "Admin") {
                    io.to(onlineUsers[userId].socketId).emit("adminMessage", msg);
                    console.log(`Gửi tin nhắn đến user ${userId}`);
                }

            } catch (err) {
                console.log("Lỗi xử lý sendMessage:", err);
            }
        });

        // Xử lý đóng chat
        socket.on("closeChat", (data ) => {
            const userId = data.userId;
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
                if (onlineUsers[uid].socketId === socket.id) {
                    delete onlineUsers[uid];
                }
            }

            // Xóa khỏi activeChatUsers 
            for (const uid in activeChatUsers) {
                if (onlineUsers[uid] && onlineUsers[uid].socketId === socket.id) {
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
