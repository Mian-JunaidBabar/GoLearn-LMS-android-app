package com.example.mad_project.model;

public class CommentItem {
    private String sender;
    private String message;
    private String time;

    public CommentItem(String sender, String message, String time) {
        this.sender = sender;
        this.message = message;
        this.time = time;
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
}
