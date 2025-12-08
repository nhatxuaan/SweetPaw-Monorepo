package com.example.sweetpawapp.data.model.chat;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Message {
    private UserChat senderUser;
    private AdminChat senderAdmin;
    private String senderModel;
    private String content;
    private List<String> media;
    private List<String> readBy;
    private String timestamp;
    private boolean read;
    @SerializedName("_id")
    private String messageId;

    // getters và setters
    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getSenderModel() { return senderModel; }
    public void setSenderModel(String senderModel) { this.senderModel = senderModel; }

    public UserChat getSenderUser() { return senderUser; }
    public void setSenderUser(UserChat senderUser) { this.senderUser = senderUser; }

    public AdminChat getSenderAdmin() { return senderAdmin; }
    public void setSenderAdmin(AdminChat senderAdmin) { this.senderAdmin = senderAdmin; }

    public List<String> getMedia() { return media; }
    public void setMedia(List<String> media) { this.media = media; }

    public List<String> getReadBy() { return readBy; }
    public void setReadBy(List<String> readBy) { this.readBy = readBy; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
}
