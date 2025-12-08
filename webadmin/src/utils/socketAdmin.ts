import io from "socket.io-client";

type IOSocket = ReturnType<typeof io>;

class SocketAdmin {
    private static instance: IOSocket | null = null;

    static init(baseUrl: string, options?: any): IOSocket {
        if (!SocketAdmin.instance) {
            console.log("[SocketAdmin] Khởi tạo kết nối socket admin tới:", baseUrl);

            SocketAdmin.instance = io(baseUrl, {
                reconnection: true,
                reconnectionAttempts: Infinity,
                reconnectionDelay: 1000,
                ...options
            });

            SocketAdmin.instance.on("connect", () => {
                console.log("Socket admin đã kết nối. ID:", SocketAdmin.instance?.id);
            });

            SocketAdmin.instance.on("disconnect", () => {
                console.log("Socket admin bị ngắt kết nối.");
            });

            SocketAdmin.instance.on("userMessage", (msg: any) => {
                console.log("Nhận tin nhắn từ User:", msg);
            });

            SocketAdmin.instance.on("adminMessage", (msg: any) => {
                console.log("Nhận tin nhắn broadcast từ hệ thống:", msg);
            });
        }

        return SocketAdmin.instance!;
    }

    static joinRoom(adminId: string) {
        console.log("Admin tham gia phòng của mình với ID:", adminId);
        const data = {adminId, role: "Admin" };
        SocketAdmin.instance?.emit("joinRoom", data);
    }

    static openChat(userId: string, chatId: string) {
        console.log("Admin mở cuộc chat của user", userId, "với chatId", chatId);
        const data = { userId: userId, chatId: chatId };
        SocketAdmin.instance?.emit("openChat", data);
    }

    static sendAdminMessage(userId: string, chatId: string, content: string) {
        const msg = {
            senderModel: "Admin",
            userId,
            chatId,
            content,
            timestamp: Date.now()
        };

        console.log("Admin gửi tin nhắn cho User:", msg);
        SocketAdmin.instance?.emit("sendMessage", msg);
    }

    static closeChat(userId: string) {
        console.log("Admin đóng cửa sổ chat của user:", userId);
        SocketAdmin.instance?.emit("closeChat", { userId });
    }
    static disconnect() {
        if (SocketAdmin.instance) {
            SocketAdmin.instance.disconnect();
            SocketAdmin.instance = null;
            console.log("Socket admin đã ngắt kết nối.");
        }
    }
}

export default SocketAdmin;
