package com.smartgym.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manages JDBC connections to MySQL and automatically initializes required database tables.
 */
public class DatabaseConnection {

    private static String dbHost = "localhost";
    private static String dbPort = "3306";
    private static String dbName = "smart_gym_db";
    private static String dbUser = "root";
    private static String dbPassword = "CampusOne@2026!";

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static boolean dbAvailable = false;

    static {
        loadProperties();
    }

    /**
     * Reads database connection settings from db.properties if present.
     */
    private static void loadProperties() {
        Properties props = new Properties();
        File propFile = new File("db.properties");
        if (propFile.exists()) {
            try (InputStream input = new FileInputStream(propFile)) {
                props.load(input);
                dbHost = props.getProperty("db.host", dbHost);
                dbPort = props.getProperty("db.port", dbPort);
                dbName = props.getProperty("db.name", dbName);
                dbUser = props.getProperty("db.user", dbUser);
                dbPassword = props.getProperty("db.password", dbPassword);
            } catch (Exception e) {
                System.err.println("[DatabaseConnection] Using default settings: " + e.getMessage());
            }
        }
    }

    /**
     * Establishes and returns a new JDBC Connection to MySQL.
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER_CLASS);
        String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }

    /**
     * Checks if the database is reachable.
     */
    public static boolean checkConnection() {
        try (Connection conn = getConnection()) {
            dbAvailable = (conn != null && !conn.isClosed());
            return dbAvailable;
        } catch (Exception e) {
            dbAvailable = false;
            return false;
        }
    }

    public static boolean isDbAvailable() {
        return dbAvailable;
    }

    /**
     * Creates all necessary tables on initial launch and runs schema migrations.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            dbAvailable = true;

            // 1. Users Table
            String createUsersSQL = "CREATE TABLE IF NOT EXISTS users ("
                    + "user_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "username VARCHAR(50) UNIQUE NOT NULL, "
                    + "password VARCHAR(100) NOT NULL, "
                    + "full_name VARCHAR(100) NOT NULL, "
                    + "phone VARCHAR(15), "
                    + "email VARCHAR(100), "
                    + "role VARCHAR(20) NOT NULL, "
                    + "admin_department VARCHAR(100), "
                    + "specialization VARCHAR(100), "
                    + "shift VARCHAR(100), "
                    + "monthly_salary DECIMAL(10,2) DEFAULT 0.0, "
                    + "membership_plan VARCHAR(20), "
                    + "membership_type VARCHAR(20), "
                    + "assigned_trainer VARCHAR(100), "
                    + "fitness_goal VARCHAR(100), "
                    + "initial_weight DECIMAL(5,2) DEFAULT 70.0, "
                    + "current_weight DECIMAL(5,2) DEFAULT 70.0, "
                    + "target_weight DECIMAL(5,2) DEFAULT 65.0, "
                    + "height_cm DECIMAL(5,2) DEFAULT 170.0, "
                    + "created_date DATE DEFAULT (CURRENT_DATE)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
            stmt.executeUpdate(createUsersSQL);

            // 2. Attendance Table
            String createAttendanceSQL = "CREATE TABLE IF NOT EXISTS attendance ("
                    + "attendance_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "member_id INT NOT NULL, "
                    + "member_name VARCHAR(100) NOT NULL, "
                    + "date DATE NOT NULL, "
                    + "time_in VARCHAR(20), "
                    + "status VARCHAR(20) NOT NULL, "
                    + "workout_type VARCHAR(100), "
                    + "notes TEXT, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
            stmt.executeUpdate(createAttendanceSQL);

            // Ensure notes column exists in existing attendance tables
            try {
                stmt.executeUpdate("ALTER TABLE attendance ADD COLUMN notes TEXT");
            } catch (Exception ignored) {
            }

            // 3. Diet Plans Table
            String createDietSQL = "CREATE TABLE IF NOT EXISTS diet_plans ("
                    + "plan_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "member_id INT NOT NULL, "
                    + "member_name VARCHAR(100) NOT NULL, "
                    + "assigned_trainer VARCHAR(100), "
                    + "last_updated DATE, "
                    + "target_calories INT, "
                    + "protein_grams INT, "
                    + "carbs_grams INT, "
                    + "fats_grams INT, "
                    + "breakfast TEXT, "
                    + "lunch TEXT, "
                    + "snacks TEXT, "
                    + "dinner TEXT, "
                    + "water_liters DECIMAL(4,2), "
                    + "general_notes TEXT, "
                    + "user_daily_log TEXT"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
            stmt.executeUpdate(createDietSQL);

            // 4. Workout Plans Table
            String createWorkoutSQL = "CREATE TABLE IF NOT EXISTS workout_plans ("
                    + "plan_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "member_id INT NOT NULL, "
                    + "member_name VARCHAR(100) NOT NULL, "
                    + "assigned_trainer VARCHAR(100), "
                    + "last_updated DATE, "
                    + "target_goal VARCHAR(100), "
                    + "days_per_week INT, "
                    + "monday_workout TEXT, "
                    + "tuesday_workout TEXT, "
                    + "wednesday_workout TEXT, "
                    + "thursday_workout TEXT, "
                    + "friday_workout TEXT, "
                    + "saturday_workout TEXT, "
                    + "sunday_workout TEXT, "
                    + "cardio_guidelines TEXT, "
                    + "general_notes TEXT, "
                    + "user_workout_log TEXT"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
            stmt.executeUpdate(createWorkoutSQL);

            seedDefaultAccounts(conn);
            System.out.println("[SMART GYM] MySQL Database & Tables verified successfully.");
        } catch (Exception e) {
            dbAvailable = false;
            System.err.println("[SMART GYM] MySQL offline: active in-memory fallback enabled.");
        }
    }

    /**
     * Seeds initial demonstration accounts if the database is newly created.
     */
    private static void seedDefaultAccounts(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                // Seed demo trainers
                stmt.executeUpdate("INSERT INTO users (username, password, full_name, phone, email, role, specialization, shift, monthly_salary) "
                        + "VALUES ('trainer1', 'trainer123', 'Sneha Sen', '9822233344', 'sneha@smartgym.com', 'TRAINER', 'Clinical Nutrition & Dietetics', 'Morning Shift (06:00 AM - 02:00 PM)', 42000.0)");
                stmt.executeUpdate("INSERT INTO users (username, password, full_name, phone, email, role, specialization, shift, monthly_salary) "
                        + "VALUES ('trainer2', 'trainer123', 'Kabir Joshi', '9833344455', 'kabir@smartgym.com', 'TRAINER', 'Strength & Hypertrophy', 'Evening Shift (02:00 PM - 10:00 PM)', 38000.0)");

                // Seed demo member
                stmt.executeUpdate("INSERT INTO users (username, password, full_name, phone, email, role, membership_plan, membership_type, assigned_trainer, fitness_goal, initial_weight, current_weight, target_weight, height_cm) "
                        + "VALUES ('rahul', 'member123', 'Rahul Sharma', '9876543210', 'rahul@gmail.com', 'MEMBER', 'Monthly', 'Regular', 'Sneha Sen', 'Weight Loss & Fat Burning', 85.0, 79.0, 72.0, 175.0)");

                System.out.println("[SMART GYM] Default demo accounts seeded.");
            }
        } catch (Exception ignored) {
        }
    }
}
