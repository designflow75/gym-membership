package com.smartgym.exception;

/**
 * Custom base exception class for Gym Management System.
 * Demonstrates OOP Concepts:
 * - Exception Handling (custom checked exception)
 * - Inheritance (extends Exception)
 * - Constructor Overloading
 */
public class GymException extends Exception {

    // Default Constructor
    public GymException() {
        super("An error occurred in Smart Gym Management System.");
    }

    // Overloaded Constructor with custom message
    public GymException(String message) {
        super(message);
    }

    // Overloaded Constructor with message and cause
    public GymException(String message, Throwable cause) {
        super(message, cause);
    }
}

