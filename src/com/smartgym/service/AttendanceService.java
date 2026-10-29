package com.smartgym.service;

import com.smartgym.dao.AttendanceDAO;
import com.smartgym.dao.AttendanceOperations;
import com.smartgym.exception.GymException;
import com.smartgym.model.Attendance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Service class handling member attendance operations, validations, and statistical calculations.
 */
public class AttendanceService {

    // Interface reference for persistence (Dependency Inversion)
    private final AttendanceOperations attendanceDAO;

    // Default Constructor: Injects default AttendanceDAO
    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    // Overloaded Constructor: Allows injecting mock DAOs for unit testing
    public AttendanceService(AttendanceOperations dao) {
        this.attendanceDAO = dao;
    }

    /**
     * Validates and marks attendance for a member.
     * Throws custom GymException if date or member ID is invalid.
     */
    public int markAttendance(Attendance attendance) throws GymException, SQLException, ClassNotFoundException {
        validateAttendance(attendance);
        return attendanceDAO.markAttendance(attendance);
    }

    /**
     * Retrieves all attendance records across the gym.
     */
    public List<Attendance> getAllAttendance() throws SQLException, ClassNotFoundException {
        return attendanceDAO.getAllAttendance();
    }

    /**
     * Retrieves all attendance visits for a specific member.
     * Used by the Member/User Panel to populate the attendance table and chart.
     */
    public List<Attendance> getAttendanceByMember(int memberId) throws SQLException, ClassNotFoundException {
        return attendanceDAO.getAttendanceByMember(memberId);
    }

    /**
     * Retrieves attendance for a specific date (YYYY-MM-DD).
     */
    public List<Attendance> getAttendanceByDate(String date) throws SQLException, ClassNotFoundException {
        return attendanceDAO.getAttendanceByDate(date);
    }

    /**
     * Deletes an attendance record by ID.
     */
    public boolean deleteAttendance(int attendanceId) throws SQLException, ClassNotFoundException {
        return attendanceDAO.deleteAttendance(attendanceId);
    }

    /**
     * Calculates the member's overall attendance rate as a percentage (0% - 100%).
     * Counts "Present" and "Late" check-ins as attended sessions.
     * Drives the circular meter in the AttendanceChartPanel!
     */
    public int calculateAttendancePercentage(int memberId) {
        try {
            List<Attendance> list = attendanceDAO.getAttendanceByMember(memberId);
            if (list == null || list.isEmpty()) return 0;

            int total = list.size();
            // Java Streams: filter present/late sessions and count them
            long present = list.stream()
                    .filter(a -> "Present".equalsIgnoreCase(a.getStatus()) || "Late".equalsIgnoreCase(a.getStatus()))
                    .count();
            return (int) Math.round(((double) present / total) * 100.0);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Returns the total count of gym check-ins recorded today.
     */
    public int getTodayCheckInCount() {
        try {
            String today = LocalDate.now().toString();
            List<Attendance> todayList = attendanceDAO.getAttendanceByDate(today);
            return todayList != null ? todayList.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Validates attendance data integrity before passing to DAO.
     */
    private void validateAttendance(Attendance a) throws GymException {
        if (a == null) {
            throw new GymException("Attendance entry cannot be null.");
        }
        if (a.getMemberId() <= 0) {
            throw new GymException("Please select a valid gym member.");
        }
        if (a.getDate() == null || a.getDate().trim().isEmpty()) {
            throw new GymException("Attendance date is required.");
        }
        try {
            LocalDate.parse(a.getDate().trim());
        } catch (DateTimeParseException e) {
            throw new GymException("Invalid date format. Please use YYYY-MM-DD (e.g. " + LocalDate.now() + ").");
        }
    }
}
