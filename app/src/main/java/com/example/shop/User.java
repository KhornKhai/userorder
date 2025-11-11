package com.example.shop;

public class User {
    private String id; // Assuming you have an ID field
    private String username;
    private String email;
    private String userType;

    // Constructor
    public User(String id, String username, String email, String userType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userType = userType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}