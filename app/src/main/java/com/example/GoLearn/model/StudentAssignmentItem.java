package com.example.GoLearn.model;

public class StudentAssignmentItem extends AssignmentItem {
    private boolean isSubmitted;
    private int obtainedPoints;

    // Default constructor
    public StudentAssignmentItem() {
        super();
    }

    // Parameterized constructor
    public StudentAssignmentItem(String assignmentId, String classId, long createdAt, String createdBy,
                                 String description, long dueDate, String filePath, int points, String title,
                                 boolean isSubmitted, int obtainedPoints) {
        super(assignmentId, classId, createdAt, createdBy, description, dueDate, filePath, points, title);
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
        if (points >= 0 && points <= getPoints()) {
            this.obtainedPoints = points;
        } else {
            throw new IllegalArgumentException("Obtained points must be between 0 and the total points.");
        }
    }

    // Override toString for better debugging
    @Override
    public String toString() {
        return "StudentAssignmentItem{" +
                "assignmentId='" + getAssignmentId() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", dueDate=" + getDueDate() +
                ", description='" + getDescription() + '\'' +
                ", points=" + getPoints() +
                ", isSubmitted=" + isSubmitted +
                ", obtainedPoints=" + obtainedPoints +
                '}';
    }
}