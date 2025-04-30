package com.example.mad_project.model;

public class ClassItem {
    private String id;
    private String title;
    private String teacherName;
    private String description;
    private int iconResId;
    private String status;

    public ClassItem(String id, String title, String description, int iconResId, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}