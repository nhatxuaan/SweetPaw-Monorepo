package com.example.sweetpawapp.data.model.chat;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatMessage {
    @SerializedName("_id")
    private String messageId;
    private Sender sender;
    private String content;
    private List<String> media;
    private String timestamp;
    private boolean read;

    public String getMessageId() {
        return messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Sender getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public List<String> getMedia() {
        return media;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }
}
