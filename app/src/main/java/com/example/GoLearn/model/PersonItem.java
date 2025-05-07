package com.example.GoLearn.model;

public class PersonItem {
    String id;
    private String name;
    private String role;
    private Long joinedAt;// "Teacher" or "Student"

    public PersonItem(String id, String name, String role, Long joinedAt) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public PersonItem(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }


    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Long joinedAt) {
        this.joinedAt = joinedAt;
    }
}
