package com.example.GoLearn.model;

public class StudentSubmissionItem {
    private String studentName;
    private String fileName;
    private String fileUrl;
    private String grade;

    public StudentSubmissionItem(String studentName, String fileName, String fileUrl, String grade) {
        this.studentName = studentName;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.grade = grade;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getGrade() {
        return grade;
    }
}
