import { apiGetAuth, apiPostAuth } from "./apiClient";

// Lấy danh sách tất cả đoạn chat
export function apiGetAllChats() {
  return apiGetAuth("/api/admin/messages");
}

// Lấy lịch sử chat giữa Admin & User
export function apiGetChatHistory(userId: string) {
  return apiGetAuth(`/api/admin/messages/${userId}`);
}

export function apiSendMessage(userId: string, content: string, media: any[] = []) {
  return apiPostAuth("/api/admin/messages", {
    userId,
    content,
    media,
  });
}