package com.example.GoLearn.model;

public class CommentItem {
    private String commentId;
    private String senderId;
    private String senderName;
    private String message;
    private String timestamp;

    public CommentItem() {
        // Firebase needs empty constructor
    }

    public CommentItem(String commentId, String senderId, String senderName, String message, String timestamp) {
        this.commentId = commentId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
