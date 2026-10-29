package com.smartgym.dao;

import com.smartgym.database.DatabaseConnection;
import com.smartgym.model.AdminUser;
import com.smartgym.model.MemberUser;
import com.smartgym.model.TrainerUser;
import com.smartgym.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entities.
 * Demonstrates:
 * - Interface implementation (UserOperations)
 * - Polymorphism (instantiates concrete AdminUser, TrainerUser, MemberUser)
 * - Method Overloading (searchUsers by int vs by String)
 * - JDBC PreparedStatements and ResultSets
 * - Resilient Fallback Cache
 */
public class UserDAO implements UserOperations {

    private static final List<User> FALLBACK_USERS = new ArrayList<>();
    private static int fallbackIdCounter = 100;

    static {
        // Pre-seed demo users for fallback / standalone offline mode
        FALLBACK_USERS.add(new AdminUser(1, "admin", "admin123", "Vikram Malhotra", "9811122233", "admin@smartgym.com", "2026-01-01", "Executive Management"));
        FALLBACK_USERS.add(new TrainerUser(2, "trainer1", "trainer123", "Sneha Sen", "9822233344", "sneha@smartgym.com", "2026-02-15", "Clinical Nutrition & Dietetics", "Morning Shift (06:00 AM - 02:00 PM)", 42000.0));
        FALLBACK_USERS.add(new TrainerUser(3, "trainer2", "trainer123", "Kabir Joshi", "9833344455", "kabir@smartgym.com", "2026-03-01", "Strength & Hypertrophy", "Evening Shift (02:00 PM - 10:00 PM)", 38000.0));
        FALLBACK_USERS.add(new MemberUser(4, "rahul", "member123", "Rahul Sharma", "9876543210", "rahul@gmail.com", "2026-08-01", "Monthly", "Regular", "Sneha Sen", "Weight Loss & Fat Burning", 85.0, 79.0, 72.0, 175.0));
        FALLBACK_USERS.add(new MemberUser(5, "priya", "member123", "Priya Nair", "9447123987", "priya@gmail.com", "2026-09-10", "Quarterly", "Premium", "Sneha Sen", "Lean Body Recomposition", 68.0, 63.5, 58.0, 162.0));
        FALLBACK_USERS.add(new MemberUser(6, "arjun", "member123", "Arjun Patel", "9988776655", "arjun@gmail.com", "2026-07-20", "Yearly", "Premium", "Kabir Joshi", "Muscle Hypertrophy & Bulk", 68.0, 74.0, 78.0, 180.0));
    }

