package com.smartgym.util;

import com.smartgym.model.Attendance;
import com.smartgym.model.MemberUser;
import com.smartgym.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Utility class providing file export and preview reading using Java I/O streams.
 */
public class FileHandler {

    /**
     * Exports gym members roster and fitness progress report to a text file.
     * Demonstrates PrintWriter formatted stream writing.
     */
    public static String exportMembersReport(List<User> members, File targetFile) throws IOException {
        File file = (targetFile != null) ? targetFile : new File("smart_gym_members_progress_report.txt");

        // Try-with-resources automatically closes PrintWriter even if an exception occurs
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            writer.println("=========================================================================================");
            writer.println("               SMART GYM MANAGEMENT SYSTEM - MEMBER PROGRESS ROSTER                    ");
            writer.println("=========================================================================================");
            writer.println("Generated on : " + timestamp);
            writer.println("Total Members: " + members.size());
            writer.println("-----------------------------------------------------------------------------------------------------------------------------");
            writer.printf("%-5s | %-18s | %-12s | %-14s | %-8s | %-8s | %-8s | %-8s | %-20s%n",
                    "ID", "NAME", "PHONE", "PLAN", "INIT(kg)", "CURR(kg)", "TARG(kg)", "BMI", "GOAL");
            writer.println("-----------------------------------------------------------------------------------------------------------------------------");

            for (User u : members) {
                // Downcast safely with pattern-matching instanceof
                if (u instanceof MemberUser m) {
                    writer.printf("%-5d | %-18s | %-12s | %-14s | %-8.1f | %-8.1f | %-8.1f | %-8.1f | %-20s%n",
                            m.getUserId(),
                            truncate(m.getFullName(), 18),
                            m.getPhoneNumber(),
                            m.getMembershipPlan() + "(" + m.getMembershipType() + ")",
                            m.getInitialWeight(),
                            m.getCurrentWeight(),
                            m.getTargetWeight(),
                            m.getBMI(),
                            truncate(m.getFitnessGoal(), 20));
                }
            }
            writer.println("-----------------------------------------------------------------------------------------------------------------------------");
            writer.println("End of Official Report. Smart Gym Management System 2.0");
        }
        return file.getAbsolutePath();
    }

    /**
     * Overloaded helper: Exports attendance history without specifying member name.
     */
    public static String exportAttendanceReport(List<Attendance> logs, File targetFile) throws IOException {
        return exportAttendanceReport(logs, "All Gym Members", targetFile);
    }

    /**
     * Exports attendance history logs to a formatted text file.
     */
    public static String exportAttendanceReport(List<Attendance> logs, String memberName, File targetFile) throws IOException {
        File file = (targetFile != null) ? targetFile : new File("attendance_report.txt");

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            writer.println("=========================================================================================");
            writer.println("               SMART GYM MANAGEMENT SYSTEM - ATTENDANCE LOG REPORT                      ");
            writer.println("=========================================================================================");
            writer.println("Member Name  : " + memberName);
            writer.println("Total Records: " + logs.size());
            writer.println("-----------------------------------------------------------------------------------------");
            writer.printf("%-6s | %-12s | %-10s | %-10s | %-25s | %-20s%n",
                    "LOG ID", "DATE", "TIME", "STATUS", "WORKOUT FOCUS", "TRAINER NOTES");
            writer.println("-----------------------------------------------------------------------------------------");

            for (Attendance a : logs) {
                writer.printf("%-6d | %-12s | %-10s | %-10s | %-25s | %-20s%n",
                        a.getAttendanceId(),
                        a.getDate(),
                        a.getTimeIn(),
                        a.getStatus(),
                        truncate(a.getWorkoutType(), 25),
                        truncate(a.getNotes(), 20));
            }
            writer.println("-----------------------------------------------------------------------------------------");
        }
        return file.getAbsolutePath();
    }

    /**
     * Reads the first N lines of an exported file for quick preview in Swing dialogs.
     * Demonstrates BufferedReader.readLine() in a loop.
     */
    public static String readFilePreview(String filePath, int maxLines) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < maxLines) {
                sb.append(line).append("\n");
                count++;
            }
        }
        return sb.toString();
    }

    private static String truncate(String text, int max) {
        if (text == null) return "-";
        return (text.length() > max) ? text.substring(0, max - 3) + "..." : text;
    }
}
