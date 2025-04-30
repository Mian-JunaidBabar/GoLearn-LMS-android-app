package com.example.mad_project.model;

public class AssignmentItem {
    private String title;
    private String dueDate;
    private String status;

    public AssignmentItem(String title, String dueDate, String status) {
        this.title = title;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }
}