    @Override
    public User authenticate(String username, String password) throws SQLException, ClassNotFoundException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username.trim());
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractUserFromResultSet(rs);
                    }
                }
            }
        } catch (Exception e) {
            // Fallback check
            for (User u : FALLBACK_USERS) {
                if (u.getUsername().equalsIgnoreCase(username.trim()) && u.getPassword().equals(password)) {
                    return u;
                }
            }
        }
        return null;
    }

    @Override
    public int registerUser(User user) throws SQLException, ClassNotFoundException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO users (username, password, full_name, phone, email, role, "
                    + "admin_department, specialization, shift, monthly_salary, "
                    + "membership_plan, membership_type, assigned_trainer, fitness_goal, "
                    + "initial_weight, current_weight, target_weight, height_cm) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getFullName());
                ps.setString(4, user.getPhoneNumber());
                ps.setString(5, user.getEmail());
                ps.setString(6, user.getRole());

                if (user instanceof AdminUser admin) {
                    ps.setString(7, admin.getAdminDepartment());
                    ps.setNull(8, Types.VARCHAR);
                    ps.setNull(9, Types.VARCHAR);
                    ps.setDouble(10, 0.0);
                    ps.setNull(11, Types.VARCHAR);
                    ps.setNull(12, Types.VARCHAR);
                    ps.setNull(13, Types.VARCHAR);
                    ps.setNull(14, Types.VARCHAR);
                    ps.setDouble(15, 0.0);
                    ps.setDouble(16, 0.0);
                    ps.setDouble(17, 0.0);
                    ps.setDouble(18, 0.0);
                } else if (user instanceof TrainerUser trainer) {
                    ps.setNull(7, Types.VARCHAR);
                    ps.setString(8, trainer.getSpecialization());
                    ps.setString(9, trainer.getShift());
                    ps.setDouble(10, trainer.getMonthlySalary());
                    ps.setNull(11, Types.VARCHAR);
                    ps.setNull(12, Types.VARCHAR);
                    ps.setNull(13, Types.VARCHAR);
                    ps.setNull(14, Types.VARCHAR);
                    ps.setDouble(15, 0.0);
                    ps.setDouble(16, 0.0);
                    ps.setDouble(17, 0.0);
                    ps.setDouble(18, 0.0);
                } else if (user instanceof MemberUser member) {
                    ps.setNull(7, Types.VARCHAR);
                    ps.setNull(8, Types.VARCHAR);
                    ps.setNull(9, Types.VARCHAR);
                    ps.setDouble(10, 0.0);
                    ps.setString(11, member.getMembershipPlan());
                    ps.setString(12, member.getMembershipType());
                    ps.setString(13, member.getAssignedTrainer());
                    ps.setString(14, member.getFitnessGoal());
                    ps.setDouble(15, member.getInitialWeight());
                    ps.setDouble(16, member.getCurrentWeight());
                    ps.setDouble(17, member.getTargetWeight());
                    ps.setDouble(18, member.getHeightCm());
                }

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            int newId = rs.getInt(1);
                            user.setUserId(newId);
                            syncFallback(user, true);
                            return newId;
                        }
                    }
                }
            }
        } catch (Exception e) {
            int newId = fallbackIdCounter++;
            user.setUserId(newId);
            FALLBACK_USERS.add(user);
            return newId;
        }
        return -1;
    }

    @Override
    public boolean updateUser(User user) throws SQLException, ClassNotFoundException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE users SET full_name=?, phone=?, email=?, "
                    + "admin_department=?, specialization=?, shift=?, monthly_salary=?, "
                    + "membership_plan=?, membership_type=?, assigned_trainer=?, fitness_goal=?, "
                    + "initial_weight=?, current_weight=?, target_weight=?, height_cm=? "
                    + "WHERE user_id=?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, user.getFullName());
                ps.setString(2, user.getPhoneNumber());
                ps.setString(3, user.getEmail());

                if (user instanceof AdminUser admin) {
                    ps.setString(4, admin.getAdminDepartment());
                    ps.setNull(5, Types.VARCHAR);
                    ps.setNull(6, Types.VARCHAR);
                    ps.setDouble(7, 0.0);
                    ps.setNull(8, Types.VARCHAR);
                    ps.setNull(9, Types.VARCHAR);
                    ps.setNull(10, Types.VARCHAR);
                    ps.setNull(11, Types.VARCHAR);
                    ps.setDouble(12, 0.0);
                    ps.setDouble(13, 0.0);
                    ps.setDouble(14, 0.0);
                    ps.setDouble(15, 0.0);
                } else if (user instanceof TrainerUser trainer) {
                    ps.setNull(4, Types.VARCHAR);
                    ps.setString(5, trainer.getSpecialization());
                    ps.setString(6, trainer.getShift());
                    ps.setDouble(7, trainer.getMonthlySalary());
                    ps.setNull(8, Types.VARCHAR);
                    ps.setNull(9, Types.VARCHAR);
                    ps.setNull(10, Types.VARCHAR);
                    ps.setNull(11, Types.VARCHAR);
                    ps.setDouble(12, 0.0);
                    ps.setDouble(13, 0.0);
                    ps.setDouble(14, 0.0);
                    ps.setDouble(15, 0.0);
                } else if (user instanceof MemberUser member) {
                    ps.setNull(4, Types.VARCHAR);
                    ps.setNull(5, Types.VARCHAR);
                    ps.setNull(6, Types.VARCHAR);
                    ps.setDouble(7, 0.0);
                    ps.setString(8, member.getMembershipPlan());
                    ps.setString(9, member.getMembershipType());
                    ps.setString(13 - 3, member.getAssignedTrainer());
                    ps.setString(14 - 3, member.getFitnessGoal());
                    ps.setDouble(15 - 3, member.getInitialWeight());
                    ps.setDouble(16 - 3, member.getCurrentWeight());
                    ps.setDouble(17 - 3, member.getTargetWeight());
                    ps.setDouble(18 - 3, member.getHeightCm());
                }
                ps.setInt(16, user.getUserId());

                boolean ok = ps.executeUpdate() > 0;
                if (ok) syncFallback(user, false);
                return ok;
            }
        } catch (Exception e) {
            syncFallback(user, false);
            return true;
        }
    }

    @Override
    public boolean deleteUser(int userId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM users WHERE user_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                boolean ok = ps.executeUpdate() > 0;
                FALLBACK_USERS.removeIf(u -> u.getUserId() == userId);
                return ok;
            }
        } catch (Exception e) {
            return FALLBACK_USERS.removeIf(u -> u.getUserId() == userId);
        }
    }

    @Override
    public List<User> getAllUsers() throws SQLException, ClassNotFoundException {
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users ORDER BY user_id ASC";
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractUserFromResultSet(rs));
                }
            }
            if (!list.isEmpty()) {
                FALLBACK_USERS.clear();
                FALLBACK_USERS.addAll(list);
            }
        } catch (Exception e) {
            return new ArrayList<>(FALLBACK_USERS);
        }
        return list.isEmpty() ? new ArrayList<>(FALLBACK_USERS) : list;
    }

    @Override
    public List<User> getUsersByRole(String role) throws SQLException, ClassNotFoundException {
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE UPPER(role) = UPPER(?) ORDER BY user_id ASC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, role.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(extractUserFromResultSet(rs));
                    }
                }
            }
        } catch (Exception e) {
            for (User u : FALLBACK_USERS) {
                if (u.getRole().equalsIgnoreCase(role.trim())) {
                    list.add(u);
                }
            }
        }
        return list;
    }

    @Override
    public User getUserByUsername(String username) throws SQLException, ClassNotFoundException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return extractUserFromResultSet(rs);
                }
            }
        } catch (Exception e) {
            for (User u : FALLBACK_USERS) {
                if (u.getUsername().equalsIgnoreCase(username.trim())) return u;
            }
        }
        return null;
    }

    @Override
    public User searchUsers(int userId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return extractUserFromResultSet(rs);
                }
            }
        } catch (Exception e) {
            for (User u : FALLBACK_USERS) {
                if (u.getUserId() == userId) return u;
            }
        }
        return null;
    }

    @Override
    public List<User> searchUsers(String query) throws SQLException, ClassNotFoundException {
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE LOWER(full_name) LIKE ? OR LOWER(username) LIKE ? OR phone LIKE ? ORDER BY user_id ASC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String term = "%" + query.toLowerCase().trim() + "%";
                ps.setString(1, term);
                ps.setString(2, term);
                ps.setString(3, term);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(extractUserFromResultSet(rs));
                    }
                }
            }
        } catch (Exception e) {
            for (User u : FALLBACK_USERS) {
                if (u.getFullName().toLowerCase().contains(query.toLowerCase())
                        || u.getUsername().toLowerCase().contains(query.toLowerCase())
                        || u.getPhoneNumber().contains(query)) {
                    list.add(u);
                }
            }
        }
        return list;
    }

    // --- Helper for Polymorphic Object Creation ---
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String name = rs.getString("full_name");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        String role = rs.getString("role");
        String createdDate = rs.getString("created_date");

        if ("ADMIN".equalsIgnoreCase(role)) {
            String dept = rs.getString("admin_department");
            return new AdminUser(id, username, password, name, phone, email, createdDate, dept);
        } else if ("TRAINER".equalsIgnoreCase(role)) {
            String spec = rs.getString("specialization");
            String shift = rs.getString("shift");
            double salary = rs.getDouble("monthly_salary");
            return new TrainerUser(id, username, password, name, phone, email, createdDate, spec, shift, salary);
        } else {
            String plan = rs.getString("membership_plan");
            String type = rs.getString("membership_type");
            String trainer = rs.getString("assigned_trainer");
            String goal = rs.getString("fitness_goal");
            double initW = rs.getDouble("initial_weight");
            double currW = rs.getDouble("current_weight");
            double targW = rs.getDouble("target_weight");
            double height = rs.getDouble("height_cm");
            return new MemberUser(id, username, password, name, phone, email, createdDate, plan, type, trainer, goal, initW, currW, targW, height);
        }
    }

    private void syncFallback(User user, boolean isNew) {
        if (isNew) {
            FALLBACK_USERS.add(user);
        } else {
            for (int i = 0; i < FALLBACK_USERS.size(); i++) {
                if (FALLBACK_USERS.get(i).getUserId() == user.getUserId()) {
                    FALLBACK_USERS.set(i, user);
                    return;
                }
            }
            FALLBACK_USERS.add(user);
        }
    }
}

