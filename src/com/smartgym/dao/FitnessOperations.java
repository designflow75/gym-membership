package com.smartgym.dao;

import com.smartgym.model.DietPlan;
import com.smartgym.model.WorkoutPlan;
import java.sql.SQLException;

/**
 * Interface defining contract for Diet and Workout operations.
 * Demonstrates:
 * - Interface Abstraction
 */
public interface FitnessOperations {

    DietPlan getDietPlanByMember(int memberId) throws SQLException, ClassNotFoundException;

    boolean saveOrUpdateDietPlan(DietPlan dietPlan) throws SQLException, ClassNotFoundException;

    WorkoutPlan getWorkoutPlanByMember(int memberId) throws SQLException, ClassNotFoundException;

    boolean saveOrUpdateWorkoutPlan(WorkoutPlan workoutPlan) throws SQLException, ClassNotFoundException;
}

