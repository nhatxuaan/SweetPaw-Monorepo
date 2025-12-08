import { useState, useRef, useEffect } from "react";

import Box from "@mui/material/Box";
import { SxProps } from "@mui/material";
import Typography from "@mui/material/Typography";

type ChatBoxProps = {
  sx?: SxProps;
};

export function ChatBox({ sx }: ChatBoxProps) {
  const [messages, setMessages] = useState([
    { id: 1, sender: "user", type: "text", content: "Xin chào shop!" },
    { id: 2, sender: "shop", type: "text", content: "Chào bạn nè 😄" },
  ]);

  const [input, setInput] = useState("");
  const [typing, setTyping] = useState(false);

  const chatEndRef = useRef<HTMLDivElement | null>(null);

  // Scroll xuống cuối
  const scrollToBottom = () => {
    setTimeout(() => {
      chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, 50);
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleTyping = (value: string) => {
    setInput(value);
    setTyping(true);

    setTimeout(() => setTyping(false), 1000);
  };

  const sendMessage = () => {
    if (!input.trim()) return;

    setMessages((prev) => [
      ...prev,
      {
        id: Date.now(),
        sender: "shop",
        type: "text",
        content: input,
      },
    ]);

    setInput("");
    scrollToBottom();
  };

  const sendImage = (e: any) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const url = URL.createObjectURL(file);

    setMessages((prev) => [
      ...prev,
      {
        id: Date.now(),
        sender: "shop",
        type: "image",
        content: url,
      },
    ]);

    scrollToBottom();
  };

  return (
    <Box
      sx={{
        flex: 1,
        background: "white",
        borderRadius: 2,
        p: 2,
        display: "flex",
        flexDirection: "column",
        boxShadow: "0 2px 6px rgba(0,0,0,0.08)",
        overflow: "hidden",
        mr: 5,
        ...sx,
      }}
    >
      {/* HEADER */}
      <Box sx={{ pb: 1.5, borderBottom: "1px solid #eee" }}>
        <Typography sx={{ fontSize: 18, fontWeight: 600 }}>
          Võ Hà Khả Hân
        </Typography>
        <Typography sx={{ fontSize: 12, color: "green" }}>Online</Typography>
      </Box>

      {/* CHAT BODY */}
      <Box
        sx={{
          flex: 1,
          overflowY: "auto",
          mt: 2,
          mb: 2,
          pr: 1,
          "&::-webkit-scrollbar": { width: "6px" },
          "&::-webkit-scrollbar-thumb": {
            background: "#bfbfbf",
            borderRadius: "8px",
          },
        }}
      >
        {messages.map((msg) => (
          <Box
            key={msg.id}
            sx={{
              display: "flex",
              justifyContent: msg.sender === "shop" ? "flex-end" : "flex-start",
              mb: 2,
            }}
          >
            {/* Tin nhắn TEXT */}
            {msg.type === "text" && (
              <Box
                sx={{
                  maxWidth: 350,
                  background: msg.sender === "shop" ? "#4f8dfd" : "#fff",
                  color: msg.sender === "shop" ? "white" : "black",
                  p: 1.5,
                  borderRadius: 2,
                  boxShadow: "0 1px 4px rgba(0,0,0,0.1)",
                }}
              >
                {msg.content}
              </Box>
            )}

            {/* Tin nhắn IMAGE */}
            {msg.type === "image" && (
              <img
                src={msg.content}
                style={{
                  maxWidth: 200,
                  borderRadius: 12,
                  boxShadow: "0 2px 6px rgba(0,0,0,0.2)",
                }}
              />
            )}
          </Box>
        ))}

        {typing && (
          <Typography sx={{ fontSize: 12, color: "#888", ml: 1 }}>
            Đang nhập...
          </Typography>
        )}

        <div ref={chatEndRef} />
      </Box>

      {/* INPUT */}
      <Box sx={{ display: "flex", gap: 1.5 }}>
        <input
          placeholder="Nhập tin nhắn..."
          value={input}
          onChange={(e) => handleTyping(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && sendMessage()}
          style={{
            flex: 1,
            padding: "12px",
            borderRadius: 12,
            border: "1px solid #ccc",
          }}
        />

        {/* Chọn ảnh */}
        <label
          style={{
            background: "#eee",
            padding: "10px 14px",
            borderRadius: 10,
            cursor: "pointer",
            border: "1px solid #ccc",
          }}
        >
          📷
          <input
            type="file"
            accept="image/*"
            onChange={sendImage}
            style={{ display: "none" }}
          />
        </label>

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
  );
}
