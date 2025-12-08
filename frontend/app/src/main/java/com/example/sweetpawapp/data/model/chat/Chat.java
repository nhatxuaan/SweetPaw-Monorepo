package com.example.sweetpawapp.data.model.chat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Chat {
    @SerializedName("_id")
    private String chatId;
    private UserChat user;
    private AdminChat admin;
    private List<Message> messages;
    private String lastMessage;
    private String updatedAt;

    // getters và setters
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public UserChat getUser() { return user; }
    public void setUser(UserChat user) { this.user = user; }

    public AdminChat getAdmin() { return admin; }
    public void setAdmin(AdminChat admin) { this.admin = admin; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
}

