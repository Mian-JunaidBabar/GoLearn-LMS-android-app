package com.example.mad_project.model;

public class AssignmentItem {
    private String title;
    private String dueDate;
    private boolean isSubmitted;
    private String points;

    public AssignmentItem(String title, String dueDate, boolean isSubmitted, String points) {
        this.title = title;
        this.dueDate = dueDate;
        this.isSubmitted = isSubmitted;
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public String getPoints() {
        return points;
    }
}