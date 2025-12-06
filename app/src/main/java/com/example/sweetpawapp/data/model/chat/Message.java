package com.example.sweetpawapp.data.model.chat;

public class Message {
    private String content;
    private Boolean isFromUser;

    public Message(String content, boolean isFromUser){
        this.content = content;
        this.isFromUser = isFromUser;
    }
    public String getContent(){
        return content;
    }

    public Boolean isFromUser() {
        return isFromUser;
    }
}
