package com.example.mad_project.model;

public class ClassItem {
    private String classTitle;
    private String description;
    private String teacherName;
    private int pendingAssignments;
    private int imageResId;

    public ClassItem(String classTitle, String description, String teacherName, int pendingAssignments, int imageResId) {
        this.classTitle = classTitle;
        this.description = description;
        this.teacherName = teacherName;
        this.pendingAssignments = pendingAssignments;
        this.imageResId = imageResId;
    }

    public String getClassTitle() { return classTitle; }
    public String getDescription() { return description; }
    public String getTeacherName() { return teacherName; }
    public int getPendingAssignments() { return pendingAssignments; }
    public int getImageResId() { return imageResId; }
}
