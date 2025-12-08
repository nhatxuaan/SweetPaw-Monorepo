package com.example.sweetpawapp.data.model.chat;

public class MessageRequest {
    private String content;
    private String media;

    public MessageRequest(String content, String media) {
        this.content = content;
        this.media = media;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

}
