package com.example.mad_project.model;

public class StudentSubmissionItem {
    private String studentName;
    private String submissionDate;
    private String fileName;

    public StudentSubmissionItem(String studentName, String submissionDate, String fileName) {
        this.studentName = studentName;
        this.submissionDate = submissionDate;
        this.fileName = fileName;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public String getFileName() {
        return fileName;
    }
}
