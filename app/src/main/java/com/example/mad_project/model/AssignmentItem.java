package com.example.mad_project.model;

public class AssignmentItem {
    private String title;
    private String dueDate;
    private String description;
    private int grade;
    private int totalGrade;
    private boolean isSubmitted;
    private String points;

    public AssignmentItem(String title, String dueDate, boolean isSubmitted, String points) {
        this.title = title;
        this.dueDate = dueDate;
        this.isSubmitted = isSubmitted;
        this.points = points;
    }

    public AssignmentItem(String title, String dueDate, String description, int grade, int totalGrade, boolean isSubmitted, String points) {
        this.title = title;
        this.dueDate = dueDate;
        this.description = description;
        this.grade = grade;
        this.totalGrade = totalGrade;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getTotalGrade() {
        return totalGrade;
    }

    public void setTotalGrade(int totalGrade) {
        this.totalGrade = totalGrade;
    }
}