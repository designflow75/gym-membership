package com.smartgym.service;

import com.smartgym.dao.FitnessDAO;
import com.smartgym.dao.FitnessOperations;
import com.smartgym.dao.UserDAO;
import com.smartgym.dao.UserOperations;
import com.smartgym.exception.GymException;
import com.smartgym.model.DietPlan;
import com.smartgym.model.MemberUser;
import com.smartgym.model.User;
import com.smartgym.model.WorkoutPlan;

import java.sql.SQLException;

/**
 * Service class managing personalized diet plans, workout routines, and member body measurements.
 */
public class FitnessService {

    private final FitnessOperations fitnessDAO;
    private final UserOperations userDAO;

    // Default Constructor: Injects default DAOs
    public FitnessService() {
        this.fitnessDAO = new FitnessDAO();
        this.userDAO = new UserDAO();
    }

    // Overloaded Constructor for testing
    public FitnessService(FitnessOperations fitnessDAO, UserOperations userDAO) {
        this.fitnessDAO = fitnessDAO;
        this.userDAO = userDAO;
    }

    /**
     * Retrieves the DietPlan for a member.
     * Returns a blank plan if no diet has been assigned yet.
     */
    public DietPlan getDietPlan(int memberId) throws SQLException, ClassNotFoundException {
        return fitnessDAO.getDietPlanByMember(memberId);
    }

    /**
     * Validates and saves/updates a DietPlan prescribed by a trainer.
     */
    public boolean saveDietPlan(DietPlan dietPlan) throws GymException, SQLException, ClassNotFoundException {
        if (dietPlan == null) {
            throw new GymException("Diet plan details cannot be null.");
        }
        if (dietPlan.getTargetCalories() <= 0) {
            throw new GymException("Target daily calories must be a positive number.");
        }
        return fitnessDAO.saveOrUpdateDietPlan(dietPlan);
    }

    /**
     * Retrieves the 7-day WorkoutPlan for a member.
     */
    public WorkoutPlan getWorkoutPlan(int memberId) throws SQLException, ClassNotFoundException {
        return fitnessDAO.getWorkoutPlanByMember(memberId);
    }

    /**
     * Validates and saves/updates a weekly WorkoutPlan designed by a trainer.
     */
    public boolean saveWorkoutPlan(WorkoutPlan workoutPlan) throws GymException, SQLException, ClassNotFoundException {
        if (workoutPlan == null) {
            throw new GymException("Workout routine details cannot be null.");
        }
        if (workoutPlan.getDaysPerWeek() < 1 || workoutPlan.getDaysPerWeek() > 7) {
            throw new GymException("Weekly workout frequency must be between 1 and 7 days.");
        }
        return fitnessDAO.saveOrUpdateWorkoutPlan(workoutPlan);
    }

    /**
     * Updates body measurements (current weight, target weight, height).
     * Called when a Member inputs their new scale weight in the User Panel,
     * or when a Trainer logs official scale weigh-ins in the Staff Panel!
     * 
     * Demonstrates: Validation bounds and safe polymorphic downcasting (instanceof).
     */
    public boolean updateMemberBodyMetrics(int memberId, double currentWeight, double targetWeight, double heightCm)
            throws GymException, SQLException, ClassNotFoundException {

        // Validate realistic human boundary limits
        if (currentWeight <= 20.0 || currentWeight >= 300.0) {
            throw new GymException("Please enter a realistic Current Weight (between 20kg and 300kg).");
        }
        if (targetWeight <= 20.0 || targetWeight >= 300.0) {
            throw new GymException("Please enter a realistic Target Weight (between 20kg and 300kg).");
        }
        if (heightCm <= 50.0 || heightCm >= 250.0) {
            throw new GymException("Please enter a realistic Height (between 50cm and 250cm).");
        }

        // Search user and downcast safely using pattern matching instanceof
        User user = userDAO.searchUsers(memberId);
        if (user instanceof MemberUser member) {
            member.setCurrentWeight(currentWeight);
            member.setTargetWeight(targetWeight);
            member.setHeightCm(heightCm);
            return userDAO.updateUser(member);
        }
        throw new GymException("Target user is not a gym member.");
    }
}
