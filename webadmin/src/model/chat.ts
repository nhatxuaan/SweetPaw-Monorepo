// src/models/chat.ts

export interface ChatUser {
  _id: string;
  HoTen?: string;
  Email?: string;
  username?: string;
  displayName?: string;
}

export interface ChatMessage {
  _id: string;
  sender: ChatUser;
  senderModel: "User" | "Admin";
  content: string;
  media: any[];
  readBy: string[];
  timestamp: string;
}

export interface ChatItem {
  _id: string;
  user: ChatUser | null;
  admin: ChatUser;
  messages: ChatMessage[];
  lastMessage: string;
  updatedAt: string;
}
