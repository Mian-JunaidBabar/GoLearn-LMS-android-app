package com.example.GoLearn.model;

public class AssignmentItem {

    private String assignmentId;
    private String classId;
    private long createdAt;
    private String createdBy;
    private String description;
    private long dueDate;
    private String filePath;
    private int points;
    private String title;

    // Default constructor required for Firebase
    public AssignmentItem() {
    }

    // Parameterized constructor
    public AssignmentItem(String assignmentId, String classId, long createdAt, String createdBy,
                          String description, long dueDate, String filePath, int points, String title) {
        this.assignmentId = assignmentId;
        this.classId = classId;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.description = description;
        this.dueDate = dueDate;
        this.filePath = filePath;
        this.points = points;
        this.title = title;
    }

    // Getters and Setters
    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}