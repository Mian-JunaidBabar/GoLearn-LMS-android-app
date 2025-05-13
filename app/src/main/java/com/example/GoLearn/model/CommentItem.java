package com.example.GoLearn.model;

public class CommentItem {
    private String commentId;
    private String sender;
    private String message;
    private String time;
    private boolean sentByMe;

    public CommentItem() {
    }

    public CommentItem(String commentId, String sender, String message, String time, boolean sentByMe) {
        this.commentId = commentId;
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.sentByMe = sentByMe;
    }

    public String getCommentId() {
        return commentId;
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

    public boolean isSentByMe() {
        return sentByMe;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public void setSentByMe(boolean sentByMe) {
        this.sentByMe = sentByMe;
    }
}
