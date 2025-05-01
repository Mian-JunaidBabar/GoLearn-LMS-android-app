package com.example.mad_project.model;

public class CommentItem {
    private String sender;
    private String message;
    private String time;
    private boolean sentByMe;

    public CommentItem(String sender, String message, String time, boolean sentByMe) {
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.sentByMe = sentByMe;
    }


    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSentByMe() {
        return sentByMe;
    }

    public void setSentByMe(boolean sentByMe) {
        this.sentByMe = sentByMe;
    }
}
