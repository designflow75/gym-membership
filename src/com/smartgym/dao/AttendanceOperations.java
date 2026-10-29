package com.smartgym.dao;

import com.smartgym.model.Attendance;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface defining contract for Attendance tracking operations.
 * Demonstrates:
 * - Interface Abstraction
 * - Contract-driven development
 */
public interface AttendanceOperations {

    int markAttendance(Attendance attendance) throws SQLException, ClassNotFoundException;

    List<Attendance> getAllAttendance() throws SQLException, ClassNotFoundException;

    List<Attendance> getAttendanceByMember(int memberId) throws SQLException, ClassNotFoundException;

    List<Attendance> getAttendanceByDate(String date) throws SQLException, ClassNotFoundException;

    boolean deleteAttendance(int attendanceId) throws SQLException, ClassNotFoundException;
}

