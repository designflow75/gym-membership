package com.smartgym.dao;

import com.smartgym.database.DatabaseConnection;
import com.smartgym.model.DietPlan;
import com.smartgym.model.WorkoutPlan;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object for Fitness Plans (Diets and Workouts).
 * Demonstrates:
 * - Interface implementation (FitnessOperations)
 * - Map collection for in-memory fast caching
 * - JDBC PreparedStatements with upsert/merge logic
 * - Blank starter plans for new members until assigned by trainer
 */
public class FitnessDAO implements FitnessOperations {

    private static final Map<Integer, DietPlan> FALLBACK_DIETS = new HashMap<>();
    private static final Map<Integer, WorkoutPlan> FALLBACK_WORKOUTS = new HashMap<>();

    static {
        // Pre-seed sample diet and workout ONLY for demo member Rahul Sharma (Member ID 4)
        DietPlan rahulDiet = new DietPlan(
                1, 4, "Rahul Sharma", "Sneha Sen", LocalDate.now().toString(),
                1900, 140, 180, 50,
                "3 Boiled Egg whites + 1 Whole Egg, 1 slice Multigrain bread, Black Coffee",
                "150g Grilled Chicken/Tofu, 1 bowl Brown Rice, Dal & Cucumber Salad",
                "Handful of Almonds & Walnuts + 1 Green Apple",
                "Soya chunks / Fish with Stir-fried Green Broccoli, Bell Peppers & Quinoa",
                3.5,
                "Strict calorie deficit of 400 kcal below maintenance for steady fat loss.",
                "Completed 1850 kcal yesterday. Feeling good energy on morning workouts."
        );
        FALLBACK_DIETS.put(4, rahulDiet);

        WorkoutPlan rahulWorkout = new WorkoutPlan(
                1, 4, "Rahul Sharma", "Sneha Sen", LocalDate.now().toString(),
                "Weight Loss & Fat Burning", 5,
                "Chest & Triceps: Push-ups (3x15), Dumbbell Bench Press (4x12), Cable Pushdowns (3x15)",
                "Back & Biceps: Lat Pulldowns (4x12), Dumbbell Rows (3x12), Bicep Curls (3x12)",
                "Cardio & Core: 30 mins Incline Treadmill Walk + 10 mins Mountain Climbers & Planks",
                "Shoulders: Seated Dumbbell Press (4x10), Lateral Raises (4x15), Rear Delt Flyes (3x12)",
                "Legs & Abs: Bodyweight Squats (4x15), Lunges (3x12), Leg Press (3x12), Hanging Knee Raises",
                "Active Mobility: 45 mins cycling or light swimming",
                "Full Rest & Recovery",
                "Target heart rate 130-145 bpm during cardio. Drink electrolyte water.",
                "Focus on progressive overload each week.",
                "Logged 5 days this week. Weight dropped from 85kg to 79kg."
        );
        FALLBACK_WORKOUTS.put(4, rahulWorkout);
    }

    @Override
    public DietPlan getDietPlanByMember(int memberId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM diet_plans WHERE member_id = ? ORDER BY plan_id DESC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractDietPlan(rs);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        // Check fallback cache
        if (FALLBACK_DIETS.containsKey(memberId)) {
            return FALLBACK_DIETS.get(memberId);
        }

        // For newly created accounts: Return clean blank plan so trainer can create it!
        DietPlan blankPlan = DietPlan.createBlankPlan(memberId, "Member #" + memberId, "Assigned Trainer");
        FALLBACK_DIETS.put(memberId, blankPlan);
        return blankPlan;
    }

    @Override
    public boolean saveOrUpdateDietPlan(DietPlan plan) throws SQLException, ClassNotFoundException {
        FALLBACK_DIETS.put(plan.getMemberId(), plan);

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM diet_plans WHERE member_id=?")) {
                del.setInt(1, plan.getMemberId());
                del.executeUpdate();
            }

