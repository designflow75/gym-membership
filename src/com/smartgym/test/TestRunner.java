package com.smartgym.test;

import com.smartgym.database.DatabaseConnection;
import com.smartgym.exception.AuthenticationException;
import com.smartgym.exception.GymException;
import com.smartgym.model.*;
import com.smartgym.service.AttendanceService;
import com.smartgym.service.FitnessService;
import com.smartgym.service.UserService;
import com.smartgym.util.FileHandler;

import java.io.File;
import java.util.List;

/**
 * Functional verification script testing all OOP concepts and business logic.
 * Demonstrates:
 * - Unit test execution for college project evaluation
 * - Polymorphic method dispatch verification
 * - Custom Exception handling testing
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" Running Smart Gym Management System 2.0 Tests  ");
        System.out.println("=================================================");

        int passed = 0;
        int failed = 0;

        UserService userService = new UserService();
        AttendanceService attendanceService = new AttendanceService();
        FitnessService fitnessService = new FitnessService();

        // ── Test 1: Runtime Polymorphism on User Subclasses ──
        User admin = new AdminUser("admin", "admin123", "Vikram", "9811122233", "a@g.com");
        User trainer = new TrainerUser("trainer1", "pass", "Sneha", "9822233344", "t@g.com", "Nutrition", "Morning", 40000.0);
        User member = new MemberUser("rahul", "pass", "Rahul", "9876543210", "r@g.com", "Monthly", "Regular", "Sneha", "Weight Loss", 85.0, 79.0, 72.0, 175.0);

        if (admin.getDashboardTitle().contains("Admin")
                && trainer.getDashboardTitle().contains("Trainer")
                && member.getDashboardTitle().contains("Fitness")) {
            System.out.println("[PASS] Test 1: Runtime Polymorphism (getDashboardTitle() dynamic dispatch).");
            passed++;
        } else {
            System.err.println("[FAIL] Test 1: Polymorphic dashboard title mismatch.");
            failed++;
        }

        // ── Test 2: Polymorphic Access Control ──
        if (admin.hasAccessTo("SYSTEM_SETTINGS")
                && !trainer.hasAccessTo("SYSTEM_SETTINGS")
                && member.hasAccessTo("MY_PROGRESS")) {
            System.out.println("[PASS] Test 2: Polymorphic Permission Control (hasAccessTo).");
            passed++;
        } else {
            System.err.println("[FAIL] Test 2: Access permission mismatch.");
            failed++;
        }

        // ── Test 3: Member Body Metrics & Weight Loss Calculations ──
        MemberUser m = (MemberUser) member;
        double bmi = m.getBMI(); // 79 / (1.75 * 1.75) = 25.8
        int progress = m.getProgressPercentage(); // (85 - 79) / (85 - 72) = 6/13 = ~46%

        if (bmi >= 25.0 && bmi <= 26.5 && progress > 40 && progress < 50) {
            System.out.println("[PASS] Test 3: Body metrics & Weight Loss Goal Progress calculation (" + progress + "%, BMI: " + bmi + ").");
            passed++;
        } else {
            System.err.println("[FAIL] Test 3: Calculation mismatch: progress=" + progress + ", bmi=" + bmi);
            failed++;
        }

        // ── Test 4: Custom AuthenticationException on Bad Credentials ──
        try {
            userService.login("non_existent_user_999", "wrong_password");
            System.err.println("[FAIL] Test 4: Bad login did not throw AuthenticationException!");
            failed++;
        } catch (AuthenticationException ae) {
            System.out.println("[PASS] Test 4: AuthenticationException caught as expected: " + ae.getMessage());
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 4: Unexpected exception thrown: " + e.getMessage());
            failed++;
        }

        // ── Test 5: Validation Exception on Invalid Registration ──
        try {
            userService.registerUser(new AdminUser("", "", "", "", ""));
            System.err.println("[FAIL] Test 5: Empty user did not throw GymException!");
            failed++;
        } catch (GymException ge) {
            System.out.println("[PASS] Test 5: Custom GymException validation passed: " + ge.getMessage());
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 5: Unexpected error: " + e.getMessage());
            failed++;
        }

        // ── Test 6: Method Overloading in UserService ──
        try {
            List<User> byRole = userService.getUsersByRole("MEMBER");
            User byId = userService.searchUsers(1);
            List<User> byQuery = userService.searchUsers("Rahul");
            if (byRole != null && byId != null && byQuery != null) {
                System.out.println("[PASS] Test 6: Method Overloading (searchUsers(int) vs searchUsers(String)).");
                passed++;
            } else {
                System.err.println("[FAIL] Test 6: Overloaded search returned null.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Test 6: Search exception: " + e.getMessage());
            failed++;
        }

        // ── Test 7: Attendance Tracking & Percentage Calculation ──
        try {
            Attendance att = new Attendance(4, "Rahul Sharma", "2026-10-28", "07:15 AM", "Present", "Cardio & HIIT", "Session done");
            attendanceService.markAttendance(att);
            int rate = attendanceService.calculateAttendancePercentage(4);
            if (rate > 0) {
                System.out.println("[PASS] Test 7: Attendance logging & percentage calculation (" + rate + "%).");
                passed++;
            } else {
                System.err.println("[FAIL] Test 7: Attendance percentage is 0.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Test 7: Attendance error: " + e.getMessage());
            failed++;
        }

        // ── Test 8: Diet & Workout Plan Persistence & Editing ──
        try {
            DietPlan diet = fitnessService.getDietPlan(4);
            diet.setTargetCalories(2100);
            fitnessService.saveDietPlan(diet);

            WorkoutPlan workout = fitnessService.getWorkoutPlan(4);
            workout.setTargetGoal("Intense Cut");
            fitnessService.saveWorkoutPlan(workout);

            DietPlan updatedDiet = fitnessService.getDietPlan(4);
            if (updatedDiet.getTargetCalories() == 2100) {
                System.out.println("[PASS] Test 8: Diet and Workout plan creation, editing and retrieval.");
                passed++;
            } else {
                System.err.println("[FAIL] Test 8: Diet calories mismatch.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Test 8: Fitness plan error: " + e.getMessage());
            failed++;
        }

        // ── Test 9: File I/O Roster Report Export ──
        try {
            List<User> members = userService.getUsersByRole("MEMBER");
            File temp = new File("test_members_export.txt");
            String path = FileHandler.exportMembersReport(members, temp);
            String preview = FileHandler.readFilePreview(path, 5);

            if (temp.exists() && preview.contains("SMART GYM MANAGEMENT SYSTEM")) {
                System.out.println("[PASS] Test 9: File I/O (PrintWriter/FileReader report generation).");
                passed++;
                temp.delete();
            } else {
                System.err.println("[FAIL] Test 9: Export preview verification failed.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("[FAIL] Test 9: File I/O error: " + e.getMessage());
            failed++;
        }

        // ── Test 10: Database Auto-Initialization ──
        try {
            DatabaseConnection.initializeDatabase();
            System.out.println("[PASS] Test 10: Database auto-initialization & table schema check completed.");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 10: DB error: " + e.getMessage());
            failed++;
        }

        System.out.println("=================================================");
        System.out.printf(" Test Summary: %d Passed, %d Failed%n", passed, failed);
        System.out.println("=================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}

