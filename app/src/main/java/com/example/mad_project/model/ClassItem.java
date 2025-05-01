package com.example.mad_project.model;

public class ClassItem {

    private String id;
    private String title;
    private String description;
    private String teacherName;
    private int status;
    private int iconResId;
    private String code; // New field for class code

    // Fields for student-specific assignments
    private boolean isStudentAssignment;
    private boolean isSubmitted;
    private int obtainedPoints;

    // Full constructor
    public ClassItem(String title, String description, String teacherName, int status, int iconResId, boolean isStudentAssignment, boolean isSubmitted, int obtainedPoints, String code) {
        this.title = title;
        this.description = description;
        this.teacherName = teacherName;
        this.status = status;
        this.iconResId = iconResId;
        this.isStudentAssignment = isStudentAssignment;
        this.isSubmitted = isSubmitted;
        this.obtainedPoints = obtainedPoints;
        this.code = code; // Initialize the new field
    }

    public ClassItem(String id, String title, String description, int iconResId, String teacherName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.teacherName = teacherName;
    }

    // Overloaded constructor for simpler use cases
    public ClassItem(String id, String title, String description, int iconResId, String teacherName, String code) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.teacherName = teacherName;
        this.status = 0;                 // Default value
        this.isStudentAssignment = false; // Default value
        this.isSubmitted = false;        // Default value
        this.obtainedPoints = 0;         // Default value
        this.code = code; // Initialize the new field
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public int getStatus() {
        return status;
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean isStudentAssignment() {
        return isStudentAssignment;
    }

    public void setStudentAssignment(boolean studentAssignment) {
        isStudentAssignment = studentAssignment;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }

    public int getObtainedPoints() {
        return obtainedPoints;
    }

    public void setObtainedPoints(int obtainedPoints) {
        this.obtainedPoints = obtainedPoints;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getCode() {
        return code; // Getter for the new field
    }

    public void setCode(String code) {
        this.code = code; // Setter for the new field
    }
}