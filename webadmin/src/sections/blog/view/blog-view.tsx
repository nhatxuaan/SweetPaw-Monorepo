import type { ChatItem, ChatMessage as ChatMessageModel, ChatUser } from "src/model/chat";

import React, { useEffect, useRef, useState } from "react";

import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";

import SocketAdmin from "src/utils/socketAdmin";

import { DashboardChatContent } from "src/layouts/chat/chat-content";
import { apiGetAllChats, apiGetChatHistory, apiSendMessage  } from "src/services/chatApi";


// Local UI message type (normalized)
type UiMessage = {
  id: string | number;
  text: string;
  from: "me" | "user";
  timestamp?: string;
};

export function MessagesView() {
  const [chatList, setChatList] = useState<ChatItem[]>([]);
  const [messageList, setMessageList] = useState<UiMessage[]>([]);
  const [selectedChat, setSelectedChat] = useState<ChatItem | null>(null);
  const [inputMsg, setInputMsg] = useState("");
  const chatEndRef = useRef<HTMLDivElement | null>(null);
  const socketRef = useRef<any>(null);

  // ---------- init socket + load chats ----------
  useEffect(() => {
    // init socket
    const socket = SocketAdmin.init("https://sweetpaw-be.azurewebsites.net/", { transports: ["websocket"]});
    socketRef.current = socket;

    socket.on("connect", () => {
      console.log("[SOCKET] connected", socket.id);
    });

    socket.on("disconnect", () => {
      console.log("[SOCKET] disconnected");
    });

    // when user sends message to admin
    socket.on("userMessage", (msg: any) => {
      // msg expected shape: { chatId?, userId, content, timestamp, ... }
      console.log("[SOCKET] userMessage", msg);

      // 1) if the message belongs to currently opened chat -> append to messageList
      if (selectedChat && (msg.chatId === selectedChat._id || msg.userId === selectedChat.user?._id)) {
        setMessageList((prev) => [
          ...prev,
          {
            id: msg._id || Date.now(),
            text: msg.content,
            from: "user",
            timestamp: msg.timestamp,
          },
        ]);
      }

      // 2) update chatList: set lastMessage and updatedAt (if that chat exists)
      setChatList((prev) =>
        prev.map((c) =>
          c._id === msg.chatId
            ? { ...c, lastMessage: msg.content, updatedAt: msg.timestamp || new Date().toISOString() }
            : c
        )
      );
    });

    // Optionally, adminMessage events (when server broadcast admin message back)
    socket.on("adminMessage", (msg: any) => {
      console.log("[SOCKET] adminMessage", msg);
      // similar handling as above — append if belongs to current open
      if (selectedChat && (msg.chatId === selectedChat._id || msg.userId === selectedChat.user?._id)) {
        setMessageList((prev) => [
          ...prev,
          {
            id: msg._id || Date.now(),
            text: msg.content,
            from: "me",
            timestamp: msg.timestamp,
          },
        ]);
      }

      setChatList((prev) =>
        prev.map((c) =>
          c._id === msg.chatId
            ? { ...c, lastMessage: msg.content, updatedAt: msg.timestamp || new Date().toISOString() }
            : c
        )
      );
    });

    // load chats
    (async function fetchChats() {
      try {
        const res = await apiGetAllChats(); // expected: { data: ChatItem[] } or ChatItem[]
        const data: ChatItem[] = Array.isArray(res?.data) ? res.data : res?.data || res;
        setChatList(data || []);
      } catch (err) {
        console.error("[API] apiGetAllChats error", err);
      }
    })();

    return () => {
      // cleanup socket listeners
      socket.off("userMessage");
      socket.off("adminMessage");
      socket.disconnect?.();
      socketRef.current = null;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []); // run once

  // ---------- open chat (load history) ----------
  const openChat = async (chat: ChatItem) => {
    setSelectedChat(chat);
    setMessageList([]); // clear while loading

    try {
      const res = await apiGetChatHistory(chat.user?._id || ""); // expected shape: { messages: ChatMessage[] }
      console.log("[API] apiGetChatHistory result:", res);
      const msgs = (res?.data?.messages || []).map((m: any) => ({
        id: m._id,
        text: m.content,
        from: m.senderModel.toLowerCase() === "admin" ? "me" : "user",
        timestamp: m.timestamp
      }));

      console.log("[CHAT] messages to display:", msgs);

      setMessageList(msgs);

      // optionally: mark read via API (not implemented here)
    } catch (err) {
      console.error("[API] apiGetChatHistory error", err);
    }
  };

  const sendMessage = async () => {
    if (!selectedChat || !inputMsg.trim()) return;
    
    const text = inputMsg.trim();
    const optimistic: UiMessage = {
      id: `local-${Date.now()}`,
      text,
      from: "me",
      timestamp: new Date().toISOString(),
    };

    setMessageList((prev) => [...prev, optimistic]);
    setInputMsg("");

    // emit socket realtime
    socketRef.current?.emit("adminMessage", {
      chatId: selectedChat._id,
      userId: selectedChat.user?._id,
      content: text,
      timestamp: new Date().toISOString(),
    });

    // gọi API lưu tin nhắn
    try {
      const result = await apiSendMessage(selectedChat.user?._id || "", text, []);
      console.log("Tin nhắn lưu server:", result);

      // cập nhật optimistic message nếu muốn
      setMessageList((prev) =>
        prev.map((msg) =>
          msg.id === optimistic.id
            ? { ...msg, id: result.data.message._id, timestamp: result.data.message.timestamp }
            : msg
        )
      );
    } catch (err) {
      console.error("Lỗi gửi tin nhắn:", err);
    }
  };

  // scroll to bottom when messages change
  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messageList]);

  // ---------- send message ----------
  // const sendMessage = async () => {
  //   const text = inputMsg.trim();
  //   if (!text || !selectedChat) return;

  //   // create optimistic message to UI
  //   const optimistic: UiMessage = {
  //     id: `local-${Date.now()}`,
  //     text,
  //     from: "me",
  //     timestamp: new Date().toISOString(),
  //   };
  //   setMessageList((prev) => [...prev, optimistic]);
  //   setInputMsg("");

  //   // update chatList lastMessage locally
  //   setChatList((prev) =>
  //     prev.map((c) =>
  //       c._id === selectedChat._id
  //         ? { ...c, lastMessage: text, updatedAt: new Date().toISOString() }
  //         : c
  //     )
  //   );

  //   // emit via socket (so user receives realtime)
  //   try {
  //     socketRef.current?.emit("adminMessage", {
  //       chatId: selectedChat._id,
  //       userId: selectedChat.user?._id,
  //       content: text,
  //       timestamp: new Date().toISOString(),
  //     });
  //   } catch (err) {
  //     console.warn("Socket emit failed:", err);
  //   }

  //   // persist to server via REST API if available (best practice)
  //   // try {
  //   //   if (typeof apiSendMessage === "function") {
  //   //     // apiSendMessage(chatId, { content, senderModel })
  //   //     await apiSendMessage(selectedChat._id, {
  //   //       content: text,
  //   //       senderModel: "Admin",
  //   //     });
  //   //     // If server returns saved message with _id, you may want to replace optimistic id with real id.
  //   //   }
  //   // } catch (err) {
  //   //   console.error("[API] apiSendMessage error", err);
  //   //   // optional: show error indicator or revert optimistic message
  //   // }
  // };

  // ---------- UI helpers ----------
  const displayName = (user: ChatUser | null | undefined) => {
    if (!user) return "Người dùng đã xóa";
    return user.HoTen || user.displayName || user.username || "Người dùng";
  };

  return (
    <DashboardChatContent>
      <Box sx={{ mt: 2, flex: 1, overflowY: "auto", pr: 1, display: "flex", gap: 2,  height: "calc(100vh - 16px)", }}>
        {/* SIDEBAR */}
        <Box sx={{ width: "28%", background: "white", borderRadius: 2, p: 2, display: "flex", flexDirection: "column", boxShadow: "0 2px 6px rgba(0,0,0,0.08)" }}>
          <Typography sx={{ fontSize: 20, fontWeight: 700, mb: 2 }}>TIN NHẮN</Typography>

          <TextField placeholder="Tìm kiếm" size="small" variant="outlined" sx={{ mb: 2 }} />

          <Box sx={{ mt: 1, flex: 1, overflowY: "auto", pr: 1 }}>
            {chatList.length === 0 && <Typography sx={{ color: "#777", mt: 2 }}>Chưa có cuộc trò chuyện nào</Typography>}

            {chatList.map((chat) => {
              const user = chat.user;
              const isActive = selectedChat?._id === chat._id;
              return (
                <Box
                  key={chat._id}
                  onClick={() => openChat(chat)}
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 2,
                    p: 1.2,
                    mb: 1,
                    borderRadius: 2,
                    cursor: user ? "pointer" : "not-allowed",
                    background: isActive ? "#e8f0fe" : "white",
                    boxShadow: "0 1px 4px rgba(0,0,0,0.06)",
                    "&:hover": { background: user ? "#f5f7fa" : "white" },
                  }}
                >
                  <Box sx={{ flex: 1 }}>
                    <Typography sx={{ fontSize: 14, fontWeight: 600 }}>{displayName(user)}</Typography>
                    <Typography sx={{ fontSize: 12, color: "#666", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
                      {chat.lastMessage || "Chưa có tin nhắn"}
                    </Typography>
                  </Box>
                  <Typography sx={{ fontSize: 11, color: "#999" }}>
                    {chat.updatedAt ? new Date(chat.updatedAt).toLocaleString() : ""}
                  </Typography>
                </Box>
              );
            })}
          </Box>
        </Box>

        {/* CHAT WINDOW */}
        <Box sx={{ flex: 1, background: "white", borderRadius: 2, p: 2, display: "flex", flexDirection: "column", boxShadow: "0 2px 6px rgba(0,0,0,0.08)" }}>
          <Box sx={{ pb: 1.5, borderBottom: "1px solid #eee" }}>
            <Typography sx={{ fontSize: 18, fontWeight: 600 }}>{selectedChat ? displayName(selectedChat.user) : "Chọn một cuộc trò chuyện"}</Typography>
          </Box>

          <Box sx={{ flex: 1, overflowY: "auto", mt: 2, mb: 2, pr: 1 }}>
            {selectedChat ? (
              messageList.map((msg) =>
                msg.from === "user" ? (
                  <Box key={msg.id} sx={{ display: "flex", gap: 1.5, mb: 2 }}>
                    <Box sx={{ width: 6 }} />
                    <Box sx={{ maxWidth: 520, background: "white", p: 1.5, borderRadius: 2, boxShadow: "0 1px 4px rgba(0,0,0,0.06)" }}>
                      <Typography>{msg.text}</Typography>
                      {msg.timestamp && <Typography sx={{ fontSize: 11, color: "#999", mt: 0.5 }}>{new Date(msg.timestamp).toLocaleString()}</Typography>}
                    </Box>
                  </Box>
                ) : (
                  <Box key={msg.id} sx={{ display: "flex", justifyContent: "flex-end", mb: 2 }}>
                    <Box sx={{ maxWidth: 520, background: "#4f8dfd", color: "white", p: 1.5, borderRadius: 2 }}>
                      <Typography>{msg.text}</Typography>
                      {msg.timestamp && <Typography sx={{ fontSize: 11, color: "#e6f0ff", mt: 0.5 }}>{new Date(msg.timestamp).toLocaleString()}</Typography>}
                    </Box>
                  </Box>
                )
              )
            ) : (
              <Typography sx={{ color: "#777", mt: 3 }}>Hãy chọn một người để bắt đầu chat</Typography>
            )}

            <div ref={chatEndRef} />
          </Box>

          {/* INPUT */}
          <Box sx={{ display: "flex", gap: 2 }}>
            <TextField
              placeholder="Nhập tin nhắn..."
              value={inputMsg}
              onChange={(e) => setInputMsg(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && sendMessage()}
              disabled={!selectedChat}
              fullWidth
              size="small"
            />

            {/* <IconButton color="primary" disabled={!selectedChat || !inputMsg.trim()} onClick={sendMessage}>
              <SendIcon />
            </IconButton> */}

            <button
              onClick={sendMessage}
              style={{
                background: "#4f8dfd",
                color: "white",
                padding: "12px 20px",
                borderRadius: "12px",
                border: "none",
                cursor: "pointer",
              }}
            >
              Gửi
          </button>

          </Box>
        </Box>
      </Box>
    </DashboardChatContent>
  );
}

export default MessagesView;
