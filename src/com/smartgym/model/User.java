package com.smartgym.model;

import java.time.LocalDate;

/**
 * Abstract base class representing a user in the gym management system.
 * Serves as the superclass for MemberUser and TrainerUser.
 */
public abstract class User {

    protected int userId;
    protected String username;
    protected String password;
    protected String fullName;
    protected String phoneNumber;
    protected String email;
    protected String role; // "TRAINER" or "MEMBER"
    protected String createdDate;

    // --- Constructor 1: Default Constructor ---
    public User() {
        this.userId = 0;
        this.username = "";
        this.password = "";
        this.fullName = "";
        this.phoneNumber = "";
        this.email = "";
        this.role = "MEMBER";
        this.createdDate = LocalDate.now().toString();
    }

    // --- Constructor 2: Overloaded Constructor for User Registration (without ID) ---
    public User(String username, String password, String fullName, String phoneNumber, String email, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.createdDate = LocalDate.now().toString();
    }

    // --- Constructor 3: Overloaded Constructor for Database Fetches (with ID & createdDate) ---
    public User(int userId, String username, String password, String fullName, String phoneNumber, String email, String role, String createdDate) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.createdDate = createdDate;
    }

    // Abstract methods implemented by subclasses (MemberUser and TrainerUser)

    /**
     * Returns a friendly role description.
     */
    public abstract String getRoleDescription();

    /**
     * Returns the specific dashboard banner title.
     */
    public abstract String getDashboardTitle();

    /**
     * Checks if the logged-in user has permission for a specific feature.
     */
    public abstract boolean hasAccessTo(String feature);

    // Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", role, fullName, username);
    }
}
