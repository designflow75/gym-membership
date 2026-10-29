package com.smartgym.model;

/**
 * Subclass representing an Administrator.
 * Demonstrates OOP Concepts:
 * - Inheritance (extends User)
 * - Method Overriding (provides Admin-specific implementations of abstract methods)
 * - Runtime Polymorphism
 */
public class AdminUser extends User {

    private String adminDepartment; // e.g., "Operations", "General Management"

    public AdminUser() {
        super();
        this.role = "ADMIN";
        this.adminDepartment = "General Management";
    }

    public AdminUser(String username, String password, String fullName, String phoneNumber, String email) {
        super(username, password, fullName, phoneNumber, email, "ADMIN");
        this.adminDepartment = "General Management";
    }

    public AdminUser(int userId, String username, String password, String fullName, String phoneNumber, String email, String createdDate, String adminDepartment) {
        super(userId, username, password, fullName, phoneNumber, email, "ADMIN", createdDate);
        this.adminDepartment = adminDepartment != null ? adminDepartment : "General Management";
    }

    // --- Method Overriding (Runtime Polymorphism) ---

    @Override
    public String getRoleDescription() {
        return "System Administrator with full executive authority over accounts, financial logs, and staff assignments.";
    }

    @Override
    public String getDashboardTitle() {
        return "Admin Executive Control Center";
    }

    @Override
    public boolean hasAccessTo(String feature) {
        // Administrator has unrestricted access across all system features
        return true;
    }

    public String getAdminDepartment() {
        return adminDepartment;
    }

    public void setAdminDepartment(String adminDepartment) {
        this.adminDepartment = adminDepartment;
    }
}

