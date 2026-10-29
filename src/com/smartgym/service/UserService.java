package com.smartgym.service;

import com.smartgym.dao.UserDAO;
import com.smartgym.dao.UserOperations;
import com.smartgym.exception.AuthenticationException;
import com.smartgym.exception.GymException;
import com.smartgym.model.AdminUser;
import com.smartgym.model.MemberUser;
import com.smartgym.model.TrainerUser;
import com.smartgym.model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class handling User Authentication, Registration, and Polymorphic Operations.
 * Demonstrates OOP Concepts:
 * - Runtime Polymorphism (invoking abstract methods on User reference)
 * - Method Overloading (multiple registerUser and searchUsers methods)
 * - Custom Exception Handling (GymException, AuthenticationException)
 * - Encapsulation and Data Validation
 */
public class UserService {

    private final UserOperations userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // Overloaded constructor for dependency injection / test isolation
    public UserService(UserOperations dao) {
        this.userDAO = dao;
    }

    /**
     * Authenticates user against credentials.
     * Demonstrates custom AuthenticationException.
     */
    public User login(String username, String password) throws AuthenticationException, SQLException, ClassNotFoundException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Username and Password are required to sign in.");
        }

        User user = userDAO.authenticate(username.trim(), password);
        if (user == null) {
            throw new AuthenticationException("Invalid username or password. Please verify your credentials.");
        }
        return user;
    }

    /**
     * Registers a new User (Admin, Trainer, or Member).
     */
    public int registerUser(User user) throws GymException, SQLException, ClassNotFoundException {
        validateUser(user);

        // Check if username already exists
        User existing = userDAO.getUserByUsername(user.getUsername());
        if (existing != null) {
            throw new GymException("Username '" + user.getUsername() + "' is already taken. Please choose another.");
        }

        return userDAO.registerUser(user);
    }

    /**
     * Method Overloading: Convenience helper to register a new Member directly from form inputs.
     */
    public int registerMember(String username, String password, String fullName, String phone, String email,
                               String plan, String type, String trainer, String goal,
                               double initialWeight, double currentWeight, double targetWeight, double heightCm)
            throws GymException, SQLException, ClassNotFoundException {

        MemberUser member = new MemberUser(username, password, fullName, phone, email,
                plan, type, trainer, goal, initialWeight, currentWeight, targetWeight, heightCm);
        return registerUser(member);
    }

    /**
     * Method Overloading: Convenience helper to register a new Trainer.
     */
    public int registerTrainer(String username, String password, String fullName, String phone, String email,
                                String specialization, String shift, double salary)
            throws GymException, SQLException, ClassNotFoundException {

        TrainerUser trainer = new TrainerUser(username, password, fullName, phone, email, specialization, shift, salary);
        return registerUser(trainer);
    }

    public boolean updateUser(User user) throws GymException, SQLException, ClassNotFoundException {
        if (user.getUserId() <= 0) {
            throw new GymException("Cannot update user with invalid ID.");
        }
        validateUser(user);
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int userId) throws GymException, SQLException, ClassNotFoundException {
        if (userId <= 0) {
            throw new GymException("Invalid User ID provided for deletion.");
        }
        return userDAO.deleteUser(userId);
    }

    public List<User> getAllUsers() throws SQLException, ClassNotFoundException {
        return userDAO.getAllUsers();
    }

    public List<User> getUsersByRole(String role) throws SQLException, ClassNotFoundException {
        return userDAO.getUsersByRole(role);
    }

    /**
     * Method Overloading 1: Search by string query.
     */
    public List<User> searchUsers(String query) throws SQLException, ClassNotFoundException {
        if (query == null || query.trim().isEmpty()) {
            return userDAO.getAllUsers();
        }
        return userDAO.searchUsers(query.trim());
    }

    /**
     * Method Overloading 2: Search by numeric ID.
     */
    public User searchUsers(int userId) throws SQLException, ClassNotFoundException {
        return userDAO.searchUsers(userId);
    }

    /**
     * Runtime Polymorphism in action:
     * Generates a dynamic greeting card and authorization message by invoking
     * overridden methods on the User superclass reference.
     */
    public String getPolymorphicGreeting(User user) {
        if (user == null) return "Welcome, Guest!";
        return String.format("%s | %s", user.getDashboardTitle(), user.getRoleDescription());
    }

    private void validateUser(User user) throws GymException {
        if (user == null) {
            throw new GymException("User details cannot be null.");
        }
        if (user.getUsername() == null || user.getUsername().trim().length() < 3) {
            throw new GymException("Username must be at least 3 characters long.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 4) {
            throw new GymException("Password must be at least 4 characters long.");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new GymException("Full name cannot be blank.");
        }
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            String cleanPhone = user.getPhoneNumber().replaceAll("[^0-9]", "");
            if (cleanPhone.length() != 10) {
                throw new GymException("Phone number must contain exactly 10 digits.");
            }
        }
    }
}

