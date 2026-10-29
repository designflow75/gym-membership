package com.smartgym.model;

/**
 * Represents a gym staff trainer or coach with specialization, shift, and compensation details.
 */
public class TrainerUser extends User {

    // Presets for coach specialization
    public static final String[] SPECIALIZATIONS = {
        "Strength & Hypertrophy",
        "Weight Loss & HIIT Coach",
        "Clinical Nutrition & Dietetics",
        "Functional Fitness & Calisthenics",
        "Yoga, Mobility & Rehabilitation"
    };

    // Presets for work shifts
    public static final String[] SHIFTS = {
        "Morning Shift (06:00 AM - 02:00 PM)",
        "Evening Shift (02:00 PM - 10:00 PM)",
        "General Shift (09:00 AM - 06:00 PM)"
    };

    // Private trainer-specific fields (Encapsulation)
    private String specialization;
    private String shift;
    private double monthlySalary;

    // --- Constructor 1: Default Constructor ---
    public TrainerUser() {
        super();
        this.role = "TRAINER";
        this.specialization = SPECIALIZATIONS[0];
        this.shift = SHIFTS[0];
        this.monthlySalary = 35000.0;
    }

    // --- Constructor 2: For Trainer Registration (Without ID) ---
    public TrainerUser(String username, String password, String fullName, String phoneNumber, String email, String specialization, String shift, double monthlySalary) {
        super(username, password, fullName, phoneNumber, email, "TRAINER");
        this.specialization = specialization != null ? specialization : SPECIALIZATIONS[0];
        this.shift = shift != null ? shift : SHIFTS[0];
        this.monthlySalary = monthlySalary;
    }

    // --- Constructor 3: For Database Retrieval (With ID and createdDate) ---
    public TrainerUser(int userId, String username, String password, String fullName, String phoneNumber, String email, String createdDate, String specialization, String shift, double monthlySalary) {
        super(userId, username, password, fullName, phoneNumber, email, "TRAINER", createdDate);
        this.specialization = specialization != null ? specialization : SPECIALIZATIONS[0];
        this.shift = shift != null ? shift : SHIFTS[0];
        this.monthlySalary = monthlySalary;
    }

    // Overridden methods from User base class

    @Override
    public String getRoleDescription() {
        return "Certified Trainer responsible for logging attendance, designing custom diet plans, and programming workouts.";
    }

    @Override
    public String getDashboardTitle() {
        return "Trainer & Staff Operations Workspace";
    }

    @Override
    public boolean hasAccessTo(String feature) {
        return "ATTENDANCE_MARKING".equalsIgnoreCase(feature)
                || "DIET_DESIGN".equalsIgnoreCase(feature)
                || "WORKOUT_ASSIGNMENT".equalsIgnoreCase(feature)
                || "ROSTER_MANAGEMENT".equalsIgnoreCase(feature);
    }

    // Getters and Setters

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(double monthlySalary) {
        if (monthlySalary >= 0) this.monthlySalary = monthlySalary;
    }

    /**
     * Converts trainer details into a table row for the Staff Panel Staff Directory.
     */
    public Object[] toTableRow() {
        return new Object[]{
                userId,
                fullName,
                username,
                specialization,
                shift,
                String.format("₹%.2f", monthlySalary),
                phoneNumber != null ? phoneNumber : "-",
                email != null ? email : "-"
        };
    }
}
