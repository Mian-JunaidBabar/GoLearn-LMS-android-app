package com.example.GoLearn.util;

public class ClassDataHolder {
    public static String classId;
    public static String description;
    public static String teacher;
    public static String status;

    // Optional: Add methods to clear data if needed
    public static void clear() {
        classId = null;
        description = null;
        teacher = null;
        status = null;
    }

    public static String getClassId() {
        return classId;
    }

    public static void setClassId(String classId) {
        ClassDataHolder.classId = classId;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        ClassDataHolder.description = description;
    }

    public static String getTeacher() {
        return teacher;
    }

    public static void setTeacher(String teacher) {
        ClassDataHolder.teacher = teacher;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        ClassDataHolder.status = status;
    }
}