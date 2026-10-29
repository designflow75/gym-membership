package com.smartgym.dao;

import com.smartgym.model.User;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface defining contract for User Operations (CRUD & Authentication).
 * Demonstrates OOP Concepts:
 * - Interface
 * - Abstraction
 * - Method Overloading (searchUsers by String and searchUsers by int)
 */
public interface UserOperations {

    /**
     * Authenticates user against credentials.
     */
    User authenticate(String username, String password) throws SQLException, ClassNotFoundException;

    /**
     * Registers a new user.
     * @return Generated user_id or -1 on failure
     */
    int registerUser(User user) throws SQLException, ClassNotFoundException;

    /**
     * Updates an existing user record.
     */
    boolean updateUser(User user) throws SQLException, ClassNotFoundException;

    /**
     * Deletes a user by ID.
     */
    boolean deleteUser(int userId) throws SQLException, ClassNotFoundException;

    /**
     * Retrieves all users.
     */
    List<User> getAllUsers() throws SQLException, ClassNotFoundException;

    /**
     * Retrieves users filtered by role ("ADMIN", "TRAINER", "MEMBER").
     */
    List<User> getUsersByRole(String role) throws SQLException, ClassNotFoundException;

    /**
     * Retrieves a user by their unique username.
     */
    User getUserByUsername(String username) throws SQLException, ClassNotFoundException;

    /**
     * Method Overloading: Search users by numeric ID.
     */
    User searchUsers(int userId) throws SQLException, ClassNotFoundException;

    /**
     * Method Overloading: Search users by string query (name, username, phone).
     */
    List<User> searchUsers(String query) throws SQLException, ClassNotFoundException;
}

