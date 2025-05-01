package com.example.mad_project.model;

public class StudentAssignmentItem extends AssignmentItem {
    private boolean isSubmitted;
    private int obtainedPoints;

    public StudentAssignmentItem(String title, String dueDate, String description, String points, boolean isSubmitted, int obtainedPoints) {
        super(title, dueDate, description, points);
        this.isSubmitted = isSubmitted;
        this.obtainedPoints = obtainedPoints;
    }

    // Getter and Setter for isSubmitted
    public boolean isSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }

    // Getter and Setter for obtainedPoints
    public int getObtainedPoints() {
        return obtainedPoints;
    }

    public void setObtainedPoints(int obtainedPoints) {
        this.obtainedPoints = obtainedPoints;
    }

    // Method to update assignment submission status
    public void updateSubmission(boolean submitted, int points) {
        this.isSubmitted = submitted;
        this.obtainedPoints = points;
    }

    // Override toString for better debugging
    @Override
    public String toString() {
        return "StudentAssignmentItem{" +
                "title='" + getTitle() + '\'' +
                ", dueDate='" + getDueDate() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", points='" + getPoints() + '\'' +
                ", isSubmitted=" + isSubmitted +
                ", obtainedPoints=" + obtainedPoints +
                '}';
    }
}