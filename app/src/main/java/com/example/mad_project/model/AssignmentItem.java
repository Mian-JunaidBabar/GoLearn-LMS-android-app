package com.example.mad_project.model;

public class AssignmentItem {

    public AssignmentItem(String title, String dueDate, String description, String points) {
        this.title = title;
        this.dueDate = dueDate;
        this.description = description;
        this.points = points;
    }

    private String title;
    private String dueDate;
    private String description;
    private String points;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}