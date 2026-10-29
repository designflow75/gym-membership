package com.smartgym.dao;

import com.smartgym.database.DatabaseConnection;
import com.smartgym.model.Attendance;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object managing attendance logs with database persistence and in-memory caching.
 */
public class AttendanceDAO implements AttendanceOperations {

    // Fallback cache used when database is unreachable
    private static final List<Attendance> FALLBACK_ATTENDANCE = new ArrayList<>();
    private static int fallbackIdCounter = 200;

    static {
        // Sample records for demo account (Rahul Sharma)
        String today = LocalDate.now().toString();
        String yest = LocalDate.now().minusDays(1).toString();
        String twoDaysAgo = LocalDate.now().minusDays(2).toString();

        FALLBACK_ATTENDANCE.add(new Attendance(1, 4, "Rahul Sharma", today, "07:15 AM", "Present", "Cardio & HIIT", "30 mins treadmill + core"));
        FALLBACK_ATTENDANCE.add(new Attendance(2, 4, "Rahul Sharma", yest, "07:30 AM", "Present", "Weight Training (Upper Body)", "Chest & Triceps"));
        FALLBACK_ATTENDANCE.add(new Attendance(3, 4, "Rahul Sharma", twoDaysAgo, "08:00 AM", "Late", "Full Body Conditioning", "Arrived 20 mins late"));
    }

    /**
     * Records a new attendance entry into both the local cache and MySQL.
     */
    @Override
    public int markAttendance(Attendance attendance) throws SQLException, ClassNotFoundException {
        int newId = fallbackIdCounter++;
        attendance.setAttendanceId(newId);
        FALLBACK_ATTENDANCE.add(0, attendance);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO attendance (member_id, member_name, date, time_in, status, workout_type, notes) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, attendance.getMemberId());
                ps.setString(2, attendance.getMemberName());
                ps.setString(3, attendance.getDate());
                ps.setString(4, attendance.getTimeIn());
                ps.setString(5, attendance.getStatus());
                ps.setString(6, attendance.getWorkoutType());
                ps.setString(7, attendance.getNotes() != null ? attendance.getNotes() : "");

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            int genId = rs.getInt(1);
                            attendance.setAttendanceId(genId);
                            return genId;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[AttendanceDAO] Saved to local cache: " + e.getMessage());
        }

        return newId;
    }

    /**
     * Retrieves all attendance records sorted by date descending.
     */
    @Override
    public List<Attendance> getAllAttendance() throws SQLException, ClassNotFoundException {
        List<Attendance> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM attendance ORDER BY date DESC, attendance_id DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql); 
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAttendance(rs));
                }
            }
        } catch (Exception ignored) {
        }

        if (!list.isEmpty()) {
            return list;
        }

        return new ArrayList<>(FALLBACK_ATTENDANCE);
    }

    /**
     * Retrieves attendance history for a specific member.
     */
    @Override
    public List<Attendance> getAttendanceByMember(int memberId) throws SQLException, ClassNotFoundException {
        List<Attendance> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM attendance WHERE member_id = ? ORDER BY date DESC, attendance_id DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(extractAttendance(rs));
                    }
                }
            }
        } catch (Exception ignored) {
        }

        if (!list.isEmpty()) {
            return list;
        }

        // Fallback to memory cache
        for (Attendance a : FALLBACK_ATTENDANCE) {
            if (a.getMemberId() == memberId) {
                list.add(a);
            }
        }
        return list;
    }

    /**
     * Retrieves attendance records recorded on a specific date.
     */
    @Override
    public List<Attendance> getAttendanceByDate(String date) throws SQLException, ClassNotFoundException {
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM attendance WHERE date = ? ORDER BY attendance_id DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, date);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(extractAttendance(rs));
                    }
                }
            }
        } catch (Exception ignored) {
        }

        if (!list.isEmpty()) return list;

        for (Attendance a : FALLBACK_ATTENDANCE) {
            if (a.getDate().equalsIgnoreCase(date)) list.add(a);
        }
        return list;
    }

    /**
     * Deletes an attendance log by its unique ID.
     */
    @Override
    public boolean deleteAttendance(int attendanceId) throws SQLException, ClassNotFoundException {
        FALLBACK_ATTENDANCE.removeIf(a -> a.getAttendanceId() == attendanceId);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM attendance WHERE attendance_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, attendanceId);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Helper to map a ResultSet row to an Attendance object.
     */
    private Attendance extractAttendance(ResultSet rs) throws SQLException {
        String notes = "";
        try {
            notes = rs.getString("notes");
        } catch (Exception ignored) {
        }

        return new Attendance(
                rs.getInt("attendance_id"),
                rs.getInt("member_id"),
                rs.getString("member_name"),
                rs.getString("date"),
                rs.getString("time_in"),
                rs.getString("status"),
                rs.getString("workout_type"),
                notes
        );
    }
}
