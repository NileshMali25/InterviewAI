package com.aiinterview.dto;

public class UserProfileDto {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String targetRole;
    private String bio;

    public UserProfileDto() {}

    public UserProfileDto(Long id, String fullName, String email, String role, String targetRole, String bio) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.targetRole = targetRole;
        this.bio = bio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