            String sql = "INSERT INTO diet_plans (member_id, member_name, assigned_trainer, last_updated, "
                    + "target_calories, protein_grams, carbs_grams, fats_grams, "
                    + "breakfast, lunch, snacks, dinner, water_liters, general_notes, user_daily_log) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, plan.getMemberId());
                ps.setString(2, plan.getMemberName());
                ps.setString(3, plan.getAssignedTrainer());
                ps.setString(4, LocalDate.now().toString());
                ps.setInt(5, plan.getTargetCalories());
                ps.setInt(6, plan.getProteinGrams());
                ps.setInt(7, plan.getCarbsGrams());
                ps.setInt(8, plan.getFatsGrams());
                ps.setString(9, plan.getBreakfast());
                ps.setString(10, plan.getLunch());
                ps.setString(11, plan.getSnacks());
                ps.setString(12, plan.getDinner());
                ps.setDouble(13, plan.getWaterIntakeLiters());
                ps.setString(14, plan.getGeneralNotes());
                ps.setString(15, plan.getUserDailyLog());

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) plan.setPlanId(rs.getInt(1));
                    }
                    return true;
                }
            }
        } catch (Exception ignored) {
            return true;
        }
        return true;
    }

    @Override
    public WorkoutPlan getWorkoutPlanByMember(int memberId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM workout_plans WHERE member_id = ? ORDER BY plan_id DESC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, memberId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractWorkoutPlan(rs);
                    }
                }
            }
        } catch (Exception ignored) {
        }

        if (FALLBACK_WORKOUTS.containsKey(memberId)) {
            return FALLBACK_WORKOUTS.get(memberId);
        }

        // For newly created accounts: Return clean blank plan so trainer can create it!
        WorkoutPlan blankPlan = WorkoutPlan.createBlankPlan(memberId, "Member #" + memberId, "Assigned Trainer");
        FALLBACK_WORKOUTS.put(memberId, blankPlan);
        return blankPlan;
    }

    @Override
    public boolean saveOrUpdateWorkoutPlan(WorkoutPlan plan) throws SQLException, ClassNotFoundException {
        FALLBACK_WORKOUTS.put(plan.getMemberId(), plan);

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM workout_plans WHERE member_id=?")) {
                del.setInt(1, plan.getMemberId());
                del.executeUpdate();
            }

            String sql = "INSERT INTO workout_plans (member_id, member_name, assigned_trainer, last_updated, "
                    + "target_goal, days_per_week, monday_workout, tuesday_workout, wednesday_workout, "
                    + "thursday_workout, friday_workout, saturday_workout, sunday_workout, "
                    + "cardio_guidelines, general_notes, user_workout_log) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, plan.getMemberId());
                ps.setString(2, plan.getMemberName());
                ps.setString(3, plan.getAssignedTrainer());
                ps.setString(4, LocalDate.now().toString());
                ps.setString(5, plan.getTargetGoal());
                ps.setInt(6, plan.getDaysPerWeek());
                ps.setString(7, plan.getMondayWorkout());
                ps.setString(8, plan.getTuesdayWorkout());
                ps.setString(9, plan.getWednesdayWorkout());
                ps.setString(10, plan.getThursdayWorkout());
                ps.setString(11, plan.getFridayWorkout());
                ps.setString(12, plan.getSaturdayWorkout());
                ps.setString(13, plan.getSundayWorkout());
                ps.setString(14, plan.getCardioGuidelines());
                ps.setString(15, plan.getGeneralNotes());
                ps.setString(16, plan.getUserWorkoutLog());

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) plan.setPlanId(rs.getInt(1));
                    }
                    return true;
                }
            }
        } catch (Exception ignored) {
            return true;
        }
        return true;
    }

    private DietPlan extractDietPlan(ResultSet rs) throws SQLException {
        return new DietPlan(
                rs.getInt("plan_id"),
                rs.getInt("member_id"),
                rs.getString("member_name"),
                rs.getString("assigned_trainer"),
                rs.getString("last_updated"),
                rs.getInt("target_calories"),
                rs.getInt("protein_grams"),
                rs.getInt("carbs_grams"),
                rs.getInt("fats_grams"),
                rs.getString("breakfast"),
                rs.getString("lunch"),
                rs.getString("snacks"),
                rs.getString("dinner"),
                rs.getDouble("water_liters"),
                rs.getString("general_notes"),
                rs.getString("user_daily_log")
        );
    }

    private WorkoutPlan extractWorkoutPlan(ResultSet rs) throws SQLException {
        return new WorkoutPlan(
                rs.getInt("plan_id"),
                rs.getInt("member_id"),
                rs.getString("member_name"),
                rs.getString("assigned_trainer"),
                rs.getString("last_updated"),
                rs.getString("target_goal"),
                rs.getInt("days_per_week"),
                rs.getString("monday_workout"),
                rs.getString("tuesday_workout"),
                rs.getString("wednesday_workout"),
                rs.getString("thursday_workout"),
                rs.getString("friday_workout"),
                rs.getString("saturday_workout"),
                rs.getString("sunday_workout"),
                rs.getString("cardio_guidelines"),
                rs.getString("general_notes"),
                rs.getString("user_workout_log")
        );
    }
}
