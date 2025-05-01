package com.example.mad_project.model;

public class AssignmentSubmissionItem {
    private String studentName;
    private String fileName;
    private String fileUrl;
    private String grade;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public AssignmentSubmissionItem(String studentName, String fileName, String fileUrl, String grade) {
        this.studentName = studentName;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.grade = grade;
    }
}
