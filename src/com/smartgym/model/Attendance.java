package com.smartgym.model;

/**
 * Model class representing member attendance logs.
 * Demonstrates OOP Concepts:
 * - Encapsulation (private fields with public getters/setters)
 * - Constructor Overloading
 * - Array constants
 */
public class Attendance {

    public static final String[] STATUSES = {"Present", "Late", "Absent"};
    public static final String[] WORKOUT_TYPES = {
        "Cardio & HIIT",
        "Weight Training (Upper Body)",
        "Weight Training (Lower Body)",
        "Full Body Conditioning",
        "Yoga & Core Mobility",
        "Rest & Recovery"
    };

    private int attendanceId;
    private int memberId;
    private String memberName;
    private String date;        // Format: YYYY-MM-DD
    private String timeIn;      // Format: HH:mm AM/PM
    private String status;      // Present, Late, Absent
    private String workoutType; // From WORKOUT_TYPES
    private String notes;

    public Attendance() {
        this.attendanceId = 0;
        this.memberId = 0;
        this.memberName = "";
        this.date = "";
        this.timeIn = "07:00 AM";
        this.status = STATUSES[0];
        this.workoutType = WORKOUT_TYPES[0];
        this.notes = "Completed scheduled workout.";
    }

    public Attendance(int memberId, String memberName, String date, String timeIn, String status, String workoutType, String notes) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.date = date;
        this.timeIn = timeIn;
        this.status = status;
        this.workoutType = workoutType;
        this.notes = notes;
    }

    public Attendance(int attendanceId, int memberId, String memberName, String date, String timeIn, String status, String workoutType, String notes) {
        this.attendanceId = attendanceId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.date = date;
        this.timeIn = timeIn;
        this.status = status;
        this.workoutType = workoutType;
        this.notes = notes;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Object[] toTableRow() {
        return new Object[]{
            date,
            timeIn,
            status,
            workoutType,
            notes != null ? notes : ""
        };
    }
}

